package cz.cuzk.services.atomfeed.database;

/**
 * Vyhozena když není nalezen JDBC driver.
 */
public class DriverException extends Exception {
    public DriverException(String message){
        super(message);
    }
}
