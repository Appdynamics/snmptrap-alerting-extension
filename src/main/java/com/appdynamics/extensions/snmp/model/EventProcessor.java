package com.appdynamics.extensions.snmp.model;

import com.appdynamics.extensions.alerts.customevents.Event;
import com.appdynamics.extensions.snmp.api.ControllerApiService;
import com.appdynamics.extensions.snmp.api.EndpointBuilder;
import com.appdynamics.extensions.snmp.api.models.Node;
import com.appdynamics.extensions.snmp.config.Configuration;
import com.appdynamics.extensions.snmp.config.ControllerConfig;
import com.google.common.base.Joiner;

import java.io.UnsupportedEncodingException;
import java.util.List;

abstract class EventProcessor {

    ControllerApiService controllerApiService;
    EndpointBuilder endpointBuilder = new EndpointBuilder();

    static final Joiner JOIN_ON_COMMA = Joiner.on(",");
    static final String APPLICATION_COMPONENT_NODE = "APPLICATION_COMPONENT_NODE";
    static final String BUSINESS_TRANSACTION = "BUSINESS_TRANSACTION";
    static final String APPLICATION_COMPONENT = "APPLICATION_COMPONENT";

    abstract void build(Configuration config,ADSnmpData snmpData, Event violationEvent);

    void collectMachineInfo(List<String> machines, List<String> ipAddresses, List<Node> nodesInAffectedTiers) {
        if(nodesInAffectedTiers != null){
            for(Node aNode : nodesInAffectedTiers){
                machines.add(aNode.getMachineName());
                ipAddresses.addAll(aNode.getIpAddresses());
            }
        }
    }

    List<Node> getNodeFromNodeName(ControllerConfig controller, int appId, String affectedNode) throws UnsupportedEncodingException {
        String endpoint = endpointBuilder.getANodeEndpoint(controller,appId,affectedNode);
        List<Node> nodes = controllerApiService.getNodes(endpoint);
        return nodes;
    }

}
