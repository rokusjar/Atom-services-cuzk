package cz.cuzk.services.atomfeed.config;

/**
 * Vyhozena když v konfiguračním souboru není nalezeno schéma některé tabulky.
 * Tabulky jsou hledány podle atributu type.
 */
public class TableNotFoundException extends Exception{
    public TableNotFoundException(String message){
        super(message);
    }
}
