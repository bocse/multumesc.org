package com.bocse.multumesc.statistics;

import com.bocse.multumesc.data.Person;
import com.bocse.multumesc.data.Vote;
import com.bocse.multumesc.data.VoteTypes;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Weeks;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by bogdan.bocse on 11/11/2015.
 */
public class StatsProcessor {
    private Person person;

    public StatsProcessor(Person person) {
        this.person = person;
    }

    private Map<VoteTypes, AtomicLong> initMap() {
        Map<VoteTypes, AtomicLong> map = new ConcurrentHashMap<>();
        map.put(VoteTypes.ABSENT, new AtomicLong(0L));
        map.put(VoteTypes.ABSTAIN, new AtomicLong(0L));
        map.put(VoteTypes.NONE, new AtomicLong(0L));
        map.put(VoteTypes.AGAINST, new AtomicLong(0L));
        map.put(VoteTypes.FOR, new AtomicLong(0L));
        map.put(VoteTypes.UNKNOWN, new AtomicLong(0L));
        map.put(VoteTypes.TOTAL, new AtomicLong(0L));
        return map;
    }

    public List<Double> processWeeklyAttendence() {
        SortedMap<Long, AtomicLong> totals = new TreeMap<>();
        SortedMap<Long, AtomicLong> attended = new TreeMap<>();
        final int yearIndex = 2000;
        final DateTime epoch = new DateTime().withYear(yearIndex).withTimeAtStartOfDay();
        for (Map.Entry<Long, Vote> vote : person.getVoteMap().entrySet()) {
            DateTime voteTimestamp = new DateTime(vote.getValue().getTimestamp());
            Long weekYear = new Long(Weeks.weeksBetween(epoch, voteTimestamp).getWeeks());
            if (!totals.containsKey(weekYear)) {
                totals.put(weekYear, new AtomicLong(0L));
                attended.put(weekYear, new AtomicLong(0L));
            }
            totals.get(weekYear).addAndGet(1);
            if (vote.getValue().getValue() == VoteTypes.ABSTAIN ||
                    vote.getValue().getValue() == VoteTypes.FOR ||
                    vote.getValue().getValue() == VoteTypes.AGAINST) {
                attended.get(weekYear).addAndGet(1);
            }
        }
        List<Double> attendancePerWeek = new ArrayList<>();
        Long firstWeek = totals.firstKey();
        Long lastWeek = totals.lastKey();
        for (long weekIndex = firstWeek; weekIndex <= lastWeek; weekIndex++) {
            Double attendanceRatio = 0.0;
            if (totals.containsKey(weekIndex)) {
                Long attendedValue = 0L;
                if (attended.containsKey(weekIndex)) {
                    attendedValue = attended.get(weekIndex).get();
                }
                attendanceRatio = Math.round(100.0 * 100.0 * (double) attendedValue / (double) totals.get(weekIndex).get()) / 100.0;
            }
            attendancePerWeek.add(attendanceRatio);
        }
        return attendancePerWeek;
    }

    public List<Double> processWeeklyAttendenceExclusingVacation() {
        SortedMap<Long, AtomicLong> totals = new TreeMap<>();
        SortedMap<Long, AtomicLong> attended = new TreeMap<>();
        final int yearIndex = 2000;
        final DateTime epoch = new DateTime().withYear(yearIndex).withTimeAtStartOfDay();
        for (Map.Entry<Long, Vote> vote : person.getVoteMap().entrySet()) {
            DateTime voteTimestamp = new DateTime(vote.getValue().getTimestamp());
            Long weekYear = new Long(Weeks.weeksBetween(epoch, voteTimestamp).getWeeks());
            if (!totals.containsKey(weekYear)) {
                totals.put(weekYear, new AtomicLong(0L));
                attended.put(weekYear, new AtomicLong(0L));
            }
            totals.get(weekYear).addAndGet(1);
            if (vote.getValue().getValue() == VoteTypes.ABSTAIN ||
                    vote.getValue().getValue() == VoteTypes.FOR ||
                    vote.getValue().getValue() == VoteTypes.AGAINST) {
                attended.get(weekYear).addAndGet(1);
            }
        }
        List<Double> attendancePerWeek = new ArrayList<>();
        for (Map.Entry<Long, AtomicLong> entry : totals.entrySet()) {
            Long weekIndex = entry.getKey();
            Double attendanceRatio = 0.0;

            Long attendedValue = 0L;
            if (attended.containsKey(weekIndex)) {
                attendedValue = attended.get(weekIndex).get();
            }
            attendanceRatio = Math.round(100.0 * 100.0 * (double) attendedValue / (double) entry.getValue().get()) / 100.0;

            attendancePerWeek.add(attendanceRatio);
        }
        return attendancePerWeek;
    }

    public Map<VoteTypes, AtomicLong> process(DateTime startDate, DateTime endDate) {
        Map<VoteTypes, AtomicLong> map = initMap();
        Long startTimestamp = startDate.getMillis();
        Long endTimestamp = endDate.getMillis();
        for (Map.Entry<Long, Vote> vote : person.getVoteMap().entrySet()) {
            Long timestamp = vote.getValue().getTimestamp();
            if (timestamp >= startTimestamp && timestamp <= endTimestamp) {
                map.get(vote.getValue().getValue()).incrementAndGet();
                map.get(VoteTypes.TOTAL).incrementAndGet();
            }
        }
        return map;
    }

    public Map<VoteTypes, AtomicLong> process() {
        Map<VoteTypes, AtomicLong> map = initMap();
        for (Map.Entry<Long, Vote> vote : person.getVoteMap().entrySet()) {
            map.get(vote.getValue().getValue()).incrementAndGet();
            map.get(VoteTypes.TOTAL).incrementAndGet();
        }
        return map;
    }

    public Map<String, Map<VoteTypes, AtomicLong>> processPartyFromPerson(Map<String, Map<VoteTypes, AtomicLong>> previousState, List<Person> persons, int period) {


        Map<String, Map<VoteTypes, AtomicLong>> votesByParty = null;
        if (previousState != null)
            votesByParty = previousState;
        else
            votesByParty = new ConcurrentHashMap<>();

        for (Person person : persons) {
            if (!person.getActive())
                continue;
            Map<VoteTypes, AtomicLong> chosenMap;
            switch (period) {
                case 30:
                    chosenMap = person.getStatsLast30Days();
                    break;
                case 90:
                    chosenMap = person.getStatsLast90Days();
                    break;
                case 365:
                    chosenMap = person.getStatsLast365Days();
                    break;
                case -1:
                    chosenMap = person.getStatsAllTerm();
                    break;
                default:
                    throw new IllegalStateException("Period not supported in this method " + period);
            }

            String party = person.getCurrentParty();
            Map<VoteTypes, AtomicLong> partyVotes;
            if (!votesByParty.containsKey(party)) {
                partyVotes = initMap();
                votesByParty.put(party, partyVotes);
            } else {
                partyVotes = votesByParty.get(party);
            }

            for (Map.Entry<VoteTypes, AtomicLong> vote : chosenMap.entrySet()) {
                partyVotes.get(vote.getKey()).addAndGet(vote.getValue().get());
            }

        }
        return votesByParty;
    }

    public Map<String, Map<VoteTypes, AtomicLong>> processPartyFromVotes(Map<String, Map<VoteTypes, AtomicLong>> previousState, List<Person> persons, DateTime startDate, DateTime endDate) {
        Long startTimestamp = startDate.getMillis();
        Long endTimestamp = endDate.getMillis();

        Map<String, Map<VoteTypes, AtomicLong>> votesByParty = null;
        if (previousState != null)
            votesByParty = previousState;
        else
            votesByParty = new ConcurrentHashMap<>();

        for (Person person : persons) {

            if (!person.getActive())
                continue;
            String party = person.getCurrentParty();
            Map<VoteTypes, AtomicLong> partyVotes;
            if (!votesByParty.containsKey(party)) {
                partyVotes = initMap();
                votesByParty.put(party, partyVotes);
            } else {
                partyVotes = votesByParty.get(party);
            }

            for (Map.Entry<Long, Vote> vote : person.getVoteMap().entrySet()) {
                Long timestamp = vote.getValue().getTimestamp();
                if (timestamp >= startTimestamp && timestamp <= endTimestamp) {
                    partyVotes.get(vote.getValue().getValue()).incrementAndGet();
                    partyVotes.get(VoteTypes.TOTAL).incrementAndGet();
                }
            }

        }
        return votesByParty;
    }
}