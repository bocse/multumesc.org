package com.bocse.multumesc.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

/**
 * Created by bogdan.bocse on 11/10/2015.
 */
public class JsonSerializer {
    public static File serialize(String path,  Object object) throws IOException {
       return serialize(path, object, false);
    }
        public static File serialize(String path,  Object object, boolean pretty) throws IOException {


        File file = new File(path);

        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fos=new FileOutputStream(file);
        Gson gson;
        if (pretty)
        gson = new GsonBuilder().setPrettyPrinting().create();
        else
            gson = new GsonBuilder().create();

        String jsonString=gson.toJson(object);

        byte[] latin2JsonString =  jsonString.getBytes("UTF-8");
        //byte[] utf8JsonString = new String(latin2JsonString, "ISO-8859-2").getBytes("UTF-8");
        fos.write(latin2JsonString);
        fos.close();

        return file;
    }
}
