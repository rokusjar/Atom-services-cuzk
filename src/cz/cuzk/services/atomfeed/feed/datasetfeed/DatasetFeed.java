package cz.cuzk.services.atomfeed.feed.datasetfeed;

import cz.cuzk.services.atomfeed.config.*;
import cz.cuzk.services.atomfeed.database.DatabaseHandler;
import cz.cuzk.services.atomfeed.database.DriverException;
import cz.cuzk.services.atomfeed.feed.common.*;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Třída pro generování datasetových kanálů stahovacích služeb Atom.
 */
public class DatasetFeed extends AtomFeed{

    private String serviceCode;

    public DatasetFeed(String datasetCode, String serviceCode, DatabaseHandler db)
    {
        this.setDatasetCode(datasetCode);
        this.serviceCode = serviceCode;
        this.setDb(db);
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void construct() throws TableNotFoundException, DriverException,
            InvalidConfigFileException, ColumnNotFoundException, SQLException, AtomFeedException {

        try {
            String datasetCode = this.getDatasetCode();
            ConfigReader cfReader = new ConfigReader();
            cfReader.read();
            Config config = cfReader.getConfigData();

            this.createDocument();

            FeedElement feedElement = new FeedElement(this.getDocument());
            this.setFeedElement(feedElement);

            feedElement.setTitle(this.getDb().getDatasetTitle(datasetCode));
            feedElement.setId(this.getDb().getDatasetId(datasetCode));
            feedElement.setRights(this.getDb().getMainFeedRights(serviceCode));

            //aby odpovidalo datum v databazi datumu ve feedu
            this.getDb().setDatasetUpdatedToCTime(datasetCode);
            feedElement.setUpdated(this.getDb().getDatasetUpdated(datasetCode));

            Author author = new Author();
            author.setName(this.getDb().getMainFeedAuthorName(serviceCode));
            author.setEmail(this.getDb().getMainFeedAuthorEmail(serviceCode));
            author.setUri(this.getDb().getMainFeedAuthorUri(serviceCode));
            feedElement.addAuthor(author);

            Link parent = new Link(LinkType.PARENT);
            parent.setHref(this.getDb().getMainFeedid(serviceCode));
            parent.setHreflang("cs");
            feedElement.addLink(parent);

            Link spatialObject = new Link(LinkType.INSPIRE_REGISTRY);
            spatialObject.setHref(spatialObjectLink(serviceCode));
            spatialObject.setHreflang("cs");
            feedElement.addLink(spatialObject);

            ArrayList<DatasetFile> files = this.getDb().getFilesData(datasetCode);

            for (DatasetFile file : files) {

                EntryElement entry = new EntryElement(this.getDocument());
                this.getFeedElement().addEntry(entry);

                entry.setTitle(getDb().createFileTitle(datasetCode, file));
                entry.setId(file.getWebPath());
                entry.setUpdated(file.getUpdated());

                Category cat = new Category();
                cat.setTerm("http://www.opengis.net/def/crs/EPSG/0/" + file.getCrs_epsg());
                switch (file.getCrs_epsg()) {
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

                Link data = new Link(LinkType.DATA);
                data.setHref(file.getWebPath());
                data.setLenght(file.getFile_size());
                data.setType(mediaType(file.getFormat()));
                entry.addLink(data);
            }

            String targetPath = config.getRepository().getTempRepository();
            String feedName = datasetCode;

            this.writeXML(targetPath, feedName);
        }
        catch (CategoryElementException e) {
            throw new AtomFeedException("Chyba při generování dataset feedu [" + this.getDatasetCode()
                    + "]:" + e.getMessage());
        } catch (ElementNotValidException e) {
            throw new AtomFeedException("Chyba při generování dataset feedu [" + this.getDatasetCode()
                    + "]:" + e.getMessage());
        } catch (LinkElementException e) {
            throw new AtomFeedException("Chyba při generování dataset feedu [" + this.getDatasetCode()
                    + "]:" + e.getMessage());
        } catch (DOMtoXMLException e) {
            throw new AtomFeedException("Chyba při generování dataset feedu [" + this.getDatasetCode()
                    + "]:" + e.getMessage());
        } catch (SecondRootElementException e) {
            throw new AtomFeedException("Chyba při generování dataset feedu [" + this.getDatasetCode()
                    + "]:" + e.getMessage());
        } catch (DocumentNotCreatedException e) {
            throw new AtomFeedException("Chyba při generování dataset feedu [" + this.getDatasetCode()
                    + "]:" + e.getMessage());
        } catch (AuthorElementException e) {
            throw new AtomFeedException("Chyba při generování dataset feedu [" + this.getDatasetCode()
                    + "]:" + e.getMessage());
        } catch (NullPointerException e){
            throw new AtomFeedException("Chyba při generování dataset feedu [" + this.getDatasetCode()
                    + "]:" + e.getMessage());
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    private String mediaType(String format){

        String type = null;
        if(format.toLowerCase().equals("gml")){
            type = "application/x-gmz";
        }
        else if(format.toLowerCase().equals("shp")){
            type = "application/x-shapefile";
        }
        else if(format.toLowerCase().equals("vfk") || format.toLowerCase().equals("vkm")){
            type = "neexistuje";
        }
        else {
            type = "neznámý";
        }
        return type;
    }
    //------------------------------------------------------------------------------------------------------------------
    private String spatialObjectLink(String serviceCode){
        String link;

        if(serviceCode.equals("CP")){
            link = "http://inspire.ec.europa.eu/applicationschema/cp/";
        }
        else if(serviceCode.equals("AD")){
            link = "http://inspire.ec.europa.eu/applicationschema/ad/";
        }
        else if(serviceCode.equals("AU")){
            link = "http://inspire.ec.europa.eu/applicationschema/au/";
        }
        else if(serviceCode.equals("BU")){
            link = "http://inspire.ec.europa.eu/applicationschema/bu/";
        }
        else if(serviceCode.contains("RUIAN")){
            link = "http://www.cuzk.cz/vfr/";
        }
        else if(serviceCode.contains("VFK")){
            link = "http://geoportal.cuzk.cz";
        }
        else if(serviceCode.contains("VKM")){
            link = "http://www.cuzk.cz/Katastr-nemovitosti/Poskytovani-udaju-z-KN/Vymenny-format-KN/Stary-vymenny-format/Stary-vymenny-format-cast-1.aspx";
        }
        else {
            link = "neznámý";
        }

        return link;
    }

}
