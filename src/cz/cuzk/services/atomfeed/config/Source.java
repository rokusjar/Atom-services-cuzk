package cz.cuzk.services.atomfeed.config;

/**
 *
 */
public class Source {

    private String dirPath;
    private String webPath;
    private String epsg;
    private String unit_type;
    private String georss_type;
    private String format;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUnit_type() {
        return unit_type;
    }

    public void setUnit_type(String unit_type) {
        this.unit_type = unit_type;
    }

    public String getGeorss_type() {
        return georss_type;
    }

    public void setGeorss_type(String georss_type) {
        this.georss_type = georss_type;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public String getEpsg() {
        return epsg;
    }

    public void setEpsg(String epsg) {
        this.epsg = epsg;
    }

    public String getWebPath() {
        return webPath;
    }

    public void setWebPath(String webPath) {
        this.webPath = webPath;
    }
}
