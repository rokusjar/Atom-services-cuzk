package cz.cuzk.services.atomfeed.config;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Definuje převod  hlavního JSON objektu z konfiguračního souboru na objekt v Javě.
 * @author Jaromír Rokusek
 * @version 1.0
 * @since 2015-08-23
 */
//----------------------------------------------------------------------------------------------------------------------
public class ConfigDeserializer implements JsonDeserializer<Config>{

    @Override
    public Config deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        DbConnection dbConnection = context.deserialize(jsonObject.get("dbConnection"), DbConnection.class);
        Repository repository = context.deserialize(jsonObject.get("repository"), Repository.class);
        Table[] tables = context.deserialize(jsonObject.getAsJsonArray("tables"), Table[].class);
        DownloadService[] downloadServices = context.deserialize(jsonObject.getAsJsonArray("downloadService"), DownloadService[].class);
        CustomFeed[] customFeed = context.deserialize(jsonObject.getAsJsonArray("customFeed"), CustomFeed[].class);
        FTP ftp = context.deserialize(jsonObject.get("FTP"), FTP.class);

        Config config = new Config();
        config.setDbConn(dbConnection);
        config.setDownloadService(downloadServices);
        config.setTables(tables);
        config.setRepository(repository);
        config.setCustomFeed(customFeed);
        config.setFtp(ftp);

        return config;
    }

}
