package com.bocse.multumesc;


import com.bocse.multumesc.crawlers.DeputyCrawler;
import com.bocse.multumesc.data.Person;
import com.bocse.multumesc.uploader.FTPUploader;
import com.bocse.multumesc.uploader.S3Uploader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.time.StopWatch;
import org.joda.time.DateTime;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Multumesc Main app.
 */
public class DeputyIterator {
    private final static Logger logger = Logger.getLogger(MultumescDeputyParallelMain.class.toString());

    public final FileConfiguration configuration = new PropertiesConfiguration();

    public void init(String configFile) throws ConfigurationException, IOException {


        configuration.load(configFile);
        logger.info(configuration.toString());

    }

    public void iterate() throws IOException {

        StopWatch iteatorWatch = new StopWatch();
        iteatorWatch.start();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Reader reader = null;
        try {
            //FileReader reader;
            String filePath=configuration.getString("output.profileStatsTogether.path")+".json";
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));
            Type listType = new TypeToken<Map<String,Object>>() {
            }.getType();
            Map<String, Object> wrapper=gson.fromJson(reader, listType);;

            Map<String,Map<String, Object>> localPersonList=(Map<String,Map<String,Object>>)wrapper.get("payload");
            iteatorWatch.stop();

            logger.info("Loaded " + localPersonList.size() + " items in " + iteatorWatch.getTime() +"ms");
            int yahooCount=0;
            for (Map<String, Object> person : localPersonList.values()) {
                Object emailObject=person.get("email");
                if (emailObject==null)
                    continue;
                String email=emailObject.toString();
                if (email.contains("yahoo")) {
                    System.out.println(person.get("fullName").toString() + "\t" + email);
                    yahooCount++;
                }
            }
            System.out.println("Yahoo count: "+yahooCount);


            int gmailCount=0;
            for (Map<String, Object> person : localPersonList.values()) {
                Object emailObject=person.get("email");
                if (emailObject==null)
                    continue;
                String email=emailObject.toString();
                if (email.contains("gmail")) {
                    System.out.println(person.get("fullName").toString() + "\t" + email);
                    gmailCount++;
                }
            }
            System.out.println("gmail count: "+gmailCount);

            return;
        } finally {

            if (reader != null)
                reader.close();
            System.gc();
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException, ConfigurationException {
        DeputyIterator deputyIterator=new DeputyIterator();
        deputyIterator.init(args[0]);
        deputyIterator.iterate();
    }
}
