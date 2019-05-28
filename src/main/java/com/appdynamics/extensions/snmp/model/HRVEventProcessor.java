package com.appdynamics.extensions.snmp.model;

import com.appdynamics.extensions.alerts.customevents.EvaluationEntity;
import com.appdynamics.extensions.alerts.customevents.Event;
import com.appdynamics.extensions.alerts.customevents.HealthRuleViolationEvent;
import com.appdynamics.extensions.snmp.CommonUtils;
import com.appdynamics.extensions.snmp.api.ControllerApiService;
import com.appdynamics.extensions.snmp.api.models.BusinessTransaction;
import com.appdynamics.extensions.snmp.api.models.Node;
import com.appdynamics.extensions.snmp.config.Configuration;
import com.appdynamics.extensions.snmp.config.ControllerConfig;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

class HRVEventProcessor extends EventProcessor {

    private static Logger logger = Logger.getLogger(HRVEventProcessor.class);

    HRVEventProcessor(ControllerApiService controllerApiService) {
        this.controllerApiService = controllerApiService;
    }

    @Override
    void build(Configuration config, ADSnmpData snmpData, Event event) {
        logger.debug("Event is a HRV event");
        HealthRuleViolationEvent violationEvent = (HealthRuleViolationEvent)event;
        snmpData.setApplication(violationEvent.getAppName());
        snmpData.setTriggeredBy(violationEvent.getHealthRuleName());
        snmpData.setEventTime(violationEvent.getPvnAlertTime());
        snmpData.setSeverity(violationEvent.getSeverity());
        snmpData.setType(violationEvent.getAffectedEntityType());
        snmpData.setSubtype(" ");
        snmpData.setSummary(violationEvent.getSummaryMessage());
        if (config.getController() != null) {
            snmpData.setLink(CommonUtils.getAlertUrl(violationEvent));
        }
        snmpData.setTag(violationEvent.getTag());
        snmpData.setEventType(violationEvent.getEventType());
        snmpData.setIncidentId(violationEvent.getIncidentID());
        snmpData.setAccountId(CommonUtils.cleanUpAccountInfo(violationEvent.getAccountId()));
        snmpData.setTxns(" ");
        snmpData.setNodes(" ");
        snmpData.setTiers(" ");
        snmpData.setMachines(" ");
        snmpData.setIpAddresses(" ");
        //get BTs
        List<String> affectedBTs = getBTs(violationEvent);
        if(!affectedBTs.isEmpty()){
            snmpData.setTxns( JOIN_ON_COMMA.join((affectedBTs)));
        }
        //get nodes
        List<String> affectedNodes = getNodes(violationEvent);
        //get tiers
        List<String> affectedTiers = getTiers(config.getController(),violationEvent);
        //get ip addresses and populate ip addresses, machine names
        if(config.isFetchMachineInfoFromApi()){
            populateMachineInfo(config.getController(),violationEvent, affectedNodes, affectedTiers, snmpData);
        }
        if(!affectedNodes.isEmpty()){
            snmpData.setNodes( JOIN_ON_COMMA.join((affectedNodes)));
        }
        if(!affectedTiers.isEmpty()){
            snmpData.setTiers( JOIN_ON_COMMA.join((affectedTiers)));
        }
    }

    private List<String> getBTs(HealthRuleViolationEvent violationEvent) {
        List<String> bts = Lists.newArrayList();
        if(isAffectedEntityType(violationEvent, BUSINESS_TRANSACTION)){
            bts.add(violationEvent.getAffectedEntityName());
        }
        else if(violationEvent.getEvaluationEntity() != null) {
            for (EvaluationEntity evaluationEntity : violationEvent.getEvaluationEntity()) {
                if (evaluationEntity.getType().equalsIgnoreCase(BUSINESS_TRANSACTION)) {
                    bts.add(evaluationEntity.getName());
                }
            }
        }
        return bts;
    }

    private List<String> getTiers(ControllerConfig controllerConfig,HealthRuleViolationEvent violationEvent) {
        List<String> tiers = Lists.newArrayList();
        if(isAffectedEntityType(violationEvent, APPLICATION_COMPONENT)){
            tiers.add(violationEvent.getAffectedEntityName());
        }
        else if(violationEvent.getEvaluationEntity() != null) {
            for (EvaluationEntity evaluationEntity : violationEvent.getEvaluationEntity()) {
                if (evaluationEntity.getType().equalsIgnoreCase(APPLICATION_COMPONENT)) {
                    tiers.add(evaluationEntity.getName());
                }
            }
        }
        //for BTs, when the health rule is configured to be triggered when the condition fails on
        // avergae number of nodes in the tier, the controller doesn't pass tier name but just the application name.
        //In such cases, tier name needs to be pulled from API.
        if(tiers.isEmpty() && isAffectedEntityType(violationEvent, BUSINESS_TRANSACTION)){
            String btTiers = getTiersFromBTApi(controllerConfig,violationEvent);
            if(!Strings.isNullOrEmpty(btTiers)){
                tiers.add(btTiers);
            }
        }
        return tiers;
    }

    private List<String> getNodes(HealthRuleViolationEvent violationEvent) {
        List<String> nodes = Lists.newArrayList();
        if(isAffectedEntityType(violationEvent, APPLICATION_COMPONENT_NODE)){
            nodes.add(violationEvent.getAffectedEntityName());
        }
        else if(violationEvent.getEvaluationEntity() != null) {
            for (EvaluationEntity evaluationEntity : violationEvent.getEvaluationEntity()) {
                if (evaluationEntity.getType().equalsIgnoreCase(APPLICATION_COMPONENT_NODE)) {
                    nodes.add(evaluationEntity.getName());
                }
            }
        }
        return nodes;
    }

    private String getTiersFromBTApi(ControllerConfig controller,HealthRuleViolationEvent violationEvent) {
        String endpoint = endpointBuilder.buildBTsEndpoint(controller,Integer.parseInt(violationEvent.getAppID()));
        List<BusinessTransaction> bts = controllerApiService.getBTs(endpoint);
        for(BusinessTransaction bt : bts){
            if(bt.getId() == Integer.parseInt(violationEvent.getAffectedEntityID())){
                return bt.getTierName();
            }
        }
        return "";
    }

    private void populateMachineInfo(ControllerConfig controller, HealthRuleViolationEvent violationEvent, List<String> affectedNodes, List<String> affectedTiers, ADSnmpData snmpData) {
        logger.debug("Affected Tiers : " + affectedTiers);
        logger.debug("Affected Nodes : " + affectedNodes);
        List<String> machines = Lists.newArrayList();
        List<String> ipAddresses = Lists.newArrayList();

        try {
            List<Node> nodesInAffectedTiers = null;
            if(!affectedTiers.isEmpty()){
                nodesInAffectedTiers  = getAllNodesFromTiers(controller,Integer.parseInt(violationEvent.getAppID()),affectedTiers);
                collectMachineInfo(machines, ipAddresses, nodesInAffectedTiers);
            }
            if(!affectedNodes.isEmpty()){
                for(String affectedNode : affectedNodes){
                    List<Node> nodes = getNodeFromNodeName(controller,Integer.parseInt(violationEvent.getAppID()),affectedNode);
                    collectMachineInfo(machines, ipAddresses, nodes);
                    //extracting tiers from the nodes and setting it..ugly..needs a clean approach.
                    affectedTiers.addAll(collectTierInfo(nodes,affectedTiers));
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("Encoding error",e);
        }
        if(!machines.isEmpty()){
            snmpData.setMachines(JOIN_ON_COMMA.join(machines));
        }
        if(!ipAddresses.isEmpty()){
            snmpData.setIpAddresses(JOIN_ON_COMMA.join(ipAddresses));
        }
    }


    private List<Node> getAllNodesFromTiers(ControllerConfig controller, int applicationId, List<String> tiers) throws UnsupportedEncodingException {
        List<Node> nodes = Lists.newArrayList();
        for(String tier:tiers){
            nodes.addAll(getAllNodesInTier(controller,applicationId,tier));
        }
        return nodes;
    }

    private List<Node> getAllNodesInTier(ControllerConfig controller,int applicationId,String tier) throws UnsupportedEncodingException {
        String endpoint = endpointBuilder.getNodesFromTierEndpoint(controller,applicationId,tier);
        return controllerApiService.getNodes(endpoint);
    }

    private boolean isAffectedEntityType(HealthRuleViolationEvent violationEvent, String type) {
        if(type.equalsIgnoreCase(violationEvent.getAffectedEntityType())){
            return true;
        }
        return false;
    }

    private Set<String> collectTierInfo(List<Node> nodes, List<String> affectedTiers) {
        Set<String> setOfAffectedTiers = Sets.newHashSet(affectedTiers);
        for(Node aNode : nodes){
            setOfAffectedTiers.add(aNode.getTierName());
        }
        return setOfAffectedTiers;
    }


}
