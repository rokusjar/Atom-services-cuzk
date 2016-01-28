package cz.cuzk.services.atomfeed.config;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by rokusekj on 14.12.2015.
 */
public class CustomFeedDeserializer implements JsonDeserializer<CustomFeed> {

    @Override
    public CustomFeed deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        CustomFeed customFeed = new CustomFeed();

        JsonObject mainObject = jsonElement.getAsJsonObject();
        String fileName = mainObject.get("fileName").getAsString();
        String title = mainObject.get("title").getAsString();
        JsonArray services = mainObject.getAsJsonArray("serviceCodes");

        ArrayList<String> ser = new ArrayList<>();

        for(JsonElement el : services){
            ser.add(el.getAsJsonObject().get("code").getAsString());
        }

        customFeed.setFileName(fileName);
        customFeed.setTitle(title);
        customFeed.setServiceCodes(ser);

        return customFeed;
    }
}
