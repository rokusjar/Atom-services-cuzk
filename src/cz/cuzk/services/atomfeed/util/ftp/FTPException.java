package cz.cuzk.services.atomfeed.util.ftp;

/**
 * Výjimka je vyhozena pokud dojde k chybě při použití třídy {@link cz.cuzk.services.atomfeed.util.ftp.MyFTPClient}.
 * @author Jaromir Rokusek
 */
public class FTPException extends Exception {
    public FTPException(String message) {
        super(message);
    }
}