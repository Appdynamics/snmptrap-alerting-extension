/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.snmp;


import com.appdynamics.extensions.alerts.customevents.Event;
import com.appdynamics.extensions.alerts.customevents.EventBuilder;
import com.appdynamics.extensions.alerts.customevents.HealthRuleViolationEvent;
import com.appdynamics.extensions.alerts.customevents.OtherEvent;
import com.appdynamics.extensions.http.SimpleHttpClient;
import com.appdynamics.extensions.snmp.api.ControllerApiService;
import com.appdynamics.extensions.snmp.api.HttpClientBuilder;
import com.appdynamics.extensions.snmp.config.ConfigLoader;
import com.appdynamics.extensions.snmp.config.Configuration;
import com.appdynamics.extensions.snmp.config.ControllerConfig;
import com.appdynamics.extensions.snmp.model.ADSnmpData;
import com.appdynamics.extensions.snmp.model.SNMPDataBuilder;
import org.apache.log4j.Logger;

import java.util.Arrays;

public class SnmpTrapAlertExtension {

    private static final String TRAP_OID_NOTIFICATIONS = "1.3.6.1.4.1.40684.1.1.1.500";
    private static final String TRAP_OID_01 = "1.3.6.1.4.1.40684.1.1.1.500.1";
    private static final String TRAP_OID_02 = "1.3.6.1.4.1.40684.1.1.1.500.2";
    private static final String TRAP_OID_03 = "1.3.6.1.4.1.40684.1.1.1.500.3";
    private static final String TRAP_OID_04 = "1.3.6.1.4.1.40684.1.1.1.500.4";
    private static final String TRAP_OID_05 = "1.3.6.1.4.1.40684.1.1.1.500.5";
    private static final String TRAP_OID_06 = "1.3.6.1.4.1.40684.1.1.1.500.6";
    private static final String TRAP_OID_07 = "1.3.6.1.4.1.40684.1.1.1.500.7";
    private static final String TRAP_OID_08 = "1.3.6.1.4.1.40684.1.1.1.500.8";
    private static final String TRAP_OID_09 = "1.3.6.1.4.1.40684.1.1.1.500.9";
    private static final String TRAP_OID_10 = "1.3.6.1.4.1.40684.1.1.1.500.10";
    private static final String TRAP_OID_11 = "1.3.6.1.4.1.40684.1.1.1.500.11";

    private static Logger logger = Logger.getLogger(SnmpTrapAlertExtension.class);

    //To create the AppDynamics Health Rule Violation event
    private static final EventBuilder eventBuilder = new EventBuilder();

    private static final SNMPSender snmpSender = new SNMPSender();

    private Configuration config;

    public SnmpTrapAlertExtension(Configuration config){
        String msg = "SnmpTrapAlertExtension Version ["+getImplementationTitle()+"]";
        logger.info(msg);
        this.config = config;
    }


    public static void main(String[] args){
        try {
            logger.info("\n\n*************START****************");
            logger.debug("Args passed => " + Arrays.asList(args));
            if (args == null || args.length == 0) {
                logger.error("No arguments passed to the extension, exiting the program.");
                return;
            }
            Event event = eventBuilder.build(args);

            Configuration config = ConfigLoader.getConfig();
            SnmpTrapAlertExtension trapExtension = new SnmpTrapAlertExtension(config);
            boolean status = trapExtension.process(event);
            if (status) {
                logger.info("SnmpTrapAlertExtension completed successfully.");
                return;
            }
            logger.error("SnmpTrapAlertExtension completed with errors");
        } catch(Exception e){
            logger.error("Error in the execution of the extension",e);
        } finally{
            logger.info("*************END******************\n\n");
        }
    }


    private String getImplementationTitle(){
        return this.getClass().getPackage().getImplementationTitle();
    }


    public boolean process(Event event) {
        if(event != null){
            logger.debug("Processing an Event");
            ControllerConfig controller = config.getController();
            logger.debug("Building HTTP client");
            SimpleHttpClient httpClient = buildHttpClient(controller);
            ADSnmpData snmpData = new SNMPDataBuilder(config,new ControllerApiService(httpClient)).build(event);
            logger.debug("SNMP Data => " + snmpData);
            String trapOid = getOID(event);
            logger.debug("Trap OID => " + trapOid);
            try {
                snmpSender.sendTrap(config, snmpData,trapOid);
                logger.info("-------------Trap Sent!---------------");
                return true;
            } catch (SNMPTrapException e){
                logger.error("Error in sending one or more traps",e);
            }
        }
        return false;
    }

    private SimpleHttpClient buildHttpClient(ControllerConfig controller) {
        HttpClientBuilder clientBuilder = new HttpClientBuilder(controller.isUseSsl(), controller.getUserAccount(), controller.getPassword(), controller.getConnectTimeoutInSeconds() * 1000, controller.getSocketTimeoutInSeconds() * 1000);
        return clientBuilder.buildHttpClient();
    }


    /**
     * Determines and sets the appropriate OID for the notification based on the value set
     * for the configuration option "mib-version".
     *
     */
    private String getOID(Event event) {
        String TRAP_OID = TRAP_OID_01;
        if(event instanceof OtherEvent) {
            switch (config.getMibVersion()) {
                case 1:
                    TRAP_OID = TRAP_OID_NOTIFICATIONS;
                    break;
                case 2:
                    TRAP_OID = TRAP_OID_03;
                    break;
                case 3:
                    TRAP_OID = TRAP_OID_07;
                    break;
            }
            return TRAP_OID;
        }

        HealthRuleViolationEvent violationEvent = (HealthRuleViolationEvent) event;
        String eventType = violationEvent.getEventType();
        switch (config.getMibVersion()) {
            case 1:
                TRAP_OID = TRAP_OID_NOTIFICATIONS;
                break;

            case 2:
                if (eventType.startsWith(EventTypeEnum.POLICY_CLOSE.name()) || eventType.startsWith(EventTypeEnum.POLICY_CANCELED.name())) {
                    TRAP_OID = TRAP_OID_02;
                } else {
                    TRAP_OID = TRAP_OID_01;
                }
                break;

            case 3:
                if (eventType.equals(EventTypeEnum.POLICY_OPEN_WARNING.name())) {
                    TRAP_OID = TRAP_OID_01;
                } else if (eventType.equals(EventTypeEnum.POLICY_OPEN_CRITICAL.name())) {
                    TRAP_OID = TRAP_OID_02;
                } else if (eventType.equals(EventTypeEnum.POLICY_UPGRADED.name())) {
                    TRAP_OID = TRAP_OID_03;
                } else if (eventType.equals(EventTypeEnum.POLICY_DOWNGRADED.name())) {
                    TRAP_OID = TRAP_OID_04;
                } else if (eventType.equals(EventTypeEnum.POLICY_CLOSE_WARNING.name())) {
                    TRAP_OID = TRAP_OID_05;
                } else if (eventType.equals(EventTypeEnum.POLICY_CLOSE_CRITICAL.name())) {
                    TRAP_OID = TRAP_OID_06;
                } else if (eventType.equals("NON_POLICY_EVENT")) {
                    TRAP_OID = TRAP_OID_07;
                } else if(eventType.equals(EventTypeEnum.POLICY_CANCELED_WARNING.name())) {
                    TRAP_OID = TRAP_OID_08;
                } else if(eventType.equals(EventTypeEnum.POLICY_CANCELED_CRITICAL.name())) {
                    TRAP_OID = TRAP_OID_09;
                } else if(eventType.equals(EventTypeEnum.POLICY_CONTINUES_WARNING.name())) {
                    TRAP_OID = TRAP_OID_10;
                } else if(eventType.equals(EventTypeEnum.POLICY_CONTINUES_CRITICAL.name())) {
                    TRAP_OID = TRAP_OID_11;
                } else {
                    TRAP_OID = TRAP_OID_05;
                }
                break;

            default:
                TRAP_OID = TRAP_OID_01;
                break;
        }
        return TRAP_OID;

    }



}
