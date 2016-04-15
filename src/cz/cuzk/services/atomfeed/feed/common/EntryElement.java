package cz.cuzk.services.atomfeed.feed.common;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
//----------------------------------------------------------------------------------------------------------------------
/**
 * Slouží k vytvoření entry elementu.
 * Umožnujě nastavit libovolný element záznamu a jejich atributy.
 * Konstruktoru je nutné předat objekt reprezentující dokument do kterého má být záznam zapsán.
 * Následně stačí pomocí jednotlivých metod definovat hodnoty elementů záznamu a jejich atributů.
 * Nakonec je nutné zavolat metodu {@link EntryElement#write()}, která záznam zapíše do cílového dokumentu.
 * @author Jaromír Rokusek
 * @version 1.0
 * @since 2015-08-16
 */
public class EntryElement {
    //------------------------------------------------------------------------------------------------------------------
    //DATA MEMBERS
    //------------------------------------------------------------------------------------------------------------------
    private Document targetDoc;
    private Element entry;
    private State state;
    //REQUIRED (Atom)
    private String id = null;
    private String title = null;
    private String updated = null;
    //RECOMMENDED (Atom)
    private ArrayList<Author> authors;
    private String content = null;
    private ArrayList<Link> links;
    private String summary = null;
    //OPTIONAL (Atom)
    private ArrayList<Category> categories;
    private String contributor = null;
    private String published = null;
    private String source = null;
    private String rights = null;
    private String georssPoint = null;
    private String georssPolygon = null;

    private String inspireDLSCode = null;
    private String inspireDLSNamespace = null;

    private String timeZone = "01:00";
    //------------------------------------------------------------------------------------------------------------------
    //PUBLIC METHODS
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Nastaví cílový dokument a výchozí State.
     * @param target Dokument do kterého ma být entry element přidán.
     */
    public EntryElement(Document target){
        this.setState(State.NOT_VALID);
        this.setTargetDoc(target);
        this.links = new ArrayList<Link>();
        this.authors = new ArrayList<Author>();
        this.categories = new ArrayList<Category>();
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vlozi zaznam (entry) do dokumentu.
     * Tato metoda ma byt zavolana az na konci, ve chvili kdy je zaznam zcela sestaven.
     */
    void write() throws ElementNotValidException, NullPointerException, LinkElementException,
            AuthorElementException, CategoryElementException{

        if(this.isValid() == false){
            throw new ElementNotValidException("EntryElement: Element neobsahuje vsechny povinne informace.");
        }
        if(this.targetDoc == null){
            throw new NullPointerException("FeedElement: targetDocument je null");
        }

        Element feed = this.getTargetDoc().getDocumentElement(); //feed element je vzdy root
        this.entry = this.getTargetDoc().createElement("entry");
        feed.appendChild(this.getEntry());
        this.createEntry();

    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vrátí true pokud entry element obsahuje všechny povinné informace.
     * @return True když je validní, jinak false.
     */
    public boolean isValid(){
        if(this.getState() == State.NOT_VALID){
            return false;
        }else{
            return true;
        }
    }
    public ArrayList<Link> getLinks() {
        return links;
    }
    //------------------------------------------------------------------------------------------------------------------
    public void setInspireDLS(String code, String namespace){

        this.inspireDLSCode = code;
        this.inspireDLSNamespace = namespace;
    }
    //------------------------------------------------------------------------------------------------------------------
    public void addLink(Link link) {
        this.links.add(link);
    }
    //------------------------------------------------------------------------------------------------------------------
    public ArrayList<Category> getCategories() {
        return categories;
    }
    //------------------------------------------------------------------------------------------------------------------
    public void addCategory(Category category) {
        this.categories.add(category);
    }

    //------------------------------------------------------------------------------------------------------------------
    public String getTimeZone(){
        return this.timeZone;
    }
    public void setTimeZone(String timeZone){
        this.timeZone = timeZone;
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
    public Document getTargetDoc() {
        return targetDoc;
    }
    //------------------------------------------------------------------------------------------------------------------
    public Element getEntry() {
        return entry;
    }

    public void setEntry(Element entry) {
        this.entry = entry;
    }
    //------------------------------------------------------------------------------------------------------------------
    public State getState() {
        return state;
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
    public String getGeorssPoint() {
        return georssPoint;
    }

    public void setGeorssPoint(String georssPoint) {
        this.georssPoint = georssPoint;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getGeorssPolygon() {
        return georssPolygon;
    }

    public void setGeorssPolygon(String georssPolygon) {
        this.georssPolygon = georssPolygon;
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
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.checkState();
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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
    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
        this.checkState();
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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
    //PRIVATE METHODS
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Zkontroluje u kterých elementů je známa hodnota a podle toho nastaví State.
     * Je volána v každém "setru". Zajišťuje že objekt má neustále aktuální State.
     */
    private void checkState(){
        if(this.getId() != null && this.getTitle() != null && this.getUpdated() != null){
            if(this.getAuthors().isEmpty() == false && this.links.isEmpty() == false && this.getContent() != null &&
                    this.getSummary() != null){
                if(this.getCategories().isEmpty() == false && this.getContributor() != null && this.getPublished() != null
                        && this.getSource() != null && this.getRights() != null){
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
    /**
     * Vloží do entry elementu element title.
     */
    private void createTitle(){
        Element title = this.getTargetDoc().createElement("title");
        title.appendChild(this.getTargetDoc().createTextNode(this.getTitle()));
        this.getEntry().appendChild(title);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do entry elementu element id.
     */
    private void createId(){
        Element id = this.getTargetDoc().createElement("id");
        id.appendChild(this.getTargetDoc().createTextNode(this.getId()));
        this.getEntry().appendChild(id);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do entry elementu element updated.
     */
    private void createUpdated(){
        Element updated = this.getTargetDoc().createElement("updated");
        updated.appendChild(this.getTargetDoc().createTextNode(this.getUpdated()));
        this.getEntry().appendChild(updated);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do entry elementu element author.
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

            this.getEntry().appendChild(author);
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do entry elementu element link.
     */
    private void createLink()throws LinkElementException{
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
            this.getEntry().appendChild(link);
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do entry elementu element category.
     */
    private void createCategory() throws CategoryElementException{
        for(Category c : this.getCategories()) {

            if(c.isValid() == false){
                throw new CategoryElementException("Category element neni kompletni.");
            }

            Element category = this.getTargetDoc().createElement("category");

            if(c.getTerm() != null){
                category.setAttribute("term", c.getTerm());
            }
            if(c.getLabel() != null){
                category.setAttribute("label", c.getLabel());
            }
            if(c.getScheme() != null){
                category.setAttribute("scheme", c.getScheme());
            }

            this.getEntry().appendChild(category);
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do entry elementu element contributor.
     */
    private void createContributor(){
        Element contributor = this.getTargetDoc().createElement("contributor");
        contributor.appendChild(this.getTargetDoc().createTextNode(this.getContributor()));
        this.getEntry().appendChild(contributor);
    }
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do entry elementu element rights.
     */
    private void createRights(){
        Element rights = this.getTargetDoc().createElement("rights");
        rights.appendChild(this.getTargetDoc().createTextNode(this.getRights()));
        this.getEntry().appendChild(rights);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do entry elementu element content.
     */
    private void createContent(){
        Element content = this.getTargetDoc().createElement("content");
        content.appendChild(this.getTargetDoc().createTextNode(this.getContent()));
        this.getEntry().appendChild(content);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do entry elementu element subtitle.
     */
    private void createSummary(){
        Element summary = this.getTargetDoc().createElement("summary");
        summary.appendChild(this.getTargetDoc().createTextNode(this.getSummary()));
        this.getEntry().appendChild(summary);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do entry elementu element published.
     */
    private void createPublished(){
        Element published = this.getTargetDoc().createElement("published");
        published.appendChild(this.getTargetDoc().createTextNode(this.getPublished()));
        this.getEntry().appendChild(published);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do entry elementu element content.
     */
    private void createSource(){
        Element source = this.getTargetDoc().createElement("source");
        source.appendChild(this.getTargetDoc().createTextNode(this.getSource()));
        this.getEntry().appendChild(source);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do entry elementu element content.
     */
    private void createGeorssPoint(){
        Element georssPoint = this.getTargetDoc().createElement("georss:point");
        georssPoint.appendChild(this.getTargetDoc().createTextNode(this.getGeorssPoint()));
        this.getEntry().appendChild(georssPoint);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do entry elementu element content.
     */
    private void createGeorssPolygon(){
        Element georssPolygon = this.getTargetDoc().createElement("georss:polygon");
        georssPolygon.appendChild(this.getTargetDoc().createTextNode(this.getGeorssPolygon()));
        this.getEntry().appendChild(georssPolygon);
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Vloží do entry elementu inspire_dls identifikator.
     */
    private void createInspireDLS(){
        Element dlscode = this.getTargetDoc().createElement("inspire_dls:spatial_dataset_identifier_code");
        Element dlsnamespace = this.getTargetDoc().createElement("inspire_dls:spatial_dataset_identifier_namespace");
        dlscode.appendChild(this.getTargetDoc().createTextNode(this.inspireDLSCode));
        dlsnamespace.appendChild(this.getTargetDoc().createTextNode(this.inspireDLSNamespace));
        this.getEntry().appendChild(dlscode);
        this.getEntry().appendChild(dlsnamespace);
    }
    //------------------------------------------------------------------------------------------------------------------
    private void setTargetDoc(Document targetDoc) {
        this.targetDoc = targetDoc;
    }
    //------------------------------------------------------------------------------------------------------------------
    private void setState(State state) {
        this.state = state;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Projde všechny parametry této třídy a pro každý, jehož hodnota není null vytvoří element a
     * vloží ho do elementu entry.
     * @throws LinkElementException {@link LinkElementException}
     */
    private void createEntry() throws LinkElementException, AuthorElementException, CategoryElementException{
        if(this.getId() != null){
            this.createId();
        }
        if(this.getTitle() != null){
            this.createTitle();
        }
        if(this.getSummary() != null){
            this.createSummary();
        }
        if(this.getUpdated() != null) {
            this.createUpdated();
        }
        if(this.getAuthors().isEmpty() == false){
            this.createAuthor();
        }
        if(this.getRights() != null){
            this.createRights();
        }
        if(this.getPublished() != null){
            this.createPublished();
        }
        if(this.getLinks().isEmpty() == false) {
            this.createLink();
        }
        if(this.getCategories().isEmpty() == false){
            this.createCategory();
        }
        if(this.getContributor() != null){
            this.createContributor();
        }
        if(this.getContent() != null){
            this.createContent();
        }
        if(this.getSource() != null){
            this.createSource();
        }
        if(this.inspireDLSCode != null && this.inspireDLSNamespace != null){
            this.createInspireDLS();
        }
        if(this.georssPoint != null){
            this.createGeorssPoint();
        }
        if(this.georssPolygon != null){
            this.createGeorssPolygon();
        }
    }
}
