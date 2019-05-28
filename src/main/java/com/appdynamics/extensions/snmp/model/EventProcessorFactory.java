package com.appdynamics.extensions.snmp.model;

import com.appdynamics.extensions.alerts.customevents.Event;
import com.appdynamics.extensions.alerts.customevents.HealthRuleViolationEvent;
import com.appdynamics.extensions.snmp.api.ControllerApiService;

public class EventProcessorFactory {

    public static EventProcessor init(Event event, ControllerApiService controllerApiService) {
        if(event instanceof HealthRuleViolationEvent) {
            return new HRVEventProcessor(controllerApiService);
        }
        else{
            return new OtherEventProcessor(controllerApiService);
        }
    }
}
