package cz.cuzk.services.atomfeed.config;

import java.util.ArrayList;

/**
 *
 */
public class CustomFeed {

    private String fileName;
    private String title;
    private ArrayList<String> serviceCodes;
    //------------------------------------------------------------------------------------------------------------------
    public ArrayList<String> getServiceCodes() {
        return serviceCodes;
    }

    public void setServiceCodes(ArrayList<String> serviceCodes) {
        this.serviceCodes = serviceCodes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
