package cz.cuzk.services.atomfeed.feed.common;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Objekty této třídy představují jednotlivé soubory datasetu a usnaďnují práci s informacemi při čtení databáze.
 */
//----------------------------------------------------------------------------------------------------------------------
public class DatasetFile {
    private String file_name;
    private String file_extension;
    private String file_size;
    private String unit_code;
    private String unit_type;
    private String updated;
    private String inspire_dls_code;
    private String service_id;
    private String webPath;
    private String crs_epsg;
    private String georss_type;
    private String metadata_link;
    private String format;
    private String file_id;

    public void printData(Logger logger){
        logger.log(Level.INFO, String.format("%s.%s: size: %s byte  modified: %s  dataset: %s  unit_type %s  " +
                        "unit_code %s  epsg %s  georss_type %s  service_id %s  format %s  id %s",
                this.getFile_name(), this.getFile_extension(), this.getFile_size(), this.getUpdated(),
                this.getInspire_dls_code(), this.getUnit_type(), this.getUnit_code(), this.getCrs_epsg(),
                this.getGeorss_type(), this.getService_id(), this.getFormat(), this.getFile_id()));
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_extension() {
        return file_extension;
    }

    public void setFile_extension(String file_extension) {
        this.file_extension = file_extension;
    }

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public String getUnit_code() {
        return unit_code;
    }

    public void setUnit_code(String unit_code) {
        this.unit_code = unit_code;
    }

    public String getUnit_type() {
        return unit_type;
    }

    public void setUnit_type(String unit_type) {
        this.unit_type = unit_type;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getInspire_dls_code() {
        return inspire_dls_code;
    }

    public void setInspire_dls_code(String inspire_dls_code) {
        this.inspire_dls_code = inspire_dls_code;
    }

    public String getCrs_epsg() {
        return crs_epsg;
    }

    public void setCrs_epsg(String crs_epsg) {
        this.crs_epsg = crs_epsg;
    }

    public String getGeorss_type() {
        return georss_type;
    }

    public void setGeorss_type(String georss_type) {
        this.georss_type = georss_type;
    }

    public String getMetadata_link() {
        return metadata_link;
    }

    public void setMetadata_link(String metadata_link) {
        this.metadata_link = metadata_link;
    }

    public String getWebPath() {
        return webPath;
    }

    public void setWebPath(String webPath) {
        this.webPath = webPath;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

}
