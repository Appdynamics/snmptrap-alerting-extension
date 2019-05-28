package com.appdynamics.extensions.snmp.api;

import com.appdynamics.extensions.http.Response;
import com.appdynamics.extensions.http.SimpleHttpClient;
import com.appdynamics.extensions.snmp.api.models.BusinessTransaction;
import com.appdynamics.extensions.snmp.api.models.ControllerEvent;
import com.appdynamics.extensions.snmp.api.models.Node;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;

public class ControllerApiService {
    private static Logger logger = Logger.getLogger(ControllerApiService.class);
    private SimpleHttpClient simpleHttpClient;

    public ControllerApiService(SimpleHttpClient client) {
        this.simpleHttpClient = client;
    }

    public List<BusinessTransaction> getBTs(String endpoint) throws ServiceException {
        logger.debug("getBTs :: building http client");
        try {
            logger.debug("getBTs :: target url" + endpoint);
            Response response = simpleHttpClient.target(endpoint).get();
            BusinessTransaction[] bts = null;
            if (response != null && response.getStatus() == HttpURLConnection.HTTP_OK) {
                bts = response.json(BusinessTransaction[].class);
                if (bts != null ) {
                    logger.debug("getBTs :: returning successfully");
                    return Arrays.asList(bts);
                }
            }
        }
        catch(Exception e){
            String msg = "getBTs :: unable to get applications for " + endpoint;
            logger.error(msg,e);
            throw new ServiceException(msg,e);
        }
        finally{
            simpleHttpClient.close();
        }
        return Lists.newArrayList();
    }

    public List<Node> getNodes(String endpoint) throws ServiceException {
        logger.debug("getNodes :: building http client");
        try {
            logger.debug("getNodes :: target url" + endpoint);
            Response response = simpleHttpClient.target(endpoint).get();
            Node[] nodes = null;
            if (response != null && response.getStatus() == HttpURLConnection.HTTP_OK) {
                nodes = response.json(Node[].class);
                if (nodes != null) {
                    logger.debug("getNodes :: returning successfully");
                    return Arrays.asList(nodes);
                }
            }
        }
        catch(Exception e){
            String msg = "getNodes :: unable to get nodes for " + endpoint;
            logger.error(msg,e);
            throw new ServiceException(msg,e);
        }
        finally{
            simpleHttpClient.close();
        }
        return Lists.newArrayList();
    }

    public List<ControllerEvent> getEvents(String endpoint) throws ServiceException{
        logger.debug("getEvents :: building http client");
        try {
            logger.debug("getEvents :: target url" + endpoint);
            Response response = simpleHttpClient.target(endpoint).get();
            ControllerEvent[] events = null;
            if (response != null && response.getStatus() == HttpURLConnection.HTTP_OK) {
                events = response.json(ControllerEvent[].class);
                if (events != null) {
                    logger.debug("getEvents :: returning successfully");
                    return Arrays.asList(events);
                }
            }
        }
        catch(Exception e){
            String msg = "getEvents :: unable to get events for " + endpoint;
            logger.error(msg,e);
            throw new ServiceException(msg,e);
        }
        return Lists.newArrayList();
    }

   /* @Override
    public List<Application> getApplications(ServiceBuilder serviceBuilder,String endpoint) throws ServiceException{
        logger.debug("getApplications :: building http client");
        try {
            SimpleHttpClient simpleHttpClient = serviceBuilder.buildHttpClient(ApplicationWrapper.class);
            logger.debug("getApplications :: target url" + endpoint);
            Response response = simpleHttpClient.target(endpoint).get();
            ApplicationWrapper applicationWrapper = null;
            if (response != null && response.getStatus() == HttpURLConnection.HTTP_OK) {
                applicationWrapper = response.xml(ApplicationWrapper.class);
                if (applicationWrapper != null && applicationWrapper.getApplications() != null) {
                    logger.debug("getApplications :: returning successfully");
                    return applicationWrapper.getApplications();
                }
            }
        }
        catch(Exception e){
            String msg = "getApplications :: unable to get applications for " + endpoint;
            logger.error(msg,e);
            throw new ServiceException(msg,e);
        }
        return Lists.newArrayList();
    }

    @Override
    public List<PolicyViolation> getHealthRuleViolations(ServiceBuilder serviceBuilder, String endpoint) throws ServiceException{
        logger.debug("getHealthRuleViolations :: building http client");
        try {
            SimpleHttpClient simpleHttpClient = serviceBuilder.buildHttpClient(PolicyViolationWrapper.class);
            logger.debug("getHealthRuleViolations :: target url" + endpoint);
            Response response = simpleHttpClient.target(endpoint).get();
            PolicyViolationWrapper violationWrapper = null;
            if (response != null && response.getStatus() == HttpURLConnection.HTTP_OK) {
                violationWrapper = response.xml(PolicyViolationWrapper.class);
                if (violationWrapper != null && violationWrapper.getPolicyViolations() != null) {
                    logger.debug("getHealthRuleViolations :: returning successfully");
                    return violationWrapper.getPolicyViolations();
                }
            }
        }
        catch(Exception e){
            String msg = "getHealthRuleViolations :: unable to get applications for " + endpoint;
            logger.error(msg,e);
            throw new ServiceException(msg,e);
        }
        return Lists.newArrayList();
    }
    */


}


