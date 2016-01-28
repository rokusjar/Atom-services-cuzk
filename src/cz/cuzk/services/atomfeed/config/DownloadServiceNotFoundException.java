package cz.cuzk.services.atomfeed.config;

/**
 * Vyhozena kdyz funkce getServiceByCode ze tridy Config nenalezne v konfiguracnim souboru sluzbu s danym kodem.
 */
public class DownloadServiceNotFoundException extends Exception {
    public DownloadServiceNotFoundException(String message) {
        super(message);
    }
}