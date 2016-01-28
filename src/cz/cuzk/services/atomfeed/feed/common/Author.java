package cz.cuzk.services.atomfeed.feed.common;

/**
 * Created by jaromir.rokusek on 8/27/2015.
 */

/**
 * Reprezentuje element Author. Povinnou součástí je pouze element name.
 * Obsah elementů se nastavuje pomocí příslušných metod.
 * @author Jaromír Rokusek
 * @version 1.0
 * @since 2015-08-28
 */
public class Author {
    //------------------------------------------------------------------------------------------------------------------
    //DATA MEMBERS
    //------------------------------------------------------------------------------------------------------------------
    //REQUIRED (Atom)
    private String name;
    //OPTIONAL (Atom)
    private String uri;
    private String email;
    //------------------------------------------------------------------------------------------------------------------
    //PUBLIC METHODS
    //------------------------------------------------------------------------------------------------------------------
    /**
     * @return True když jsou vyplněny všechny povinné elementy, jinak False.
     */
    public boolean isValid(){
        if(name == null){
            return false;
        }else{
            return true;
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    //------------------------------------------------------------------------------------------------------------------
    //PRIVATE METHODS
    //------------------------------------------------------------------------------------------------------------------
}
