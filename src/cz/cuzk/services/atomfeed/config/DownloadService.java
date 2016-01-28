package cz.cuzk.services.atomfeed.config;
//----------------------------------------------------------------------------------------------------------------------

import java.util.ArrayList;

/**
 * Slouží jako modelová třída pro čtení konfiguračního souboru pomocí knihovny Gson.
 * @author Jaromír Rokusek
 * @version 1.0
 * @since 2015-08-23
 */
//----------------------------------------------------------------------------------------------------------------------
public class DownloadService {

    private String name;
    private String code;
    private ArrayList<Source> sources = new ArrayList<Source>();
    //------------------------------------------------------------------------------------------------------------------

    public ArrayList<Source> getSources() {
        return sources;
    }

    public void setSources(ArrayList<Source> sources) {
        this.sources = sources;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
