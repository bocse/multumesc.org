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
import org.apache.pdfbox.PDFBox;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Unit test for simple MultumescDeputyMain.
 */
public class PDFTests
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public PDFTests(String testName)
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( PDFTests.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testPDF() throws IOException, InterruptedException {

        PDFTextStripper pdfStripper = null;
        PDDocument pdDoc = null;
        COSDocument cosDoc = null;
        File file = new File("/home/bocse/pv112426.pdf");
        try {
            PDFParser parser = new PDFParser(new FileInputStream(file));
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            //pdfStripper.setStartPage(1);
            //pdfStripper.setEndPage(100);
            String parsedText = pdfStripper.getText(pdDoc);
            //pdfStripper.get
            System.out.println(parsedText);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
