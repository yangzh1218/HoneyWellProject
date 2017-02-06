/**
 * Project Name:  iCam
 * File Name:     V2AddDeviceActivity.java
 * Package Name:  com.wulian.icam.view.device
 * @Date:         2015年7月24日
 * Copyright (c)  2015, wulian All Rights Reserved.
*/

package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.config;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.config.barcode.decode.CaptureActivity;


/**
 * @ClassName: AddDeviceFirstPageActivity
 * @Function:  V2添加摄像机
 * @Date:      2015年7月24日
 * @author     Puml
 * @email      puml@wuliangroup.cn
 */
public class AddDeviceFirstPageActivity extends BaseFragmentActivity{

	Button btn_start;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_add_device_first_page);
		mBaseView.bindView();
		mBaseView.setTitleView(R.string.config_add_camera);
		initView();
	}

	void initView(){
		btn_start = (Button)findViewById(R.id.btn_start);
		btn_start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent it=new Intent(AddDeviceFirstPageActivity.this, CaptureActivity.class);
				it.putExtra("launchFrom", "addDevice");
				it.putExtra("isV2BarcodeScan", true);
				startActivity(it);
				finish();
			}
		});
	}

}

