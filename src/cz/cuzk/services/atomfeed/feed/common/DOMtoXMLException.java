package cz.cuzk.services.atomfeed.feed.common;

/**
 * Vyhozena když dojde k chybě při transformaci.
 */
public class DOMtoXMLException extends Exception {
    public DOMtoXMLException(String message){
        super(message);
    }
}
