package cz.cuzk.services.atomfeed.logger;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Můj vlastní looger.
 */
public class MyLogger {

    private PrintWriter pw;
    private String path;
    private String name;
    //------------------------------------------------------------------------------------------------------------------
    /**
     *
     * @param path cesta kde má být log vytvořen
     * @param name i s příponou
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public MyLogger(String path, String name) throws  MyLoggerException{

        this.path = path;
        this.name = name;

        try {
            this.openPrintWriter();
            this.pw.close();

        } catch (FileNotFoundException e) {
            throw new MyLoggerException("Nepodařilo se vytvořit soubor.");
        } catch (UnsupportedEncodingException e) {
            throw new MyLoggerException("Nepodporované kódování.");
        }

    }
    //------------------------------------------------------------------------------------------------------------------
    public void log(Level level, String text){

        String message = cTime().trim() + " " + level.toString().trim() + ": " + text.trim();

        try {
            this.openPrintWriter();
            this.pw.println(message);

        } catch (FileNotFoundException e) {

        } catch (UnsupportedEncodingException e) {

        } finally {
            try{ if(this.pw != null) this.pw.flush(); } catch (Exception err){};
            try{ if(this.pw != null) this.pw.close(); } catch (Exception err){};
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public void log(Level level, String text, Exception e) {

        String message = cTime().trim() + " " + level.toString().trim() + ": " + text.trim() + " EXCEPTION: " + e.toString().trim();

        try {
            this.openPrintWriter();
            this.pw.println(message);
        } catch (FileNotFoundException e1) {

        } catch (UnsupportedEncodingException e1) {

        } finally {
            try{ if(this.pw != null) this.pw.flush(); } catch (Exception err){};
            try{ if(this.pw != null) this.pw.close(); } catch (Exception err){};
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    private void openPrintWriter() throws FileNotFoundException, UnsupportedEncodingException {

        File logDir = new File(this.path);
        if(!logDir.exists()) logDir.mkdirs();

        this.pw = new PrintWriter(new FileOutputStream(new File(path + "\\" + name), true));
    }
    //------------------------------------------------------------------------------------------------------------------
    private String cTime(){
        LocalDateTime cTime = LocalDateTime.now();
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd.MM.YYYY:HH:mm:ss");
        return "[" + cTime.format(formatter1) + "]";
    }
    //------------------------------------------------------------------------------------------------------------------
}
