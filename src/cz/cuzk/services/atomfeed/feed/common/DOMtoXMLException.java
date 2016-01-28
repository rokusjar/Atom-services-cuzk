package cz.cuzk.services.atomfeed.feed.common;

/**
 * Created by jaromir.rokusek on 8/23/2015.
 */

/**
 * Vyhozena když dojde k chybě při transformaci.
 */
public class DOMtoXMLException extends Exception {
    public DOMtoXMLException(String message){
        super(message);
    }
}
