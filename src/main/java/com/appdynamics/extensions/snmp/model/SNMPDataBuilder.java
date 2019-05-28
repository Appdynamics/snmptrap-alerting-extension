/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.snmp.model;


import com.appdynamics.extensions.alerts.customevents.Event;
import com.appdynamics.extensions.snmp.api.ControllerApiService;
import com.appdynamics.extensions.snmp.config.Configuration;
import org.apache.log4j.Logger;

public class SNMPDataBuilder {

    private static Logger logger = Logger.getLogger(SNMPDataBuilder.class);

    private Configuration config;
    private ControllerApiService controllerApiService;

    public SNMPDataBuilder(Configuration config,ControllerApiService controllerApiService) {
        this.config = config;
        this.controllerApiService = controllerApiService;
    }

    public ADSnmpData build(Event event) {
        ADSnmpData adSnmpData = new ADSnmpData();
        EventProcessor eventProcessor = EventProcessorFactory.init(event,controllerApiService);
        eventProcessor.build(config,adSnmpData,event);
        return adSnmpData;
    }














}
