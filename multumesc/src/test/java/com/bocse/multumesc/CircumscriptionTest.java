package com.bocse.multumesc;

import com.bocse.multumesc.data.Location;
import com.bocse.multumesc.data.Person;
import com.bocse.multumesc.data.Vote;
import com.bocse.multumesc.data.VoteTypes;
import com.bocse.multumesc.parser.PresenceParser;
import com.bocse.multumesc.serializer.JsonSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.joda.time.DateTime;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**
 * Unit test for simple MultumescMain.
 */
public class CircumscriptionTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CircumscriptionTest(String testName)
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( CircumscriptionTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testCircumscriptio() throws IOException, InterruptedException {
        PresenceParser pp=new PresenceParser();
        List<String> counties=new ArrayList<>();
        //counties.add("CARAS-SEVERIN");
        //counties.add("BISTRITA-NASAUD");
        counties.add("CLUJ");
        List<Location> locations=pp.getAllCircumscriptions();
        JsonSerializer jsonSerializer=new JsonSerializer();
        jsonSerializer.serialize("C:\\Temp\\countyList", 0L, locations);
        //byte[] latin2JsonString =  jsonString.getBytes("UTF-8");
        //System.out.println(jsonString);

    }



}
