

package com.appdynamics.extensions.snmp;

/**
 * SNMP Data Object
 */
public class ADSnmpData
{
    String application;
    String triggeredBy;
    String nodes;
    String BTs = "";
    String machines;
    String tiers;
    String eventTime;
    String severity;
    String type;
    String subtype;
    String summary;
    String link;
    String tag;
    String eventType;
    String ipAddresses;
    String incidentId;


    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getTriggeredBy() {
        return triggeredBy;
    }

    public void setTriggeredBy(String triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    public String getNodes() {
        return nodes;
    }

    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    public String getBTs() {
        return BTs;
    }

    public void setBTs(String BTs) {
        this.BTs = BTs;
    }

    public String getMachines() {
        return machines;
    }

    public void setMachines(String machines) {
        this.machines = machines;
    }

    public String getTiers() {
        return tiers;
    }

    public void setTiers(String tiers) {
        this.tiers = tiers;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(String ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    @Override
    public String toString() {
        return "ADSnmpData{" +
                "application='" + application + '\'' +
                ", triggeredBy='" + triggeredBy + '\'' +
                ", nodes='" + nodes + '\'' +
                ", BTs='" + BTs + '\'' +
                ", machines='" + machines + '\'' +
                ", tiers='" + tiers + '\'' +
                ", eventTime='" + eventTime + '\'' +
                ", severity='" + severity + '\'' +
                ", type='" + type + '\'' +
                ", subtype='" + subtype + '\'' +
                ", summary='" + summary + '\'' +
                ", link='" + link + '\'' +
                ", tag='" + tag + '\'' +
                ", eventType='" + eventType + '\'' +
                ", ipAddresses='" + ipAddresses + '\'' +
                ", incidentId='" + incidentId + '\'' +
                '}';
    }
}