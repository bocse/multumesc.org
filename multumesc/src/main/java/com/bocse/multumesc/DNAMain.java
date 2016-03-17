package com.bocse.multumesc;


import com.bocse.multumesc.data.DNARecord;
import com.bocse.multumesc.data.Person;
import com.bocse.multumesc.parser.DNAParser;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Multumesc Main app.
 */
public class DNAMain {
    private final static Logger logger = Logger.getLogger(DNAMain.class.toString());
    public static void main(String[] args) throws IOException, InterruptedException {
        DNAParser dnaParser=new DNAParser();
        //dnaParser.init();
        Person person=new Person();
        person.setFirstName("Dan Cristian");
        person.setLastName("Popescu");
    List<DNARecord> recordList=dnaParser.doSearch(person,false);
        Integer validCounter=0;
        for (DNARecord record: person.getConfirmedRecordList())
        {
            //Boolean isValid=dnaParser.doValidation("Dan Cristian", "Popescu", record);
            logger.info("Confirmed:  "+ record.getLink());

        }
        for (DNARecord record: person.getOtherRecordList())
        {
            //Boolean isValid=dnaParser.doValidation("Dan Cristian", "Popescu", record);
            logger.info("Other:  "+ record.getLink());

        }

    }
}
