/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.snmp.config;


import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class EngineProperties extends Properties{

    private static final int DEFAULT_ENGINE_BOOT_COUNT = 0;
    private long referenceDate = 1443121086695L; //Sept 24 2015 11:159am
    private static Logger logger = Logger.getLogger(EngineProperties.class);

    public static final String ENGINE_BOOTS = "engineBoots";
    private String propertiesFile;

    //keeps track of number of times SNMP engine was booted. We keep it 0 and play with the engineTime only.
    private int engineBoots = DEFAULT_ENGINE_BOOT_COUNT;

    private int engineTime;

    public EngineProperties(String propertiesFile) throws IOException {
        this.propertiesFile = propertiesFile;
        //load();
        calculateProps();
    }

    private void calculateProps() {
        long currentTime = System.currentTimeMillis();
        long prevTime = referenceDate;
        long timeDiffInMs = TimeUnit.MILLISECONDS.toSeconds(currentTime - prevTime);
        engineTime = (int)(timeDiffInMs % 2147483648L);
    }

    public int getEngineBoots() {
        return engineBoots;
    }

    /*public void load() throws IOException {
        FileInputStream is = null;
        try{
            is = new FileInputStream(propertiesFile);
            load(is);
            engineBoots = Integer.parseInt(getProperty(ENGINE_BOOTS, "0"));
        }
        finally {
            is.close();
        }
    }



    public void store() throws IOException {
        engineBoots++;
        setProperty(ENGINE_BOOTS, Integer.toString(engineBoots));
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(propertiesFile);
            store(fos,null);
        }
        finally {
            fos.close();
        }
    }*/


    @Override
    public String toString() {
        return "EngineProperties{" +
                "engineBoots=" + engineBoots +
                ",engineTime=" + engineTime +
                '}';
    }

    public int getEngineTime() {
        return engineTime;
    }
}
