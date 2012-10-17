/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.database.model;

import java.util.List;

/**
 *
 * @author frbl
 */
public class RepositoryRelational {

    /**
     *
     */
    public static final int NAME_LOC = 1;
    /**
     *
     */
    public static final int ID_LOC = 2;
    /**
     *
     */
    public static final int URL_LOC = 3;
    /**
     *
     */
    public static final int DESCRIPTION_LOC = 4;
    private int id = 0;
    private String name = "";
    private String url = "";
    private String description = "";
    private List<RevisionRelational> revisions = null;

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        String string = "ID: " + getId()
                + "\tName: " + getName()
                + "\tURL: " + getUrl()
                + "\tDescription: " + getDescription()
                + "\n\tRevisions: \t";
        
        if (revisions != null) {
            
            for (RevisionRelational revision : getRevisions()) {

                string += revision.toString() + "\n\t\t\t";

            }
            
        }
        
        return string;

    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the revisions
     */
    public List<RevisionRelational> getRevisions() {
        return revisions;
    }

    /**
     * @param revisions the revisions to set
     */
    public void setRevisions(List<RevisionRelational> revisions) {
        this.revisions = revisions;
    }
}
