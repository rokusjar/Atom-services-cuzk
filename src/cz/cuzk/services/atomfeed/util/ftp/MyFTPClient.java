package cz.cuzk.services.atomfeed.util.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jaromir Rokusek
 * @version 1.0
 *
 * Trida slouží jako jednoduchý FTP klient.
 * Pro nahrání dat na FTP server slouží funkce upload. Pro mazání dat slouží funkce deleteFile.
 */
public class MyFTPClient{

    private FTPClient ftpClient = null;
    //------------------------------------------------------------------------------------------------------------------
    public MyFTPClient(){}
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Nahraje soubor na FTP server. Nahraný soubor bude mít svůj název.
     * Pokud soubor existuje tak je přepsán.
     * @param localFile soubor který má být nahrán
     * @param remotePath cesta na ftp serveru kam ma být soubor uložen. Může být relativní.
     * @return True pokud funkce doběhne bez problému, jinak False
     * @throws IOException
     * @throws cz.rokusek.util.ftp.FTPException kdyz se operace nepodaří - storeFile vratí false
     */
    public void upload(File localFile, String remotePath) throws IOException, FTPException {

        if(!this.isConnected()) {
            throw new FTPException("FTP server neni pripojen");
        }

        if(remotePath == null || remotePath.equals("")){
            throw new FTPException("Promenna remotePath je null nebo prazdny String.");
        }

        if(localFile == null){
            throw new FTPException("Promenna localFile je null");
        }

        InputStream inputStream = null;
        boolean done = false;
        String remoteFileName = null;
        try {
            inputStream = new FileInputStream(localFile);
            remoteFileName = localFile.getName();
            done = this.ftpClient.storeFile(remotePath + "/" + remoteFileName, inputStream);
        }
        finally {
            try{ if(inputStream != null) inputStream.close(); }catch (IOException e){};
        }
        if(done) {
            System.out.println("Soubor " +  remoteFileName  + " nahran do: " + remotePath + "/" + remoteFileName);
        }else{
            throw new FTPException("Soubor se nepodarilo nahrat");
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Nahraje soubor na FTP server. Nahraný soubor bude mít název remoteFileName.
     * Pokud soubor existuje tak je přepsán.
     * @param localFile soubor který má být nahrán
     * @param remoteFileName název nahraného souboru
     * @param remotePath cesta na ftp serveru kam má být soubor ulozen. Může být relativní
     * @return True pokud funkce doběhne bez problému, jinak False
     * @throws IOException
     * @throws cz.rokusek.util.ftp.FTPException kdyz se operace nepodari - storeFile vrátí false
     */
    public void upload(File localFile, String remoteFileName, String remotePath) throws IOException, FTPException {

        if(!this.isConnected()) {
            throw new FTPException("FTP server neni pripojen");
        }

        if(remotePath == null || remotePath.equals("")){
            throw new FTPException("Promenna remotePath je null nebo prazdny String.");
        }

        if(localFile == null){
            throw new FTPException("Promenna localFile je null");
        }

        InputStream inputStream = null;
        boolean done = false;
        try {
            inputStream = new FileInputStream(localFile);
            done = this.ftpClient.storeFile(remotePath + "/" + remoteFileName, inputStream);
        }
        finally {
            try{ if(inputStream != null) inputStream.close(); }catch (IOException e){};
        }
        if(done) {
            System.out.println("Soubor " +  remoteFileName  + " nahran do: " + remotePath + "/" + remoteFileName);
        }else{
            throw new FTPException("Soubor se nepodarilo nahrat");
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vytvoří na FTP serveru adresář.
     * @param dirName název adresáře
     * @param remotePath cesta kam má být adresář vytvořen - bez posledního lomítka
     * @return
     * @throws IOException
     * @throws cz.rokusek.util.ftp.FTPException když se operace nepodaří - makeDirectory vrátí false
     */
    public void makeDirectory(String dirName, String remotePath) throws IOException, FTPException {

        if(!this.isConnected()) {
            throw new FTPException("FTP server neni pripojen");
        }

        if(remotePath == null || remotePath.equals("")){
            throw new FTPException("Promenna remotePath je null nebo prazdny String.");
        }

        if(dirName == null || dirName.equals("")){
            throw new FTPException("Promenna dirName je null nebo prazdny String.");
        }

        boolean done = false;
        done = this.ftpClient.makeDirectory(remotePath + "/" + dirName);

        if(done) {
            System.out.println("Adresar " +  dirName  + " vytvoren. Umisteni: " + remotePath + "/" + dirName);
        }else{
            throw  new FTPException("Adresar se nepodarilo vytvorit");
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Odtsraní z FTP serveru adresář.
     * @param pathName
     * @throws FTPException když funkce removeDirectory vrátí False.
     * @throws IOException
     */
    public void removeDirectory(String pathName) throws FTPException, IOException {

        if(!this.isConnected()) {
            throw new FTPException("FTP server neni pripojen");
        }

        if(pathName == null || pathName.equals("")){
            throw new FTPException("Promenna pathName je null nebo prazdny String.");
        }

        boolean done = false;
        done = this.ftpClient.removeDirectory(pathName);

        if(done) {
            System.out.println("Adresar " +  pathName  + " smazan z FTP serveru");
        }else{
            throw  new FTPException("Adresar se nepodarilo smazat");
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrací true pokud adresář určený cestou fullPathName na FTP serveru existuje.
     * @param fullPathName
     * @return
     * @throws IOException
     */
    public boolean directoryExist(String fullPathName) throws IOException {
        boolean exist = false;
        String currentWorkingDirectory = this.ftpClient.printWorkingDirectory();

        if(this.ftpClient.changeWorkingDirectory(fullPathName)){
            exist = true;
            this.ftpClient.changeWorkingDirectory(currentWorkingDirectory);
        }
        return exist;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí aktuální pracovní adresář FTP serveru.
     * @return
     * @throws IOException
     * @throws FTPException
     */
    public String printWorkingDirectory() throws IOException, FTPException {
        if(!this.isConnected()) {
            throw new FTPException("FTP server neni pripojen");
        }
        return this.ftpClient.printWorkingDirectory();
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Změní pracovní adresář na FTp serveru.
     * @param pathName
     * @throws FTPException
     * @throws IOException
     */
    public void changeWorkingDirectory(String pathName) throws FTPException, IOException {
        if(!this.isConnected()) {
            throw new FTPException("FTP server neni pripojen");
        }

        if(pathName == null || pathName.equals("")){
            throw new FTPException("Promenna pathName je null nebo prazdny String.");
        }

        boolean done = this.ftpClient.changeWorkingDirectory(pathName);

        if(done) {
            System.out.println("Pracovni adresar zmenen na: " +  pathName);
        }else{
            throw  new FTPException("Pracovni adresar se nepodarilo zmenit - pozadavoana cesta: " + pathName);
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Smaže soubor z FTP serveru
     * @param pathname cesta k souboru ktery ma byt smazán, může být relativní.
     * @return True pokud funkce doběhne bez problému, jinak False
     * @throws IOException
     * @throws cz.rokusek.util.ftp.FTPException kdyz se operace nepodari - deleteFile vrati false
     */
    public void deleteFile(String pathname) throws IOException, FTPException {

        if(!this.isConnected()) {
            throw new FTPException("FTP server neni pripojen");
        }

        if(pathname == null || pathname.equals("")){
            throw new FTPException("Promenna pathname je null nebo prazdny String.");
        }

        boolean done = false;
        done = this.ftpClient.deleteFile(pathname);

        if(done) {
            System.out.println("Soubor " +  pathname  + " smazan.");
        }else{
            throw new FTPException("Soubor se nepodarilo smazat");
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Připojí se k FTP serveru.
     * Pro připojení použije port 21.
     * Nastaví pasivní mód a typ prenášených souborů na FTP.BINARY_FILE_TYPE což znamená libovolný typ souboru.
     * @return
     * @throws IOException Pokud se nepodaří otevřít socket, nebo rozpoznat host.
     */
    public void connect(String host, String user, String password) throws IOException{

        if(this.ftpClient != null) this.ftpClient.disconnect();

        this.ftpClient = new FTPClient();
        this.ftpClient.connect(host);
        this.ftpClient.login(user, password);
        this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        this.ftpClient.enterLocalPassiveMode();
        System.out.println("FTP server: " + host + " pripojen");
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Připojí se k FTP serveru.
     * Nastaví pasivní mód a typ prenášených souborů na FTP.BINARY_FILE_TYPE což znamená libovolný typ souboru.
     * @return
     * @throws IOException Pokud se nepodaří otevřít socket, nebo rozpoznat host.
     */
    public void connect(String host, String user, String password, Integer port) throws IOException{

        if(this.ftpClient != null) this.ftpClient.disconnect();

        this.ftpClient = new FTPClient();
        this.ftpClient.connect(host, port.intValue());
        this.ftpClient.login(user, password);
        this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        this.ftpClient.enterLocalPassiveMode();
        System.out.println("FTP server: " +  host  + " pripojen");
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Odpojí se od FTP serveru.
     * @throws IOException
     */
    public void disconnect() throws IOException {
        if(this.ftpClient != null) {
            if (this.ftpClient.isConnected()) {
                this.ftpClient.logout();
                this.ftpClient.disconnect();
            }
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vratí true pokud je FTP server připojen.
     * @return
     */
    public boolean isConnected(){
        if(this.ftpClient != null){
            return this.ftpClient.isConnected();
        }else{
            return false;
        }
    }
    //------------------------------------------------------------------------------------------------------------------
}
