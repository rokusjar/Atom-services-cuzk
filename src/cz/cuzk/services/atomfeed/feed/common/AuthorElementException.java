package cz.cuzk.services.atomfeed.feed.common;

/**
 * Created by jaromir.rokusek on 8/28/2015.
 */

/**
 * Vyhozena při pokusu o zápis neplatného author elementu.
 */
public class AuthorElementException extends Exception {
    public AuthorElementException(String message){
        super(message);
    }
}
