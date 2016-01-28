package cz.cuzk.services.atomfeed.config;

/**
 * Vyhozena kdyz v konfiguracnim souboru neni nalezeno schema nektere tabulky.
 * Tabulky jsou hledany podle atributu type.
 */
public class TableNotFoundException extends Exception{
    public TableNotFoundException(String message){
        super(message);
    }
}
