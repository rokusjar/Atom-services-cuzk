package cz.cuzk.services.atomfeed.opensearch;

import cz.cuzk.services.atomfeed.config.*;
import cz.cuzk.services.atomfeed.database.DriverException;
import cz.cuzk.services.atomfeed.util.ftp.FTPException;
import cz.cuzk.services.atomfeed.util.ftp.MyFTPClient;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Třída zajišťuje generování opensearch description dokumentů a je vytvořena jako Singleton.
 */
public class OpenSearchDescription {

    private static OpenSearchDescription instance;
    private Connection conn;
    private Config config;
    private ArrayList<Dataset> datasets = new ArrayList<>();
    private static URL location = OpenSearchDescription.class.getProtectionDomain().getCodeSource().getLocation();
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Načte data z konfiguračního souboru a připojí JDBC driver potřebný pro připojení do databáze.
     * @throws InvalidConfigFileException
     * @throws DriverException
     */
    private OpenSearchDescription() throws InvalidConfigFileException, DriverException {
        ConfigReader cr = new ConfigReader();
        cr.read();
        this.config = cr.getConfigData();
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }catch (ClassNotFoundException e){
            throw new DriverException("Oracle JDBC driver nenalezen.");
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrací instanci třídy. (Třída je Singleton)
     * @return
     * @throws InvalidConfigFileException
     * @throws DriverException
     */
    public static OpenSearchDescription getInstance() throws InvalidConfigFileException, DriverException {
        if(instance == null){
            instance = new OpenSearchDescription();
        }
        return instance;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Naplní šablonu daty a vygeneruje soubor do dočasného adresáře definovaného v konfiguračním souboru.
     * @throws IOException
     * @throws TemplateException
     * @throws TableNotFoundException
     * @throws SQLException
     * @throws DriverException
     * @throws InvalidConfigFileException
     * @throws ColumnNotFoundException
     */
    public void createOSD(String service_id) throws IOException, TemplateException, TableNotFoundException, SQLException,
            DriverException, InvalidConfigFileException, ColumnNotFoundException, FTPException {
        //Konfigurace

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        //cfg.setDirectoryForTemplateLoading(new File("templates"));
        cfg.setDirectoryForTemplateLoading(new File(getJarLocation() + "/templates"));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        loadModel(service_id);

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("datasets", this.datasets);

        //Ziskat sablonu
        Template template = cfg.getTemplate("OSD-" + service_id + ".xml");

        //Spojeni modelu a sablony - vytvoreni souboru
        try(
            FileOutputStream fos = new FileOutputStream(
                    new File(this.config.getRepository().getTempRepository() + "\\OSD-"+ service_id + ".xml"));
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8")
        ){
            template.process(dataModel, osw);
        }

    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * připojí se do databáze a zavolá funci loadDatasets.
     * @throws TableNotFoundException
     * @throws SQLException
     * @throws DriverException
     * @throws InvalidConfigFileException
     * @throws ColumnNotFoundException
     */
    public void loadModel(String service_id) throws TableNotFoundException, SQLException, DriverException,
            InvalidConfigFileException, ColumnNotFoundException {
        connectToDatabase();
        loadDatasets(service_id);
        disconnectFromDatabase();
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Načte z databáze data o datasetech dané služby a naplní ArrayList datasets.
     * @throws SQLException
     * @throws ColumnNotFoundException
     * @throws TableNotFoundException
     */
    public void loadDatasets(String service_id) throws SQLException, ColumnNotFoundException, TableNotFoundException {
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s, %s, %s FROM %s WHERE %s = '%s'";
        String title, namespace, code;

        if(!this.datasets.isEmpty()) this.datasets.clear();

        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(String.format(query,
                    this.config.getDatasetTable().getColumns().get("title"),
                    this.config.getDatasetTable().getColumns().get("spatial_dataset_identifier_namespace"),
                    this.config.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"),
                    this.config.getDatasetTable().getTableName(),
                    this.config.getDatasetTable().getColumns().get("service_id"),
                    service_id));

            while(rs.next()){

                title = rs.getString(1);
                namespace = rs.getString(2);
                code = rs.getString(3);

                Dataset dataset = new Dataset();
                dataset.setTitle(title);
                dataset.setNamespace(namespace);
                dataset.setCode(code);
                this.datasets.add(dataset);
            }
        }finally {
            try { if(rs != null) rs.close();} catch (SQLException e){}
            try { if(stmt != null) stmt.close();} catch (SQLException e){}
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Varátí cestu k jar souboru.
     * @return
     */
    private static String getJarLocation(){

        String jarLocation = "";
        String[] peaces = location.getPath().split("/");

        for(int i = 1; i < peaces.length - 1; i++){
            jarLocation += "/" + peaces[i];
        }

        return jarLocation.substring(1) + "\\";
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

        this.conn = DriverManager.getConnection(this.config.getDbConn().getConnectionString());
        this.conn.setAutoCommit(false);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Odpojí se od databáze.
     * @throws SQLException
     */
    private void disconnectFromDatabase() throws SQLException{
        try { if(this.conn != null) this.conn.close();} catch (SQLException e){}
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Nahraje vytvořený OSD na FTP server nastavený v konfiguračním souboru.
     * @throws IOException
     * @throws FTPException
     */
    public void uploadToFTP(String service) throws IOException, FTPException {
        MyFTPClient ftpClient = new MyFTPClient();

        ftpClient.connect(this.config.getFtp().getHost(), this.config.getFtp().getUser(),
                this.config.getFtp().getPassword());

        File osd = new File(this.config.getRepository().getTempRepository() + "/OSD-" + service + ".xml");

        if(osd.exists()) {
            ftpClient.upload(osd, this.config.getRepository().getRepository() + "/" + service);
        }else{
            System.out.println(osd.getAbsolutePath() + " nenalezen");
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Pomocná vnitřní třída, která představuje dataset. Udržuje si namespace a kód datasetu, které dohromady tvoří
     * identifikátor, a název datasetu.
     */
    public static class Dataset{

        private String namespace;
        private String code;
        private String title;

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
