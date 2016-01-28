package cz.cuzk.services.atomfeed.config;

/**
 *
 */
public class Repository {
    private String localRepository;
    private String webPath;
    private String tempRepository;

    public String getTempRepository() {
        return tempRepository;
    }

    public String getLocalRepository() {
        return localRepository;
    }

    public String getWebPath() {
        return webPath;
    }

}
