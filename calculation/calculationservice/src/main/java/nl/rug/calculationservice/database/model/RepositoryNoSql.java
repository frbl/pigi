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
public class RepositoryNoSql {

    private String name = "";
    private String url = "";
    private String description = "";

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        String string = "\tName: " + getName()
                + "\tURL: " + getUrl()
                + "\tDescription: " + getDescription();

        return string;

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
}
