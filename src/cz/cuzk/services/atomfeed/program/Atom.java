package cz.cuzk.services.atomfeed.program;

import cz.cuzk.services.atomfeed.config.*;
import cz.cuzk.services.atomfeed.database.DriverException;
import cz.cuzk.services.atomfeed.feed.common.AtomFeedException;
import cz.cuzk.services.atomfeed.keeper.UnknownDatasetException;
import cz.cuzk.services.atomfeed.keeper.Updater;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

//----------------------------------------------------------------------------------------------------------------------
public class Atom {
    private static Logger logger = null;
    private static FileHandler fh = null;
    private static URL location = Atom.class.getProtectionDomain().getCodeSource().getLocation();
    private static final Boolean JAR = true; //pokud chci debugovat tak false
    //------------------------------------------------------------------------------------------------------------------
    public static void main(String[] args){
        Updater updater = null;

        try {
            //Aby program nemohl bezet vicekrat
            if(!isRunning()){
                createRunningFile();
            }else {
                System.out.println("PROGRAM ATOM UZ JE JEDNOU SPUSTEN");
                System.exit(0);
            }

            System.out.println("Program atom spusten.");

            createLogDir(JAR);
            logger = Logger.getAnonymousLogger();
            fh = createFileHandlerForLogging(JAR);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.addHandler(fh);

            logger.info("Program Atom spuštěn");

            updater = new Updater(logger);
            updater.initialize();
            updater.update();

            logger.info("Program Atom ukončen");
            deleteRunningFile();

        } catch (InvalidConfigFileException e) {
            logger.log(Level.SEVERE, "Chyba při čtění konfiguračního souboru", e);
        } catch (DriverException e) {
            logger.log(Level.SEVERE, "JDBC Driver nenalezen", e);
        } catch (TableNotFoundException e) {
            logger.log(Level.SEVERE, "V konfiguračním souboru chybí definice tabulky", e);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL chyba", e);
        } catch (ColumnNotFoundException e) {
            logger.log(Level.SEVERE, "V konfiguračním souboru chybí definice sloupce", e);
        } catch (UnknownDatasetException e) {
            logger.log(Level.SEVERE, "Neznámý dataset", e);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Mapování disků bylo přerušeno.", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Nepodařilo se nalézt nebo vytvořit soubor.", e);
        } catch (AtomFeedException e) {
            logger.log(Level.SEVERE, "Chyba při sestavení feedu.", e);
        }  catch (Exception e) {
            logger.log(Level.SEVERE, "Neznámá chyba", e);
        }
        finally {
            try{ if(updater.getConn() != null) updater.getConn().rollback(); } catch (Exception e){}
            try{ if(updater.getConn() != null) updater.getConn().close(); } catch (Exception e){}
            try{ if(fh != null) fh.close(); } catch (Exception e){}
            deleteRunningFile();
            System.out.println("Program atom ukoncen.");
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí aktuální čas
     * @return
     */
    private static String cTime(){
        LocalDateTime cTime = LocalDateTime.now();
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("YYYY-MM-dd-HH-mm-ss");
        return cTime.format(formatter1);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí aktuální datum
     * @return
     */
    private static String cDate(){
        LocalDateTime cTime = LocalDateTime.now();
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("YYYY-MM-dd");
        return cTime.format(formatter1);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Pokud adresar logs neexistuje, tak ho vytvori
     * @param jar nastavit na true pokud bude program spousten z jaru, pokud bude spousten jinak tak false
     */
    private static void createLogDir(Boolean jar){
        if(jar) {
            File logDir = new File(getJarLocation() + "logs\\" + cDate());
            if(!logDir.exists()){
                logDir.mkdirs();
            }
        }else{
            File logDir = new File("logs\\" + cDate());
            if(!logDir.exists()){
                logDir.mkdirs();
            }
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vytvori file handler pro logger
     * @param jar nastavit na true pokud bude program spousten z jaru, pokud bude spousten jinak tak false
     */
    private static FileHandler createFileHandlerForLogging(Boolean jar) throws IOException {
        FileHandler fh = null;
        if(jar) {
            fh = new FileHandler(getJarLocation() + "logs\\" + cDate() + "\\" + "atom_" + cTime() + ".log");
        }else{
            fh = new FileHandler("logs\\" + cDate() + "\\" + "atom_" + cTime() + ".log");
        }
        return fh;
    }
    //------------------------------------------------------------------------------------------------------------------
    private static String getJarLocation(){

        String jarLocation = "";
        String[] peaces = location.getPath().split("/");

        for(int i = 1; i < peaces.length - 1; i++){
            jarLocation += "/" + peaces[i];
        }

        return jarLocation.substring(1) + "\\";
    }
    //------------------------------------------------------------------------------------------------------------------
    private static void createRunningFile() throws IOException {
        File runningFile = new File("running.log");
        //File runningFile = new File(getJarLocation() + "running.log");
        runningFile.createNewFile();
    }
    //------------------------------------------------------------------------------------------------------------------
    private static void deleteRunningFile(){
        File runningFile = new File("running.log");
        //File runningFile = new File(getJarLocation() + "running.log");
        if(runningFile.exists()){
            runningFile.delete();
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    private static boolean isRunning(){
        Boolean result = true;
        File runningFile = new File("running.log");
        //File runningFile = new File(getJarLocation() + "running.log");

        if(runningFile.exists()){
            result = true;
        }else {
            result = false;
        }
        return result;
    }
    //------------------------------------------------------------------------------------------------------------------
}
