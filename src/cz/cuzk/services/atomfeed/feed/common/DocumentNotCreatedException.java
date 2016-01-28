package cz.cuzk.services.atomfeed.feed.common;

/**
 * Created by jaromir.rokusek on 8/23/2015.
 */

import javax.xml.parsers.DocumentBuilder;

/**
 * Vyhozena když dojde k chybě při vytvoření dokumentu pomocí metody {@link DocumentBuilder#newDocument()}
 */
public class DocumentNotCreatedException extends Exception {
    public DocumentNotCreatedException(String message){
        super(message);
    }
}
