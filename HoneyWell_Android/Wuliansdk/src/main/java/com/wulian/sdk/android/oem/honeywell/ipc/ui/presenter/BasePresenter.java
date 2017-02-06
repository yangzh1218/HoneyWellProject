/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter;

import android.content.SharedPreferences;
import android.os.Handler;

import com.wulian.sdk.android.oem.honeywell.ipc.model.UserInfo;

public interface BasePresenter extends Presenter {
    Handler getUIHandler();
}
