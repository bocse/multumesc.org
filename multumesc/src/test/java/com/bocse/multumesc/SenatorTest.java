package com.bocse.multumesc;

import com.bocse.multumesc.parser.SenatorPresenceParser;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.joda.time.DateTime;

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
        //19.12.2012
        DateTime now=new DateTime().minusDays(4).withTime(0,0,0,1);
        DateTime earliest=new DateTime().withYear(2012).withMonthOfYear(12).withDayOfMonth(19).withTime(0,0,0,1);
        DateTime reference=new DateTime().withYear(2000).withDayOfMonth(1).withMonthOfYear(1).withTime(0,0,0,1);

        int lastYear=now.getYear();
        int lastMonth=now.getMonthOfYear();

        SenatorPresenceParser spp=new SenatorPresenceParser();
        spp.init();
        spp.initProfilePage("9d0635d5-1743-4696-8acd-e97e070da503");

        while (now.isAfter(earliest))
        {

            if (now.getYear()!=lastYear)
            {
                spp.setYearPage(now.getYear());
                lastYear=now.getYear();
            }
            if (now.getMonthOfYear()!=lastMonth){
                spp.setMonthPage(now.getMonthOfYear());
                lastMonth=now.getMonthOfYear();
            }
            int dayIndex=(int)((now.getMillis()-reference.getMillis())/1000/3600/24);
            spp.getVoteList(dayIndex);
            now=now.minusDays(1);
        }
    }


}
