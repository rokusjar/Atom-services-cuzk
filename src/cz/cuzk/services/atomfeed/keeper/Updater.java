package cz.cuzk.services.atomfeed.keeper;

import cz.cuzk.services.atomfeed.config.*;
import cz.cuzk.services.atomfeed.database.DatabaseHandler;
import cz.cuzk.services.atomfeed.database.DriverException;
import cz.cuzk.services.atomfeed.feed.common.AtomFeedException;
import cz.cuzk.services.atomfeed.feed.common.DatasetFile;
import cz.cuzk.services.atomfeed.feed.datasetfeed.DatasetFeed;
import cz.cuzk.services.atomfeed.feed.downloadservicefeed.DownloadServiceFeed;
import cz.cuzk.services.atomfeed.feed.dsfeed.ServiceGroupFeed;

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

    public void initialize() throws IOException, InterruptedException, TableNotFoundException, SQLException,
            DriverException, InvalidConfigFileException, ColumnNotFoundException {

        this.mapDisks();
        this.deleteTempDir();
        this.connectToDatabase();
    }
    //------------------------------------------------------------------------------------------------------------------
    public void update() throws DriverException, InvalidConfigFileException, ColumnNotFoundException, SQLException,
            TableNotFoundException, UnknownDatasetException, AtomFeedException, DownloadServiceNotFoundException {

        this.checkForChanges();

        if(!this.changedService.isEmpty()) {

            for (Service service : this.changedService) {
                logger.info("Obdržen pokyn k aktualizaci - stahovací služby: " + service.getServiceId());
                this.dbHandler.updateServiceFeedId(service.getServiceId());
                this.dbHandler.updateServiceOpenSearchlink(service.getServiceId());
                this.dbHandler.updateServiceMetadataLink(service.getServiceId());

                this.updateFilesTable(service);
                this.updateDatasetTable(service);
                logger.info("Aktualizace databáze dokončena");

                this.updateFeeds(service.getServiceId());
                this.updateCustomFeeds();

                publishFeeds(service.getServiceId());

                this.dbHandler.commitChangesToDatabase(service.getServiceId(), service.getDateOfChange());
                logger.info("Aktualizace stahovací služby: " + service.getServiceId() + " dokončena a potvrzena");
                this.conn.commit();
            }
        }else{
            logger.log(Level.INFO, "Není co aktualizovat");
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    private void updateCustomFeeds() throws AtomFeedException, InvalidConfigFileException, ColumnNotFoundException,
            SQLException, TableNotFoundException, DriverException {

        for(CustomFeed cf : this.config.getCustomFeed()){
            logger.info("Aktualizuji skupinový feed: " + cf.getTitle());
            ServiceGroupFeed sgfeed = new ServiceGroupFeed(cf.getFileName(), cf.getTitle(),
                                                           cf.getServiceCodes(), this.dbHandler, this.logger);
            sgfeed.construct();
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public void updateFeeds(String serviceId) throws SQLException, TableNotFoundException, AtomFeedException, DriverException,
            InvalidConfigFileException, ColumnNotFoundException, DownloadServiceNotFoundException {

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
            logger.info("Hlavní feed sestaven");

        } else {
            logger.info("Žádné dataset feedy k aktualizaci.");
        }

    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Přesune vygenerované feedy z dočasného uložiště do publikačního uložiště.
     * Přepíše původní soubory čímž provede aktualizaci feedu.
     * @throws NullPointerException
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
                    File newDir = new File(this.config.getRepository().getLocalRepository()
                            + "\\" + serviceId);

                    if(!newDir.exists()){
                        newDir.mkdirs();
                    }
                    File newFile = new File(newDir.getAbsolutePath() + "\\" + mainFeed);
                    newFile.delete();
                    file.renameTo(newFile);

                }else if(getCustomFeedsFileNames().contains(file.getName().split("\\.")[0])){

                    File newFile = new File(this.config.getRepository().getLocalRepository() + "\\" + file.getName());
                    newFile.delete();
                    file.renameTo(newFile);

                }else{
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

    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí jména souborů všech skupinových feedů (feedů které sdružují stahovací služby)
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
        logger.info("Mapování disků dokončeno");
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
        logger.info("Dočasný adresář promazán");
    }
    //------------------------------------------------------------------------------------------------------------------
    private void connectToDatabase() throws SQLException, TableNotFoundException, DriverException,
            InvalidConfigFileException, ColumnNotFoundException {

        this.conn = DriverManager.getConnection(this.connectionString);
        this.conn.setAutoCommit(false);
        this.dbHandler = new DatabaseHandler(this.conn);
        logger.info("Připojení do databáze úspěšné");
    }
    //------------------------------------------------------------------------------------------------------------------
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

        for(ChangedService service : this.dbHandler.getChanges()){
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
        }else{
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
            throw new UnknownDatasetException(String.format("Neznámy kód tématu - %s", themeCode));
        }
        return sources;
    }
    //------------------------------------------------------------------------------------------------------------------
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
    private ArrayList<String> getNewDataset() throws SQLException {

        return dbHandler.getNewDatasets();
    }
    //------------------------------------------------------------------------------------------------------------------
    private ArrayList<String> getEmptyDataset() throws SQLException {

        return dbHandler.getEmptyDataset();
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Porovná poslední známý stav adresáře se současným stavem. Poslední známý stav odpovída tomu co je ve feedu.
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
        dbHandler.insertMetadataRequests("DELETE", missing);
        //dbHandler.insertUpdateRequest(missing);

        dbHandler.insertMetadataRequests("CREATE", neew);
        dbHandler.insertUpdateRequest(neew);

        dbHandler.insertMetadataRequests("UPDATE", modified);
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
    private ArrayList<DatasetFile> getMissing(String theme_id) throws SQLException,
            TableNotFoundException, DriverException, InvalidConfigFileException, ColumnNotFoundException {

        return this.dbHandler.getMissingFiles(theme_id);
    }
    //------------------------------------------------------------------------------------------------------------------
    private ArrayList<DatasetFile> getNew(String theme_id) throws TableNotFoundException, DriverException,
            InvalidConfigFileException, ColumnNotFoundException, SQLException {

        return this.dbHandler.getNewFiles(theme_id);
    }
    //------------------------------------------------------------------------------------------------------------------
    private ArrayList<DatasetFile> getModified(String theme_id) throws SQLException,
            TableNotFoundException, DriverException, InvalidConfigFileException, ColumnNotFoundException {

        return this.dbHandler.getModifiedFiles(theme_id);
    }
    //------------------------------------------------------------------------------------------------------------------
    private void deleteOldData(String theme_id) throws TableNotFoundException, DriverException,
            InvalidConfigFileException, ColumnNotFoundException, SQLException {

        this.dbHandler.deleteOldFilesData(theme_id);
    }
    //------------------------------------------------------------------------------------------------------------------
    private void setNewData(String theme_id) throws TableNotFoundException, DriverException,
            InvalidConfigFileException, ColumnNotFoundException, SQLException {

        this.dbHandler.setNewData(theme_id);
    }
    //------------------------------------------------------------------------------------------------------------------
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
