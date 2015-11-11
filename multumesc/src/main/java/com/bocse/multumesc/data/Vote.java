package com.bocse.multumesc.data;

/**
 * Created by bogdan.bocse on 11/11/2015.
 */

public class Vote {
    private Long subjectMatterId;
    private Long personId;
    private VoteTypes value;
    private Long timestamp;


    public Long getSubjectMatterId() {
        return subjectMatterId;
    }

    public void setSubjectMatterId(Long subjectMatter) {
        this.subjectMatterId = subjectMatter;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public VoteTypes getValue() {
        return value;
    }

    public void setValue(String value)
    {
        value=value.toLowerCase().trim();
        if (value.equals("da"))
            this.value=VoteTypes.FOR;
        else
            if (value.equals("nu"))
                this.value=VoteTypes.AGAINST;
        else if (value.equals("nu a votat"))
                this.value=VoteTypes.NONE;
        else if (value.equals("abţinere"))
                this.value=VoteTypes.ABSTAIN;
        else if (value.equals("absent"))
                this.value=VoteTypes.ABSENT;
        else
                this.value=VoteTypes.UNKNOWN;
    }
    public void setValue(VoteTypes value) {
        this.value = value;
    }


    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
