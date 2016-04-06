package cz.cuzk.services.atomfeed.config;

/**
 *
 */
public class Repository {
    private String Repository;
    private String webPath;
    private String tempRepository;

    public String getTempRepository() {
        return tempRepository;
    }

    public String getLocalRepository() {
        return Repository;
    }

    public String getWebPath() {
        return webPath;
    }

}
