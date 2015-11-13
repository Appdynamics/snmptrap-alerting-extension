package com.appdynamics.extensions.snmp.api;

import java.util.List;

public class Node {

    private int id;
    private String name;
    private String type;
    private int tierId;
    private String tierName;
    private int machineId;
    private String machineName;
    private String machineOSType;
    private boolean machineAgentPresent;
    private boolean appAgentPresent;
    private String appAgentVersion;
    private List<String> ipAddresses;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTierId() {
        return tierId;
    }

    public void setTierId(int tierId) {
        this.tierId = tierId;
    }

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getMachineOSType() {
        return machineOSType;
    }

    public void setMachineOSType(String machineOSType) {
        this.machineOSType = machineOSType;
    }

    public boolean isMachineAgentPresent() {
        return machineAgentPresent;
    }

    public void setMachineAgentPresent(boolean machineAgentPresent) {
        this.machineAgentPresent = machineAgentPresent;
    }

    public boolean isAppAgentPresent() {
        return appAgentPresent;
    }

    public void setAppAgentPresent(boolean appAgentPresent) {
        this.appAgentPresent = appAgentPresent;
    }

    public String getAppAgentVersion() {
        return appAgentVersion;
    }

    public void setAppAgentVersion(String appAgentVersion) {
        this.appAgentVersion = appAgentVersion;
    }

    public List<String> getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(List<String> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }
}

