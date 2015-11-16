package com.bocse.multumesc.data;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by bocse on 16.11.2015.
 */
public class Counties {
    Set<String> counties = new HashSet<>();

    private void add(String name) {
        counties.add(name.trim().toLowerCase());
    }

    private void init() {


        add("ALBA");

        add("ARAD");

        add("ARGEŞ");

        add("BACĂU");

        add("BIHOR");

        add("BISTRIŢA-NĂSĂUD");

        add("BOTOŞANI");

        add("BRAŞOV");

        add("BRĂILA");

        add("BUZĂU");

        add("CARAŞ-SEVERIN");

        add("CĂLĂRAŞI");

        add("CLUJ");

        add("CONSTANŢA");

        add("COVASNA");

        add("DÂMBOVIŢA");

        add("DOLJ");

        add("GALAŢI");

        add("GIURGIU");

        add("GORJ");

        add("HARGHITA");

        add("HUNEDOARA");

        add("IALOMIŢA");

        add("IAŞI");

        add("ILFOV");

        add("MARAMUREŞ");

        add("MEHEDINŢI");

        add("MUREŞ");

        add("NEAMŢ");

        add("OLT");

        add("PRAHOVA");

        add("SATU MARE");

        add("SĂLAJ");

        add("SIBIU");

        add("SUCEAVA");

        add("TELEORMAN");

        add("TIMIŞ");

        add("TULCEA");

        add("VASLUI");

        add("VÂLCEA");

        add("VRANCEA");

        add("BUCUREȘTI");

        add("STRĂINĂTATE");


    }
}
