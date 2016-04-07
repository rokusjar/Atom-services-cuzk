package cz.cuzk.services.atomfeed.config;
import com.google.gson.*;
import java.io.*;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by rokusekj on 22.9.2015.
 */

/**
 * Slouží k přečtení konfiguračního souboru. Ten je přečten pomocí metody read().
 * Všechny údaje z konfiguračního souboru jsou pak uloženy v proměnné configData,
 * která je objektem třídy Config.
 * @author Jaromír Rokusek
 * @version 1.0
 * @since 2015-08-23
 */
public class ConfigReader {

    private Config configData;
    private URL location = Config.class.getProtectionDomain().getCodeSource().getLocation();
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Pomocí knihovny Gson přečte konfigurační soubor config.json a převede ho na objektový model.
     * @throws InvalidConfigFileException když dojde k chybě při čtení konfiguračního souboru.
     */
    public void read() throws InvalidConfigFileException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Config.class, new ConfigDeserializer());
        gsonBuilder.registerTypeAdapter(Table.class, new TableDeserializer());
        gsonBuilder.registerTypeAdapter(DownloadService.class, new DownloadServiceDeserializer());
        gsonBuilder.registerTypeAdapter(CustomFeed.class, new CustomFeedDeserializer());
        Gson gson = gsonBuilder.create();

        FileInputStream fis = null;
        InputStreamReader reader = null;

        try {
            //fis = new FileInputStream(getJarLocation() + "/config.json");
            fis = new FileInputStream("config.json");
            reader = new InputStreamReader(fis, "UTF-8");
            Config config = gson.fromJson(reader, Config.class);
            this.configData = config;

        } catch (FileNotFoundException e) {
            throw new InvalidConfigFileException("konfiguracni soubor nenalezen " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new InvalidConfigFileException("chyba kodovani retezcu - ocekavane kodovani je UTF-8 " + e.getMessage());
        } catch (JsonSyntaxException e){
                throw new InvalidConfigFileException("chyba v syntax JSON: " + e.getMessage());
        } catch (JsonParseException e){
            throw new InvalidConfigFileException("chyba ve vzorove tride: " + e.getMessage());
        }
        finally {
            try {
                reader.close();
            }catch (Exception e){}
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    private String getJarLocation(){

        String jarLocation = "";
        String[] peaces = location.getPath().split("/");

        for(int i = 1; i < peaces.length - 1; i++){
            jarLocation += "/" + peaces[i];
        }

        return jarLocation.substring(1);
    }
    //------------------------------------------------------------------------------------------------------------------
    public Config getConfigData() {
        return configData;
    }

}
