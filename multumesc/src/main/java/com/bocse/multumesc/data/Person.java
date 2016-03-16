package com.bocse.multumesc.data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by bogdan.bocse on 11/11/2015.
 */
public class Person  {
    private Long personId;
    private String fullName;
    private String firstName;
    private String lastName;
    private String pictureURL;
    private String currentParty;
    private String email;
    private List<DNARecord> recordList;
    private Long investitureTimestamp=-1L;
    private List<String> allPartyList =new ArrayList<>();
    private SortedMap<Long, Vote> voteMap=new TreeMap<>();
    private Integer speeches;
    private Integer statements;
    private Integer inquiries;
    private Integer motions;
    private Integer proposedLaw;

    public Integer getSpeeches() {
        return speeches;
    }

    public void setSpeeches(Integer speeches) {
        this.speeches = speeches;
    }

    public Integer getStatements() {
        return statements;
    }

    public void setStatements(Integer statements) {
        this.statements = statements;
    }

    public Integer getInquiries() {
        return inquiries;
    }

    public void setInquiries(Integer inquiries) {
        this.inquiries = inquiries;
    }

    public Integer getMotions() {
        return motions;
    }

    public void setMotions(Integer motions) {
        this.motions = motions;
    }

    public Integer getProposedLaw() {
        return proposedLaw;
    }

    public void setProposedLaw(Integer proposedLaw) {
        this.proposedLaw = proposedLaw;
    }

    public Integer getPassedLaw() {
        return passedLaw;
    }

    public void setPassedLaw(Integer passedLaw) {
        this.passedLaw = passedLaw;
    }

    private Integer passedLaw;
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
    private List<Double> attendancePerWeek=new ArrayList<>();
    private List<Double> attendancePerWeekExcludingVacation=new ArrayList<>();

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public List<Double> getAttendancePerWeek() {
        return attendancePerWeek;
    }

    public void setAttendancePerWeek(List<Double> attendancePerWeek) {
        this.attendancePerWeek = attendancePerWeek;
    }

    public List<Double> getAttendancePerWeekExcludingVacation() {
        return attendancePerWeekExcludingVacation;
    }

    public void setAttendancePerWeekExcludingVacation(List<Double> attendancePerWeekExcludingVacation) {
        this.attendancePerWeekExcludingVacation = attendancePerWeekExcludingVacation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getInvestitureTimestamp() {
        return investitureTimestamp;
    }

    public void setInvestitureTimestamp(Long investitureTimestamp) {
        if (investitureTimestamp==null)
            throw new IllegalStateException("Investiture date cannot be null");
        this.investitureTimestamp = investitureTimestamp;
    }

    public List<DNARecord> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<DNARecord> recordList) {
        this.recordList = recordList;
    }
}
