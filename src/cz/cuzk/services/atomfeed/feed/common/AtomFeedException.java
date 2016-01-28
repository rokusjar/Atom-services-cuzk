package cz.cuzk.services.atomfeed.feed.common;

/**
 * Obaluje všechny výjimky z balíčku feed.common.
 */
public class AtomFeedException extends Exception {
    public AtomFeedException(String message) {
        super(message);
    }
}