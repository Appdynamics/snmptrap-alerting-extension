/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.snmp.config;


import com.appdynamics.extensions.crypto.CryptoUtil;
import com.appdynamics.extensions.yml.YmlReader;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Map;

import static com.appdynamics.TaskInputArgs.ENCRYPTION_KEY;
import static com.appdynamics.TaskInputArgs.PASSWORD_ENCRYPTED;


public class ConfigLoader {

    private static final String TRAP_SENDER_HOME = "SNMP_TRAP_SENDER_HOME";
    private static final String CONFIG_FILENAME =  "config.yaml";
    private static final String CONFIG_DIR = "conf" + File.separator;
    private static final String SNMP_ENGINE_PROPERTIES = "snmp_engine.properties";
    private static Logger logger = Logger.getLogger(ConfigLoader.class);

    public static Configuration getConfig(){
        String configFile = getConfigFile();
        Configuration config = YmlReader.readFromFile(configFile, Configuration.class);
        if(validateEncryptionFields(config)) {
            decryptPasswords(config);
        }
        logger.debug("Configuration Loaded.");
        return config;
    }

    private static String getConfigFile() {
        String trapSenderHome = getTrapSenderHome();
        return trapSenderHome + CONFIG_DIR + CONFIG_FILENAME;
    }

    private static boolean validateEncryptionFields(Configuration config) {
        if (!Strings.isNullOrEmpty(config.getEncryptionKey())) {
            return true;
        }
        return false;
    }

    private static void decryptPasswords(Configuration config) {
        if(config.getController() != null && !Strings.isNullOrEmpty(config.getController().getEncryptedPassword())){
            Map<String,String> taskArgs = createTaskArgs(config.getEncryptionKey(),config.getController().getEncryptedPassword());
            config.getController().setPassword(CryptoUtil.getPassword(taskArgs));
        }
        if(config.getSnmpV3Configuration() != null) {
            if (!Strings.isNullOrEmpty(config.getSnmpV3Configuration().getEncryptedPassword())) {
                Map<String, String> taskArgs = createTaskArgs(config.getEncryptionKey(), config.getSnmpV3Configuration().getEncryptedPassword());
                config.getSnmpV3Configuration().setPassword(CryptoUtil.getPassword(taskArgs));
            }
            if (!Strings.isNullOrEmpty(config.getSnmpV3Configuration().getEncryptedPrivProtocolPassword())) {
                Map<String, String> taskArgs = createTaskArgs(config.getEncryptionKey(), config.getSnmpV3Configuration().getEncryptedPrivProtocolPassword());
                config.getSnmpV3Configuration().setPrivProtocolPassword(CryptoUtil.getPassword(taskArgs));
            }
        }
    }

    private static Map<String,String> createTaskArgs(String encryptionKey, String password) {
        Map<String,String> taskArgs = Maps.newHashMap();
        taskArgs.put(ENCRYPTION_KEY, encryptionKey);
        taskArgs.put(PASSWORD_ENCRYPTED, password);
        return taskArgs;
    }

    private static String getTrapSenderHome() {
        return System.getProperty(TRAP_SENDER_HOME,"");
    }


    public static String getEngineConfig() {
        String enginePropFile = CONFIG_DIR + SNMP_ENGINE_PROPERTIES;
        String trapSenderHome = getTrapSenderHome();
        return trapSenderHome + enginePropFile;
    }

}
