package cz.cuzk.services.atomfeed.keeper;

import cz.cuzk.services.atomfeed.config.*;
import cz.cuzk.services.atomfeed.database.DatabaseHandler;
import cz.cuzk.services.atomfeed.database.DriverException;
import cz.cuzk.services.atomfeed.feed.common.AtomFeedException;
import cz.cuzk.services.atomfeed.feed.common.DatasetFile;
import cz.cuzk.services.atomfeed.feed.datasetfeed.DatasetFeed;
import cz.cuzk.services.atomfeed.feed.downloadservicefeed.DownloadServiceFeed;
import cz.cuzk.services.atomfeed.feed.dsfeed.ServiceGroupFeed;
import cz.cuzk.services.atomfeed.index.AtomIndex;
import cz.cuzk.services.atomfeed.opensearch.OpenSearchDescription;
import cz.cuzk.services.atomfeed.util.ftp.FTPException;
import cz.cuzk.services.atomfeed.util.ftp.MyFTPClient;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Třída Updater je hlavní třídou balíčku keeper. Má za úkol aktualizovat databázi, generovat kanály a vystavit je.
 */
public class Updater {
    //------------------------------------------------------------------------------------------------------------------
    //DATA MEMBERS
    //------------------------------------------------------------------------------------------------------------------
    private String connectionString;
    private Config config = null;
    private Connection conn;
    private DatabaseHandler dbHandler;
    private ArrayList<Service> changedService;

    private Logger logger = null;
    private URL location = Updater.class.getProtectionDomain().getCodeSource().getLocation();
    //------------------------------------------------------------------------------------------------------------------
    //PUBLIC METHODS
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Načte data z konfiguračního souboru a připojí driver pro práci s databází.
     * @param logger kam má logovat
     * @throws InvalidConfigFileException
     * @throws DriverException
     */
    public Updater(Logger logger) throws InvalidConfigFileException, DriverException {

        ConfigReader cr = new ConfigReader();
        cr.read();

        this.config = cr.getConfigData();
        this.connectionString = this.config.getDbConn().getConnectionString();
        this.changedService = new ArrayList<Service>();
        this.logger = logger;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }catch (ClassNotFoundException e){
            throw new DriverException("Oracle JDBC driver nenalezen.");
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Připojí potřebné disky ze sítě a připojí se do databáze.
     * @throws IOException
     * @throws InterruptedException
     * @throws TableNotFoundException
     * @throws SQLException
     * @throws DriverException
     * @throws InvalidConfigFileException
     * @throws ColumnNotFoundException
     */
    public void initialize() throws IOException, InterruptedException, TableNotFoundException, SQLException,
            DriverException, InvalidConfigFileException, ColumnNotFoundException {

        this.mapDisks();
        this.connectToDatabase();
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Zjistí zda nějaká služba nebo složby potřebují aktualizovat a pokud ano provede aktualizaci a
     * vystaví nové kanály.
     * @throws DriverException
     * @throws InvalidConfigFileException
     * @throws ColumnNotFoundException
     * @throws SQLException
     * @throws TableNotFoundException
     * @throws UnknownDatasetException
     * @throws AtomFeedException
     * @throws DownloadServiceNotFoundException
     * @throws IOException
     * @throws FTPException
     * @throws TemplateException
     */
    public void update() throws DriverException, InvalidConfigFileException, ColumnNotFoundException, SQLException,
            TableNotFoundException, UnknownDatasetException, AtomFeedException, DownloadServiceNotFoundException,
            IOException, FTPException, TemplateException {

        this.checkForChanges();

        if(!this.changedService.isEmpty()) {

            for (Service service : this.changedService) {
                logger.info("Obdrzen pokyn k aktualizaci - stahovaci sluzby: " + service.getServiceId());
                this.deleteTempDir();

                //Pro jistotu bych mohl promazavat tabulky, ale pak by mi mohla uniknout chyba
                //this.dbHandler.clearDatasetUpdateTable();
                //this.dbHandler.clearDatasetDeleteTable();

                this.dbHandler.updateServiceFeedId(service.getServiceId());
                this.dbHandler.updateServiceOpenSearchlink(service.getServiceId());
                this.dbHandler.updateServiceMetadataLink(service.getServiceId());
                updateServiceAbstrakt(service.getServiceId());

                logger.info("Aktualizuji databazi");
                this.updateFilesTable(service);
                this.updateDatasetTable(service);
                logger.info("Aktualizace databaze dokoncena");

                this.updateFeeds(service.getServiceId());
                this.updateCustomFeeds();

                //publishFeeds(service.getServiceId());
                publishFeedsToFTP(service.getServiceId());

                this.dbHandler.commitChangesToDatabase(service.getServiceId(), service.getDateOfChange());
                logger.info("Aktualizace stahovací sluzby: " + service.getServiceId() + " dokoncena a potvrzena");
                this.conn.commit();

                logger.info("generuji osd");
                OpenSearchDescription.getInstance().createOSD(service.getServiceId());
                logger.info("nahravam osd na FTP server");
                OpenSearchDescription.getInstance().uploadToFTP(service.getServiceId());

                //uz nepotrebujeme
//                logger.info("generuji index");
//                AtomIndex.getInstance().createIndex();
//                logger.info("nahravam index na FTP server");
//                AtomIndex.getInstance().uploadToFTP();
            }
        }else{
            logger.log(Level.INFO, "Neni co aktualizovat");
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Sestavuje sdružující feedy, které jsou definovány v konfiguračním souboru.
     * @throws AtomFeedException
     * @throws InvalidConfigFileException
     * @throws ColumnNotFoundException
     * @throws SQLException
     * @throws TableNotFoundException
     * @throws DriverException
     */
    private void updateCustomFeeds() throws AtomFeedException, InvalidConfigFileException, ColumnNotFoundException,
            SQLException, TableNotFoundException, DriverException {

        logger.info("Aktualizuji skupinove feedy");

        for(CustomFeed cf : this.config.getCustomFeed()){
            ServiceGroupFeed sgfeed = new ServiceGroupFeed(cf.getFileName(), cf.getTitle(),
                                                           cf.getServiceCodes(), this.dbHandler, this.logger);
            sgfeed.construct();
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Nejprve zavolá funkci deleteCancelFeeds, která smaže již neplatné datasetové kanály a poté vygeneruje nové,
     * nebo aktualizované datasetové kanály.
     * @param serviceId
     * @throws SQLException
     * @throws TableNotFoundException
     * @throws AtomFeedException
     * @throws DriverException
     * @throws InvalidConfigFileException
     * @throws ColumnNotFoundException
     * @throws DownloadServiceNotFoundException
     * @throws IOException
     * @throws FTPException
     */
    public void updateFeeds(String serviceId) throws SQLException, TableNotFoundException, AtomFeedException, DriverException,
            InvalidConfigFileException, ColumnNotFoundException, DownloadServiceNotFoundException, IOException, FTPException {

        deleteCanceledFeeds(serviceId);

        ArrayList<String> dlsCodes = dbHandler.getUpdateRequests();

        if(!dlsCodes.isEmpty()) {
            logger.info(dlsCodes.size() + " dataset feedu k aktualizaci");
            for (String dlsCode : dlsCodes) {
                DatasetFeed dFeed = new DatasetFeed(dlsCode, serviceId, this.dbHandler);
                dFeed.construct();
                dbHandler.deleteUpdateRequest(dlsCode);
            }
            logger.info("Dataset feedy sestaveny");
            DownloadServiceFeed topFeed = new DownloadServiceFeed(serviceId, this.dbHandler);
            topFeed.construct();
            logger.info("Hlavni feed sestaven");

        } else {
            logger.info("Zadne dataset feedy k aktualizaci.");
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Smaže z FTP serveru všechny datasetové kanály, které najde v tabulce atom_dataset_delete.
     * @param serviceId
     * @throws SQLException
     * @throws IOException
     * @throws FTPException
     */
    private void deleteCanceledFeeds(String serviceId) throws SQLException, IOException, FTPException {

        ArrayList<String> dlsCodes = dbHandler.getDeleteRequests();
        logger.info("Nalezeno " + dlsCodes.size() + " feedu ke smazani");

        if(dlsCodes.size() > 0) {

            MyFTPClient myFTPClient = new MyFTPClient();

            try {
                myFTPClient.connect(this.config.getFtp().getHost(), this.config.getFtp().getUser(),
                        this.config.getFtp().getPassword());

                for (String dlsCode : dlsCodes) {

                    myFTPClient.deleteFile("atom/" + serviceId + "/datasetFeeds/" + dlsCode + ".xml");
                    dbHandler.deleteDeleteRequest(dlsCode);
                }
            } finally {
                myFTPClient.disconnect();
            }
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Přesune vygenerované kanály z dočasného uložiště do publikačního uložiště.
     * Přepíše původní soubory čímž provede aktualizaci feedu.
     * @throws NullPointerException
     * @deprecated Momentálně se kanály publikují na FTP server.
     */
    private void publishFeeds(String serviceId) throws NullPointerException, DownloadServiceNotFoundException {
        File tempDir = new File(this.config.getRepository().getTempRepository());
        String mainFeed = serviceId + ".xml";

        File datasetMetadataDir = new File(this.config.getRepository().getLocalRepository() + "\\" + serviceId
                + "\\datasetMetadata");
        if(!datasetMetadataDir.exists()){
            datasetMetadataDir.mkdirs();
        }

        for(File file : tempDir.listFiles()){
            if(file.isFile()){
                if(file.getName().equals(mainFeed)){
                    //Hlavni feed
                    File newDir = new File(this.config.getRepository().getLocalRepository()
                            + "\\" + serviceId);

                    if(!newDir.exists()){
                        newDir.mkdirs();
                    }
                    File newFile = new File(newDir.getAbsolutePath() + "\\" + mainFeed);
                    newFile.delete();
                    file.renameTo(newFile);

                }else if(getCustomFeedsFileNames().contains(file.getName().split("\\.")[0])){
                    //sdruzujici feedy
                    File newFile = new File(this.config.getRepository().getLocalRepository() + "\\" + file.getName());
                    newFile.delete();
                    file.renameTo(newFile);

                }else{
                    //datasetove feedy
                    File newDir = new File(this.config.getRepository().getLocalRepository()
                            + "\\" + serviceId + "\\datasetFeeds");

                    if(!newDir.exists()){
                        newDir.mkdirs();
                    }
                    File newFile = new File(newDir.getAbsolutePath() + "\\" + file.getName());
                    newFile.delete();
                    file.renameTo(newFile);
                }
            }
        }
        logger.info("Feedy vystaveny");
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Nakopíruje vygenerované kanály spolu s OSD dokumenty a novým indexem na FTP server.
     * @param serviceId
     * @throws IOException
     * @throws FTPException
     */
    private void publishFeedsToFTP(String serviceId) throws IOException, FTPException {

        System.out.println("Nahravam feedy na FTP server.");

        File tempDir = new File(this.config.getRepository().getTempRepository());
        String mainFeed = serviceId + ".xml";
        MyFTPClient ftpClient = new MyFTPClient();
        try {
            ftpClient.connect(this.config.getFtp().getHost(), this.config.getFtp().getUser(),
                    this.config.getFtp().getPassword());

            File hlavniFeed = null;
            String ftpDirPath = this.config.getRepository().getLocalRepository();
            if (!ftpClient.directoryExist(ftpDirPath + "/" + serviceId)) {
                ftpClient.makeDirectory(serviceId, ftpDirPath);
            }
            String datasetDirPath = this.config.getRepository().getLocalRepository() + "/" + serviceId + "/datasetFeeds";

            if (!ftpClient.directoryExist(datasetDirPath)) {
                ftpClient.makeDirectory("datasetFeeds", ftpDirPath + "/" + serviceId);
            }

            for (File file : tempDir.listFiles()) {
                if (file.isFile()) {
                    if (file.getName().equals(mainFeed)) {
                        //Hlavni feed
                        hlavniFeed = file;

                    } else if (getCustomFeedsFileNames().contains(file.getName().split("\\.")[0])) {
                        //sdruzujici feedy
                        continue;

                    } else {
                        //datasetove feedy
                        ftpClient.upload(file, datasetDirPath);
                    }
                }
            }

            //Hlavni feed nahravam az po nahrani vsech datasetovych feedu a jen pokud existuje
            //hlavni feed neexistuje v pripade ze nebylo co aktualizovat
            if(hlavniFeed != null) ftpClient.upload(hlavniFeed, ftpDirPath + "/" + serviceId);

            //Sdruzujici feedy nahravam az nakonec
            for (File file : tempDir.listFiles()) {
                if (file.isFile()) {
                    if (getCustomFeedsFileNames().contains(file.getName().split("\\.")[0])) {
                        //sdruzujici feedy
                        ftpClient.upload(file, ftpDirPath);
                    }
                }
            }

        }finally {
            ftpClient.disconnect();
        }
        logger.info("Feedy nahrany na FTP");
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí jména souborů všech sdružujících kanálů.
     * @return
     */
    private ArrayList<String> getCustomFeedsFileNames(){

        ArrayList<String> cfNames = new ArrayList<>();
        for(CustomFeed cf : this.config.getCustomFeed()){
            cfNames.add(cf.getFileName());
        }
        return cfNames;
    }
    //------------------------------------------------------------------------------------------------------------------
    //PRIVATE METHODS
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Zavolá bat soubor, který připojí všechny disky.
     * @throws IOException
     */
    private void mapDisks() throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec("cmd /C " + getJarLocation() + "/connect_disk_services.bat");
        p.waitFor();
        logger.info("Mapovani disku dokonceno");
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vymaže všechny soubory z adresáře do kterého se generují feedy.
     */
    private void deleteTempDir(){
        File tempDir = new File(this.config.getRepository().getTempRepository());
        for(File file : tempDir.listFiles()){
            file.delete();
        }
        logger.info("Docasny adresar promazan");
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Připojí se do databáze.
     * @throws SQLException
     * @throws TableNotFoundException
     * @throws DriverException
     * @throws InvalidConfigFileException
     * @throws ColumnNotFoundException
     */
    private void connectToDatabase() throws SQLException, TableNotFoundException, DriverException,
            InvalidConfigFileException, ColumnNotFoundException {

        this.conn = DriverManager.getConnection(this.connectionString);
        this.conn.setAutoCommit(false);
        this.dbHandler = new DatabaseHandler(this.conn);
        logger.info("Pripojení do databaze uspesne");
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Odpojí se od databáze.
     * @throws SQLException
     */
    private void disconnectFromDatabase() throws SQLException{
        this.conn.close();
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Pokud se v tabulce atom_stav_publikace nacházejí nějaké řádky které mají ve sloupci datum_publikace_atom
     * hodnotu null tak vloží kódy datasetů kterých se to týká, do proměnné changedService jinak neudělá nic;
     * @return
     * @throws SQLException
     */
    private void checkForChanges() throws SQLException, UnknownDatasetException, TableNotFoundException,
            DriverException, InvalidConfigFileException, ColumnNotFoundException {

        for(ChangedService service : this.dbHandler.getChangedServices()){
            this.changedService.add(classify(service.getServiceCode(), service.getDateOfChange()));
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Na základě dataset kódu rozhodne kterou ze tříd použít.
     * Inicializuje její objekt a vrátí ho.
     * @param datasetCode
     * @return
     * @throws UnknownDatasetException
     */
    private Service classify(String themeCode, String dateOfChange) throws UnknownDatasetException{

        if(themeCode.equals("CP") || themeCode.equals("AD") || themeCode.equals("AU") || themeCode.equals("BU")){
            return new InspireService(getSources(themeCode), themeCode, dateOfChange);

        }else if(themeCode.contains("VFK")){
            return new KmVfkService(getSources(themeCode), themeCode, dateOfChange);

        }else if(themeCode.contains("VKM")){
            return new KmVkmService(getSources(themeCode), themeCode, dateOfChange);

        }else if(themeCode.contains("RUIAN")){
            return new RuianService(getSources(themeCode), themeCode, dateOfChange);

        }else if(themeCode.contains("SHP")){
            return new KmShpService(getSources(themeCode), themeCode, dateOfChange);

        }else if(themeCode.contains("DGN")){
            return new KmShpService(getSources(themeCode), themeCode, dateOfChange);

        } else{
            throw new UnknownDatasetException("Nalezeny dataset se nepodarilo klasifikovat.");
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Z konfiguračního souboru zjistí cestu k adresáři podle kódu datasetu.
     * @param datasetCode
     * @return
     * @throws UnknownDatasetException Když dataset s kódem datasetCode nenalezne v konfiguračním souboru.
     */
    private ArrayList<Source> getSources(String themeCode) throws UnknownDatasetException{

        ArrayList<Source> sources = new ArrayList<Source>();
        DownloadService[] downloadServices = this.config.getDownloadService();
        boolean found = false;

        for(int i = 0; i < downloadServices.length; i++){
            if(downloadServices[i].getCode().equals(themeCode)){
                sources = downloadServices[i].getSources();
                found = true;
                break;
            }
        }
        if(!found){
            throw new UnknownDatasetException(String.format("Neznamy kod tematu - %s", themeCode));
        }
        return sources;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Aktualizuje tabulku atom_files.
     * @param service
     * @throws InvalidConfigFileException
     * @throws ColumnNotFoundException
     * @throws SQLException
     * @throws TableNotFoundException
     * @throws DriverException
     * @throws UnknownDatasetException
     */
    private void updateFilesTable(Service service) throws InvalidConfigFileException, ColumnNotFoundException, SQLException,
            TableNotFoundException, DriverException, UnknownDatasetException {

        boolean generate = solveChanges(service);

        if(!generate) {
            this.dbHandler.commitChangesToDatabase(service.getServiceId(), service.getDateOfChange());
            this.conn.commit();
        }else {
            deleteOldData(service.getServiceId());
            setNewData(service.getServiceId());
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Aktualizuje tabulku atom_datasets. Vkládá nové datasety a maže nepotřebné.
     * @param service
     * @throws SQLException
     */
    private void updateDatasetTable(Service service) throws SQLException {
        this.insertNewDataset(service);
        this.deleteEmptyDataset();
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Do tabulky datasetů doplní nové datasety.
     * @throws SQLException
     */
    private void insertNewDataset(Service service) throws SQLException {

        ArrayList<String> datasets = this.getNewDataset();

        if(!datasets.isEmpty()){
            for(String dataset : datasets){
                dbHandler.insertNewDataset(dataset, service.getServiceId());
            }
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Z tabulky datasetů smaže již neexistující datasety.
     * @throws SQLException
     */
    private void deleteEmptyDataset() throws SQLException {

        ArrayList<String> datasets = this.getEmptyDataset();

        if(!datasets.isEmpty()){
            for(String dataset : datasets){
                dbHandler.deleteDataset(dataset);
            }
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrací kódy nově vytvořených datasetů.
     * @return
     * @throws SQLException
     */
    private ArrayList<String> getNewDataset() throws SQLException {

        return dbHandler.getNewDatasets();
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrací kódy prázdných datasetů. Prázdné datasety jsou mazány.
     * @return
     * @throws SQLException
     */
    private ArrayList<String> getEmptyDataset() throws SQLException {

        return dbHandler.getEmptyDataset();
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Porovná poslední známý stav adresáře se současným stavem. Poslední známý stav odpovída tomu co je ve
     * feedu a databázi.
     * Na základě porovnání zapíše záznamy do tabulky pro metadata a do tabulky dataset_updated zapíše kódy
     * datasetových feedů, které mají být aktualizovány.
     * Vrátí true pokud nastaly změny. Pokud se nic nezměnil vrátí false.
     * @param dataset
     * @return
     * @throws TableNotFoundException
     * @throws SQLException
     * @throws DriverException
     * @throws InvalidConfigFileException
     * @throws ColumnNotFoundException
     */
    private boolean solveChanges(Service service) throws TableNotFoundException, SQLException,
            DriverException, InvalidConfigFileException, ColumnNotFoundException, UnknownDatasetException {


        ArrayList<DatasetFile> filesFromDir = service.getCurrentState();
        this.dbHandler.insertFilesData(filesFromDir);
        ArrayList<DatasetFile> missing = getMissing(service.getServiceId());
        ArrayList<DatasetFile> neew = getNew(service.getServiceId());
        ArrayList<DatasetFile> modified = getModified(service.getServiceId());

        if(missing.isEmpty() && neew.isEmpty() && modified.isEmpty()){
            //neni treba nic delat
            this.dbHandler.deleteTempData();
            return false;
        }

        // zapsat do databaze - tabulka metadat
        //dbHandler.insertMetadataRequests("DELETE", missing);
        dbHandler.insertDeleteRequest(missing);

        //dbHandler.insertMetadataRequests("CREATE", neew);
        dbHandler.insertUpdateRequest(neew);

        //dbHandler.insertMetadataRequests("UPDATE", modified);
        dbHandler.insertUpdateRequest(modified);

        return true;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Pro daný dataset vloží do databáze hodnotu updated. Hodnota je určena jako maximum z datumů poslední
     * aktualizace souborů.
     * @param datasetCode
     */
    private void setDatasetUpdated(String datasetCode) throws TableNotFoundException, DriverException,
            InvalidConfigFileException, ColumnNotFoundException, SQLException {

        this.dbHandler.setDatasetUpdatedToCTime(datasetCode);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí objekty datasetů, které mají být smazány.
     * @param theme_id
     * @return
     * @throws SQLException
     * @throws TableNotFoundException
     * @throws DriverException
     * @throws InvalidConfigFileException
     * @throws ColumnNotFoundException
     */
    private ArrayList<DatasetFile> getMissing(String theme_id) throws SQLException,
            TableNotFoundException, DriverException, InvalidConfigFileException, ColumnNotFoundException {

        return this.dbHandler.getMissingFiles(theme_id);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí objekty datasetů, které mají být vytvořeny.
     * @param theme_id
     * @return
     * @throws TableNotFoundException
     * @throws DriverException
     * @throws InvalidConfigFileException
     * @throws ColumnNotFoundException
     * @throws SQLException
     */
    private ArrayList<DatasetFile> getNew(String theme_id) throws TableNotFoundException, DriverException,
            InvalidConfigFileException, ColumnNotFoundException, SQLException {

        return this.dbHandler.getNewFiles(theme_id);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí objekty datasetů, které se změnily.
     * @param theme_id
     * @return
     * @throws SQLException
     * @throws TableNotFoundException
     * @throws DriverException
     * @throws InvalidConfigFileException
     * @throws ColumnNotFoundException
     */
    private ArrayList<DatasetFile> getModified(String theme_id) throws SQLException,
            TableNotFoundException, DriverException, InvalidConfigFileException, ColumnNotFoundException {

        return this.dbHandler.getModifiedFiles(theme_id);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Smaže z databáze dosavadní informace o souborech.
     * @param theme_id
     * @throws TableNotFoundException
     * @throws DriverException
     * @throws InvalidConfigFileException
     * @throws ColumnNotFoundException
     * @throws SQLException
     */
    private void deleteOldData(String theme_id) throws TableNotFoundException, DriverException,
            InvalidConfigFileException, ColumnNotFoundException, SQLException {

        this.dbHandler.deleteOldFilesData(theme_id);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do databáze nová data o souborech.
     * @param theme_id
     * @throws TableNotFoundException
     * @throws DriverException
     * @throws InvalidConfigFileException
     * @throws ColumnNotFoundException
     * @throws SQLException
     */
    private void setNewData(String theme_id) throws TableNotFoundException, DriverException,
            InvalidConfigFileException, ColumnNotFoundException, SQLException {

        this.dbHandler.setNewData(theme_id);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Prečte z databáze aktuálni abstrakt a vloží ho do tabulky atom_services do sloupce subtitle.
     * Dělá update ne insert
     * @param serviceID
     * @throws SQLException
     */
    private void updateServiceAbstrakt(String serviceID) throws SQLException {
        String abstrakt = this.dbHandler.getAbstractForService(serviceID);
        this.dbHandler.updateServiceAbstrakt(abstrakt, serviceID);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí absolutní cestu k umístění, kde se nachází jar soubor.
     * @return
     */
    private String getJarLocation(){

        String jarLocation = "";
        String[] peaces = location.getPath().split("/");

        for(int i = 1; i < peaces.length - 1; i++){
            jarLocation += "/" + peaces[i];
        }

        return jarLocation.substring(1);
    }
    //------------------------------------------------------------------------------------------------------------------
    public Connection getConn() {
        return conn;
    }

}
