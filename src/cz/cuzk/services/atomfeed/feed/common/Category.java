package cz.cuzk.services.atomfeed.feed.common;

/**
 * Created by jaromir.rokusek on 8/28/2015.
 */

/**
 * Reprezentuje element category.
 * @author Jaromír Rokusek
 * @version 1.0
 * @since 2015-08-28
 */
public class Category {
    //------------------------------------------------------------------------------------------------------------------
    //DATA MEMBERS
    //------------------------------------------------------------------------------------------------------------------
    //REQUIRED (Atom)
    private String term;
    //OPTIONAL (Atom)
    private String label;
    private String scheme;
    //------------------------------------------------------------------------------------------------------------------
    //PUBLIC METHODS
    //------------------------------------------------------------------------------------------------------------------
    /**
     * @return True když jsou vyplněny všechny povinné atributy, jinak False.
     */
    public boolean isValid(){
        if(this.getTerm() != null){
            return true;
        }else {
            return false;
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getTerm() {
        return term;
    }
    public void setTerm(String term) {
        this.term = term;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getScheme() {
        return scheme;
    }
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }
    //------------------------------------------------------------------------------------------------------------------
    //PRIVATE METHODS
    //------------------------------------------------------------------------------------------------------------------

}
