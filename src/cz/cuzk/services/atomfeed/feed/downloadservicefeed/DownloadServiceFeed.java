package cz.cuzk.services.atomfeed.feed.downloadservicefeed;

import cz.cuzk.services.atomfeed.config.*;
import cz.cuzk.services.atomfeed.database.DatabaseHandler;
import cz.cuzk.services.atomfeed.database.DriverException;
import cz.cuzk.services.atomfeed.feed.common.*;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Třída pro generování hlavních kanálů stahovacích služeb Atom.
 */
public class DownloadServiceFeed extends AtomFeed{

    private String serviceId;
    //------------------------------------------------------------------------------------------------------------------
    public DownloadServiceFeed(String serviceId, DatabaseHandler db){
        this.serviceId = serviceId;
        this.setDb(db);
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void construct() throws TableNotFoundException, DriverException,
            InvalidConfigFileException, ColumnNotFoundException, SQLException, AtomFeedException {
        try {
            this.createDocument();

            FeedElement feedElement = new FeedElement(this.getDocument());
            this.setFeedElement(feedElement);

            ConfigReader cfReader = new ConfigReader();
            cfReader.read();
            Config config = cfReader.getConfigData();

            feedElement.setTitle(this.getDb().getMainFeedTitle(serviceId));
            feedElement.setSubtitle(this.getDb().getMainFeedSubtitle(serviceId)); //abstrakt
            feedElement.setId(this.getDb().getMainFeedid(serviceId));
            feedElement.setRights(this.getDb().getMainFeedRights(serviceId));

            Author author = new Author();
            author.setName(this.getDb().getMainFeedAuthorName(serviceId));
            author.setEmail(this.getDb().getMainFeedAuthorEmail(serviceId));
            author.setUri(this.getDb().getMainFeedAuthorUri(serviceId));

            feedElement.addAuthor(author);

            Link metadata = new Link(LinkType.MAIN_FEED_METADATA);
            metadata.setTitle("metadata stahovací služby");
            metadata.setHref(this.getDb().getMainFeedMetadataLink(serviceId));

            Link openSearch = new Link(LinkType.OPENSEARCH);
            openSearch.setTitle("ČÚZK-Atom-" + serviceId);
            openSearch.setHref(this.getDb().getMainFeedOpenSearchLink(serviceId));
            openSearch.setHreflang("cs");

            Link self = new Link(LinkType.SELF);
            self.setHref(this.getDb().getMainFeedid(serviceId));
            self.setHreflang("cs");

            feedElement.addLink(metadata);
            feedElement.addLink(openSearch);
            feedElement.addLink(self);

            //ENTRY
            for (String datasetCode : this.getDb().getDatasetCodes(serviceId)) {

                EntryElement entry = new EntryElement(this.getDocument());
                feedElement.addEntry(entry);

                entry.setTitle(this.getDb().getDatasetTitle(datasetCode));
                entry.setId(this.getDb().getDatasetId(datasetCode));
                entry.setUpdated(this.getDb().getDatasetUpdated(datasetCode));
                entry.addAuthor(author);
                entry.setRights(this.getDb().getMainFeedRights(serviceId));
                entry.setInspireDLS(this.getDb().getDatasetSpatialCode(datasetCode), this.getDb().getDatasetSpatialNamespace(datasetCode));

                entry.setGeorssPolygon(getDb().getBBox(datasetCode).trim());

                ArrayList<String> epsgList = this.getDb().getEpsgList(datasetCode);

                for (String epsg : epsgList) {
                    Category cat = new Category();
                    cat.setTerm("http://www.opengis.net/def/crs/EPSG/0/" + epsg);
                    switch (epsg) {
                        case "5514":
                            cat.setLabel("S-JTSK");
                            break;
                        case "4258":
                            cat.setLabel("ETRS89");
                            break;
                        case "stabilni_katastr":
                            cat.setTerm("https://cs.wikipedia.org/wiki/Stabilní_katastr");
                            cat.setLabel("Stabilní katastr");
                            break;
                        default:
                            cat.setLabel("Neznámá");
                            break;
                    }
                    entry.addCategory(cat);
                }

                Link datasetMetadata = new Link(LinkType.DATASET_METADATA);
                datasetMetadata.setTitle("metadata datasetu");
                datasetMetadata.setHref(this.getDb().getDatasetMetadataLink(datasetCode));

                Link datasetFeed = new Link(LinkType.DATASET_FEED);
                datasetFeed.setTitle("dataset feed");
                datasetFeed.setHref(this.getDb().getDatasetId(datasetCode));

                entry.addLink(datasetMetadata);
                entry.addLink(datasetFeed);
            }

            this.getDb().setMainFeedUpdatedToCTime(serviceId);
            feedElement.setUpdated(getDb().getMainFeedUpdated(serviceId));

            String targetPath = config.getRepository().getTempRepository();
            String feedName = serviceId;

            this.writeXML(targetPath, feedName);

        } catch (CategoryElementException e) {
            e.printStackTrace();
            throw new AtomFeedException("Chyba při generování hlavního feedu [" + this.getDatasetCode()
                    + "]:" + e.getMessage());
        } catch (ElementNotValidException e) {
            e.printStackTrace();
            throw new AtomFeedException("Chyba při generování hlavního feedu [" + this.getDatasetCode()
                    + "]:" + e.getMessage());
        } catch (LinkElementException e) {
            e.printStackTrace();
            throw new AtomFeedException("Chyba při generování hlavního feedu [" + this.getDatasetCode()
                    + "]:" + e.getMessage());
        } catch (DOMtoXMLException e) {
            e.printStackTrace();
            throw new AtomFeedException("Chyba při generování hlavního feedu [" + this.getDatasetCode()
                    + "]:" + e.getMessage());
        } catch (SecondRootElementException e) {
            e.printStackTrace();
            throw new AtomFeedException("Chyba při generování hlavního feedu [" + this.getDatasetCode()
                    + "]:" + e.getMessage());
        } catch (DocumentNotCreatedException e) {
            e.printStackTrace();
            throw new AtomFeedException("Chyba při generování hlavního feedu [" + this.getDatasetCode()
                    + "]:" + e.getMessage());
        } catch (AuthorElementException e) {
            e.printStackTrace();
            throw new AtomFeedException("Chyba při generování hlavního feedu [" + this.getDatasetCode()
                    + "]:" + e.getMessage());
        } catch (NullPointerException e){
            e.printStackTrace();
            throw new AtomFeedException("Chyba při generování hlavního feedu [" + this.getDatasetCode()
                    + "]:" + e.getMessage());
        }
    }
}
