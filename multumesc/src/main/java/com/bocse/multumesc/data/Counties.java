package com.bocse.multumesc.data;

import com.bocse.multumesc.utils.TextUtils;

import java.text.Normalizer;
import java.util.*;

/**
 * Created by bocse on 16.11.2015.
 */
public class Counties {

    private final Map<String, Long> counties = new HashMap<>();
    private final Map<String, Long> countiesFlattened=new HashMap<>();

    public Counties()
    {
        init();
        for (Map.Entry<String, Long> entry: counties.entrySet())
        {
            countiesFlattened.put(TextUtils.flattenToAscii(entry.getKey()), entry.getValue());
        }
    }
    public Long getCircumscription(String county)
    {
        Long circumscription=null;
        circumscription=counties.get(county);
        if (circumscription==null)
            circumscription=countiesFlattened.get(county);
        return circumscription;
    }

    public List<String> getCounties(){
        return new ArrayList<>(counties.keySet());
    }

    public List<String> getCountiesFlattened() {
        return new ArrayList<>(countiesFlattened.keySet());
    }


    private void add(String name, Long circumscription) {
        counties.put(name.trim().toLowerCase(), circumscription);
    }

    public boolean contains(String county)
    {
        return countiesFlattened.containsKey(county) || counties.containsKey(county);
    }

    private void init() {


        add("ALBA", 1L);

        add("ARAD", 2L);

        add("ARGEŞ", 3L);

        add("BACĂU", 4L);

        add("BIHOR", 5L);

        add("BISTRIŢA-NĂSĂUD", 6L);

        add("BOTOŞANI", 7L);

        add("BRAŞOV", 8L);

        add("BRĂILA", 9L);

        add("BUZĂU", 10L);

        add("CARAŞ-SEVERIN", 11L);

        add("CĂLĂRAŞI", 12L);

        add("CLUJ", 13L);

        add("CONSTANŢA", 14L);

        add("COVASNA", 15L);

        add("DÂMBOVIŢA", 16L);

        add("DOLJ", 17L);

        add("GALAŢI", 18L);

        add("GIURGIU", 19L);

        add("GORJ", 20L);

        add("HARGHITA", 21L);

        add("HUNEDOARA", 22L);

        add("IALOMIŢA", 23L);

        add("IAŞI", 24L);

        add("ILFOV", 25L);

        add("MARAMUREŞ", 26L);

        add("MEHEDINŢI", 27L);

        add("MUREŞ", 28L);

        add("NEAMŢ", 29L);

        add("OLT", 30L);

        add("PRAHOVA", 31L);

        add("SATU-MARE", 32L);

        add("SĂLAJ", 33L);

        add("SIBIU", 34L);

        add("SUCEAVA", 35L);

        add("TELEORMAN", 36L);

        add("TIMIŞ", 37L);

        add("TULCEA", 38L);

        add("VASLUI", 39L);

        add("VÂLCEA", 40L);

        add("VRANCEA", 41L);

        add("BUCUREȘTI", 42L);

        add("DIASPORA", 43L);


    }
}
