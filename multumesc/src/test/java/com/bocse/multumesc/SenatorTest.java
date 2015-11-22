package com.bocse.multumesc;

import com.bocse.multumesc.parser.SenatorPresenceParser;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;

/**
 * Unit test for simple MultumescMain.
 */
public class SenatorTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SenatorTest(String testName)
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( SenatorTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testSenator() throws IOException, InterruptedException {
        SenatorPresenceParser spp=new SenatorPresenceParser();
        spp.init();
        spp.initProfilePage("http://senat.ro/FisaSenator.aspx?ParlamentarID=9d0635d5-1743-4696-8acd-e97e070da503");

        spp.getVoteList(5784);

    }


}
