package com.bocse.multumesc;

import com.bocse.multumesc.data.Counties;
import com.bocse.multumesc.data.Person;
import com.bocse.multumesc.parser.DeputyPresenceParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;

/**
 * Unit test for simple MultumescDeputyMain.
 */
public class PersonTests
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public PersonTests(String testName)
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( PersonTests.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() throws IOException, InterruptedException {

        for (Long personId=85L; personId<=85L; personId++) {

            DeputyPresenceParser pp = new DeputyPresenceParser();
            Counties counties=new Counties();

            Person person = new Person();
            person.setPersonId(personId);
            pp.getPersonProfile(person);
            Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            String jsonString = gson.toJson(person);
            System.out.println(jsonString);
            //person 30 - maramures, mures
            if (person.getCircumscription()!=-1L)
            Assert.assertEquals(person.getCircumscription(), counties.getCircumscription(person.getCounty()));
        }
    }
}
