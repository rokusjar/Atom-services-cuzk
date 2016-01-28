package cz.cuzk.services.atomfeed.database;

/**
 * Vyhozena kdyz neni nalezen SQLite JDBC driver
 */
public class DriverException extends Exception {
    public DriverException(String message){
        super(message);
    }
}
