package com.networkmanager;

public class Device {

    private int id;
    private String deviceName;
    private String ipAddress;
    private String macAddress;
    private String status;

    public Device() {}

    public Device(String deviceName, String ipAddress,
                   String macAddress, String status) {
        this.deviceName = deviceName;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}