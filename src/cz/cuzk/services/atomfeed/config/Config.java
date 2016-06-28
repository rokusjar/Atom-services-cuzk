package cz.cuzk.services.atomfeed.config;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Slouží jako modelová třída pro čtení konfiguračního souboru pomocí knihovny Gson.
 * Drží všechny informace z konfiguračního souboru pohromadě.
 * @author Jaromír Rokusek
 * @version 1.0
 * @since 2015-08-23
 */
public class Config {
    private DbConnection dbConn;
    private DownloadService[] downloadService;
    private Table[] tables;
    private Repository repository;
    private CustomFeed[] customFeed;
    private FTP ftp;
    //------------------------------------------------------------------------------------------------------------------
    public Table[] getTables() {
        return tables;
    }
    //------------------------------------------------------------------------------------------------------------------
    public Table getServiceTable() throws TableNotFoundException, ColumnNotFoundException{
        Table rTable = null;
        for(Table t : tables){
            if(t.getType().equals("service")){
                rTable = t;
            }
        }
        if(rTable == null){
            throw new TableNotFoundException("Tabulka s atributem type:service nebyla nalezena.");
        }else {
            checkColumns(rTable);
            return rTable;
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public Table getDatasetTable() throws TableNotFoundException, ColumnNotFoundException{
        Table rTable = null;
        for(Table t : tables){
            if(t.getType().equals("datasets")){
                rTable = t;
            }
        }
        if(rTable == null){
            throw new TableNotFoundException("Tabulka s atributem type:datasets nebyla nalezena.");
        }else {
            checkColumns(rTable);
            return rTable;
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public Table getFilesTable() throws TableNotFoundException, ColumnNotFoundException{
        Table rTable = null;
        for(Table t : tables){
            if(t.getType().equals("files")){
                rTable = t;
            }
        }
        if(rTable == null){
            throw new TableNotFoundException("Tabulka s atributem type:files nebyla nalezena.");
        }else {
            checkColumns(rTable);
            return rTable;
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public Table getMetadataTable() throws TableNotFoundException, ColumnNotFoundException{
        Table rTable = null;
        for(Table t : tables){
            if(t.getType().equals("metadata")){
                rTable = t;
            }
        }
        if(rTable == null){
            throw new TableNotFoundException("Tabulka s atributem type:files nebyla nalezena.");
        }else {
            checkColumns(rTable);
            return rTable;
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public Table getPublikaceTable() throws TableNotFoundException, ColumnNotFoundException{
        Table rTable = null;
        for(Table t : tables){
            if(t.getType().equals("publikace")){
                rTable = t;
            }
        }
        if(rTable == null){
            throw new TableNotFoundException("Tabulka s atributem type:publikace nebyla nalezena.");
        }else {
            checkColumns(rTable);
            return rTable;
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public Table getAbstraktTable() throws TableNotFoundException, ColumnNotFoundException{
        Table rTable = null;
        for(Table t : tables){
            if(t.getType().equals("abstrakt")){
                rTable = t;
            }
        }
        if(rTable == null){
            throw new TableNotFoundException("Tabulka s atributem type:abstrakt nebyla nalezena.");
        }else {
            checkColumns(rTable);
            return rTable;
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public Table getDatasetUpdateTable() throws TableNotFoundException, ColumnNotFoundException{
        Table rTable = null;
        for(Table t : tables){
            if(t.getType().equals("update")){
                rTable = t;
            }
        }
        if(rTable == null){
            throw new TableNotFoundException("Tabulka s atributem type:update nebyla nalezena.");
        }else {
            checkColumns(rTable);
            return rTable;
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public Table getDatasetDeleteTable() throws TableNotFoundException, ColumnNotFoundException{
        Table rTable = null;
        for(Table t : tables){
            if(t.getType().equals("delete")){
                rTable = t;
            }
        }
        if(rTable == null){
            throw new TableNotFoundException("Tabulka s atributem type:delete nebyla nalezena.");
        }else {
            checkColumns(rTable);
            return rTable;
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public Table getStatsTable() throws TableNotFoundException, ColumnNotFoundException{
        Table rTable = null;
        for(Table t : tables){
            if(t.getType().equals("statistiky")){
                rTable = t;
            }
        }
        if(rTable == null){
            throw new TableNotFoundException("Tabulka s atributem type:delete nebyla nalezena.");
        }else {
            checkColumns(rTable);
            return rTable;
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public Table getGeorssStat() throws TableNotFoundException, ColumnNotFoundException{
        Table rTable = null;
        for(Table t : tables){
            if(t.getType().equals("georss_stat")){
                rTable = t;
            }
        }
        if(rTable == null){
            throw new TableNotFoundException("Tabulka s atributem type:georss_stat nebyla nalezena.");
        }else {
            checkColumns(rTable);
            return rTable;
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public Table getGeorssObce() throws TableNotFoundException, ColumnNotFoundException{
        Table rTable = null;
        for(Table t : tables){
            if(t.getType().equals("georss_obce")){
                rTable = t;
            }
        }
        if(rTable == null){
            throw new TableNotFoundException("Tabulka s atributem type:georss_obce nebyla nalezena.");
        }else {
            checkColumns(rTable);
            return rTable;
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public Table getGeorssKu() throws TableNotFoundException, ColumnNotFoundException{
        Table rTable = null;
        for(Table t : tables){
            if(t.getType().equals("georss_ku")){
                rTable = t;
            }
        }
        if(rTable == null){
            throw new TableNotFoundException("Tabulka s atributem type:georss_ku nebyla nalezena.");
        }else {
            checkColumns(rTable);
            return rTable;
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public Table getGeorssKp() throws TableNotFoundException, ColumnNotFoundException{
        Table rTable = null;
        for(Table t : tables){
            if(t.getType().equals("georss_kp")){
                rTable = t;
            }
        }
        if(rTable == null){
            throw new TableNotFoundException("Tabulka s atributem type:georss_kp nebyla nalezena.");
        }else {
            checkColumns(rTable);
            return rTable;
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vráté objekt třídy DownloadService který vyhledá podle kódu.
     * @param code
     * @return
     */
    public DownloadService getServiceByCode(String code) throws DownloadServiceNotFoundException {

        DownloadService ss = null;

        for(DownloadService s : this.getDownloadService()){
            if(s.getCode().equals(code)){
                ss = s;
            }
        }
        if(ss == null){
            throw new DownloadServiceNotFoundException("Služba s kódem: " + code + " nebyla nalezena");
        }
        return ss;
    }
    //------------------------------------------------------------------------------------------------------------------
    public Source getSourceByDatasetCode(String datasetCode){
        //TODO get source by datasetCode
        Source result;

        DownloadService downloadServices[] = this.getDownloadService();
        for(DownloadService t : downloadServices){
            ArrayList<Source> sources = t.getSources();
            for(Source s : sources){

            }
        }

        return null;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Zkontroluje jestli Json objekt tables v konfiguračním souboru obsahuje všechny tabulky a všechny sloupce
     * @throws InvalidConfigFileException když objekt tables neobsahuje všechny tabulky a všechny sloupce
     */
    public void checkColumns(Table t) throws ColumnNotFoundException{

        HashMap<String, String> sloupce = t.getColumns();
        String type = t.getType();

        String[] mainFeedSloupce = {"id", "title", "subtitle", "metadata_link", "opensearch_link",
                "contributor", "generator", "icon", "logo", "service_id"};

        String[] datasetSloupce = {"id", "title", "subtitle", "metadata_link",
                "spatial_dataset_identifier_code", "spatial_dataset_identifier_namespace",
                "contributor", "generator", "icon", "logo", "service_id"};

        String[] filesSloupce = {"file_name", "file_extension", "file_size", "unit_code", "unit_type",
                "updated", "spatial_dataset_identifier_code", "web_path", "crs_epsg", "georss_type", "metadata_link",
                "service_id", "format", "file_id"};

        if(type.equals("service")){
            for(String s : mainFeedSloupce){
                if(sloupce.containsKey(s) == false){
                    throw new ColumnNotFoundException(String.format("U tabulky %s chybi sloupec %s",
                            t.getTableName(), s));
                }
            }
        }
        if(type.equals("dataset")){
            for(String s : datasetSloupce){
                if(sloupce.containsKey(s) == false){
                    throw new ColumnNotFoundException(String.format("U tabulky %s chybi sloupec %s",
                            t.getTableName(), s));
                }
            }
        }
        if(type.equals("files")){
            for(String s : filesSloupce){
                if(sloupce.containsKey(s) == false){
                    throw new ColumnNotFoundException(String.format("U tabulky %s chybi sloupec %s",
                            t.getTableName(), s));
                }
            }
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setDbConn(DbConnection dbConn) {
        this.dbConn = dbConn;
    }

    public void setDownloadService(DownloadService[] downloadService) {
        this.downloadService = downloadService;
    }

    public void setTables(Table[] tables) {
        this.tables = tables;
    }

    public DbConnection getDbConn() {
        return dbConn;
    }

    public DownloadService[] getDownloadService() {
        return downloadService;
    }

    public CustomFeed[] getCustomFeed() {
        return customFeed;
    }

    public void setCustomFeed(CustomFeed[] customFeeds) {
        this.customFeed = customFeeds;
    }

    public FTP getFtp() {
        return ftp;
    }

    public void setFtp(FTP ftp) {
        this.ftp = ftp;
    }

}
