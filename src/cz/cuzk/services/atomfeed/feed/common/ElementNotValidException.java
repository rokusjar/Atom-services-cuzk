package cz.cuzk.services.atomfeed.feed.common;

/**
 * Vyhozena pokud je zapisován element, který neobsahuje všechny povinné informace.
 * Jeho stav({@link State#values()}) je NOT_VALID.
 */
public class ElementNotValidException extends Exception {
    public ElementNotValidException(String message){
        super(message);
    }
}
