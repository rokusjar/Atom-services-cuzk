package cz.cuzk.services.atomfeed.util.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rokusekj on 9.2.2016.
 *
 *
 * @author Jaromir Rokusek
 * @version 1.0
 *
 * Trida slouzi jako jednoduchy FTP klient. Verze 1.0 umi nahravat a mazat soubory.
 * pro nahrani slouzi funkce upload. Pro mazani funkce deleteFile.
 */
public class MyFTPClient{

    private FTPClient ftpClient = null;
    //------------------------------------------------------------------------------------------------------------------
    public MyFTPClient(){}
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Nahraje soubor na FTP server. Nahrany soubor bude mit svuj nazev.
     * Pokud soubor existuje tak je prepsan.
     * @param localFile soubor ktery ma byt nahran
     * @param remotePath cesta na ftp serveru kam ma byt soubor ulozen. Muze byt relativni
     * @return True if successfully completed, false if not.
     * @throws IOException
     * @throws cz.rokusek.util.ftp.FTPException kdyz se operace nepodari - storeFile vrati false
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
     * Nahraje soubor na FTP server. Nahrany soubor bude mit nazev remoteFileName.
     * Pokud soubor existuje tak je prepsan.
     * @param localFile soubor ktery ma byt nahran
     * @param remoteFileName nazev nahraneho souboru
     * @param remotePath cesta na ftp serveru kam ma byt soubor ulozen. Muze byt relativni
     * @return True if successfully completed, false if not.
     * @throws IOException
     * @throws cz.rokusek.util.ftp.FTPException kdyz se operace nepodari - storeFile vrati false
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
     * Vytvori na FTP serveru adresar.
     * @param dirName nazev adresare
     * @param remotePath cesta kam ma byt vytvoren - bez posledniho lomitka
     * @return
     * @throws IOException
     * @throws cz.rokusek.util.ftp.FTPException kdyz se operace nepodari - makeDirecotry vrati false
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
     * Odtsrani z FTP serveru adresar
     * @param pathName
     * @throws FTPException kdyz funkce removeDirectory vrati false
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
     * Vraci true pokud adresar urceny cestou fullPathName na FTP serveru existuje
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
     * Vrati aktualni pracovni adresar FTP serveru.
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
     * Smaze soubor z FTP serveru
     * @param pathname cesta k souboru ktery ma byt smazan, muze byt relativni
     * @return True if successfully completed, false if not.
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
     * Pripoji se k FTP serveru.
     * Pokud je zadan port tak ho pouzije. Pokud neni zadan port pouzije port 21.
     * Nastavi pasivni mod a typ prenasenych souboru na FTP.BINARY_FILE_TYPE coz znamena libovolny typ souboru.
     * @return
     * @throws IOException If the socket could not be opened or if the hostname cannot be resolved.
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
     * Pripoji se k FTP serveru.
     * Pokud je zadan port tak ho pouzije. Pokud neni zadan port pouzije port 21.
     * Nastavi pasivni mod a typ prenasenych souboru na FTP.BINARY_FILE_TYPE coz znamena libovolny typ souboru.
     * @return
     * @throws IOException If the socket could not be opened or if the hostname cannot be resolved.
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
     * Odpoji se od FTP serveru.
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
     * Vrati true pokud je FTP server pripojen
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
