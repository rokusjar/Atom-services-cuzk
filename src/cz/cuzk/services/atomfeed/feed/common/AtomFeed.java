package cz.cuzk.services.atomfeed.feed.common;

import cz.cuzk.services.atomfeed.config.ColumnNotFoundException;
import cz.cuzk.services.atomfeed.config.InvalidConfigFileException;
import cz.cuzk.services.atomfeed.config.TableNotFoundException;
import cz.cuzk.services.atomfeed.database.DatabaseHandler;
import cz.cuzk.services.atomfeed.database.DriverException;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Třída AtomFeed je myšlena jako základní třída pro konstrukci Atom kanálu.
 * Každá třída jejímž účelem je vytvořit kanál Atom tuto třídu dědí a následně přepisuje abstraktní metodu construct,
 * ve které by celý kanál měl být sestaven.
 * @author Jaromír Rokusek
 * @version 1.0
 * @since 2015-08-23
 */
public abstract class AtomFeed {
    //------------------------------------------------------------------------------------------------------------------
    //DATA MEMBERS
    //------------------------------------------------------------------------------------------------------------------
    Document document = null;
    FeedElement feedElement = null;
    private DatabaseHandler db = null;
    private String datasetCode;
    //------------------------------------------------------------------------------------------------------------------
    //PUBLIC METHODS
    //------------------------------------------------------------------------------------------------------------------
    /**
     * V této metodě by měl být sestaven celý feed. Důvodem je snaha mít vše co feed obsahuje přehledně
     * na jednom místě.
     * @throws DocumentNotCreatedException
     * @throws DOMtoXMLException
     * @throws NullPointerException
     * @throws SecondRootElementException
     * @throws ElementNotValidException
     * @throws LinkElementException
     * @throws AuthorElementException
     * @throws CategoryElementException
     */
    public abstract void construct() throws TableNotFoundException, DriverException,
            InvalidConfigFileException, ColumnNotFoundException, SQLException, AtomFeedException;
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vytvoří nový DOM dokument. Pokud dokument již existuje neudělá nic.
     * Každý objekt třídy AtomFeed pracuje právě s jedním dokumentem.
     * @throws DocumentNotCreatedException když se dokument nepodaří vytvořit.
     */
    protected void createDocument() throws DocumentNotCreatedException{

        if(this.getDocument() != null)
            return;

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            this.setDocument(docBuilder.newDocument());
        }catch(ParserConfigurationException err){
            throw new DocumentNotCreatedException(err.getMessage());
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Transformuje DOM model na XML soubor.
     * @param path Kde má být soubor vytvořen.
     * @param name Jak se má soubor jmenovat. Bez přípony.
     * @throws DOMtoXMLException Když dojde k chybě při transformaci.
     */
    protected void writeXML(String path, String name) throws DOMtoXMLException,
            NullPointerException, SecondRootElementException, ElementNotValidException,
            LinkElementException, AuthorElementException, CategoryElementException{
        try {
            this.getFeedElement().write();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            //Formatovani vystupu
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(this.getDocument());
            StreamResult result = new StreamResult(new File(path + "\\" + name + ".xml"));

            transformer.transform(source, result);
        }catch (TransformerConfigurationException err){
            throw new DOMtoXMLException("AtomFeed#writeXML: doslo k chybe pri transformaci: " + err.getMessage());
        }catch (TransformerException err){
            throw new DOMtoXMLException("AtomFeed#writeXML: doslo k chybe pri transformaci: " + err.getMessage());
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public FeedElement getFeedElement() {
        return feedElement;
    }
    //------------------------------------------------------------------------------------------------------------------
    public void setFeedElement(FeedElement feedElement) {
        this.feedElement = feedElement;
    }
    //------------------------------------------------------------------------------------------------------------------
    public Document getDocument() {
        return document;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getDatasetCode() {
        return datasetCode;
    }

    public void setDatasetCode(String datasetCode) {
        this.datasetCode = datasetCode;
    }

    public DatabaseHandler getDb() {
        return db;
    }

    public void setDb(DatabaseHandler db) {
        this.db = db;
    }
    //------------------------------------------------------------------------------------------------------------------
    //PRIVATE METHODS
    //------------------------------------------------------------------------------------------------------------------
    private void setDocument(Document document) {
        this.document = document;
    }

    /**
     * Vrací aktuální čas ve formátu který je správný pro Atom feed.
     * @return
     */
    //------------------------------------------------------------------------------------------------------------------
    protected String cTime(){
        LocalDateTime cTime = LocalDateTime.now();
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd.MM.YYYY");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HH:mm:ss");
        return cTime.format(formatter1) + "T" + cTime.format(formatter2) + "+01:00";
    }
}
