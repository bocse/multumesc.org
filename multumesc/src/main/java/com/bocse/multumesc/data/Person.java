package com.bocse.multumesc.data;

import java.util.*;

/**
 * Created by bogdan.bocse on 11/11/2015.
 */
public class Person  {
    private Long personId;
    private String name;
    private String currentParty;
    private List<String> previousPartyList=new ArrayList<>();
    private SortedMap<Long, Vote> voteMap=new TreeMap<>();



    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public SortedMap<Long, Vote> getVoteMap() {
        return voteMap;
    }

    public void setVoteMap(SortedMap<Long, Vote> voteMap) {
        this.voteMap = voteMap;
    }

    public String getCurrentParty() {
        return currentParty;
    }

    public void setCurrentParty(String currentParty) {
        this.currentParty = currentParty;
    }

    public List<String> getPreviousPartyList() {
        return previousPartyList;
    }

    public void setPreviousPartyList(List<String> previousPartyList) {
        this.previousPartyList = previousPartyList;
    }
}
