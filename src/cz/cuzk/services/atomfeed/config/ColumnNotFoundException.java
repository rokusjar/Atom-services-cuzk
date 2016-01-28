package cz.cuzk.services.atomfeed.config;

/**
 * Vyhozena když není v konfiguračním souboru nalezen některý sloupec.
 */
public class ColumnNotFoundException extends Exception {
    public ColumnNotFoundException(String message) {
        super(message);
    }
}