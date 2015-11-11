package com.bocse.multumesc;

import com.bocse.multumesc.data.Person;
import com.bocse.multumesc.data.Vote;
import com.bocse.multumesc.data.VoteTypes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.joda.time.DateTime;

import java.util.TreeMap;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        Vote vote=new Vote();
        vote.setPersonId(1L);
        vote.setValue(VoteTypes.ABSTAIN);
        vote.setTimestamp(new DateTime().getMillis());
        vote.setSubjectMatterId(2L);

        Person person=new Person();
        person.setPersonId(13L);
        person.setName("Gigi Voicu");
        person.setVoteMap(new TreeMap<Long, Vote>());
        person.getVoteMap().put(10L, vote);
        Gson gson = new GsonBuilder().create();
        String jsonString=gson.toJson(person);
        System.out.println(jsonString);

        Person p = gson.fromJson(jsonString, Person.class);

        System.out.println(p.getVoteMap().get(10L).getValue());

    }
}
