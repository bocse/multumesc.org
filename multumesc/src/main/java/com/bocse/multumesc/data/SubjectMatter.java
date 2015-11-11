package com.bocse.multumesc.data;

/**
 * Created by bogdan.bocse on 11/11/2015.
 */
public class SubjectMatter {
    private Long subjectMatterId;
    private String description;
    private String url;
    private String fullText;

    public Long getSubjectMatterId() {
        return subjectMatterId;
    }

    public void setSubjectMatterId(Long subjectMatterId) {
        this.subjectMatterId = subjectMatterId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }
}
