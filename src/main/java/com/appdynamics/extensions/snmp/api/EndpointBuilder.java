/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.snmp.api;


import com.appdynamics.extensions.snmp.config.ControllerConfig;
import com.google.common.base.Joiner;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class EndpointBuilder {

;
    private static final String APP_ID_HOLDER = "<#APP_ID#>";
    private static final String TIER_HOLDER = "<#TIER#>";
    private static final String NODE_HOLDER = "<#NODE#>";
    private static final String BT_ENDPOINT = "/controller/rest/applications/"+ APP_ID_HOLDER + "/business-transactions";
    private static final String NODES_ENDPOINT = "/controller/rest/applications/"+ APP_ID_HOLDER + "/nodes";
    private static final String NODES_FROM_TIER_ENDPOINT = "/controller/rest/applications/"+ APP_ID_HOLDER + "/tiers/" + TIER_HOLDER + "/nodes";
    private static final String A_NODE_ENDPOINT = "/controller/rest/applications/"+ APP_ID_HOLDER + "/nodes/" + NODE_HOLDER;
    private static final String EVENTS_ENDPOINT = "/controller/rest/applications/"+ APP_ID_HOLDER + "/events";


    public String buildBTsEndpoint(ControllerConfig controller,int applicationId) {
        StringBuffer sb = controller.getControllerBaseUrl(controller).append(BT_ENDPOINT).append("?output=json");
        String endpoint =  sb.toString();
        return endpoint.replaceFirst(APP_ID_HOLDER,Integer.toString(applicationId));
    }

    public String buildNodesEndpoint(ControllerConfig controller,int applicationId) {
        StringBuffer sb = controller.getControllerBaseUrl(controller).append(NODES_ENDPOINT);
        String endpoint =  sb.toString();
        return endpoint.replaceFirst(APP_ID_HOLDER,Integer.toString(applicationId));
    }

    public String getNodesFromTierEndpoint(ControllerConfig controller,int applicationId,String tier) throws UnsupportedEncodingException {
        StringBuffer sb = controller.getControllerBaseUrl(controller).append(NODES_FROM_TIER_ENDPOINT);
        sb.append("?output=json");
        String endpoint = sb.toString();
        endpoint = endpoint.replaceFirst(APP_ID_HOLDER,Integer.toString(applicationId));
        endpoint = endpoint.replaceFirst(TIER_HOLDER, URLEncoder.encode(tier,"UTF-8"));
        return endpoint;
    }

    public String getANodeEndpoint(ControllerConfig controller,int applicationId,String node) throws UnsupportedEncodingException {
        StringBuffer sb = controller.getControllerBaseUrl(controller).append(A_NODE_ENDPOINT);
        sb.append("?output=json");
        String endpoint = sb.toString();
        endpoint = endpoint.replaceFirst(APP_ID_HOLDER,Integer.toString(applicationId));
        endpoint = endpoint.replaceFirst(NODE_HOLDER,URLEncoder.encode(node,"UTF-8"));
        return endpoint;
    }




    public String getEventsEndpoint(ControllerConfig controller, String applicationId, List<String> eventTypes, List<String> severities, int durationInMins) throws UnsupportedEncodingException {
        StringBuffer sb = controller.getControllerBaseUrl(controller).append(EVENTS_ENDPOINT);
        sb.append("?");
        sb.append("time-range-type=" + URLEncoder.encode("BEFORE_NOW","UTF-8"));
        sb.append("&duration-in-mins=" + URLEncoder.encode(Integer.toString(durationInMins),"UTF-8"));
        sb.append("&severities=" + URLEncoder.encode(Joiner.on(",").join(severities),"UTF-8"));
        sb.append("&event-types=" + URLEncoder.encode(Joiner.on(",").join(eventTypes),"UTF-8"));
        sb.append("&output=json");
        String endpoint = sb.toString();
        endpoint = endpoint.replaceFirst(APP_ID_HOLDER,applicationId);
        return endpoint;
    }

}
