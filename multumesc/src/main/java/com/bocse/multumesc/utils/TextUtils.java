package com.bocse.multumesc.utils;

import java.text.Normalizer;

/**
 * Created by bocse on 17.11.2015.
 */
public class TextUtils {
    public static String flattenToAscii(String string) {
        StringBuilder sb = new StringBuilder(string.length());
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        for (char c : string.toCharArray()) {
            if (c <= '\u007F') sb.append(c);
        }
        return sb.toString();
    }
}
