package com.bocse.multumesc;

import com.bocse.multumesc.data.Location;
import com.bocse.multumesc.parser.DeputyPresenceParser;
import com.bocse.multumesc.serializer.JsonSerializer;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for simple MultumescDeputyMain.
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
        DeputyPresenceParser pp=new DeputyPresenceParser();
        List<String> counties=new ArrayList<>();
        //counties.add("CARAS-SEVERIN");
        //counties.add("BISTRITA-NASAUD");
        counties.add("CLUJ");
        List<Location> locations=pp.getAllCircumscriptions();
        JsonSerializer jsonSerializer=new JsonSerializer();
        JsonSerializer.serialize("C:\\Temp\\countyList" + ".txt", locations);
        //byte[] latin2JsonString =  jsonString.getBytes("UTF-8");
        //System.out.println(jsonString);

    }



}
