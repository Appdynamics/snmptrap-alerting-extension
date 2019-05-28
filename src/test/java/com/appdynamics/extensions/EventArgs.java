/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions;


import com.appdynamics.extensions.alerts.customevents.Event;
import com.appdynamics.extensions.alerts.customevents.EventBuilder;
import com.appdynamics.extensions.snmp.SnmpTrapAlertExtension;
import com.appdynamics.extensions.snmp.config.ConfigLoader;
import com.appdynamics.extensions.snmp.config.Configuration;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

public class EventArgs {

    public String[] getHealthRuleViolationEventWithOneEvalEntityAndTriggerNoBaseline(){
        List<String> strings = Lists.newArrayList();
        generateLeadingArgs(strings);

        strings.add("\"1\"");  //number of eval entities
        strings.add("\"APPLICATION_COMPONENT_NODE\"");   //eval entity type
        strings.add("\"ECommerce_JMS_NODE\""); //eval entity name
        strings.add("\"8\"");  //eval entity id
        strings.add("\"1\"");  //number of triggered cond per eval entity
        strings.add("\"APPLICATION_COMPONENT_NODE\""); //scope type 1
        strings.add("\"ECommerce_WEB1_NODE\""); //scope name 1
        strings.add("\"8\"");  //scope id 1
        strings.add("\"Hardware Resources|CPU|%Busy Condition\""); // condition name 1
        strings.add("\"113\"");  //condition id 1
        strings.add("\"GREATER_THAN\"");  //operator 1
        strings.add("\"ABSOLUTE\"");  //condition unit type 1
        strings.add("\"4\"");  //threshhold value 1
        strings.add("\"40.0\"");  //observed value 1

        generateTrailingArg(strings);
        generateAccountInfo(strings);
        return Iterables.toArray(strings,String.class);
    }

    private void generateAccountInfo(List<String> strings) {
        strings.add("customer1");
        strings.add("12sab23asl23");
    }


    public String[] getHealthRuleViolationEventWithMultipleEvalEntityAndATriggerNoBaseline(){
        List<String> strings = Lists.newArrayList();
        generateLeadingArgs(strings);
        strings.add("\"2\"");  //number of eval entities
            strings.add("\"APPLICATION_COMPONENT_NODE\"");   //eval entity type
            strings.add("\"ECommerce_JMS_NODE\""); //eval entity name
            strings.add("\"8\"");  //eval entity id
            strings.add("\"1\"");  //number of triggered cond per eval entity
            strings.add("\"APPLICATION_COMPONENT_NODE\""); //scope type 1
            strings.add("\"ECommerce_WEB1_NODE\""); //scope name 1
            strings.add("\"8\"");  //scope id 1
            strings.add("\"Hardware Resources|CPU|%Busy Condition\""); // condition name 1
            strings.add("\"113\"");  //condition id 1
            strings.add("\"GREATER_THAN\"");  //operator 1
            strings.add("\"ABSOLUTE\"");  //condition unit type 1
            strings.add("\"4\"");  //threshhold value 1
            strings.add("\"40.0\"");  //observed value 1

            strings.add("\"APPLICATION\"");   //eval entity type
            strings.add("\"AD-Travel\""); //eval entity name
            strings.add("\"9\"");  //eval entity id
            strings.add("\"1\"");  //number of triggered cond per eval entity
            strings.add("\"APPLICATION\""); //scope type 2
            strings.add("\"AD-Financial-Cloud\""); //scope name 2
            strings.add("\"10\"");  //scope id 2
            strings.add("\"Hardware Resources|CPU|%Busy Condition\""); // condition name 2
            strings.add("\"114\"");  //condition id 2
            strings.add("\"LESS_THAN\"");  //operator 2
            strings.add("\"ABSOLUTE\"");  //condition unit type 2
            strings.add("\"5\"");  //threshhold value 2
            strings.add("\"20.0\"");  //observed value 2

        //summary message
        generateTrailingArg(strings);
        generateAccountInfo(strings);
        return Iterables.toArray(strings,String.class);
    }


    public String[] getHealthRuleViolationEventWithMultipleEvalEntityAndMultipleTriggerNoBaseline(){
        List<String> strings = Lists.newArrayList();
        generateLeadingArgs(strings);
        strings.add("\"2\"");  //number of eval entities

            strings.add("\"APPLICATION_COMPONENT_NODE\"");   //eval entity type
            strings.add("\"MyMacMachineAgentNode1\""); //eval entity name
            strings.add("\"8\"");  //eval entity id
            strings.add("\"2\"");  //number of triggered cond per eval entity

                strings.add("\"APPLICATION_COMPONENT_NODE\""); //scope type 1
                strings.add("\"MyMacMachineAgentNode1\""); //scope name 1
                strings.add("\"8\"");  //scope id 1
                strings.add("\"Hardware Resources|CPU|%Busy Condition\""); // condition name 1
                strings.add("\"113\"");  //condition id 1
                strings.add("\"GREATER_THAN\"");  //operator 1
                strings.add("\"ABSOLUTE\"");  //condition unit type 1
                strings.add("\"4\"");  //threshhold value 1
                strings.add("\"40.0\"");  //observed value 1

                strings.add("\"APPLICATION_COMPONENT\""); //scope type 2
                strings.add("\"MyMacMachineAgentNode1\""); //scope name 2
                strings.add("\"9\"");  //scope id 2
                strings.add("\"Hardware Resources|CPU|%Busy Condition\""); // condition name 2
                strings.add("\"114\"");  //condition id 2
                strings.add("\"GREATER_THAN_EQUALS\"");  //operator 2
                strings.add("\"ABSOLUTE\"");  //condition unit type 2
                strings.add("\"2\"");  //threshhold value 2
                strings.add("\"50.0\"");  //observed value 2

            strings.add("\"APPLICATION\"");   //eval entity type
            strings.add("\"MyMacMachineAgentNode2\""); //eval entity name
            strings.add("\"9\"");  //eval entity id
            strings.add("\"1\"");  //number of triggered cond per eval entity

                strings.add("\"APPLICATION\""); //scope type 1
                strings.add("\"MyMacMachineAgentNode2\""); //scope name 1
                strings.add("\"10\"");  //scope id 1
                strings.add("\"Hardware Resources|CPU|%Busy Condition\""); // condition name 1
                strings.add("\"114\"");  //condition id 1
                strings.add("\"LESS_THAN\"");  //operator 1
                strings.add("\"ABSOLUTE\"");  //condition unit type 1
                strings.add("\"5\"");  //threshhold value 1
                strings.add("\"20.0\"");  //observed value 1

        //summary message
        generateTrailingArg(strings);
        generateAccountInfo(strings);
        return Iterables.toArray(strings,String.class);

    }

    public String[] getHealthRuleViolationEventWithMultipleEvalEntityAndMultipleTriggerBaseline(){

        List<String> strings = Lists.newArrayList();
        generateLeadingArgs(strings);
        strings.add("\"2\"");  //number of eval entities

        /*strings.add("\"APPLICATION_COMPONENT_NODE\"");   //eval entity type
        strings.add("\"ECommerce_JMS_NODE\""); //eval entity name*/
        strings.add("BUSINESS_TRANSACTION");
        strings.add("MessageConsumer:OrderQueue");
        strings.add("\"8\"");  //eval entity id
        strings.add("\"2\"");  //number of triggered cond per eval entity

        strings.add("\"APPLICATION_COMPONENT_NODE\""); //scope type 1
        strings.add("\"ECommerce_WEB2_NODE\""); //scope name 1
        strings.add("\"8\"");  //scope id 1
        strings.add("\"Hardware Resources|CPU|%Busy Condition\""); // condition name 1
        strings.add("\"113\"");  //condition id 1
        strings.add("\"GREATER_THAN\"");  //operator 1
        strings.add("\"BASELINE_STANDARD_DEVIATION\"");  //condition unit type 1
        strings.add("\"false\""); //use default baseline
        strings.add("\"baseline name\""); //base line name
        strings.add("\"base line id\""); //base line id
        strings.add("\"4\"");  //threshhold value 1
        strings.add("\"40.0\"");  //observed value 1

        strings.add("\"APPLICATION_COMPONENT_NODE\""); //scope type 2
        strings.add("\"ECommerce_WEB1_NODE\""); //scope name 2
        strings.add("\"9\"");  //scope id 2
        strings.add("\"Hardware Resources|CPU|%Busy Condition\""); // condition name 2
        strings.add("\"114\"");  //condition id 2
        strings.add("\"GREATER_THAN_EQUALS\"");  //operator 2
        strings.add("\"BASELINE_PERCENTAGE\"");  //condition unit type 2
        strings.add("\"true\""); //use default baseline
        strings.add("\"2\"");  //threshhold value 2
        strings.add("\"50.0\"");  //observed value 2

        strings.add("\"APPLICATION\"");   //eval entity type
        strings.add("\"AD-Financial-Cloud\""); //eval entity name
        strings.add("\"9\"");  //eval entity id
        strings.add("\"1\"");  //number of triggered cond per eval entity

        strings.add("\"APPLICATION\""); //scope type 1
        strings.add("\"AD-Travel\""); //scope name 1
        strings.add("\"10\"");  //scope id 1
        strings.add("\"Hardware Resources|CPU|%Busy Condition\""); // condition name 1
        strings.add("\"114\"");  //condition id 1
        strings.add("\"LESS_THAN\"");  //operator 1
        strings.add("\"ABSOLUTE\"");  //condition unit type 1
        strings.add("\"5\"");  //threshhold value 1
        strings.add("\"20.0\"");  //observed value 1

        //summary message
        generateTrailingArg(strings);
        generateAccountInfo(strings);
        return Iterables.toArray(strings,String.class);
    }

    public String[] getOtherEvent(){
        List<String> strings = Lists.newArrayList();
        strings.add("\"MyMacMachineAgent\""); //appname
        strings.add("\"269\""); //appID
        strings.add("\"Wed Apr 30 09:42:55 PDT 2014\""); //event notification time
        strings.add("\"1\""); //priority
        strings.add("\"ERROR\""); //severity
        strings.add("\"VictorOpsAction\"");  //tag
        strings.add("\"App Server restart\"");  //event notification name
        strings.add("\"249166438\"");  //event notification id
        strings.add("\"2\"");  //event notification time period in mins
        strings.add("\"2\"");  //number of event types

        strings.add("\"ERROR\"");  //event type 1
        strings.add("\"2\""); //event type num 1
        strings.add("\"AGENT_STATUS\"");  //event type 1
        strings.add("\"1\""); //event type num 1

        strings.add("\"2\""); // num event summaries

        strings.add("\"I\""); //event summary id 1
        strings.add("\"Wed Apr 30 09:42:55 PDT 2014\""); //event summary time 1
        strings.add("APP_SERVER_RESTART"); //event summary type 1
        strings.add("ERROR"); //event summary severity 1
        strings.add("summary string"); //event summary string 1

        strings.add("\"II\""); //event summary id 2
        strings.add("\"Wed Apr 30 09:42:55 PDT 2014\""); //event summary time 2
        strings.add("DIAGNOSTIC_SESSION"); //event summary type 2
        strings.add("WARN"); //event summary severity 2
        strings.add("summary string"); //event summary string 2

        strings.add("\"http://PRINHYLTPHP0158:8090/controller/#location=APP_EVENT_VIEWER_MODAL&eventSummary=\"");
        generateAccountInfo(strings);
        return Iterables.toArray(strings,String.class);
    }

    private void generateTrailingArg(List<String> strings) {
        //summary message
        strings.add("\"CPU utilization is too high triggerded at Wed Apr 30 09:42:55 PDT 2014. This policy was violated because the following conditions were met for the MyMacMachineAgentNode1 Node for the last 1 minute(s):   For Evaluation Entity: MyMacMachineAgentNode1 Node - Hardware Resources|CPU|%Busy Condition is greater than 4. Observed value = 40.0\"");  //observed value 1
        strings.add("\"3\"");  //incident id
        strings.add("\"http://WIN-OAR4D8QEG3K:8090/controller/#location=APP_INCIDENT_DETAIL&incident=\"");  //deep link url
        strings.add("\"POLICY_CANCELED_CRITICAL\"");  //event type
    }

    private void generateLeadingArgs(List<String> strings) {
        strings.add("\"MyMacMachineAgent\""); //appname
        strings.add("\"269\""); //appID
        strings.add("\"Wed Apr 30 09:42:55 PDT 2014\""); //pvn alert time
        strings.add("\"1\""); //priority
        strings.add("\"ERROR\""); //severity
        strings.add("\"VictorOpsAction\"");  //tag
        strings.add("\"CPU utilization is too high\"");  //health rule name
        strings.add("\"24\"");  //health rule id
        strings.add("\"1\"");  //pvn time period in min
        /*strings.add("\"APPLICATION_COMPONENT_NODE\"");  //affected entity type
        strings.add("\"ECommerce-Apache\""); //affected entity name*/
        /*strings.add("\"APPLICATION_COMPONENT\"");  //affected entity type
        strings.add("\"Inventory-Services\""); //affected entity name*/
        strings.add("\"BUSINESS_TRANSACTION\"");  //affected entity type
        strings.add("\"/cart/orderService\""); //affected entity name
        strings.add("\"8\"");  //affected entity id
    }

    @Test
    public void testEvent(){
        String[] args = getHealthRuleViolationEventWithMultipleEvalEntityAndMultipleTriggerBaseline();
        Event event = new EventBuilder().build(args);
        System.setProperty("SNMP_TRAP_SENDER_HOME",this.getClass().getResource("/").getFile());
        Configuration config = ConfigLoader.getConfig();
        SnmpTrapAlertExtension snmpTrap = new SnmpTrapAlertExtension(config);
        snmpTrap.process(event);
    }

    @Test
    public void testOtherEvent(){
        String[] args = getOtherEvent();
        Event event = new EventBuilder().build(args);
        System.setProperty("SNMP_TRAP_SENDER_HOME",this.getClass().getResource("/").getFile());
        Configuration config = ConfigLoader.getConfig();
        SnmpTrapAlertExtension snmpTrap = new SnmpTrapAlertExtension(config);
        snmpTrap.process(event);
    }
}
