/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache;

import android.content.Context;
import android.util.Pair;

import com.wulian.routelibrary.utils.LibraryLoger;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class DeviceCacheImpl implements DeviceCache {

  private final Context context;
  private final File cacheDir;
  private List<Device> deviceList;
  private List<Pair<String, Device>> listPair;
  private String ipcUsername;

  public DeviceCacheImpl(Context context) {
    this.context = context.getApplicationContext();
    this.cacheDir = this.context.getCacheDir();
    deviceList=new ArrayList<Device>();
  }

  @Override
  public boolean isCached() {
    if(deviceList!= null&&deviceList.size()>0){
        return true;
    } else {
        return false;
    }
  }

  @Override
  public List<Device> getDeviceList() {
    return deviceList;
  }

  @Override
  public void evictAll() {
    if(deviceList!=null) {
      deviceList.clear();
    }
    deviceList = null;
  }

  @Override
  public void put(List<Device> deviceList) {
    synchronized (this.deviceList) {
      if (this.deviceList != null) {
        this.deviceList.clear();
        this.deviceList.addAll(deviceList);
        LibraryLoger.d("Device List size is:"+this.deviceList.size());
      }
    }
  }

  @Override
  public void putIpcUsername(String ipcUsername) {
    this.ipcUsername = ipcUsername;
  }

  @Override
  public String getIpcUsername() {
    return ipcUsername;
  }
}
