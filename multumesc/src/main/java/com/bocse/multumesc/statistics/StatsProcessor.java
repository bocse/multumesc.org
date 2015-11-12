package com.bocse.multumesc.statistics;

import com.bocse.multumesc.data.Person;
import com.bocse.multumesc.data.Vote;
import com.bocse.multumesc.data.VoteTypes;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by bogdan.bocse on 11/11/2015.
 */
public class StatsProcessor {
    private Person person;

    public StatsProcessor(Person person)
    {
        this.person=person;
    }
    private Map<VoteTypes, AtomicLong> initMap(){
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
    public Map<VoteTypes, AtomicLong> process(DateTime startDate, DateTime endDate)
    {
        Map<VoteTypes, AtomicLong> map=initMap();
        Long startTimestamp=startDate.getMillis();
        Long endTimestamp=endDate.getMillis();
        for (Map.Entry<Long, Vote> vote : person.getVoteMap().entrySet())
        {
            Long timestamp=vote.getValue().getTimestamp();
            if (timestamp>=startTimestamp && timestamp<=endTimestamp) {
                map.get(vote.getValue().getValue()).incrementAndGet();
                map.get(VoteTypes.TOTAL).incrementAndGet();
            }
        }
        return map;
    }
    public Map<VoteTypes, AtomicLong> process()
    {
        Map<VoteTypes, AtomicLong> map=initMap();
        for (Map.Entry<Long, Vote> vote : person.getVoteMap().entrySet())
        {
                map.get(vote.getValue().getValue()).incrementAndGet();
                map.get(VoteTypes.TOTAL).incrementAndGet();
        }
        return map;
    }

    public Map<String, Map<VoteTypes, AtomicLong>> processPartyFromPerson(Map<String, Map<VoteTypes, AtomicLong>> previousState,List<Person> persons, int period)
    {


        Map<String, Map<VoteTypes, AtomicLong>> votesByParty=null;
        if (previousState!=null)
            votesByParty=previousState;
        else
            votesByParty=new ConcurrentHashMap<>();

        for(Person person : persons)
        {

            Map<VoteTypes, AtomicLong> chosenMap;
            switch (period)
            {
                case 30:
                    chosenMap=person.getStatsLast30Days();
                    break;
                case 90:
                    chosenMap=person.getStatsLast90Days();
                    break;
                case 365:
                    chosenMap=person.getStatsLast365Days();
                    break;
                case -1:
                    chosenMap=person.getStatsAllTerm();
                    break;
                default:
                    throw new IllegalStateException("Period not supported in this method "+period);
            }

            String party=person.getCurrentParty();
            Map<VoteTypes, AtomicLong> partyVotes;
            if (!votesByParty.containsKey(party)) {
                partyVotes=initMap();
                votesByParty.put(party, partyVotes);
            }
            else
            {
                partyVotes=votesByParty.get(party);
            }

            for (Map.Entry<VoteTypes, AtomicLong> vote :person.getStatsLast90Days().entrySet())
            {
                partyVotes.get(vote.getKey()).addAndGet(vote.getValue().get());
            }

        }
        return votesByParty;
    }

    public Map<String, Map<VoteTypes, AtomicLong>> processPartyFromVotes(Map<String, Map<VoteTypes, AtomicLong>> previousState,List<Person> persons, DateTime startDate, DateTime endDate)
    {
        Long startTimestamp=startDate.getMillis();
        Long endTimestamp=endDate.getMillis();

        Map<String, Map<VoteTypes, AtomicLong>> votesByParty=null;
        if (previousState!=null)
            votesByParty=previousState;
        else
            votesByParty=new ConcurrentHashMap<>();

        for(Person person : persons)
        {


            String party=person.getCurrentParty();
            Map<VoteTypes, AtomicLong> partyVotes;
            if (!votesByParty.containsKey(party)) {
                partyVotes=initMap();
                votesByParty.put(party, partyVotes);
            }
            else
            {
                partyVotes=votesByParty.get(party);
            }

            for (Map.Entry<Long, Vote> vote :person.getVoteMap().entrySet())
            {
                Long timestamp=vote.getValue().getTimestamp();
                if (timestamp>=startTimestamp && timestamp<=endTimestamp) {
                    partyVotes.get(vote.getValue().getValue()).incrementAndGet();
                    partyVotes.get(VoteTypes.TOTAL).incrementAndGet();
                }
            }

        }
        return votesByParty;
    }
}
