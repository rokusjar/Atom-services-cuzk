package cz.cuzk.services.atomfeed.feed.common;

import javax.xml.parsers.DocumentBuilder;

/**
 * Vyhozena když dojde k chybě při vytvoření dokumentu pomocí metody {@link DocumentBuilder#newDocument()}
 */
public class DocumentNotCreatedException extends Exception {
    public DocumentNotCreatedException(String message){
        super(message);
    }
}
