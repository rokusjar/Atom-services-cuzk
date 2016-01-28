package cz.cuzk.services.atomfeed.feed.common;

/**
 * Created by jaromir.rokusek on 8/19/2015.
 */

/**
 * Vyhozena když je do dokumentu zapisován druhý feed element. Feed element je vždy jen jeden. Je to root.
 */
public class SecondRootElementException extends Exception{

    public SecondRootElementException(String message){
        super(message);
    }
}
