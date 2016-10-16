package com.bocse.multumesc;

import com.bocse.multumesc.uploader.FTPUploader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.io.IOException;

/**
 * Unit test for simple MultumescDeputyMain.
 */
public class FTPTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public FTPTest(String testName)
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( FTPTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testFTP() throws IOException, InterruptedException {
        FTPUploader ftp=new FTPUploader("ftp.XXXX.com", 21, "YYYY", "ZZZZZ");
        //ftp.init();
        ftp.uploadFileAsync("/data/personStats.json", new File("/home/bocse/cdep/personStatsTogether.txt"));
        ftp.disconnect();
    }


}
