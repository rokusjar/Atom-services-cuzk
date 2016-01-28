package cz.cuzk.services.atomfeed.feed.common;

/**
 * Created by jaromir.rokusek on 8/17/2015.
 */
//----------------------------------------------------------------------------------------------------------------------

/**
 * Vyhozena pokud je zapisován element, který neobsahuje všechny povinné informace.
 * Jeho stav({@link State#values()}) je NOT_VALID.
 */
public class ElementNotValidException extends Exception {
    public ElementNotValidException(String message){
        super(message);
    }
}
