package com.bocse.multumesc;

import com.bocse.multumesc.data.Counties;
import com.bocse.multumesc.data.Person;
import com.bocse.multumesc.parser.DeputyPresenceParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for simple MultumescDeputyMain.
 */
public class InactiveFilterTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public InactiveFilterTest(String testName)
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( InactiveFilterTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() throws IOException, InterruptedException {
        List<Long> inactiveList=new ArrayList<>();
        List<Person> inactivePerson=new ArrayList<>();
        for (Long personId=1L; personId<=417L; personId++) {

            DeputyPresenceParser pp = new DeputyPresenceParser();
            Counties counties=new Counties();

            Person person = new Person();
            person.setPersonId(personId);
            pp.getPersonProfile(person);

            if (!person.getActive()) {
                Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
                String jsonString = gson.toJson(person);
                inactiveList.add(person.getPersonId());
                inactivePerson.add(person);
                System.out.println(jsonString);
            }

        }
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        System.out.println(gson.toJson(inactivePerson));
        System.out.print(gson.toJson(inactiveList));
    }
}
