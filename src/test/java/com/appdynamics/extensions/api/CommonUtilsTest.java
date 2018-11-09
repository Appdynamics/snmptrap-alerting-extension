/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.api;


import com.appdynamics.extensions.alerts.customevents.HealthRuleViolationEvent;
import com.appdynamics.extensions.snmp.CommonUtils;
import org.junit.Assert;
import org.junit.Test;

public class CommonUtilsTest {

    @Test
    public void testAlertUrl(){
        HealthRuleViolationEvent event = new HealthRuleViolationEvent();
        event.setDeepLinkUrl("https://localhost:8080/#location=APP_INCIDENT_DETAIL&incident=");
        event.setIncidentID("12345");
        String url = CommonUtils.getAlertUrl(event);
        Assert.assertTrue(url.equalsIgnoreCase("https://localhost:8080/#location=APP_INCIDENT_DETAIL&incident=12345"));
    }

    @Test
    public void testCleanupAccountInfo(){
        String accountId = "JAMS_034cda68-fb5c-4914-b161-5a35eff69a6c";
        String accountName = CommonUtils.cleanUpAccountInfo(accountId);
        Assert.assertTrue(accountName.equalsIgnoreCase("JAMS"));
    }

    @Test
    public void testCleanupAccountInfoWithFullAccountName(){
        String accountId = "CONTENT_MANAGEMENT_ON_DEMAND_PRD";
        String accountName = CommonUtils.cleanUpAccountInfo(accountId);
        Assert.assertEquals("Should return same account name if full name is given", accountId, accountName);
    }
}