package com.wulian.sdk.android.oem.honeywell.ipc.model;


import java.io.Serializable;

/**
 * Created by PML on 2016/9/18.
 */
public class Alarm implements Serializable {
    public String session;
    public long alarmID;
    public long startTime;
    public long endTime;
    public long fileSize;
    public String deviceID;
    public String deviceName;

    public long getAlarmID() {
        return alarmID;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getSession() {
        return session;
    }

    public void setAlarmID(long alarmID) {
        this.alarmID = alarmID;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
