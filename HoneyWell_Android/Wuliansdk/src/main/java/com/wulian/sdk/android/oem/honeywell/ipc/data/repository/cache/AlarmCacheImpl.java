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

import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;

import java.io.File;
import java.util.List;


public class AlarmCacheImpl implements AlarmCache {

  private final Context context;
  private final File cacheDir;
  private List<Pair<String, Device>> listPair;

  public AlarmCacheImpl(Context context) {
    this.context = context.getApplicationContext();
    this.cacheDir = this.context.getCacheDir();
  }

  @Override
  public boolean isCached() {
    if(listPair == null){
      return false;
    } else {
      return true;
    }
  }

  @Override
  public void evictAll() {
    listPair = null;
  }

  @Override
  public void put(List<Pair<String, Device>> listPair) {
    this.listPair = listPair;
  }

  public List<Pair<String, Device>> getListPair() {
    return listPair;
  }

}
