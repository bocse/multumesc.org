package com.bocse.multumesc.data;

import org.apache.commons.lang.mutable.MutableLong;

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
    private List<String> previousPartyList=new ArrayList<>();
    private SortedMap<Long, Vote> voteMap=new TreeMap<>();
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

    public List<String> getPreviousPartyList() {
        return previousPartyList;
    }

    public void setPreviousPartyList(List<String> previousPartyList) {
        this.previousPartyList = previousPartyList;
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
}
