package cz.cuzk.services.atomfeed.config;

/**
 * Vyhozena když se nepodaří sestavit link na některý feed.
 *
 * Linky jsou sestavovány na základě údajů v
 * konfiguračním souboru. Pokud při sestavování dojde k chybě, feed nebude mít link a proto nemůže být vystaven.
 * Tato chyba informuje o tom že tento případ nastal.
 */
public class CorruptedFeedLinkException extends Exception {
    public CorruptedFeedLinkException(String message) {
        super(message);
    }
}