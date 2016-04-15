package cz.cuzk.services.atomfeed.keeper;

import cz.cuzk.services.atomfeed.config.Source;
import cz.cuzk.services.atomfeed.feed.common.DatasetFile;

import java.io.File;
import java.util.ArrayList;

/**
 * Abstraktní třída představující stahovací službu. Má dvě povinné metody.
 * Metoda {@link cz.cuzk.services.atomfeed.keeper.Service#getCurrentState} má vrátit
 * pole objektů třídy {@link cz.cuzk.services.atomfeed.feed.common.DatasetFile} které představují
 * předpřipravené soubory.
 * metoda {@link cz.cuzk.services.atomfeed.keeper.Service#dlsCode} má sestavit identifikátor datasetu.
 */
public abstract class Service {

    private ArrayList<Source> sources;
    private String serviceId;
    private String dateOfChange;
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrací seznam který obsahuje všechny potřebné informace o souborech které se právě nachází
     * ve zdrojovém (zdrojových) adresářích.
     * @return
     */
    public abstract ArrayList<DatasetFile> getCurrentState() throws UnknownDatasetException;
    /**
     * Vrací jediněčný identifikátor datasetu.
     * @param file
     * @return
     */
    public abstract String dlsCode(File file);
    /**
     * Vrací jedinečný identifikátor souboru
     * @param file
     * @param epsg
     * @param format
     * @return
     */
    public abstract String fileCode(File file, String epsg, String format);
    //------------------------------------------------------------------------------------------------------------------
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public ArrayList<Source> getSources() {
        return sources;
    }

    public void setSources(ArrayList<Source> sources) {
        this.sources = sources;
    }

    public String getDateOfChange() {
        return dateOfChange;
    }

    public void setDateOfChange(String dateOfChange) {
        this.dateOfChange = dateOfChange;
    }

}
