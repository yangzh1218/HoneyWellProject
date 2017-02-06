/**
 * Project Name:  iCam
 * File Name:     Device.java
 * Package Name:  com.wulian.icam.model
 *
 * @Date: 2014年10月21日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.wulian.sdk.android.oem.honeywell.ipc.APPConfig;

/**
 * @ClassName: Device
 * @Function: 设备bean
 * @Date: 2014年10月21日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class Device implements Parcelable {
    private String did;
    private int online;
    private String nick;
    private String description;
    private String sdomain;
    private String sip_username;// 暂时等同device_id
    private String ip, owner;
    private int video_port, shares, protect;
    private long updated;
    private boolean is_BindDevice;// 是否是主绑定设备

    public boolean getIs_BindDevice() {
        return is_BindDevice;
    }

    public void setIs_BindDevice(boolean is_BindDevice) {
        this.is_BindDevice = is_BindDevice;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSip_domain() {
        if (TextUtils.isEmpty(sdomain)) {
            return APPConfig.DEFAULT_DOMAIN;
        }
        return sdomain;
    }

    public void setSdomain(String sip_domain) {
        this.sdomain = sip_domain;
    }

    public String getSip_username() {
        return sip_username;
    }

    public void setSip_username(String sip_username) {
        this.sip_username = sip_username;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getVideo_port() {
        return video_port;
    }

    public void setVideo_port(int video_port) {
        this.video_port = video_port;
    }


    @Override
    public String toString() {
        return "Device [did=" + did + ", online=" + online
                + ", nick=" + nick + ", description="
                + description + ", sip_domain=" + sdomain
                + ", sip_username=" + sip_username + ", ip=" + ip
                + ", video_port=" + video_port + ", updated=" + updated
                + ", is_BindDevice=" + is_BindDevice + "]";
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public int getProtect() {
        return protect;
    }

    public void setProtect(int protect) {
        this.protect = protect;
    }

    /**
     * owner.
     *
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * owner
     *
     * @param owner
     *            the owner to set
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }



    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(did);
        dest.writeInt(online);
        dest.writeString(nick);
        dest.writeString(description);
        dest.writeString(sdomain);
        dest.writeString(sip_username);
        dest.writeString(ip);
        dest.writeString(owner);
        dest.writeInt(video_port);
        dest.writeInt(shares);
        dest.writeInt(protect);
        dest.writeLong(updated);
        dest.writeByte((byte)(is_BindDevice ?1:0));
    }
    public static final Parcelable.Creator<Device> CREATOR = new Parcelable.Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel parcel) {
            Device device = new Device();
            // 按变量定义的顺序读取
            device.did = parcel.readString();
            device.online = parcel.readInt();
            device.nick = parcel.readString();
            device.description = parcel.readString();
            device.sdomain = parcel.readString();
            device.sip_username = parcel.readString();
            device.ip = parcel.readString();
            device.owner = parcel.readString();
            device.video_port = parcel.readInt();
            device.shares = parcel.readInt();
            device.protect = parcel.readInt();
            device.updated = parcel.readLong();
            device.is_BindDevice = parcel.readByte()!=0;
            return device;
        }
        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };
}
