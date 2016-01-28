package cz.cuzk.services.atomfeed.config;

import java.util.HashMap;
//----------------------------------------------------------------------------------------------------------------------
/**
 * Slouží jako modelová třída pro čtení konfiguračního souboru pomocí knihovny Gson.
 * columns - hodnota v mapě je nazev sloupce v databazi klic je klic ze souboru config.json
 * @author Jaromír Rokusek
 * @version 1.0
 * @since 2015-08-23
 */
//----------------------------------------------------------------------------------------------------------------------
public class Table {
    private String type;
    private String tableName;
    private HashMap<String, String> columns = new HashMap<String, String>();

    public void setType(String type) {
        this.type = type;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setColumns(HashMap<String, String> columns) {
        this.columns = columns;
    }

    public String getType() {
        return type;
    }

    public String getTableName() {
        return tableName;
    }

    public HashMap<String, String> getColumns() {
        return columns;
    }

    public void addColumn(String key, String value){
        this.columns.put(key, value);
    }
}
