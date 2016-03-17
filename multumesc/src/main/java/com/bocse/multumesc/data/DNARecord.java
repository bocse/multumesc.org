package com.bocse.multumesc.data;

/**
 * Created by bogdan.bocse on 3/16/2016.
 */
public class DNARecord {
    private String date;
    private String title;
    private String link;
    private Boolean strongValidation=false;
    private Boolean weakValidation=false;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    public Boolean getStrongValidation() {
        return strongValidation;
    }

    public void setStrongValidation(Boolean strongValidation) {
        this.strongValidation = strongValidation;
    }

    public Boolean getWeakValidation() {
        return weakValidation;
    }

    public void setWeakValidation(Boolean weakValidation) {
        this.weakValidation = weakValidation;
    }
}
