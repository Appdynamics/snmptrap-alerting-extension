package com.appdynamics.extensions.snmp;


import com.appdynamics.extensions.alerts.customevents.*;
import com.appdynamics.extensions.snmp.config.Configuration;
import com.google.common.base.Strings;
import org.apache.log4j.Logger;

public class SNMPDataBuilder {

    private static Logger logger = Logger.getLogger(SNMPDataBuilder.class);




    public ADSnmpData buildFromHealthRuleViolationEvent(HealthRuleViolationEvent violationEvent,Configuration config){
        ADSnmpData snmpData = new ADSnmpData();
        snmpData.setApplication(violationEvent.getAppName());
        snmpData.setTriggeredBy(violationEvent.getHealthRuleName());
        snmpData.setNodes(getNodes(violationEvent));
        snmpData.setTxns(getBTs(violationEvent));
        snmpData.setMachines(getNodes(violationEvent));
        snmpData.setTiers(getTiers(violationEvent));
        snmpData.setEventTime(violationEvent.getPvnAlertTime());
        snmpData.setSeverity(violationEvent.getSeverity());
        snmpData.setType(violationEvent.getAffectedEntityType());
        snmpData.setSubtype(" ");
        snmpData.setSummary(violationEvent.getSummaryMessage());
        snmpData.setLink(getAlertUrl(config.getControllerHost(), Integer.toString(config.getControllerPort()), violationEvent));
        snmpData.setTag(violationEvent.getTag());
        snmpData.setEventType(violationEvent.getEventType());
        snmpData.setIncidentId(violationEvent.getIncidentID());
        snmpData.setIpAddresses(" ");
        return snmpData;
    }




    public ADSnmpData buildFromOtherEvent(OtherEvent otherEvent,Configuration config){
        ADSnmpData snmpData = new ADSnmpData();
        snmpData.setApplication(otherEvent.getAppName());
        snmpData.setTriggeredBy(otherEvent.getEventNotificationName());
        snmpData.setNodes(" ");
        snmpData.setTxns(" ");
        snmpData.setMachines(" ");
        snmpData.setTiers(" ");
        snmpData.setEventTime(otherEvent.getEventNotificationTime());
        snmpData.setSeverity(otherEvent.getSeverity());
        snmpData.setType(getTypes(otherEvent));
        snmpData.setSubtype(" ");
        snmpData.setSummary(getSummary(otherEvent));
        snmpData.setLink(getAlertUrl(config.getControllerHost(), Integer.toString(config.getControllerPort()), otherEvent));
        snmpData.setTag(otherEvent.getTag());
        snmpData.setEventType("NON-POLICY-EVENT");
        snmpData.setIncidentId(otherEvent.getEventNotificationId());
        return snmpData;
    }




    private String getAlertUrl(String controllerHost,String controllerPort, Event event) {
        String url = event.getDeepLinkUrl();
        if(Strings.isNullOrEmpty(controllerHost) || Strings.isNullOrEmpty(controllerPort)){
            logger.debug("ControllerHost and/or ControllerPort not configured correctly.");
            return url;
        }
        int startIdx = 0;
        if(url.startsWith("http://")){
            startIdx = "http://".length();
        }
        else if(url.startsWith("https://")){
            startIdx = "https://".length();
        }
        int endIdx = url.indexOf("/",startIdx + 1);
        String toReplace = url.substring(0,endIdx);
        String alertUrl = url.replaceFirst(toReplace,controllerHost + ":" + controllerPort);
        if(event instanceof HealthRuleViolationEvent){
            alertUrl += ((HealthRuleViolationEvent) event).getIncidentID();
        }
        else{
            alertUrl += ((OtherEvent) event).getEventSummaries().get(0).getEventSummaryId();
        }
        return alertUrl;
    }


    private String getNodes(HealthRuleViolationEvent violationEvent) {
        StringBuilder nodes = new StringBuilder("");
        if(isAffectedEntityType(violationEvent, "APPLICATION_COMPONENT_NODE")){
            nodes.append(violationEvent.getAffectedEntityName());
        }
        else if(violationEvent.getEvaluationEntity() != null) {
            for (EvaluationEntity evaluationEntity : violationEvent.getEvaluationEntity()) {
                if (evaluationEntity.getType().equalsIgnoreCase("APPLICATION_COMPONENT_NODE")) {
                    nodes.append(evaluationEntity.getName() + " ");
                }
            }
        }
        return nodes.toString();
    }

    private boolean isAffectedEntityType(HealthRuleViolationEvent violationEvent, String type) {
        if(type.equalsIgnoreCase(violationEvent.getAffectedEntityType())){
            return true;
        }
        return false;
    }

    private boolean isClosedOrCancelledEvent(HealthRuleViolationEvent violationEvent) {
        if(violationEvent.getEventType().startsWith(EventTypeEnum.POLICY_CANCELED.name()) || violationEvent.getEventType().startsWith(EventTypeEnum.POLICY_CLOSE.name())){
            return true;
        }
        return false;
    }

    private String getBTs(HealthRuleViolationEvent violationEvent) {
        StringBuilder bts = new StringBuilder("");
        if(isAffectedEntityType(violationEvent, "BUSINESS_TRANSACTION")){
            bts.append(violationEvent.getAffectedEntityName());
        }
        else if(violationEvent.getEvaluationEntity() != null) {
            for (EvaluationEntity evaluationEntity : violationEvent.getEvaluationEntity()) {
                if (evaluationEntity.getType().equalsIgnoreCase("BUSINESS_TRANSACTION")) {
                    bts.append(evaluationEntity.getName() + " ");
                }
            }
        }
        return bts.toString();
    }

    private String getTiers(HealthRuleViolationEvent violationEvent) {
        StringBuilder tiers = new StringBuilder("");
        if(isAffectedEntityType(violationEvent, "APPLICATION_COMPONENT")){
            tiers.append(violationEvent.getAffectedEntityName());
        }
        else if(violationEvent.getEvaluationEntity() != null) {
            for (EvaluationEntity evaluationEntity : violationEvent.getEvaluationEntity()) {
                if (evaluationEntity.getType().equalsIgnoreCase("APPLICATION_COMPONENT")) {
                    tiers.append(evaluationEntity.getName() + " ");
                }
            }
        }
        return tiers.toString();
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
