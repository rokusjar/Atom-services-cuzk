package cz.cuzk.services.atomfeed.keeper;

/**
 * Vyhozena když je někde použit neznámý kód datasetu. Například když program který vystavuje soubory
 * vloží do tabulky atom_stav_publikace neplatný kód datasetu.
 */
public class UnknownDatasetException extends Exception {
    public UnknownDatasetException(String message) {
        super(message);
    }
}