package com.bocse.multumesc;

import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by bogdan.bocse on 11/10/2015.
 */
public class JsonSerializer {
    public void serialize(String path, Long personId, JSONObject object) throws IOException {

        String content = "This is the content to write into file";

        File file = new File("C:\\Temp\\Cdep\\"+personId+".txt");

        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        object.writeJSONString(bw);
        bw.close();
    }
}
