/**
 * Project Name:  iCam
 * File Name:     DeviceOauthSharedActivity.java
 * Package Name:  com.wulian.icam.view.device
 * @Date:         2015年7月9日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.share;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.sdk.android.oem.honeywell.ipc.APPConfig;
import com.wulian.sdk.android.oem.honeywell.ipc.CallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.ErrorCode;
import com.wulian.sdk.android.oem.honeywell.ipc.Interface;
import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.UserDataRepository;
import com.wulian.sdk.android.oem.honeywell.ipc.error.ErrorUtil;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;
import com.wulian.sdk.android.oem.honeywell.ipc.model.OauthUserDetail;
import com.wulian.sdk.android.oem.honeywell.ipc.sip.SipFactory;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.adapter.UserShareDetailAdapter;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.DialogUtils;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.JsonHandler;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.MessageUtil;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;
import com.wulian.siplibrary.api.SipController;
import com.wulian.siplibrary.api.SipHandler;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

/**
 * @ClassName: DeviceShareActivity
 * @Function: 分享设备界面
 * @Date: 2015年9月23日
 * @author: yanmin
 * @email: min.yan@wuliangroup.cn
 */
public class DeviceShareActivity extends BaseFragmentActivity implements
		OnClickListener, UserShareDetailAdapter.DeleteOauthCallBack, CallBack {
	private static final int BAECODE_REQUESTCODE = 1;
	private static final int REQUEST_FLAG_AUTH_LIST = 1;
	private static final int REQUEST_FLAG_ADD_AUTH = 2;
	private static final int REQUEST_FLAG_DETELE_AUTH = 3;

	private LinearLayout ll_oauth_by_barcode;
	private TextView tv_oauth_count;
	private ListView lv_oauth_detail;
	private EditText et_uuid;
	private Button btn_share;
	private Device device;
	private AlertDialog dialogUnBindDevice;
	private View dialogUnBindDeviceView;

	private int sharedCount;
	private List<OauthUserDetail> oauthUserDetails;
	private UserShareDetailAdapter oauthUseDetailAdapter;
	private SharedPreferences sp;
	private int seq = 0;
	private boolean shareDevice;// 用于判断是否重发增加授权绑定
	private String addingUser;
	private String deletingUser;
	private int deletingPosition;
	private int requestFlag;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_device_share);
		mBaseView.bindView();
		mBaseView.setTitleView(R.string.share_device);
		initView();
		initData();
		initListener();
	}


	private void initView() {
		ll_oauth_by_barcode = (LinearLayout) findViewById(R.id.ll_oauth_by_barcode);
		et_uuid = (EditText) findViewById(R.id.et_uuid);
		btn_share = (Button) findViewById(R.id.btn_share);
		tv_oauth_count = (TextView) this.findViewById(R.id.tv_oauth_count);
		lv_oauth_detail = (ListView) this.findViewById(R.id.lv_oauth_detail);
	}

	private void initData() {
		sp = getSharedPreferences(APPConfig.SP_CONFIG, MODE_PRIVATE);
		device = (Device) getIntent().getParcelableExtra("device");

		tv_oauth_count.setVisibility(View.GONE);
		oauthUseDetailAdapter = new UserShareDetailAdapter(this);
		oauthUseDetailAdapter.setDeleteOauthCallBack(this);
		lv_oauth_detail.setAdapter(oauthUseDetailAdapter);
		getOauthListRequest();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Interface.getInstance().setCallBack(this);
		Interface.getInstance().setContext(this);
	}

	@Override
	public void DoFailed(ErrorCode errorCode, RouteApiType apiType, Exception exception) {
		try {
			JSONObject jsonObject = new JSONObject(exception.getMessage());
			handler.sendMessage(MessageUtil.set(MSG_TOAST, "msg", ErrorUtil.errorMsg(this, jsonObject)));
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case MSG_UPDATE_DETAIL_LIST:
					updateDetailList();
					break;
				case MSG_SHARE_UNSHARE:
					CustomToast.show(DeviceShareActivity.this, R.string.common_delete_success);
					updateDetailList();
					break;
				case MSG_SHARE_REQUEST:
					et_uuid.setText("");
					updateDetailList();
					break;
				case MSG_TOAST:
					CustomToast.show(DeviceShareActivity.this, msg.getData().getString("msg"));
				default:
					break;
			}
		}
	};

	static private final int MSG_UPDATE_DETAIL_LIST = 0;
	static private final int MSG_SHARE_UNSHARE = 1;
	static private final int MSG_SHARE_LIST = 2;
	static private final int MSG_SHARE = 3;
	static private final int MSG_SHARE_REQUEST = 4;
		static private final int MSG_TOAST = 5;

	@Override
	public void DoSucceed(ErrorCode errorCode, RouteApiType apiType, String json) {
		switch (apiType) {
			case V3_SHARE:
				//这里发请求主要是刷新列表用的
				getOauthListRequest();
				break;
			case V3_SHARE_LIST:
				oauthUserDetails = JsonHandler.getBindingOauthDetailList(json);
				handler.sendMessage(MessageUtil.set(MSG_UPDATE_DETAIL_LIST, "", ""));
				break;
			case V3_SHARE_REQUEST:
				Utils.sysoInfo("同步,添加设备成功");
				handler.sendMessage(MessageUtil.set(MSG_SHARE_REQUEST, "", ""));
				break;
			case V3_SHARE_UNSHARE:
				//这里发请求主要是刷新列表用的
				getOauthListRequest();
				Utils.sysoInfo("同步，删除设备成功");
				oauthUserDetails.remove(deletingPosition);
				handler.sendMessage(MessageUtil.set(MSG_SHARE_UNSHARE, "", ""));
				SipController.getInstance()
						.sendMessage(
								device.getSip_username() + "@"
										+ device.getSip_domain(),
								SipHandler.NotifySynchroPermission("sip:"
										+ device.getSip_username() + "@"
										+ device.getSip_domain(), seq++),
								SipFactory.getInstance().getSipProfile());
				break;
			default:
				break;
		}
	}


	private void updateDetailList() {
		tv_oauth_count.setVisibility(View.VISIBLE);

		sharedCount = (oauthUserDetails == null) ? 0 : oauthUserDetails.size();
		oauthUseDetailAdapter.refresh(oauthUserDetails);
		if (sharedCount > 0) {
			tv_oauth_count
					.setText(Html.fromHtml(String.format(
							this.getString(R.string.share_user_situation),
							sharedCount)));
		} else {
			tv_oauth_count.setText(getResources()
					.getString(R.string.share_none));
		}

	}

	private void initListener() {
		btn_share.setOnClickListener(this);
		ll_oauth_by_barcode.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.titlebar_back) {
			Utils.hideIme(this);// 隐藏键盘

			this.finish();
		} else if (id == R.id.btn_share) {
			doShareEvent();
		}
	}

	private void getOauthListRequest() {
		Interface.getInstance().ShareList(device.getDid());
	}

	private void deleteOauthRequest(String username) {
		Interface.getInstance().UnshareRequest(device.getDid(), username);
	}


	private void share(String username) {
		Interface.getInstance().Share(device.getDid(), username);
	}

	private void doShareEvent() {
		String uuid = et_uuid.getText().toString().trim();

		if (TextUtils.isEmpty(uuid)) {
			CustomToast.show(this, R.string.share_account_empty_tip);
			return;
		}

		if (uuid.equalsIgnoreCase(UserDataRepository.getInstance().userInfo().getEmail())
				|| uuid.equalsIgnoreCase(UserDataRepository.getInstance().userInfo().getPhone())
				|| uuid.equalsIgnoreCase(UserDataRepository.getInstance().userInfo().getUsername())) {
			CustomToast.show(this, R.string.share_myself);
			return;
		}

		// 过滤已经绑定(先前绑定+刚刚绑定)
		if (isBinded(uuid)) {
			CustomToast.show(this, R.string.share_already_2);
			return;
		}

		addingUser = uuid;
		et_uuid.requestFocus();// 转移焦点
		Utils.hideIme(this);// 隐藏键盘
		shareDevice = true;
		showAlertDialog();
	}

	private boolean isBinded(String account) {
		for (OauthUserDetail m : oauthUserDetails) {
			if (m.getUsername().equalsIgnoreCase(account)
					|| m.getPhone().equalsIgnoreCase(account)
					|| m.getEmail().equalsIgnoreCase(account)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestcode, int resultcode, Intent data) {
		super.onActivityResult(requestcode, resultcode, data);
		switch (resultcode) {
		case RESULT_OK:
			if (requestcode == BAECODE_REQUESTCODE) {
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void deleteOauth(int position) {
		this.deletingPosition = position;
		OauthUserDetail item = oauthUserDetails.get(position);
		String id = item.getUsername();
		if (TextUtils.isEmpty(id)) {
			id = item.getPhone();
			if (TextUtils.isEmpty(id)) {
				id = item.getEmail();
			}
		}

		shareDevice = false;
		deletingUser = id;
		showAlertDialog();
	}

	private Dialog mAlertDialog;

	private void showAlertDialog() {
		Resources rs = getResources();
		Spanned tip;
		String title;
		if (shareDevice) {
			title = rs.getString(R.string.share_device);
			if (addingUser.length() == 12) {
				tip = Html.fromHtml(String.format(
						this.getString(R.string.share_add_gateway_confirm),
						addingUser.toUpperCase(Locale.getDefault())));
			} else {
				tip = Html
						.fromHtml(String.format(
								this.getString(R.string.share_add_confirm),
								addingUser));
			}
		} else {
			title = rs.getString(R.string.share_delete);
			tip = Html
					.fromHtml(String.format(
							this.getString(R.string.share_delete_confirm),
							deletingUser));
		}

		mAlertDialog = DialogUtils.showCommonDialog(this, true, title, tip,
				null, null, new OnClickListener() {

					@Override
					public void onClick(View v) {
						int id = v.getId();
						if (id == R.id.btn_positive) {
							mAlertDialog.dismiss();
							if (shareDevice) {
								share(addingUser);
							} else {
								deleteOauthRequest(deletingUser);
							}
						} else if (id == R.id.btn_negative) {
							mAlertDialog.dismiss();
						}
					}
				});
	}


}
