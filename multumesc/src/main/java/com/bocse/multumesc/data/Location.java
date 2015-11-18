package com.bocse.multumesc.data;

import com.bocse.multumesc.utils.TextUtils;

import javax.xml.soap.Text;

/**
 * Created by bogdan.bocse on 11/18/2015.
 */
public class Location {
    private String name;
    private LocationType locationType;
    private String county;
    private Long circumscription;
    private Long colegiu;
    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public String getFlattenedName()
    {
        return TextUtils.flattenToAscii(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = TextUtils.flattenToAscii(name).trim().toLowerCase();
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public Long getCircumscription() {
        return circumscription;
    }

    public void setCircumscription(Long circumscription) {
        this.circumscription = circumscription;
    }

    public Long getColegiu() {
        return colegiu;
    }

    public void setColegiu(Long colegiu) {
        this.colegiu = colegiu;
    }
}
