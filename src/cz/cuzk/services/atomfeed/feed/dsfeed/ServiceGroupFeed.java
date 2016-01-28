package cz.cuzk.services.atomfeed.feed.dsfeed;

import cz.cuzk.services.atomfeed.config.*;
import cz.cuzk.services.atomfeed.database.DatabaseHandler;
import cz.cuzk.services.atomfeed.database.DriverException;
import cz.cuzk.services.atomfeed.feed.common.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class ServiceGroupFeed extends AtomFeed{

    private String title;
    private String fileName;
    private ArrayList<String> serviceCodes;
    private Logger logger;
    //------------------------------------------------------------------------------------------------------------------
    public ServiceGroupFeed(String fileName, String title, ArrayList<String> serviceCodes, DatabaseHandler db,
                            Logger logger){
        this.title = title;
        this.fileName = fileName;
        this.serviceCodes = serviceCodes;
        this.setDb(db);
        this.logger = logger;
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void construct() throws TableNotFoundException, DriverException, InvalidConfigFileException,
            ColumnNotFoundException, SQLException, AtomFeedException {

        try{
            this.createDocument();
            FeedElement feedElement = new FeedElement(this.getDocument());
            this.setFeedElement(feedElement);

            ConfigReader cfReader = new ConfigReader();
            cfReader.read();
            Config config = cfReader.getConfigData();

            feedElement.setTitle(title);
            feedElement.setUpdated(cTime());
            feedElement.setId(config.getRepository().getWebPath() + "/" + this.fileName + ".xml");

            for(String code : serviceCodes){

                EntryElement entry = new EntryElement(this.getDocument());
                feedElement.addEntry(entry);

                entry.setId(config.getRepository().getWebPath() + "/" + code + "/" + code + ".xml");
                entry.setTitle(getDb().getMainFeedTitle(code));
                entry.setUpdated(getDb().getMainFeedUpdated(code));

                Link serviceFeedLink = new Link(LinkType.SIMPLE);
                serviceFeedLink.setHref(config.getRepository().getWebPath() + "/" + code + "/" + code + ".xml");

                entry.addLink(serviceFeedLink);
            }

            String targetPath = config.getRepository().getTempRepository();
            String feedName = this.fileName;

            this.writeXML(targetPath, feedName);

        } catch (DocumentNotCreatedException e) {
            logger.log(Level.SEVERE, "Chyba při generování skupinového feedu [" + this.getTitle()
                    + "]:", e);
        } catch (ElementNotValidException e) {
            logger.log(Level.SEVERE, "Chyba při generování skupinového feedu [" + this.getTitle()
                    + "]:", e);
        } catch (AuthorElementException e) {
            logger.log(Level.SEVERE, "Chyba při generování skupinového feedu [" + this.getTitle()
                    + "]:", e);
        } catch (LinkElementException e) {
            logger.log(Level.SEVERE, "Chyba při generování skupinového feedu [" + this.getTitle()
                    + "]:", e);
        } catch (CategoryElementException e) {
            logger.log(Level.SEVERE, "Chyba při generování skupinového feedu [" + this.getTitle()
                    + "]:", e);
        } catch (DOMtoXMLException e) {
            logger.log(Level.SEVERE, "Chyba při generování skupinového feedu [" + this.getTitle()
                    + "]:", e);
        } catch (SecondRootElementException e) {
            logger.log(Level.SEVERE, "Chyba při generování skupinového feedu [" + this.getTitle()
                    + "]:", e);
        } catch (Exception e){
            logger.log(Level.SEVERE, "Chyba při generování skupinového feedu [" + this.getTitle()
                    + "]:", e);
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getServiceCodes() {
        return serviceCodes;
    }

    public void setServiceCodes(ArrayList<String> serviceCodes) {
        this.serviceCodes = serviceCodes;
    }
}
