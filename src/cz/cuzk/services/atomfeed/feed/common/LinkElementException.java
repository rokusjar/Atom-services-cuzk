package cz.cuzk.services.atomfeed.feed.common;

/**
 * Vyhozena při pokusu o zápis neplatného link elementu.
 */
public class LinkElementException extends Exception {
    public LinkElementException(String message){
        super(message);
    }
}
