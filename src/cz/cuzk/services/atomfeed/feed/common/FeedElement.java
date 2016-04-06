package cz.cuzk.services.atomfeed.feed.common;

/**
 * Created by jaromir.rokusek on 8/17/2015.
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
//----------------------------------------------------------------------------------------------------------------------
//TODO doplnit @param
/**
 * Slouží k vytvoření feed elementu.
 * Konstruktoru je nutné předat objekt reprezentující dokument do kterého má být záznam zapsán.
 * Následně stačí pomocí jednotlivých metod definovat potřebné vnitřní elementy.
 * Nakonec je nutné zavolat metodu {@link FeedElement#write()}, která záznam zapíše do cílového dokumentu.
 * Objekt si sám hlídá zda element obsahuje všechny povinné informace. Aktuální stav popisuje
 * atribut {@link FeedElement#state}. Sledováno je však pouze to, zda objekt obsahuje
 * povinné parametry. To jestli je jejich obsah správný a odpovídá specifikaci už je statrost uživatele.
 * @author Jaromír Rokusek
 * @version 1.0
 * @since 2015-08-17
 */
public class FeedElement {
    //------------------------------------------------------------------------------------------------------------------
    //DATA MEMBERS
    //------------------------------------------------------------------------------------------------------------------
    private Document targetDoc;
    private Element feed;
    private State state;
    private ArrayList<EntryElement> entries;
    //REQUIRED (Atom)
    private String id = null;
    private String title = null;
    private String updated = null;
    //RECOMMENDED (Atom)
    private ArrayList<Author> authors;
    private ArrayList<Link> links;
    //OPTIONAL (Atom)
    private String category = null;
    private String contributor = null;
    private String generator = null;
    private String icon = null;
    private String logo = null;
    private String rights = null;
    private String subtitle = null;

    private String timeZone = "01:00";
    //------------------------------------------------------------------------------------------------------------------
    //PUBLIC METHODS
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Nastaví cílový dokument a hodnotu {@link FeedElement#state}
     * nastaví na {@link State#NOT_VALID}.
     * @param target Dokument do kterého ma být feed element přidán.
     */
    public FeedElement(Document target){
        this.setState(State.NOT_VALID);
        this.setTargetDoc(target);
        this.entries = new ArrayList<EntryElement>();
        this.links = new ArrayList<Link>();
        this.authors = new ArrayList<Author>();
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží feed element do dokumentu.
     * Tato metoda má být zavolána až na konci, ve chvíli kdy je feed element zcela sestaven.
     * Pokud element neobsahuje všechny povinné informace (elementy) funkce vyvolá výjimku.
     * @throws ElementNotValidException V případě že feed element neobsahuje všechny povinné informace.
     * @throws NullPointerException V případě že feed nebo targetDocument je null.
     * @throws SecondRootElementException V případě že dokument už má root element a proto není možné do něj vložit feed element.
     */
    void write() throws ElementNotValidException, NullPointerException, SecondRootElementException,
            LinkElementException, AuthorElementException, CategoryElementException{
        if(this.isValid() == false){
            throw new ElementNotValidException("FeedElement: Element neobsahuje vsechny povinne informace.");
        }
        if(this.targetDoc == null){
            throw new NullPointerException("FeedElement: targetDocument je null");
        }
        if(this.getTargetDoc().getDocumentElement() != null){
            throw new SecondRootElementException("FeedElement: Dokument jiz obsahuje root element.");
        }
        this.feed = this.getTargetDoc().createElement("feed");
        this.feed.setAttribute("xmlns", "http://www.w3.org/2005/Atom");
        this.feed.setAttribute("xmlns:georss", "http://www.georss.org/georss");
        this.feed.setAttribute("xmlns:inspire_dls", "http://inspire.ec.europa.eu/schemas/inspire_dls/1.0");
        this.feed.setAttribute("xmlns:opensearch", "http://a9.com/-/spec/opensearch/1.1/");
        this.feed.setAttribute("xml:lang", "cs");
        this.getTargetDoc().appendChild(this.getFeed());
        this.createFeed();

        for(EntryElement entry : this.getEntries()){
            entry.write();
        }

    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí true pokud feed element obsahuje všechny povinné informace.
     * @return True když je validní, jinak false.
     */
    public boolean isValid(){
        if(this.getState() == State.NOT_VALID){
            return false;
        }else{
            return true;
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public void addEntry(EntryElement e){
        this.getEntries().add(e);
    }
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    public Document getTargetDoc() {
        return targetDoc;
    }
    //------------------------------------------------------------------------------------------------------------------
    public Element getFeed() {
        return feed;
    }
    //------------------------------------------------------------------------------------------------------------------
    public ArrayList<Link> getLinks() {
        return links;
    }
    //------------------------------------------------------------------------------------------------------------------
    public void addLink(Link link) {
        this.links.add(link);
    }
    //------------------------------------------------------------------------------------------------------------------
    public ArrayList<Author> getAuthors() {
        return authors;
    }
    //------------------------------------------------------------------------------------------------------------------
    public void addAuthor(Author author) {
        this.authors.add(author);
    }
    //------------------------------------------------------------------------------------------------------------------
    public ArrayList<EntryElement> getEntries() {
        return entries;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
        this.checkState();
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
        this.checkState();
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getUpdated() {
        return updated;
    }
    public void setUpdated(String updated) {
        this.updated = updated;
        this.checkState();
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
        this.checkState();
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getContributor() {
        return contributor;
    }
    public void setContributor(String contributor) {
        this.contributor = contributor;
        this.checkState();
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getGenerator() {
        return generator;
    }
    public void setGenerator(String generator) {
        this.generator = generator;
        this.checkState();
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
        this.checkState();
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getLogo() {
        return logo;
    }
    public void setLogo(String logo) {
        this.logo = logo;
        this.checkState();
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getRights() {
        return rights;
    }
    public void setRights(String rights) {
        this.rights = rights;
        this.checkState();
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getSubtitle() {
        return subtitle;
    }
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        this.checkState();
    }
    //------------------------------------------------------------------------------------------------------------------
    public State getState() {
        return state;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getTimeZone(){
        return this.timeZone;
    }

    public void setTimeZone(String timeZone){
        this.timeZone = timeZone;
    }
    //------------------------------------------------------------------------------------------------------------------
    //PRIVATE METHODS
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Projde všechny parametry této třídy a pro každý, jehož hodnota není null vytvoří element a
     * vloží ho do elementu feed.
     */
    private void createFeed() throws LinkElementException, AuthorElementException{
        if(this.getId() != null){
            this.createId();
        }
        if(this.getTitle() != null){
            this.createTitle();
        }
        if(this.getSubtitle() != null){
            this.createSubtitle();
        }
        if(this.getUpdated() != null) {
            this.createUpdated();
        }
        if(this.authors.isEmpty() == false){
            this.createAuthor();
        }
        if(this.getRights() != null){
            this.createRights();
        }
        if(this.getLinks().isEmpty() == false) {
            this.createLink();
        }
        if(this.getCategory() != null){
            this.createCategory();
        }
        if(this.getContributor() != null){
            this.createContributor();
        }
        if(this.getGenerator() != null){
            this.createGenerator();
        }
        if(this.getIcon() != null){
            this.createIcon();
        }
        if(this.getLogo() != null){
            this.createLogo();
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Zkontroluje u kterých elementů je známa hodnota a podle toho nastaví State.
     * Je volána v každém "setru". Zajišťuje že objekt má neustále aktuální State.
     */
    private void checkState(){
        if(this.getId() != null && this.getTitle() != null && this.getUpdated() != null){
            if(this.getAuthors().isEmpty() == false && this.getLinks().isEmpty() == false){
                if(this.getCategory() != null && this.getContributor() != null && this.getGenerator() != null &&
                        this.getIcon() != null && this.getLogo() != null && this.getRights() != null &&
                        this.getSubtitle() != null){
                    this.setState(State.FULL);
                }else{
                    this.setState(State.RECOMMENDED);
                }
            }else{
                this.setState(State.VALID);
            }
        }else{
            this.setState(State.NOT_VALID);
        }

    }
    //------------------------------------------------------------------------------------------------------------------
    private void setState(State state) {
        this.state = state;
    }
    //------------------------------------------------------------------------------------------------------------------
    private void setTargetDoc(Document targetDoc) {
        this.targetDoc = targetDoc;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do feed elementu element title.
     */
    private void createTitle(){
        Element title = this.getTargetDoc().createElement("title");
        title.appendChild(this.getTargetDoc().createTextNode(this.getTitle()));
        this.getFeed().appendChild(title);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do feed elementu element id.
     */
    private void createId(){
        Element id = this.getTargetDoc().createElement("id");
        id.appendChild(this.getTargetDoc().createTextNode(this.getId()));
        this.getFeed().appendChild(id);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do feed elementu element updated.
     */
    private void createUpdated(){
        Element updated = this.getTargetDoc().createElement("updated");
        updated.appendChild(this.getTargetDoc().createTextNode(this.getUpdated()));
        this.getFeed().appendChild(updated);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do feed elementu element author.
     */
    private void createAuthor() throws AuthorElementException{
        for(Author a : this.getAuthors()) {

            if(a.isValid() == false){
                throw new AuthorElementException("Author element neni kopletni");
            }
            Element author = this.getTargetDoc().createElement("author");

            if(a.getName() != null){
                Element name = this.getTargetDoc().createElement("name");
                name.appendChild(this.getTargetDoc().createTextNode(a.getName()));
                author.appendChild(name);
            }
            if(a.getUri() != null){
                Element uri = this.getTargetDoc().createElement("uri");
                uri.appendChild(this.getTargetDoc().createTextNode(a.getUri()));
                author.appendChild(uri);
            }
            if(a.getEmail() != null){
                Element name = this.getTargetDoc().createElement("email");
                name.appendChild(this.getTargetDoc().createTextNode(a.getEmail()));
                author.appendChild(name);
            }

            this.getFeed().appendChild(author);
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do feed elementu elementy link.
     */
    private void createLink() throws LinkElementException{
        for(Link l : this.getLinks()) {

            if(l.isValid() == false){
                throw new LinkElementException("Link element neni kompletni - " + l.getLinkType());
            }

            Element link = this.getTargetDoc().createElement("link");
            if(l.getHref() != null){
                link.setAttribute("href", l.getHref());
            }
            if(l.getRel() != null){
                link.setAttribute("rel", l.getRel());
            }
            if(l.getType() != null){
                link.setAttribute("type", l.getType());
            }
            if(l.getTitle() != null){
                link.setAttribute("title", l.getTitle());
            }
            if(l.getHreflang() != null){
                link.setAttribute("hreflang", l.getHreflang());
            }
            if(l.getLenght() != null){
                link.setAttribute("length", l.getLenght());
            }
            this.getFeed().appendChild(link);
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do feed elementu element category.
     */
    private void createCategory(){
        Element category = this.getTargetDoc().createElement("category");
        category.appendChild(this.getTargetDoc().createTextNode(this.getCategory()));
        this.getFeed().appendChild(category);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do feed elementu element contributor.
     */
    private void createContributor(){
        Element contributor = this.getTargetDoc().createElement("contributor");
        contributor.appendChild(this.getTargetDoc().createTextNode(this.getContributor()));
        this.getFeed().appendChild(contributor);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do feed elementu element generator.
     */
    private void createGenerator(){
        Element generator = this.getTargetDoc().createElement("generator");
        generator.appendChild(this.getTargetDoc().createTextNode(this.getGenerator()));
        this.getFeed().appendChild(generator);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do feed elementu element icon.
     */
    private void createIcon(){
        Element icon = this.getTargetDoc().createElement("icon");
        icon.appendChild(this.getTargetDoc().createTextNode(this.getIcon()));
        this.getFeed().appendChild(icon);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do feed elementu element logo.
     */
    private void createLogo(){
        Element logo = this.getTargetDoc().createElement("logo");
        logo.appendChild(this.getTargetDoc().createTextNode(this.getLogo()));
        this.getFeed().appendChild(logo);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do feed elementu element rights.
     */
    private void createRights(){
        Element rights = this.getTargetDoc().createElement("rights");
        rights.appendChild(this.getTargetDoc().createTextNode(this.getRights()));
        this.getFeed().appendChild(rights);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do feed elementu element subtitle.
     */
    private void createSubtitle(){
        Element subtitle = this.getTargetDoc().createElement("subtitle");
        subtitle.appendChild(this.getTargetDoc().createTextNode(this.getSubtitle()));
        this.getFeed().appendChild(subtitle);
    }
    //------------------------------------------------------------------------------------------------------------------

}
