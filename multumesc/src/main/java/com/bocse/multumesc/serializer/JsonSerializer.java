package com.bocse.multumesc.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

/**
 * Created by bogdan.bocse on 11/10/2015.
 */
public class JsonSerializer {
    public static void serialize(String path, Long personId, Object object) throws IOException {


        File file = new File(path+personId+".txt");

        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fos=new FileOutputStream(file);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString=gson.toJson(object);

        byte[] latin2JsonString =  jsonString.getBytes("UTF-8");
        //byte[] utf8JsonString = new String(latin2JsonString, "ISO-8859-2").getBytes("UTF-8");
        fos.write(latin2JsonString);
        fos.close();
        /*
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);


        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String jsonString=gson.toJson(object);

        byte[] utf8JsonString = jsonString.getBytes("UTF8");

        bw.write
        bw.write(utf8JsonString, 0, utf8JsonString.length);
        bw.write(jsonString);

        //object.writeJSONString(bw);
        bw.close();
        */
    }
}
