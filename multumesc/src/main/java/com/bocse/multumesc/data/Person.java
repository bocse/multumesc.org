package com.bocse.multumesc.data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by bogdan.bocse on 11/11/2015.
 */
public class Person  {
    private Long personId;
    private String name;
    private String currentParty;
    private List<String> allPartyList =new ArrayList<>();
    private SortedMap<Long, Vote> voteMap=new TreeMap<>();
    private Long lastUpdateTimestamp=0L;
    private String description;
    private String contactInformation;
    private String county;
    private Long colegiu;
    private Boolean active;
    private Long circumscription;
    private Map<VoteTypes, AtomicLong> statsLast30Days=new ConcurrentHashMap<>();
    private Map<VoteTypes, AtomicLong> statsLast90Days=new ConcurrentHashMap<>();
    private Map<VoteTypes, AtomicLong> statsLast365Days=new ConcurrentHashMap<>();
    private Map<VoteTypes, AtomicLong> statsAllTerm=new ConcurrentHashMap<>();

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

    public List<String> getAllPartyList() {
        return allPartyList;
    }

    public void setAllPartyList(List<String> allPartyList) {
        this.allPartyList = allPartyList;
    }


    public Map<VoteTypes, AtomicLong> getStatsLast30Days() {
        return statsLast30Days;
    }

    public void setStatsLast30Days(Map<VoteTypes, AtomicLong> statsLast30Days) {
        this.statsLast30Days = statsLast30Days;
    }

    public Map<VoteTypes, AtomicLong> getStatsLast90Days() {
        return statsLast90Days;
    }

    public void setStatsLast90Days(Map<VoteTypes, AtomicLong> statsLast90Days) {
        this.statsLast90Days = statsLast90Days;
    }

    public Map<VoteTypes, AtomicLong> getStatsLast365Days() {
        return statsLast365Days;
    }

    public void setStatsLast365Days(Map<VoteTypes, AtomicLong> statsLast365Days) {
        this.statsLast365Days = statsLast365Days;
    }

    public Map<VoteTypes, AtomicLong> getStatsAllTerm() {
        return statsAllTerm;
    }

    public void setStatsAllTerm(Map<VoteTypes, AtomicLong> statsAllTerm) {
        this.statsAllTerm = statsAllTerm;
    }

    public Long getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(Long lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public Long getColegiu() {
        return colegiu;
    }

    public void setColegiu(Long colegiu) {
        this.colegiu = colegiu;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getCircumscription() {
        return circumscription;
    }

    public void setCircumscription(Long circumscription) {
        this.circumscription = circumscription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }
}
