package cz.cuzk.services.atomfeed.index;

import cz.cuzk.services.atomfeed.config.*;
import cz.cuzk.services.atomfeed.database.DriverException;
import cz.cuzk.services.atomfeed.util.ftp.FTPException;
import cz.cuzk.services.atomfeed.util.ftp.MyFTPClient;
import freemarker.core.ParseException;
import freemarker.template.*;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jaromir Rokusek
 * Generuje index.html pro stranku atom.cuzk.cz.
 *
 * Singleton
 */
public class AtomIndex {

    private static AtomIndex instance;
    private Connection conn;
    private Config config;
    private ArrayList<Service> services = new ArrayList<>();
    private static URL location = AtomIndex.class.getProtectionDomain().getCodeSource().getLocation();
    //------------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------------
    private AtomIndex() throws InvalidConfigFileException, DriverException {

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
    public static AtomIndex getInstance() throws InvalidConfigFileException, DriverException {

        if(instance == null){
            instance = new AtomIndex();
        }
        return instance;
    }
    //------------------------------------------------------------------------------------------------------------------
    public void createIndex() throws IOException, TableNotFoundException, SQLException, DriverException,
            InvalidConfigFileException, ColumnNotFoundException, TemplateException {
        //Konfigurace

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        //cfg.setDirectoryForTemplateLoading(new File("index"));
        cfg.setDirectoryForTemplateLoading(new File(getJarLocation() + "/index"));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        loadModel();

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("services", this.services);

        //Ziskat sablonu
        Template template = cfg.getTemplate("indexFM.html");

        //Spojeni modelu a sablony - vytvoreni souboru
        try(
                FileOutputStream fos = new FileOutputStream(
                        new File(this.config.getRepository().getTempRepository() + "\\index.html"));
                OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8")
        ){
            template.process(dataModel, osw);
        }

    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Nahraje vytvoreny index na FTP server nastaveny v konfiguracnim souboru.
     * @throws IOException
     * @throws FTPException
     */
    public void uploadToFTP() throws IOException, FTPException {
        MyFTPClient ftpClient = new MyFTPClient();

        ftpClient.connect(this.config.getFtp().getHost(), this.config.getFtp().getUser(),
                this.config.getFtp().getPassword());

        File index = new File(this.config.getRepository().getTempRepository() + "/index.html");

        if(index.exists()) {
            ftpClient.upload(new File(this.config.getRepository().getTempRepository() + "/index.html"), "atom");
        }else{
            System.out.println(index.getAbsolutePath() + " nenalezen");
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    private static String getJarLocation(){

        String jarLocation = "";
        String[] peaces = location.getPath().split("/");

        for(int i = 1; i < peaces.length - 1; i++){
            jarLocation += "/" + peaces[i];
        }

        return jarLocation.substring(1) + "\\";
    }
    //------------------------------------------------------------------------------------------------------------------
    private void loadModel() throws TableNotFoundException, SQLException, DriverException, InvalidConfigFileException,
            ColumnNotFoundException {

        connectToDatabase();
        loadServiceData();
        disconnectFromDatabase();
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Nacte z databaze data o stahovacich sluzbach.
     * Vytvori objekty tridy Service a naplni jimi ArrayList services
     * @throws SQLException
     */
    private void loadServiceData() throws SQLException, ColumnNotFoundException, TableNotFoundException {

        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s, %s FROM %s";
        String serviceId, feedURL;

        if(!this.services.isEmpty()) this.services.clear();

        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(String.format(query,
                    this.config.getServiceTable().getColumns().get("service_id"),
                    this.config.getServiceTable().getColumns().get("id"),
                    this.config.getServiceTable().getTableName()));

            while(rs.next()){

                serviceId = rs.getString(1);
                feedURL = rs.getString(2);

                if(feedURL == null || feedURL.equals("")) feedURL = "http//google.com";

                Service service = new Service();
                service.setServiceID(serviceId);
                service.setFeedURL(feedURL);
                service.setLastUpdate(loadLastUpdate(serviceId));
                service.setAbstrakt(loadAbstract(serviceId));
                service.setCategory(decideCategory(serviceId));

                this.services.add(service);
            }

        }finally {
            try { if(rs != null) rs.close();} catch (SQLException e){}
            try { if(stmt != null) stmt.close();} catch (SQLException e){}
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Nacte z databaze abstrakt k dane stahovaci sluzbe.
     * @param serviceId
     * @throws SQLException
     */
    private String loadAbstract(String serviceId) throws SQLException, ColumnNotFoundException, TableNotFoundException {

        String abstrakt = "abstrakt chybí";
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(String.format(query,
                    this.config.getServiceTable().getColumns().get("subtitle"),
                    this.config.getServiceTable().getTableName(),
                    this.config.getServiceTable().getColumns().get("service_id"),
                    serviceId));

            if(rs.next()) abstrakt = rs.getString(1);

        }finally {
            try { if(rs != null) rs.close();} catch (SQLException e){}
            try { if(stmt != null) stmt.close();} catch (SQLException e){}
        }

        return abstrakt;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Nacte z databaze datum posledni aktualizace stahovaci sluzby a vrati ho v pozadovanem formatu.
     * @param serviceId
     * @return
     * @throws SQLException
     */
    private String loadLastUpdate(String serviceId) throws SQLException, ColumnNotFoundException, TableNotFoundException {
        String lastUpdate = "Služba není dostupná";

        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT to_char(%s, 'DD.MM.RRRR')||' '||to_char(%s, 'HH24:MI:SS') FROM %s WHERE %s = '%s'";

        query = String.format(query,
                this.config.getServiceTable().getColumns().get("updated"),
                this.config.getServiceTable().getColumns().get("updated"),
                this.config.getServiceTable().getTableName(),
                this.config.getServiceTable().getColumns().get("service_id"),
                serviceId);
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            if(rs.next()) {
                String datum = rs.getString(1);
                if(!datum.equals("") && datum != null) lastUpdate = rs.getString(1);
            }

        }finally {
            try { if(rs != null) rs.close();} catch (SQLException e){}
            try { if(stmt != null) stmt.close();} catch (SQLException e){}
        }

        return lastUpdate;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Na zaklade serviceId zaradi sluzbu do prislusne kategorie v ramci index.html
     * @param serviceId
     * @return
     */
    private String decideCategory(String serviceId){
        String category;

        if(serviceId.contains("KM")) category = "KM";
        else if(serviceId.equals("AU") || serviceId.equals("CP") || serviceId.equals("BU") || serviceId.equals("AD")) category = "INSPIRE";
        else if(serviceId.contains("RUIAN")) category = "RUIAN";
        else if(serviceId.contains("GMPL")) category = "GMPL";
        else category = "OSTATNI";

        return category;
    }
    //------------------------------------------------------------------------------------------------------------------
    private void connectToDatabase() throws SQLException, TableNotFoundException, DriverException,
            InvalidConfigFileException, ColumnNotFoundException {

        this.conn = DriverManager.getConnection(this.config.getDbConn().getConnectionString());
        this.conn.setAutoCommit(false);
    }
    //------------------------------------------------------------------------------------------------------------------
    private void disconnectFromDatabase() throws SQLException{
        try { if(this.conn != null) this.conn.close();} catch (SQLException e){}
    }
    //------------------------------------------------------------------------------------------------------------------
    public static class Service{
        private String lastUpdate;
        private String abstrakt;
        private String feedURL;
        private String serviceID;
        private String category;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getServiceID() {
            return serviceID;
        }

        public void setServiceID(String name) {
            this.serviceID = name;
        }

        public String getFeedURL() {
            return feedURL;
        }

        public void setFeedURL(String feedURL) {
            this.feedURL = feedURL;
        }

        public String getAbstrakt() {
            return abstrakt;
        }

        public void setAbstrakt(String abstrakt) {
            this.abstrakt = abstrakt;
        }

        public String getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(String lastUpdate) {
            this.lastUpdate = lastUpdate;
        }
    }

}
