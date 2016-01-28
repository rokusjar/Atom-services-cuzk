package cz.cuzk.services.atomfeed.feed.common;

/**
 * Created by jaromir.rokusek on 8/27/2015.
 */

/**
 * Reprezentuje element Link. Konstruktor požaduje typ odkazu viz. {@link LinkType}
 * Podle typu jsou automaticky nastaveny hodnoty atributu rel a type, tak aby odpovídali požadavkům INSPIRE.
 * Ostatní atributy je nutné nastavit pomocí příslušných metod.
 * @author Jaromír Rokusek
 * @version 1.0
 * @since 2015-08-28
 */
public class Link {
    //------------------------------------------------------------------------------------------------------------------
    //DATA MEMBERS
    //------------------------------------------------------------------------------------------------------------------
    //REQUIRED (Atom)
    private String href;
    //OPTIONAL (Atom)
    private String rel;
    private String type;
    private String hreflang;
    private String title;
    private String lenght;

    private LinkType linkType;
    //------------------------------------------------------------------------------------------------------------------
    //PUBLIC METHODS
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Předvyplní atributy rel a type.
     * @param type typ odkazu
     */
    public Link(LinkType type){
        this.linkType = type;
        switch (this.getLinkType()){
            case MAIN_FEED_METADATA:
                setRel("describedby");
                setType("application/vnd.iso.19139+xml");
                break;
            case DATASET_METADATA:
                setRel("describedby");
                setType("application/xml");
                break;
            case SELF:
                setRel("self");
                setType("application/atom+xml");
                break;
            case OPENSEARCH:
                setRel("search");
                setType("application/opensearchdescription+xml");
                break;
            case DATASET_FEED:
                setRel("alternate");
                setType("application/atom+xml");
                break;
            case DATA:
                setRel("alternate");
                break;
            case INSPIRE_REGISTRY:
                setRel("describedby");
                setType("text/html"); //kdyz odkazuju na definici z inspire registru
                break;
            case PARENT:
                setRel("up");
                setType("application/atom+xml");
                break;
            default:
                //atom sam o sobe vyzaduje pouze href atribut
                break;
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * @return True když jsou vyplněny všechny povinné atributy, jinak False.
     * ODZKOUSENO
     */
    public boolean isValid(){
        if(this.getLinkType() == LinkType.MAIN_FEED_METADATA || this.getLinkType() == LinkType.DATASET_METADATA
                || this.getLinkType() == LinkType.DATASET_FEED || this.getLinkType() == LinkType.DATA
                || this.getLinkType() == LinkType.PARENT || this.getLinkType() == LinkType.SIMPLE){
            if(href == null){
                return false;
            }else{
                return true;
            }
        }else {
            if (href == null || hreflang == null) {
                return false;
            } else {
                return true;
            }
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public LinkType getLinkType() {
        return linkType;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getRel() {
        return rel;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getType() {
        return type;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getHreflang() {
        return hreflang;
    }

    public void setHreflang(String hreflang) {
        this.hreflang = hreflang;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getLenght() {
        return lenght;
    }

    public void setLenght(String lenght) {
        this.lenght = lenght;
    }
    //------------------------------------------------------------------------------------------------------------------
    public void setType(String type) {
        this.type = type;
    }
    //------------------------------------------------------------------------------------------------------------------
    //PRIVATE METHODS
    //------------------------------------------------------------------------------------------------------------------
    private void setRel(String rel) {
        this.rel = rel;
    }
    //------------------------------------------------------------------------------------------------------------------
}
