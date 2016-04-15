package cz.cuzk.services.atomfeed.feed.common;

/**
 * Vyhozena když je do dokumentu zapisován druhý feed element. Feed element je vždy jen jeden. Je to root.
 */
public class SecondRootElementException extends Exception{

    public SecondRootElementException(String message){
        super(message);
    }
}
