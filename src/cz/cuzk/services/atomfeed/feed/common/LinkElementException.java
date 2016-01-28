package cz.cuzk.services.atomfeed.feed.common;

/**
 * Created by jaromir.rokusek on 8/27/2015.
 */

/**
 * Vyhozena při pokusu o zápis neplatného link elementu.
 */
public class LinkElementException extends Exception {
    public LinkElementException(String message){
        super(message);
    }
}
