package com.bocse.multumesc;

import com.bocse.multumesc.crawlers.SenatorCrawler;
import com.bocse.multumesc.parser.SenatorPresenceParser;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.joda.time.DateTime;

import java.io.IOException;

/**
 * Unit test for simple MultumescDeputyMain.
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
        //19.12.2012
        SenatorCrawler senatorCrawler=new SenatorCrawler();
        senatorCrawler.getAllSenatorsList();
        senatorCrawler.crawlAllSenators();
        System.out.println("meow");
    }


}
