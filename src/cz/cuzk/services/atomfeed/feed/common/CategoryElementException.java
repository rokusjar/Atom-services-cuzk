package cz.cuzk.services.atomfeed.feed.common;

/**
 * Vyhozena při pokusu o zápis neplatného category elementu.
 */
public class CategoryElementException extends Exception{
    public CategoryElementException(String message){
        super(message);
    }
}
