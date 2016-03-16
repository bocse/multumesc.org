package com.bocse.multumesc;


import com.bocse.multumesc.data.DNARecord;
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
    List<DNARecord> recordList=dnaParser.doSearch("Dan Cristian","Popescu",false);
        Integer validCounter=0;
        for (DNARecord record: recordList)
        {
            Boolean isValid=dnaParser.doValidation("Dan Cristian", "Popescu", record);
            logger.info(String.valueOf(isValid) + " \t" + record.getLink());
            if (isValid) {
                validCounter++;
            }
        }
        logger.info("Found "+recordList.size()+ " records, "+validCounter+ " valid.");

    }
}
