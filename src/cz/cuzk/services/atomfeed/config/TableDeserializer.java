package cz.cuzk.services.atomfeed.config;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Definuje převod JSON objektu na objekt v Javě.
 * @author Jaromír Rokusek
 * @version 1.0
 * @since 2015-08-23
 */
//----------------------------------------------------------------------------------------------------------------------
public class TableDeserializer implements JsonDeserializer<Table>{
    @Override
    public Table deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        JsonObject mainObject = jsonElement.getAsJsonObject();

        String Type = mainObject.get("type").getAsString();
        String tableName = mainObject.get("tableName").getAsString();
        JsonObject columns = mainObject.getAsJsonObject("columns");

        Table table = new Table();
        table.setType(Type);
        table.setTableName(tableName);

        for(Map.Entry<String, JsonElement> entry : columns.entrySet()){
            table.addColumn(entry.getKey(), entry.getValue().getAsString());
        }


        return table;
    }
}
