package cz.cuzk.services.atomfeed.config;

/**
 * Vyhozena když dojde k chybě při čtení konfiguračního souboru. (config.json)
 * @author Jaromír Rokusek
 * @version 1.0
 * @since 2015-08-23
 */
public class InvalidConfigFileException extends Exception {
    public InvalidConfigFileException(String message){
        super(message);
    }
}
