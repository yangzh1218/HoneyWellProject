/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wulian.sdk.android.oem.honeywell.ipc.ui.navigation;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.DeviceListActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.setting.DeviceSettingActivity;

/**
 * Class used to navigate through the application.
 */

public class Navigator {


    public Navigator(Activity activity) {
        this.activity = activity;
        bt_title_back = (ImageView) activity.findViewById(R.id.titlebar_back);
        tv_titlebar_title = (TextView) activity.findViewById(R.id.titlebar_title);
    }

    public Navigator(Fragment fragment) {
        this.fragment = fragment;
        bt_title_back = (ImageView) fragment.getView().findViewById(R.id.titlebar_back);
        tv_titlebar_title = (TextView) fragment.getView().findViewById(R.id.titlebar_title);
    }

    private Activity activity;
    private Fragment fragment;
    private ImageView bt_title_back;// 返回
    private TextView tv_titlebar_title;// 标题

    public void setTitle(String title) {
        if (tv_titlebar_title != null && title != null) {
            tv_titlebar_title.setText(title);
        }
    }

    public void initBackListener() {
        if (bt_title_back != null) {
            bt_title_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity != null) {
                        activity.finish();
                    } else {
                        fragment.getActivity().finish();
                    }
                }
            });
        }
    }

    /**
     *
     *
     * @param context A Context needed to open the destiny activity.
     */
    public void navigateToDeviceList(Context context) {
        if (context != null) {
            Intent intentToLaunch = new Intent(context, DeviceListActivity.class);
            context.startActivity(intentToLaunch.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        }
    }

    /**
     *
     *
     * @param context A Context needed to open the destiny activity.
     */
    public void navigateToDeviceSetting(Context context) {
        if (context != null) {
            Intent intentToLaunch = new Intent(context, DeviceSettingActivity.class);
            context.startActivity(intentToLaunch.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            //
        }
    }
}
