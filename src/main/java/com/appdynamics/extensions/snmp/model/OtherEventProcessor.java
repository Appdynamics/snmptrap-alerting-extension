package com.appdynamics.extensions.snmp.model;

import com.appdynamics.extensions.alerts.customevents.Event;
import com.appdynamics.extensions.alerts.customevents.EventSummary;
import com.appdynamics.extensions.alerts.customevents.EventType;
import com.appdynamics.extensions.alerts.customevents.OtherEvent;
import com.appdynamics.extensions.snmp.CommonUtils;
import com.appdynamics.extensions.snmp.api.ControllerApiService;
import com.appdynamics.extensions.snmp.api.models.ControllerEvent;
import com.appdynamics.extensions.snmp.api.models.EntityDefinition;
import com.appdynamics.extensions.snmp.api.models.Node;
import com.appdynamics.extensions.snmp.config.Configuration;
import com.appdynamics.extensions.snmp.config.ControllerConfig;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.List;

class OtherEventProcessor extends EventProcessor {

    private static Logger logger = Logger.getLogger(OtherEventProcessor.class);

    OtherEventProcessor(ControllerApiService controllerApiService) {
        this.controllerApiService = controllerApiService;
    }

    @Override
    void build(Configuration config, ADSnmpData snmpData, Event event) {
        OtherEvent otherEvent = (OtherEvent)event;
        snmpData.setApplication(otherEvent.getAppName());
        snmpData.setTriggeredBy(otherEvent.getEventNotificationName());
        snmpData.setEventTime(otherEvent.getEventNotificationTime());
        snmpData.setSeverity(otherEvent.getSeverity());
        snmpData.setType(getTypes(otherEvent));
        snmpData.setSummary(getSummary(otherEvent));
        snmpData.setSubtype(" ");
        snmpData.setNodes(" ");
        snmpData.setTxns(" ");
        snmpData.setMachines(" ");
        snmpData.setTiers(" ");
        snmpData.setIpAddresses(" ");
        if(config.getController() != null) {
            snmpData.setLink(CommonUtils.getAlertUrl(otherEvent));
        }
        ControllerEvent controllerEvent = getEventFromApi(config,otherEvent.getAppID(),otherEvent.getEventNotificationId());
        if(controllerEvent != null){
            snmpData.setSubtype(controllerEvent.getSubtype());
            snmpData.setLink(controllerEvent.getDeepLinkUrl());
            List<String> nodes = getEntities(controllerEvent, APPLICATION_COMPONENT_NODE);
            List<String> bts = getEntities(controllerEvent, BUSINESS_TRANSACTION);
            List<String> tiers = getEntities(controllerEvent, APPLICATION_COMPONENT);
            List<String> machines = Lists.newArrayList();
            List<String> ipAddresses = Lists.newArrayList();
            populateMachineInfo(config.getController(),machines,ipAddresses,otherEvent.getAppID(),nodes);
            if(!nodes.isEmpty()){
                snmpData.setNodes(JOIN_ON_COMMA.join(nodes));
            }
            if(!bts.isEmpty()){
                snmpData.setTxns(JOIN_ON_COMMA.join(bts));
            }
            if(!tiers.isEmpty()){
                snmpData.setTiers(JOIN_ON_COMMA.join(tiers));
            }
            if(!machines.isEmpty()){
                snmpData.setMachines(JOIN_ON_COMMA.join(machines));
            }
            if(!ipAddresses.isEmpty()){
                snmpData.setIpAddresses(JOIN_ON_COMMA.join(ipAddresses));
            }
        }
        snmpData.setTag(otherEvent.getTag());
        snmpData.setEventType("NON_POLICY_EVENT");
        snmpData.setIncidentId(otherEvent.getEventNotificationId());
        snmpData.setAccountId(CommonUtils.cleanUpAccountInfo(otherEvent.getAccountId()));
    }

    private void populateMachineInfo(ControllerConfig controller,List<String> machines, List<String> ipAddresses, String appID, List<String> affectedNodes) {
        for(String affectedNode : affectedNodes) {
            List<Node> nodes = null;
            try {
                nodes = getNodeFromNodeName(controller,Integer.parseInt(appID), affectedNode);
                collectMachineInfo(machines, ipAddresses, nodes);
            } catch (UnsupportedEncodingException e) {
                logger.error("Encoding error",e);
            }
        }
    }

    private List<String> getEntities(ControllerEvent controllerEvent,String entityType) {
        List<String> entities = Lists.newArrayList();
        if(controllerEvent.getAffectedEntities() != null){
            for(EntityDefinition entityDefinition : controllerEvent.getAffectedEntities()){
                if(entityDefinition.getEntityType().equalsIgnoreCase(entityType)){
                    entities.add(entityDefinition.getName());
                }
            }
        }
        return entities;
    }


    private ControllerEvent getEventFromApi(Configuration config,String appID, String eventNotificationId) {
        ControllerConfig controller = config.getController();
        String endpoint = null;
        try {
            endpoint = endpointBuilder.getEventsEndpoint(controller,appID,config.getEventTypes(),config.getSeverities(),config.getDurationInMins());
            List<ControllerEvent> events = controllerApiService.getEvents(endpoint);
            for(ControllerEvent event : events){
                if(event.getId() == Long.parseLong(eventNotificationId)){
                    logger.debug("Found a match for eventId=" + eventNotificationId + " from events api");
                    return event;
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("Encoding error",e);
        }
        logger.warn("Match not found from events apis for event id=" + eventNotificationId);
        return null;
    }




    private String getSummary(OtherEvent otherEvent) {
        StringBuilder summaries = new StringBuilder("");
        if(otherEvent.getEventSummaries() != null){
            for(EventSummary eventSummary : otherEvent.getEventSummaries()){
                summaries.append(eventSummary.getEventSummaryString()).append(" ");
            }
        }
        return summaries.toString();
    }


    private String getTypes(OtherEvent otherEvent) {
        StringBuilder types = new StringBuilder("");
        if(otherEvent.getEventTypes() != null){
            for(EventType eventType : otherEvent.getEventTypes()){
                types.append(eventType.getEventType()).append(" ");
            }
        }
        return types.toString();
    }


}
