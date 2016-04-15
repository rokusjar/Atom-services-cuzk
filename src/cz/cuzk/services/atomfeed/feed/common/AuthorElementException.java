package cz.cuzk.services.atomfeed.feed.common;

/**
 * Vyhozena při pokusu o zápis neplatného author elementu.
 */
public class AuthorElementException extends Exception {
    public AuthorElementException(String message){
        super(message);
    }
}
