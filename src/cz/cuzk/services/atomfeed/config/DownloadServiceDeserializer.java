package cz.cuzk.services.atomfeed.config;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Slouží pro převod json objektu na java objekt. Zpracovává objekty stahovacích služeb.
 */
public class DownloadServiceDeserializer implements JsonDeserializer<DownloadService>{

    @Override
    public DownloadService deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        DownloadService downloadService = new DownloadService();

        JsonObject mainObject = jsonElement.getAsJsonObject();
        String name = mainObject.get("name").getAsString();
        String code = mainObject.get("code").getAsString();
        JsonArray sources = mainObject.getAsJsonArray("sources");

        ArrayList<Source> ss = new ArrayList<Source>();

        for(JsonElement el : sources){
            Source source = new Source();
            source.setDirPath(el.getAsJsonObject().get("dirPath").getAsString());
            source.setWebPath(el.getAsJsonObject().get("webPath").getAsString());
            source.setUnit_type(el.getAsJsonObject().get("unit_type").getAsString());
            source.setGeorss_type(el.getAsJsonObject().get("georss_type").getAsString());
            source.setFormat(el.getAsJsonObject().get("format").getAsString());
            source.setEpsg(el.getAsJsonObject().get("epsg").getAsString());
            ss.add(source);
        }

        downloadService.setName(name);
        downloadService.setCode(code);
        downloadService.setSources(ss);

        return downloadService;
    }
}
