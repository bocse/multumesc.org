package com.bocse.multumesc.statistics;

import com.bocse.multumesc.data.Person;
import com.bocse.multumesc.data.Vote;
import com.bocse.multumesc.data.VoteTypes;
import org.joda.time.DateTime;

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
}
