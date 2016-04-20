package cz.cuzk.services.atomfeed.config;

/**
 * Představuje JSON objekt z konfiguračního souboru, který definuje kam se mají generovat kanály
 * a pod jakou adresou mají být vystaveny.
 */
public class Repository {
    private String Repository;
    private String webPath;
    private String tempRepository;

    public String getTempRepository() {
        return tempRepository;
    }

    public String getRepository() {
        return Repository;
    }

    public String getWebPath() {
        return webPath;
    }

}
