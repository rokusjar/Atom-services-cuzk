package cz.cuzk.services.atomfeed.feed.common;

/**
 * Created by jaromir.rokusek on 8/28/2015.
 */

/**
 * Vyhozena při pokusu o zápis neplatného category elementu.
 */
public class CategoryElementException extends Exception{
    public CategoryElementException(String message){
        super(message);
    }
}
