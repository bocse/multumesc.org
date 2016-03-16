package com.bocse.multumesc;


import com.bocse.multumesc.parser.DNAParser;

import java.io.IOException;

/**
 * Multumesc Main app.
 */
public class DNAMain {

    public static void main(String[] args) throws IOException, InterruptedException {
        DNAParser dnaParser=new DNAParser();
        dnaParser.init();
dnaParser.doSearch("Vlad Alexandru COSMA deputat");
    }
}
