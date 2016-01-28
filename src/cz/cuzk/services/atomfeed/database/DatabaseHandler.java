package cz.cuzk.services.atomfeed.database;

import cz.cuzk.services.atomfeed.feed.common.DatasetFile;
import cz.cuzk.services.atomfeed.config.*;
import cz.cuzk.services.atomfeed.keeper.ChangedService;
import javafx.scene.control.Tab;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

//----------------------------------------------------------------------------------------------------------------------
/**
 * Třída DatabaseHandler je nástrojem pro čtení dat z databáze.
 * Některé metody se sami připojují a odpojují od databáze a některé vyžadují aby připojení již bylo definováno.
 * Z toho důvodu tady existují dva konstruktory.
 * Třída keeper.Keeper je jedinou třídou, která do databáze něco zapisuje a jako jediná používá konstruktor
 * s parametrem Connection conn, protože musí řešit commit.
 * Ostatní třídy používají konstruktor bez parametru, protože databázi jenom čtou a nemusí řešit žádný commit.
 */
public class DatabaseHandler {
    //------------------------------------------------------------------------------------------------------------------
    //DATA MEMBERS
    //------------------------------------------------------------------------------------------------------------------
    private Connection conn;
    private String connectionString;
    private Config config = null;
    private Table serviceTable = null;
    private Table datasetTable = null;
    private Table filesTable = null;
    private Table metadataTable = null;
    private Table publikaceTable = null;
    private Table abstraktTable = null;
    private Table datasetUpdateTable = null;
    private Table georssStat = null;
    private Table georssobce = null;
    private Table georsskp = null;
    private Table georssku = null;

    //------------------------------------------------------------------------------------------------------------------
    //PUBLIC METHODS
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Registruje Oracle JDBC driver.
     * Z konfiguračního souboru config.json si přečte definici připojení do databáze (jaká db a jak se připojit).
     * Z konfiguračního souboru config.json si přečte a uloží názvy tabulek a názvy jejich sloupců.
     * @param conn připojení které má být použito
     * @throws InvalidConfigFileException
     * @throws DriverException
     * @throws TableNotFoundException
     * @throws ColumnNotFoundException
     */
    public DatabaseHandler(Connection conn) throws InvalidConfigFileException, DriverException,
            TableNotFoundException, ColumnNotFoundException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            this.conn = conn;
            ConfigReader configReader = new ConfigReader();
            configReader.read();
            this.setConfig(configReader.getConfigData());
            this.setConnectionString(this.config.getDbConn().getConnectionString());
            this.setServiceTable(this.getConfig().getServiceTable());
            this.setDatasetTable(this.getConfig().getDatasetTable());
            this.setFilesTable(this.getConfig().getFilesTable());
            this.setMetadataTable(this.getConfig().getMetadataTable());
            this.setPublikaceTable(this.getConfig().getPublikaceTable());
            this.setAbstraktTable(this.getConfig().getAbstraktTable());
            this.setDatasetUpdateTable(this.getConfig().getDatasetUpdateTable());
            this.setGeorssStat(this.getConfig().getGeorssStat());
            this.setGeorssobce(this.getConfig().getGeorssObce());
            this.setGeorssku(this.getConfig().getGeorssKu());
            this.setGeorsskp(this.getConfig().getGeorssKp());
        }catch (ClassNotFoundException e){
            throw new DriverException("Oracle JDBC driver nenalezen.");
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getMainFeedTitle(String serviceId) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query,
                this.serviceTable.getColumns().get("title"),
                this.getServiceTable().getTableName(),
                this.getServiceTable().getColumns().get("service_id"),
                serviceId);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getMainFeedid(String serviceId) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query,
                this.serviceTable.getColumns().get("id"),
                this.getServiceTable().getTableName(),
                this.getServiceTable().getColumns().get("service_id"),
                serviceId);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getMainFeedRights(String serviceId) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query,
                this.serviceTable.getColumns().get("rights"),
                this.getServiceTable().getTableName(),
                this.getServiceTable().getColumns().get("service_id"),
                serviceId);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getMainFeedAuthorEmail(String serviceId) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query,
                this.serviceTable.getColumns().get("author_email"),
                this.getServiceTable().getTableName(),
                this.getServiceTable().getColumns().get("service_id"),
                serviceId);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getMainFeedAuthorName(String serviceId) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query, this.serviceTable.getColumns().get("author_name"),
                this.getServiceTable().getTableName(),
                this.getServiceTable().getColumns().get("service_id"),
                serviceId);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getMainFeedAuthorUri(String serviceId) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query, this.serviceTable.getColumns().get("author_uri"),
                this.getServiceTable().getTableName(),
                this.getServiceTable().getColumns().get("service_id"),
                serviceId);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getMainFeedLastUpdate(String serviceId) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT to_char(max(%s), 'RRRR-MM-DD')||'T'||to_char(max(%s), 'HH24:MI:SS')||'+1:00' FROM %s ";
        query += "WHERE %s = '%s'";
        query = String.format(query,
                this.datasetTable.getColumns().get("updated"),
                this.datasetTable.getColumns().get("updated"),
                this.getDatasetTable().getTableName(),
                this.getServiceTable().getColumns().get("service_id"),
                serviceId);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getMainFeedSubtitle(String serviceId) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query, this.serviceTable.getColumns().get("subtitle"),
                this.getServiceTable().getTableName(),
                this.getServiceTable().getColumns().get("service_id"),
                serviceId);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getMainFeedMetadataLink(String serviceId) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query, this.serviceTable.getColumns().get("metadata_link"),
                this.getServiceTable().getTableName(),
                this.getServiceTable().getColumns().get("service_id"),
                serviceId);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getMainFeedOpenSearchLink(String serviceId) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query, this.serviceTable.getColumns().get("opensearch_link"),
                this.getServiceTable().getTableName(),
                this.getServiceTable().getColumns().get("service_id"),
                serviceId);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getMainFeedUpdated(String serviceId) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;

        String query = "SELECT to_char(%s, 'RRRR-MM-DD')||'T'||to_char(%s, 'HH24:MI:SS')||'+1:00' FROM %s WHERE %s = '%s'";

        query = String.format(query,
                this.getDatasetTable().getColumns().get("updated"),
                this.getDatasetTable().getColumns().get("updated"),
                this.getServiceTable().getTableName(),
                this.getDatasetTable().getColumns().get("service_id"),
                serviceId);

        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Danému datasetu vyplní v databázi hodnotu updated. Nastavuje aktuální čas.
     * @param datasetCode
     * @throws SQLException
     */
    public void setMainFeedUpdatedToCTime(String serviceId) throws SQLException{
        Statement stmt = null;
        String update = "UPDATE %s SET %s = sysdate WHERE %s = '%s'";

        try{
            stmt = this.conn.createStatement();
            stmt.executeUpdate(
                    String.format(update,
                            this.getServiceTable().getTableName(),
                            this.getDatasetTable().getColumns().get("updated"),
                            this.getDatasetTable().getColumns().get("service_id"),
                            serviceId
                    ));
        }finally {
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getMainFeedContributor(String serviceId) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query, this.serviceTable.getColumns().get("contributor"),
                this.getServiceTable().getTableName(),
                this.getServiceTable().getColumns().get("service_id"),
                serviceId);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getMainFeedGenerator(String serviceId) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query, this.serviceTable.getColumns().get("generator"),
                this.getServiceTable().getTableName(),
                this.getServiceTable().getColumns().get("service_id"),
                serviceId);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getMainFeedIcon(String serviceId) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query, this.serviceTable.getColumns().get("icon"),
                this.getServiceTable().getTableName(),
                this.getServiceTable().getColumns().get("service_id"),
                serviceId);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getMainFeedLogo(String serviceId) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query, this.serviceTable.getColumns().get("logo"),
                this.getServiceTable().getTableName(),
                this.getServiceTable().getColumns().get("service_id"),
                serviceId);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getDatasetTitle(String dataset) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query, this.getDatasetTable().getColumns().get("title"),
                this.getDatasetTable().getTableName(),
                this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"),
                dataset);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }
        finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getDatasetId(String dataset) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query, this.getDatasetTable().getColumns().get("id"),
                this.getDatasetTable().getTableName(), this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"),
                dataset);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getDatasetUpdated(String dataset) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;

        String query = "SELECT to_char(%s, 'RRRR-MM-DD')||'T'||to_char(%s, 'HH24:MI:SS')||'+1:00' FROM %s WHERE %s = '%s'";

        query = String.format(query,
                this.getDatasetTable().getColumns().get("updated"),
                this.getDatasetTable().getColumns().get("updated"),
                this.getDatasetTable().getTableName(),
                this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"),
                dataset);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getDatasetSubtitle(String dataset) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query, this.getDatasetTable().getColumns().get("subtitle"),
                this.getDatasetTable().getTableName(), this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"),
                dataset);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getDatasetMetadataLink(String dataset) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query, this.getDatasetTable().getColumns().get("metadata_link"),
                this.getDatasetTable().getTableName(), this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"),
                dataset);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getDatasetSpatialCode(String dataset) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query,
                this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"),
                this.getDatasetTable().getTableName(),
                this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"),
                dataset);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getDatasetSpatialNamespace(String dataset) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query,
                this.getDatasetTable().getColumns().get("spatial_dataset_identifier_namespace"),
                this.getDatasetTable().getTableName(),
                this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"),
                dataset);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getDatasetContributor(String dataset) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query, this.getDatasetTable().getColumns().get("contributor"),
                this.getDatasetTable().getTableName(), this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"),
                dataset);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getDatasetGenerator(String dataset) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query, this.getDatasetTable().getColumns().get("generator"),
                this.getDatasetTable().getTableName(), this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"),
                dataset);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getDatasetIcon(String dataset) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query, this.getDatasetTable().getColumns().get("icon"),
                this.getDatasetTable().getTableName(), this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"),
                dataset);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getDatasetLogo(String dataset) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query, this.getDatasetTable().getColumns().get("logo"),
                this.getDatasetTable().getTableName(), this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"),
                dataset);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getDatasetDataUrl(String dataset) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = '%s'";
        query = String.format(query, this.getDatasetTable().getColumns().get("dataURL"),
                this.getDatasetTable().getTableName(), this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"),
                dataset);
        String queryResult = "";
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            queryResult = rs.getString(1);
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return queryResult;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * K danému datasetu vyhledá informace o všech souborech.
     * @param dataset unikátní kód datasetu
     * @return pole obsahující informace o souborech patřících k datasetu dataset v podobě
     *         objektů třídy {@link DatasetFile}
     * @throws SQLException
     */
    public ArrayList<DatasetFile> getFilesData(String dataset) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<DatasetFile> files = new ArrayList<DatasetFile>();
        String query = "SELECT %s, %s, %s, %s, %s, to_char(%s, 'RRRR-MM-DD')||'T'||to_char(%s, 'HH24:MI:SS')||'+1:00', %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s = '%s'";

        try{

            query = String.format(query,
                    this.getFilesTable().getColumns().get("file_name"),
                    this.getFilesTable().getColumns().get("file_extension"),
                    this.getFilesTable().getColumns().get("file_size"),
                    this.getFilesTable().getColumns().get("unit_code"),
                    this.getFilesTable().getColumns().get("unit_type"),
                    this.getFilesTable().getColumns().get("updated"),
                    this.getFilesTable().getColumns().get("updated"),
                    this.getFilesTable().getColumns().get("spatial_dataset_identifier_code"),
                    this.getFilesTable().getColumns().get("crs_epsg"),
                    this.getFilesTable().getColumns().get("georss_type"),
                    this.getFilesTable().getColumns().get("metadata_link"),
                    this.getFilesTable().getColumns().get("web_path"),
                    this.getFilesTable().getColumns().get("service_id"),
                    this.getFilesTable().getColumns().get("file_id"),
                    this.getFilesTable().getColumns().get("format"),
                    this.getFilesTable().getTableName(),
                    this.getFilesTable().getColumns().get("spatial_dataset_identifier_code"),
                    dataset);

            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()){
                DatasetFile file = new DatasetFile();
                file.setFile_name(rs.getString(1));
                file.setFile_extension(rs.getString(2));
                file.setFile_size(rs.getString(3));
                file.setUnit_code(rs.getString(4));
                file.setUnit_type(rs.getString(5));
                file.setUpdated(rs.getString(6));
                file.setInspire_dls_code(rs.getString(7));
                file.setCrs_epsg(rs.getString(8));
                file.setGeorss_type(rs.getString(9));
                file.setMetadata_link(rs.getString(10));
                file.setWebPath(rs.getString(11));
                file.setService_id(rs.getString(12));
                file.setFile_id(rs.getString(13));
                file.setFormat(rs.getString(14));
                files.add(file);
            }
        } finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return files;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Do tabulky atom_files vloží aktualní data a jako dataset vloží hodnotu 'pracovni'
     * @param files
     * @throws SQLException
     */
    public void insertFilesData(ArrayList<DatasetFile> files) throws SQLException{
        Statement stmt = null;
        String insert = "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) " +
                "VALUES ('%s', '%s', %s, %s, '%s', to_date('%s', 'DD.MM.RRRR:HH24:MI:SS'), " +
                "'%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')";
        try{
            stmt = conn.createStatement();
            for(DatasetFile file : files) {
                stmt.executeUpdate(
                        String.format(insert, this.getFilesTable().getTableName(),
                                this.getFilesTable().getColumns().get("file_name"),
                                this.getFilesTable().getColumns().get("file_extension"),
                                this.getFilesTable().getColumns().get("file_size"),
                                this.getFilesTable().getColumns().get("unit_code"),
                                this.getFilesTable().getColumns().get("unit_type"),
                                this.getFilesTable().getColumns().get("updated"),
                                this.getFilesTable().getColumns().get("spatial_dataset_identifier_code"),
                                this.getFilesTable().getColumns().get("crs_epsg"),
                                this.getFilesTable().getColumns().get("georss_type"),
                                this.getFilesTable().getColumns().get("metadata_link"),
                                this.getFilesTable().getColumns().get("web_path"),
                                this.getFilesTable().getColumns().get("service_id"),
                                this.getFilesTable().getColumns().get("format"),
                                this.getFilesTable().getColumns().get("file_id"),
                                file.getFile_name(), file.getFile_extension(),
                                file.getFile_size(), file.getUnit_code(),
                                file.getUnit_type(), file.getUpdated(),
                                file.getInspire_dls_code(), file.getCrs_epsg(),
                                file.getGeorss_type(), file.getMetadata_link(),
                                file.getWebPath(), "pracovni", file.getFormat(), file.getFile_id()));
            }
        }
        finally {
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Z tabulky atom_files smaže všechny záznamy dané služby.
     * @throws SQLException
     */
    public void deleteOldFilesData(String service_id) throws SQLException{
        Statement stmt = null;
        String delete = "DELETE FROM %s WHERE %s = '%s'";
        delete = String.format(delete, this.getFilesTable().getTableName(),
                this.getFilesTable().getColumns().get("service_id"), service_id);
        try{
            stmt = conn.createStatement();
            stmt.executeUpdate(delete);
        }finally {
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Z tabulky atom_files smaže všechny záznamy které mají service_id 'pracovni'.
     * @throws SQLException
     */
    public void deleteTempData() throws SQLException{
        Statement stmt = null;
        String delete = "DELETE FROM %s WHERE %s = 'pracovni'";
        delete = String.format(delete, this.getFilesTable().getTableName(),
                this.getFilesTable().getColumns().get("service_id"));
        try{
            stmt = conn.createStatement();
            stmt.executeUpdate(delete);
        }finally {
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Hodnota dataset všech záznamů z tabulky atom_files jejichž service_id je roven hodnotě pracovni je
     * změněna na hodnotu proměnné service_id.
     * @param dataset
     * @throws SQLException
     */
    public void setNewData(String service_id) throws SQLException{
        Statement stmt = null;
        String update = "UPDATE %s SET %s = '%s' WHERE %s = 'pracovni'";
        update = String.format(update, this.getFilesTable().getTableName(),
                this.getFilesTable().getColumns().get("service_id"), service_id,
                this.getFilesTable().getColumns().get("service_id"));
        try{
            stmt = conn.createStatement();
            stmt.executeUpdate(update);
        }finally {
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí hodnoty sloupce service_id pro všechny záznamy z tabulky atom_stav_publikace, které
     * mají hodnotu sloupce datum_publikace_atom rovnu null.
     * @return
     * @throws SQLException
     */
    public ArrayList<ChangedService> getChanges() throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<ChangedService> datasetCodes = new ArrayList<ChangedService>();
        String select = "SELECT %s, to_char(%s, 'DD.MM.RRRR:HH24:MI:SS') FROM %s WHERE %s IS NULL";
        select = String.format(select,
                this.getPublikaceTable().getColumns().get("service_id"),
                this.getPublikaceTable().getColumns().get("datum_publikace"),
                this.getPublikaceTable().getTableName(),
                this.getPublikaceTable().getColumns().get("datum_aktualizace_databaze"));
        try{
            stmt = conn.createStatement();
            rs = stmt.executeQuery(select);
            while (rs.next()){
                ChangedService chD = new ChangedService();
                chD.setServiceCode(rs.getString(1));
                chD.setDateOfChange(rs.getString(2));
                datasetCodes.add(chD);
            }
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }

        return datasetCodes;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Do tabulky atom_stav_publikace vloží aktuální datum do sloupce datum_aktualizace_db.
     */
    public void commitChangesToDatabase(String theme, String dateOfChange) throws SQLException{
        Statement stmt = null;
        String update = "UPDATE %s SET %s = to_date('%s', 'DD.MM.RRRR:HH24:MI:SS') " +
                        "WHERE %s = '%s' AND %s IS NULL AND %s = to_date('%s', 'DD.MM.RRRR:HH24:MI:SS')";

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy:HH:mm:ss");
        String currentDate = sdf.format(new Date());

        //LocalDateTime cTime = LocalDateTime.now();
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.YYYY:HH:mm:ss");
        //String time = cTime.format(formatter);

        try{
            stmt = conn.createStatement();

            stmt.executeUpdate(String.format(update,
                    this.getPublikaceTable().getTableName(),
                    this.getPublikaceTable().getColumns().get("datum_aktualizace_databaze"),
                    currentDate,
                    this.getPublikaceTable().getColumns().get("service_id"),
                    theme,
                    this.getPublikaceTable().getColumns().get("datum_aktualizace_databaze"),
                    this.getPublikaceTable().getColumns().get("datum_publikace"),
                    dateOfChange));
        }finally {
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Slouží pro porovnání předchozího a současného stavu zvoleného datasetu (adresáře).
     * Oba stavy musejí být v databázi. Předchozí stav se pozná tak že dataset má hodnotu 'dataset'.
     * Současný stav se pozná tak, že dataset má hodnotu 'pracovni'.
     *
     * K danému datasetu vyhledá soubory které v porovnání s posledním stavem feedu ubyli.
     * @param dls_code
     * @throws SQLException
     */
    public ArrayList<DatasetFile> getMissingFiles(String service_id) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<DatasetFile> files = new ArrayList<DatasetFile>();
        String query = "";
        query += "SELECT aa.%s, aa.%s, aa.%s, aa.%s, aa.%s, aa.%s, aa.%s, aa.%s, aa.%s, aa.%s, aa.%s, aa.%s, aa.%s, aa.%s FROM %s aa ";
        query += "LEFT JOIN %s bb ON( ";
        query += "aa.%s = '%s' AND bb.%s = 'pracovni' AND ";
        query += "aa.%s = bb.%s AND aa.%s = bb.%s ) ";
        query += "WHERE bb.%s IS NULL AND aa.%s = '%s' ";

        query = String.format(query,
                              this.getFilesTable().getColumns().get("file_name"),
                              this.getFilesTable().getColumns().get("file_extension"),
                              this.getFilesTable().getColumns().get("file_size"),
                              this.getFilesTable().getColumns().get("unit_code"),
                              this.getFilesTable().getColumns().get("unit_type"),
                              this.getFilesTable().getColumns().get("updated"),
                              this.getFilesTable().getColumns().get("spatial_dataset_identifier_code"),
                              this.getFilesTable().getColumns().get("crs_epsg"),
                              this.getFilesTable().getColumns().get("georss_type"),
                              this.getFilesTable().getColumns().get("metadata_link"),
                              this.getFilesTable().getColumns().get("service_id"),
                              this.getFilesTable().getColumns().get("format"),
                              this.getFilesTable().getColumns().get("web_path"),
                              this.getFilesTable().getColumns().get("file_id"),
                              this.getFilesTable().getTableName(),
                              this.getFilesTable().getTableName(),
                              this.getFilesTable().getColumns().get("service_id"), service_id,
                              this.getFilesTable().getColumns().get("service_id"),
                              this.getFilesTable().getColumns().get("file_name"),
                              this.getFilesTable().getColumns().get("file_name"),
                              this.getFilesTable().getColumns().get("crs_epsg"),
                              this.getFilesTable().getColumns().get("crs_epsg"),
                              this.getFilesTable().getColumns().get("file_name"),
                              this.getFilesTable().getColumns().get("service_id"),
                              service_id);
        try{
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while(rs.next()) {
                DatasetFile file = new DatasetFile();
                file.setFile_name(rs.getString(1));
                file.setFile_extension(rs.getString(2));
                file.setFile_size(rs.getString(3));
                file.setUnit_code(rs.getString(4));
                file.setUnit_type(rs.getString(5));
                file.setUpdated(rs.getString(6));
                file.setInspire_dls_code(rs.getString(7));
                file.setCrs_epsg(rs.getString(8));
                file.setGeorss_type(rs.getString(9));
                file.setMetadata_link(rs.getString(10));
                file.setService_id(rs.getString(11));
                file.setFormat(rs.getString(12));
                file.setWebPath(rs.getString(13));
                file.setFile_id(rs.getString(14));
                files.add(file);
            }
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return files;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Slouží pro porovnání předchozího a současného stavu zvoleného datasetu (adresáře).
     * Oba stavy musejí být v databázi. Předchozí stav se pozná tak že dataset má hodnotu 'dataset'.
     * Současný stav se pozná tak, že dataset má hodnotu 'pracovni'.
     *
     * K danému datasetu vyhledá soubory které v porovnání s posledním stavem feedu přibyli.
     * @param dls_code
     * @return
     * @throws SQLException
     */
    public ArrayList<DatasetFile> getNewFiles(String service_id) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<DatasetFile> files = new ArrayList<DatasetFile>();
        String query = "";
        query += "SELECT bb.%s, bb.%s, bb.%s, bb.%s, bb.%s, bb.%s, bb.%s, bb.%s, bb.%s, bb.%s, bb.%s, bb.%s, bb.%s, bb.%s FROM %s aa ";
        query += "RIGHT JOIN %s bb ON( ";
        query += "aa.%s = '%s' AND bb.%s = 'pracovni' AND ";
        query += "aa.%s = bb.%s AND aa.%s = bb.%s ) ";
        query += "WHERE aa.%s IS NULL AND bb.%s = 'pracovni' ";

        query = String.format(query,
                this.getFilesTable().getColumns().get("file_name"),
                this.getFilesTable().getColumns().get("file_extension"),
                this.getFilesTable().getColumns().get("file_size"),
                this.getFilesTable().getColumns().get("unit_code"),
                this.getFilesTable().getColumns().get("unit_type"),
                this.getFilesTable().getColumns().get("updated"),
                this.getFilesTable().getColumns().get("spatial_dataset_identifier_code"),
                this.getFilesTable().getColumns().get("crs_epsg"),
                this.getFilesTable().getColumns().get("georss_type"),
                this.getFilesTable().getColumns().get("metadata_link"),
                this.getFilesTable().getColumns().get("service_id"),
                this.getFilesTable().getColumns().get("format"),
                this.getFilesTable().getColumns().get("web_path"),
                this.getFilesTable().getColumns().get("file_id"),
                this.getFilesTable().getTableName(), this.getFilesTable().getTableName(),
                this.getFilesTable().getColumns().get("service_id"), service_id,
                this.getFilesTable().getColumns().get("service_id"),
                this.getFilesTable().getColumns().get("file_name"),
                this.getFilesTable().getColumns().get("file_name"),
                this.getFilesTable().getColumns().get("crs_epsg"),
                this.getFilesTable().getColumns().get("crs_epsg"),
                this.getFilesTable().getColumns().get("file_name"),
                this.getFilesTable().getColumns().get("service_id"));
        try{
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while(rs.next()) {
                DatasetFile file = new DatasetFile();
                file.setFile_name(rs.getString(1));
                file.setFile_extension(rs.getString(2));
                file.setFile_size(rs.getString(3));
                file.setUnit_code(rs.getString(4));
                file.setUnit_type(rs.getString(5));
                file.setUpdated(rs.getString(6));
                file.setInspire_dls_code(rs.getString(7));
                file.setCrs_epsg(rs.getString(8));
                file.setGeorss_type(rs.getString(9));
                file.setMetadata_link(rs.getString(10));
                file.setService_id(rs.getString(11));
                file.setFormat(rs.getString(12));
                file.setWebPath(rs.getString(13));
                file.setFile_id(rs.getString(14));
                files.add(file);
            }
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return files;
    }
    //------------------------------------------------------------------------------------------------------------------
    public ArrayList<DatasetFile> getModifiedFiles(String service_id) throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<DatasetFile> files = new ArrayList<DatasetFile>();
        String query = "";
        query += "SELECT aa.%s, aa.%s, aa.%s, aa.%s, aa.%s, aa.%s, aa.%s, aa.%s, aa.%s, aa.%s, aa.%s, aa.%s, aa.%s, aa.%s FROM %s aa ";
        query += "LEFT JOIN %s bb ON( ";
        query += "aa.%s = '%s' AND bb.%s = 'pracovni' AND ";
        query += "aa.%s = bb.%s AND aa.%s = bb.%s ) ";
        query += "WHERE bb.%s IS NOT NULL AND aa.%s = '%s' AND bb.%s > aa.%s ";

        query = String.format(query,
                this.getFilesTable().getColumns().get("file_name"),
                this.getFilesTable().getColumns().get("file_extension"),
                this.getFilesTable().getColumns().get("file_size"),
                this.getFilesTable().getColumns().get("unit_code"),
                this.getFilesTable().getColumns().get("unit_type"),
                this.getFilesTable().getColumns().get("updated"),
                this.getFilesTable().getColumns().get("spatial_dataset_identifier_code"),
                this.getFilesTable().getColumns().get("crs_epsg"),
                this.getFilesTable().getColumns().get("georss_type"),
                this.getFilesTable().getColumns().get("metadata_link"),
                this.getFilesTable().getColumns().get("service_id"),
                this.getFilesTable().getColumns().get("format"),
                this.getFilesTable().getColumns().get("web_path"),
                this.getFilesTable().getColumns().get("file_id"),
                this.getFilesTable().getTableName(), this.getFilesTable().getTableName(),
                this.getFilesTable().getColumns().get("service_id"), service_id,
                this.getFilesTable().getColumns().get("service_id"),
                this.getFilesTable().getColumns().get("file_name"),
                this.getFilesTable().getColumns().get("file_name"),
                this.getFilesTable().getColumns().get("crs_epsg"),
                this.getFilesTable().getColumns().get("crs_epsg"),
                this.getFilesTable().getColumns().get("file_name"),
                this.getFilesTable().getColumns().get("service_id"),
                service_id, this.getFilesTable().getColumns().get("updated"),
                this.getFilesTable().getColumns().get("updated"));
        try{
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while(rs.next()) {
                DatasetFile file = new DatasetFile();
                file.setFile_name(rs.getString(1));
                file.setFile_extension(rs.getString(2));
                file.setFile_size(rs.getString(3));
                file.setUnit_code(rs.getString(4));
                file.setUnit_type(rs.getString(5));
                file.setUpdated(rs.getString(6));
                file.setInspire_dls_code(rs.getString(7));
                file.setCrs_epsg(rs.getString(8));
                file.setGeorss_type(rs.getString(9));
                file.setMetadata_link(rs.getString(10));
                file.setService_id(rs.getString(11));
                file.setFormat(rs.getString(12));
                file.setWebPath(rs.getString(13));
                file.setFile_id(rs.getString(14));
                files.add(file);
            }
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return files;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Danému datasetu vyplní v databázi hodnotu updated. Nastavuje aktuální čas.
     * @param datasetCode
     * @throws SQLException
     */
    public void setDatasetUpdatedToCTime(String datasetCode) throws SQLException{
        Statement stmt = null;
        String update = "UPDATE %s SET %s = sysdate WHERE %s = '%s'";

        try{
            stmt = this.conn.createStatement();
            stmt.executeUpdate(
                    String.format(update,
                            this.getDatasetTable().getTableName(),
                            this.getDatasetTable().getColumns().get("updated"),
                            this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"),
                            datasetCode
                    ));
        }finally {
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Pro zvolený dataset vrátí seznam epsg kódů, které se vyskytují mezi soubory datasetu.
     * Seznam je sestaven na základě sloupce crs_epsg z tabulky atom_files
     * @param datasetCode
     */
    public ArrayList<String> getEpsgList(String datasetCode) throws SQLException{

        ArrayList<String> epsg = new ArrayList<String>();
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT DISTINCT %s FROM %s WHERE %s = '%s'";

        query = String.format(query,
                this.getFilesTable().getColumns().get("crs_epsg"),
                this.getFilesTable().getTableName(),
                this.getFilesTable().getColumns().get("spatial_dataset_identifier_code"),
                datasetCode);
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            while(rs.next()) {
                epsg.add(rs.getString(1));
            }
            return epsg;
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public void insertMetadataRequests(String operace, ArrayList<DatasetFile> files) throws SQLException{
        //TODO co bude metadata_id ?
        Statement stmt = null;
        String insert = "INSERT INTO %s (%s, %s, %s, %s) " +
                "VALUES ('%s', '%s', to_date('%s', 'DD.MM.RRRR:HH24:MI:SS'), '%s')";

        LocalDateTime cTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.YYYY:HH:mm:ss");
        String time = cTime.format(formatter);

        String format;

        try{
            stmt = conn.createStatement();
            for(DatasetFile file : files) {

                if(file.getService_id().split("-").length == 1 || file.getService_id().contains("RUIAN")){
                    format = "";
                }else {
                    format = "-" + file.getService_id().split("-")[2];
                }

                stmt.executeUpdate(
                        String.format(insert, this.getMetadataTable().getTableName(),
                                this.getMetadataTable().getColumns().get("spatial_dataset_identifier_code"),
                                this.getMetadataTable().getColumns().get("operace"),
                                this.getMetadataTable().getColumns().get("datum_vlozeni"),
                                this.getMetadataTable().getColumns().get("stav"),
                                file.getInspire_dls_code() + "-" + file.getCrs_epsg() + "-"  + format,
                                operace,
                                time,
                                "zpracovat"
                               ));
            }
        }
        finally {
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Do tabulky atom2_dataset_update vloží dls_kódy všech fedů, které mají být aktualizovány.
     * @param files
     * @throws SQLException
     */
    public void insertUpdateRequest(ArrayList<DatasetFile> files) throws SQLException{

        Statement stmt = null;
        String insert = "INSERT INTO %s (%s) VALUES ('%s')";

        try{
            stmt = conn.createStatement();
            for(DatasetFile file : files) {
                try {
                    stmt.executeUpdate(
                            String.format(insert,
                                    this.getDatasetUpdateTable().getTableName(),
                                    this.getDatasetUpdateTable().getColumns().get("spatial_dataset_identifier_code"),
                                    file.getInspire_dls_code()
                            ));
                }catch (SQLException err){
                    // pokus o vlozeni stejneho dls_code - preskocit
                }
            }
        }
        finally {
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public void insertNewDataset(String dlsCode, String serviceId) throws SQLException{

        Statement stmt = null;
        String insert = "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) ";
        insert += "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s')";

        try{
            stmt = conn.createStatement();
            stmt.executeUpdate(String.format(insert,
                    this.getDatasetTable().getTableName(),
                    this.getDatasetTable().getColumns().get("id"),
                    this.getDatasetTable().getColumns().get("title"),
                    this.getDatasetTable().getColumns().get("subtitle"),
                    this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"),
                    this.getDatasetTable().getColumns().get("spatial_dataset_identifier_namespace"),
                    this.getDatasetTable().getColumns().get("metadata_link"),
                    this.getDatasetTable().getColumns().get("service_id"),
                    this.config.getRepository().getWebPath() + "/" + serviceId + "/datasetFeeds/" + dlsCode + ".xml",
                    this.createDatasetTitle(dlsCode),
                    this.getAbstractForDataset(getAbstraktId(dlsCode)),
                    dlsCode,
                    "ČÚZK",
                    this.config.getRepository().getWebPath() + "/" + serviceId + "/datasetMetadata/" + dlsCode + "_M.xml",
                    serviceId
            ));
        }finally {
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    private String getAbstraktId(String dlsCode){

        String parts[] = dlsCode.split("-");
        //TODO tady by se to mohlo menit kvuli RUIANU
        return parts[2];
    }
    //------------------------------------------------------------------------------------------------------------------
    private String getAbstractForDataset(String abstraktId) throws SQLException {

        Statement stmt = null;
        ResultSet rs = null;
        String select = "SELECT %s FROM %s WHERE %s = '%s'";
        String abstrakt = "";
        try{
            stmt = conn.createStatement();
            rs = stmt.executeQuery(String.format(select,
                    this.getAbstraktTable().getColumns().get("abstrakt"),
                    this.getAbstraktTable().getTableName(),
                    this.getAbstraktTable().getColumns().get("id"),
                    abstraktId
                    ));
            if(rs.next()) {
                abstrakt = rs.getString(1);
            }else {
                abstrakt = "Abstrakt: " + abstraktId + " nebyl nalezen";
            }
        }finally {
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
            try{ if(rs != null) rs.close(); }catch (Exception e){}
        }

        //return abstrakt;
        return "Sem se doplní abstrakt";
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí jméno jednotky(obce, ku, kp). V případě RUIANU vrátí buď název obce a nebo hodnotu 'stát'
     * @param dlsCode
     * @return
     * @throws SQLException
     */
    private String getUnitName(String dlsCode) throws SQLException{

        String unitName = "Neznámý název";
        String parts[] = dlsCode.split("_");
        String unitCode = parts[2];
        String service_id = parts[1];

        String table = "";
        String nameColumn = "";
        String codeColumn = "";

        //KU
        if(service_id.contains("KU") || service_id.equals("CP")){
            table = "PUBL.PUB_KATASTRALNI_UZEMI";
            nameColumn = "nazev";
            codeColumn = "kod";
        }
        //KP
        if(service_id.contains("KP")){
            table = "PUBL.PUB_PRACOVISTE";
            nameColumn = "nazev_zkraceny";
            codeColumn = "prares_kod";
        }
        //Obec
        if(service_id.equals("AD") || service_id.equals("BU") || (service_id.contains("RUIAN") && unitCode.length() == 6)){
            table = "PUBL.PUB_OBCE";
            nameColumn = "nazev";
            codeColumn = "kod";
        }
        //Stat
        if(service_id.contains("RUIAN") && unitCode.length() != 6){
            unitName = "stát";
            return unitName;
        }

        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT %s FROM %s WHERE %s = %s";
        query = String.format(query, nameColumn, table, codeColumn, unitCode);

        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            try {
                unitName = rs.getString(1);
            }
            catch (SQLException err){}
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }

        return unitName;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí title pro entry uvnitř dataset feedu.
     * @param dlsCode
     * @param file
     * @return
     * @throws SQLException
     */
    public String createFileTitle(String dlsCode, DatasetFile file) throws SQLException {
        String parts[] = dlsCode.split("_");
        String title = null;
        String serviceId = parts[1];
        String unitCode = parts[2];

        if(serviceId.equals("CP")){
            String unitName = getUnitName(dlsCode);
            title = String.format("%s [%s] %s", unitName, unitCode, getCrsNameFromEpsg(file.getCrs_epsg()) );
        }
        else if(serviceId.equals("AD")){
            String unitName = getUnitName(dlsCode);
            title = String.format("%s [%s] %s", unitName, unitCode, getCrsNameFromEpsg(file.getCrs_epsg()) );
        }
        else if(serviceId.equals("AU")){
            title = String.format("%s", getCrsNameFromEpsg(file.getCrs_epsg()) );
        }
        else if(serviceId.equals("BU")){
            String unitName = getUnitName(dlsCode);
            title = String.format("%s [%s] %s", unitName, unitCode, getCrsNameFromEpsg(file.getCrs_epsg()) );
        }
        else if(serviceId.contains("KM-KU")){
            String unitName = getUnitName(dlsCode);
            title = String.format("%s [%s] %s", unitName, unitCode, getCrsNameFromEpsg(file.getCrs_epsg()) );
        }
        else if(serviceId.contains("KM-KP")){
            String unitName = getUnitName(dlsCode);
            title = String.format("%s [%s] %s", unitName, unitCode, getCrsNameFromEpsg(file.getCrs_epsg()) );
        }
        else if(serviceId.contains("GMPL-KU")){
            String unitName = getUnitName(dlsCode);
            title = String.format("%s [%s] %s", unitName, unitCode, getCrsNameFromEpsg(file.getCrs_epsg()) );
        }
        else if(serviceId.contains("GMPL-KP")){
            String unitName = getUnitName(dlsCode);
            title = String.format("%s [%s] %s", unitName, unitCode, getCrsNameFromEpsg(file.getCrs_epsg()) );
        }
        else if(serviceId.equals("RUIAN-S-K-Z") || serviceId.equals("RUIAN-S-ZA-Z") || serviceId.equals("RUIAN-H-ZA-Z")){
            String unitName = getUnitName(dlsCode);
            title = String.format("%s - %s", unitName, file.getFile_name() );
        }
        else if(serviceId.equals("RUIAN-S-K-U") || serviceId.equals("RUIAN-S-ZA-U") || serviceId.equals("RUIAN-H-ZA-Z")){
            String unitName = getUnitName(dlsCode);
            title = String.format("%s [%s]", unitName, unitCode );
        }
        else if(serviceId.equals("RUIAN-SP-CIS-U") || serviceId.equals("RUIAN-SP-VO-U")){
            String unitName = getUnitName(dlsCode);
            title = String.format("%s - %s", unitName, file.getFile_name());
        }
        else if(serviceId.equals("RUIAN-CSV-ADR-OB")){
            String unitName = getUnitName(dlsCode);
            title = String.format("%s [%s]", unitName, unitCode );
        }
        else if(serviceId.equals("RUIAN-CSV-ADR-ST") || serviceId.equals("RUIAN-CSV-HIE-ST")){
            String unitName = getUnitName(dlsCode);
            title = String.format("%s - %s", unitName, file.getFile_name());
        }
        else{
            title = file.getFile_name() + "\\." + file.getFile_extension();
        }

        return title;
    }
    //------------------------------------------------------------------------------------------------------------------
    private String getCrsNameFromEpsg(String epsg){
        String crsName = null;
        if(epsg.equals("5514")){
            crsName = "S-JTSK";
        }else if(epsg.equals("4258")){
            crsName = "ETRS89";
        }else if(epsg.equals("4326")){
            crsName = "WGS-84";
        }
        return crsName;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí title datasetového feedu na základě inspire_dls_kódu
     * @param dlsCode
     * @return
     */
    private String createDatasetTitle(String dlsCode) throws SQLException {

        String parts[] = dlsCode.split("_");
        String title = "";
        String serviceId = parts[1];
        String unitCode = parts[2];

        if(serviceId.equals("CP")){
            String unitName = getUnitName(dlsCode);
            title = String.format("INSPIRE - katastrální parcely - katastrální území - %s [%s]", unitName, unitCode );
        }
        else if(serviceId.equals("AD")){
            String unitName = getUnitName(dlsCode);
            title = String.format("INSPIRE - adresní místa - obec - %s [%s]", unitName, unitCode );
        }
        else if(serviceId.equals("AU")){
            title = String.format("INSPIRE - administrativní jednotky");
        }
        else if(serviceId.equals("BU")){
            String unitName = getUnitName(dlsCode);
            title = String.format("INSPIRE - budovy - obec - %s [%s]", unitName, unitCode );
        }
        else if(serviceId.contains("KM-KU")){
            String unitName = getUnitName(dlsCode);
            title = String.format("Katastrální mapa pro katastrální území - %s [%s]", unitName, unitCode );
        }
        else if(serviceId.contains("KM-KP")){
            String unitName = getUnitName(dlsCode);
            title = String.format("Katastrální mapa pro katastrální pracoviště - %s [%s]", unitName, unitCode );
        }
        else if(serviceId.contains("GMPL-KU")){
            String unitName = getUnitName(dlsCode);
            title = String.format("Mapa geometrických plánů pro katastrální území - %s [%s]", unitName, unitCode );
        }
        else if(serviceId.contains("GMPL-KP")){
            String unitName = getUnitName(dlsCode);
            title = String.format("Mapa geometrických plánů pro katastrální pracoviště - %s [%s]", unitName, unitCode );
        }
        else if(serviceId.contains("RUIAN-S-K")){
            String unitName = getUnitName(dlsCode);
            if(unitName.equals("stát")){
                if(serviceId.split("-")[3].equals("Z")){
                    title = String.format("RÚIAN současná data-kompletní datová sada - %s [%s] - %s", unitName, unitCode.split("-")[1], unitCode.split("-")[2] );
                }else{
                    title = String.format("RÚIAN současná data-kompletní datová sada - %s [%s]", unitName, unitCode.split("-")[1]);
                }

            }else{
                title = String.format("RÚIAN současná data-kompletní datová sada - obec: %s [%s]", unitName, unitCode );
            }
        }
        else if(serviceId.contains("RUIAN-S-ZA")){
            String unitName = getUnitName(dlsCode);
            if(unitName.equals("stát")){
                if(serviceId.split("-")[3].equals("Z")){
                    title = String.format("RÚIAN současná data-základní datová sada - %s [%s] - %s", unitName, unitCode.split("-")[1], unitCode.split("-")[2] );
                }else{
                    title = String.format("RÚIAN současná data-základní datová sada - %s [%s]", unitName, unitCode.split("-")[1]);
                }
            }else{
                title = String.format("RÚIAN současná data-základní datová sada - obec: %s [%s]", unitName, unitCode );
            }
        }
        else if(serviceId.contains("RUIAN-H-ZA")){
            String unitName = getUnitName(dlsCode);
            if(unitName.equals("stát")){
                title = String.format("RÚIAN historická data-základní datová sada - %s", unitName);
            }else{
                title = String.format("RÚIAN historická data-základní datová sada - obec: %s [%s]", unitName, unitCode );
            }
        }
        else if(serviceId.contains("RUIAN-SP-CIS")){
            title = "RÚIAN speciální data - číselníky - stát";
        }
        else if(serviceId.contains("RUIAN-SP-VO")){
            title = "RÚIAN speciální data - volební okrsky - stát";
        }
        else if(serviceId.equals("RUIAN-CSV-ADR-OB")){
            String unitName = getUnitName(dlsCode);
            title = String.format("RÚIAN csv - adresy - obec: %s [%s]", unitName, unitCode);
        }
        else if(serviceId.equals("RUIAN-CSV-ADR-ST")){
            title = "RÚIAN csv - adresy - stát";
        }
        else if(serviceId.equals("RUIAN-CSV-HIE-ST")) {
            title = "RÚIAN csv - hierarchie prvků - stát";
        }
        else{
            title = "Neznámý kód služby";
        }
        return title;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Z tabulky datasetů smaže záznam s daným inspire_dls_kódem.
     * @param inspire_dls_code
     * @throws SQLException
     */
    public void deleteDataset(String inspire_dls_code) throws SQLException{

        Statement stmt = null;
        String delete = "DELETE FROM %s WHERE %s = '%s'";

        try{
            stmt = conn.createStatement();
            stmt.executeUpdate(String.format(delete,
                    this.getDatasetTable().getTableName(),
                    this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"),
                    inspire_dls_code));
        }finally {
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Zjistí, zda v tabulce souborů existuje soubor, pro nějž neexistuje v tabulce datasetů žádný dataset.
     * Vrátí seznam inspire_dls_kódů.
     * @return
     * @throws SQLException
     */
    public ArrayList<String> getNewDatasets() throws SQLException{

        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<String> datasetCodes = new ArrayList<>();
        String select = "SELECT DISTINCT files.%s FROM %s files ";
        select += "LEFT JOIN %s dataset ON (files.%s = dataset.%s) ";
        select += "WHERE  dataset.%s IS NULL";
        select = String.format(select,
                this.getFilesTable().getColumns().get("spatial_dataset_identifier_code"),
                this.getFilesTable().getTableName(),
                this.getDatasetTable().getTableName(),
                this.getFilesTable().getColumns().get("spatial_dataset_identifier_code"),
                this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"),
                this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"));
        try{
            stmt = conn.createStatement();
            rs = stmt.executeQuery(select);
            while (rs.next()){
                datasetCodes.add(rs.getString(1));
            }
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return datasetCodes;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Zjistí, zda v tabulce datasetů existuje dataset, ke kterému neexistují žádné soubory.
     * Vrátí seznam inspire_dls_kódů.
     * @return
     * @throws SQLException
     */
    public ArrayList<String> getEmptyDataset() throws SQLException{
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<String> datasetCodes = new ArrayList<>();
        String select = "SELECT dataset.%s FROM %s dataset ";
        select += "LEFT JOIN %s files ON (files.%s = dataset.%s) ";
        select += "WHERE  files.%s IS NULL";
        select = String.format(select,
                this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"),
                this.getDatasetTable().getTableName(),
                this.getFilesTable().getTableName(),
                this.getFilesTable().getColumns().get("spatial_dataset_identifier_code"),
                this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"),
                this.getFilesTable().getColumns().get("spatial_dataset_identifier_code"));
        try{
            stmt = conn.createStatement();
            rs = stmt.executeQuery(select);
            while (rs.next()){
                //System.out.println(rs.getString(1));
                datasetCodes.add(rs.getString(1));
            }
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return datasetCodes;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí seznam dls_kódu datasetů, které mají být aktualizovány.
     * @return
     * @throws SQLException
     */
    public ArrayList<String> getUpdateRequests() throws SQLException{

        ArrayList<String> dls_codes = new ArrayList<>();

        Statement stmt = null;
        ResultSet rs = null;
        String select = "SELECT %s FROM %s";
        select = String.format(select,
                this.getDatasetUpdateTable().getColumns().get("spatial_dataset_identifier_code"),
                this.getDatasetUpdateTable().getTableName());
        try{
            stmt = conn.createStatement();
            rs = stmt.executeQuery(select);
            while (rs.next()){
                dls_codes.add(rs.getString(1));
            }
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return dls_codes;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Z tabulky atom2_dataset_update smaže záznam s daným dls_kódem
     * @param dlsCode
     * @throws SQLException
     */
    public void deleteUpdateRequest(String dlsCode) throws SQLException{

        Statement stmt = null;
        String delete = "DELETE FROM %s WHERE %s = '%s'";
        delete = String.format(delete,
                this.getDatasetUpdateTable().getTableName(),
                this.getDatasetUpdateTable().getColumns().get("spatial_dataset_identifier_code"),
                dlsCode
                );
        try{
            stmt = conn.createStatement();
            stmt.executeUpdate(delete);
        }finally {
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí seznam všech dls_kódu z tabulky datasetů, které patří ka dané službě.
     * @return
     * @throws SQLException
     */
    public ArrayList<String> getDatasetCodes(String serviceId) throws SQLException{

        ArrayList<String> dls_codes = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;
        String select = "SELECT %s FROM %s WHERE %s = '%s' ORDER BY %s";
        select = String.format(select,
                this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"),
                this.getDatasetTable().getTableName(),
                this.getDatasetTable().getColumns().get("service_id"),
                serviceId,
                this.getDatasetTable().getColumns().get("spatial_dataset_identifier_code"));
        try{
            stmt = this.conn.createStatement();
            rs = stmt.executeQuery(select);
            while (rs.next()){
                dls_codes.add(rs.getString(1));
            }
        }finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
        return dls_codes;
    }
    //------------------------------------------------------------------------------------------------------------------
    public void updateServiceFeedId(String serviceId) throws SQLException {
        Statement stmt = null;
        String update = "UPDATE %s SET %s = '%s' WHERE %s = '%s'";
        update = String.format(update,
                this.getServiceTable().getTableName(),
                this.getServiceTable().getColumns().get("id"),
                this.config.getRepository().getWebPath() + "/" + serviceId + "/" + serviceId + ".xml",
                this.getDatasetTable().getColumns().get("service_id"),
                serviceId);
        try{
            stmt = this.conn.createStatement();
            stmt.executeUpdate(update);
        }finally {
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public void updateServiceMetadataLink(String serviceId) throws SQLException{
        Statement stmt = null;
        String update = "UPDATE %s SET %s = '%s' WHERE %s = '%s'";
        update = String.format(update,
                this.getServiceTable().getTableName(),
                this.getServiceTable().getColumns().get("metadata_link"),
                this.config.getRepository().getWebPath() + "/" + serviceId + "/" + serviceId + "_M.xml",
                this.getDatasetTable().getColumns().get("service_id"),
                serviceId);
        try{
            stmt = this.conn.createStatement();
            stmt.executeUpdate(update);
        }finally {
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public void updateServiceOpenSearchlink(String serviceId) throws SQLException{
        Statement stmt = null;
        String update = "UPDATE %s SET %s = '%s' WHERE %s = '%s'";
        update = String.format(update,
                this.getServiceTable().getTableName(),
                this.getServiceTable().getColumns().get("opensearch_link"),
                this.config.getRepository().getWebPath() + "/" + serviceId + "/" + serviceId + "_S.xml",
                this.getDatasetTable().getColumns().get("service_id"),
                serviceId);
        try{
            stmt = this.conn.createStatement();
            stmt.executeUpdate(update);
        }finally {
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getBBox(String datasetCode) throws SQLException {
        String unitCode = datasetCode.split("_")[2];
        String serviceCode = datasetCode.split("_")[1];
        String result = "polygon nenalezen";

        Statement stmt = null;
        ResultSet rs = null;
        String select = "SELECT %s FROM %s WHERE %s = %s";

        try {
            stmt = this.conn.createStatement();
            if (serviceCode.contains("KP")) {
                select = String.format(select,
                        this.getGeorsskp().getColumns().get("wgs84"),
                        this.getGeorsskp().getTableName(),
                        this.getGeorsskp().getColumns().get("kod"),
                        unitCode);
                rs = stmt.executeQuery(select);
                if(rs.next()) {
                    result = rs.getString(1);
                }
            } else if (serviceCode.contains("KU") || serviceCode.equals("CP")) {
                select = String.format(select,
                        this.getGeorssku().getColumns().get("wgs84"),
                        this.getGeorssku().getTableName(),
                        this.getGeorssku().getColumns().get("kod"),
                        unitCode);
                rs = stmt.executeQuery(select);
                if(rs.next()) {
                    result = rs.getString(1);
                }
            } else if (serviceCode.equals("AD") || serviceCode.equals("BU")) {
                select = String.format(select,
                        this.getGeorssobce().getColumns().get("wgs84"),
                        this.getGeorssobce().getTableName(),
                        this.getGeorssobce().getColumns().get("kod"),
                        unitCode);
                rs = stmt.executeQuery(select);
                if(rs.next()) {
                    result = rs.getString(1);
                }
            } else if (serviceCode.equals("AU")) {
                select = String.format(select,
                        this.getGeorssStat().getColumns().get("wgs84"),
                        this.getGeorssStat().getTableName(),
                        this.getGeorssStat().getColumns().get("kod"),
                        unitCode);
                rs = stmt.executeQuery(select);
                if(rs.next()) {
                    result = rs.getString(1);
                }
            } else{
                result = "";
            }
        }
        finally {
            try{ if(rs != null) rs.close(); }catch (Exception e){}
            try{ if(stmt != null) stmt.close(); }catch (Exception e){}
        }

        return result;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public Table getServiceTable() {
        return serviceTable;
    }

    public void setServiceTable(Table serviceTable) {
        this.serviceTable = serviceTable;
    }

    public Table getDatasetTable() {
        return datasetTable;
    }

    public void setDatasetTable(Table datasetTable) {
        this.datasetTable = datasetTable;
    }

    public Table getFilesTable() {
        return filesTable;
    }

    public void setFilesTable(Table filesTable) {
        this.filesTable = filesTable;
    }

    public Table getMetadataTable() {
        return metadataTable;
    }

    public void setMetadataTable(Table metadataTable) {
        this.metadataTable = metadataTable;
    }

    public Table getPublikaceTable() {
        return publikaceTable;
    }

    public void setPublikaceTable(Table publikaceTable) {
        this.publikaceTable = publikaceTable;
    }

    public Table getAbstraktTable() {
        return abstraktTable;
    }

    public void setAbstraktTable(Table abstraktTable) {
        this.abstraktTable = abstraktTable;
    }

    public Table getDatasetUpdateTable() {
        return datasetUpdateTable;
    }

    public void setDatasetUpdateTable(Table datasetUpdateTable) {
        this.datasetUpdateTable = datasetUpdateTable;
    }

    public Table getGeorssStat() {
        return georssStat;
    }

    public void setGeorssStat(Table georssStat) {
        this.georssStat = georssStat;
    }

    public Table getGeorssobce() {
        return georssobce;
    }

    public void setGeorssobce(Table georssobce) {
        this.georssobce = georssobce;
    }

    public Table getGeorsskp() {
        return georsskp;
    }

    public void setGeorsskp(Table georsskp) {
        this.georsskp = georsskp;
    }

    public Table getGeorssku() {
        return georssku;
    }

    public void setGeorssku(Table georssku) {
        this.georssku = georssku;
    }
    //------------------------------------------------------------------------------------------------------------------
    //PRIVATE METHODS
    //------------------------------------------------------------------------------------------------------------------


}
