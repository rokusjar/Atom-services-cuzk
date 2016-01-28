package cz.cuzk.services.atomfeed.keeper;

/**
 * Pomocná třída. Používá se při čtení tabulky atom_stav_publikace.
 */
public class ChangedService {
    private String serviceCode;
    private String dateOfChange;

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getDateOfChange() {
        return dateOfChange;
    }

    public void setDateOfChange(String dateOfChange) {
        this.dateOfChange = dateOfChange;
    }
}
