/**
 * Project Name:  iCam
 * File Name:     ApplyDetailActivity.java
 * Package Name:  com.wulian.icam.view.detail
 * @Date:         2015年9月8日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.message;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.sdk.android.oem.honeywell.ipc.CallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.ErrorCode;
import com.wulian.sdk.android.oem.honeywell.ipc.Interface;
import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.model.OauthMessage;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;

/**
 * @ClassName: ApplyDetailActivity
 * @Function: 授权消息详细信息类
 * @Date: 2015年9月8日
 * @author: yuanjs
 * @email: JianSheng.Yuan@wuliangroup.com
 */
public class ApplyDetailActivity extends BaseFragmentActivity implements
		OnClickListener, CallBack{
	private TextView tv_username, tv_result, tv_request_description, tv_device;
	private Button btn_accept, btn_reject;
	private LinearLayout ll_btn, ll_request;
	private ImageView titlebar_back;
	private TextView titlebar_title;
	private OauthMessage oauthMsg;
	private String username;
	public final static int RESULTCODE = 0X3;
	public final static int ACCEPT = 0X3;
	public final static int DECLINE = 0X4;
	private int type;
	private int request_type;
	private boolean isHandle = false;// 是否已处理
	// 1:request,2:add,3:response_accept,4:response_decline,5:delete,6:addresp_acc,7:addresp_dec,0:unknown
	public final static int REQUST = 0x1;
	public final static int ADD = 0x2;
	public final static int RESPONSE_ACCEPT = 0x3;
	public final static int RESPONSE_DECLINE = 0x4;
	public final static int DELETE = 0x5;
	public final static int ADDRESP_ACC = 0x6;
	public final static int ADDRESP_DEC = 0x7;

	@Override
	protected void onCreate(Bundle arg0) {
		initDateBeforeSuper();
		super.onCreate(arg0);
		setContentView(R.layout.activity_apply_detail);
		mBaseView.bindView();
		initView();
		setListener();
		initData();
	}

	@Override
	public void onBackPressed() {
		Utils.sysoInfo("onBackPressed()");
		setResult2Main(type);
		this.finish();
	}

	private void initDateBeforeSuper() {
		oauthMsg = (OauthMessage) getIntent().getExtras().getSerializable(
				"oauthmsg");
		type = oauthMsg.getType();
	}

	private void initView() {
		tv_username = (TextView) this.findViewById(R.id.tv_username);
		tv_result = (TextView) this.findViewById(R.id.tv_result);
		tv_request_description = (TextView) this
				.findViewById(R.id.tv_request_description);
		tv_device = (TextView) this.findViewById(R.id.tv_device);
		btn_accept = (Button) this.findViewById(R.id.btn_agree);
		btn_reject = (Button) this.findViewById(R.id.btn_reject);
		ll_btn = (LinearLayout) this.findViewById(R.id.ll_btn);
		ll_request = (LinearLayout) this.findViewById(R.id.ll_request);
		titlebar_back = (ImageView) this.findViewById(R.id.titlebar_back);
		titlebar_title = (TextView)this.findViewById(R.id.titlebar_title);
		titlebar_title.setText(showTitle(type));
	}

	private void showTypeUI(int type, boolean isUnread, boolean isHandle) {
		switch (type) {
		case REQUST:
			ll_request.setVisibility(View.VISIBLE);
			tv_request_description.setText(oauthMsg.getDesc());
			if (!isHandle) {
				tv_result.setVisibility(View.GONE);
				ll_btn.setVisibility(View.VISIBLE);
				ll_request.setVisibility(View.VISIBLE);
			} else {
				tv_result.setVisibility(View.VISIBLE);
				ll_btn.setVisibility(View.INVISIBLE);
				if (oauthMsg.isAccept()) {
					tv_result.setText(getResources().getString(
							R.string.message_agree_reques));
				} else {
					tv_result.setText(getResources().getString(
							R.string.message_reject_reques));
				}
			}
			break;
		case ADD:
			ll_request.setVisibility(View.GONE);
			if (!isHandle) {
				tv_result.setVisibility(View.GONE);
				ll_btn.setVisibility(View.VISIBLE);
			} else {
				tv_result.setVisibility(View.VISIBLE);
				ll_btn.setVisibility(View.INVISIBLE);
				if (oauthMsg.isAccept()) {
					tv_result.setText(getResources().getString(
							R.string.message_response_accept_invitation));
				} else {
					tv_result.setText(getResources().getString(
							R.string.message_response_decline_invitation));
				}
			}
			break;
		case RESPONSE_ACCEPT:
			tv_result.setVisibility(View.VISIBLE);
			ll_request.setVisibility(View.GONE);
			ll_btn.setVisibility(View.INVISIBLE);
			tv_result.setText(getResources()
					.getString(R.string.message_response_accept));
			break;
		case RESPONSE_DECLINE:
			tv_result.setVisibility(View.VISIBLE);
			ll_request.setVisibility(View.GONE);
			ll_btn.setVisibility(View.INVISIBLE);
			tv_result.setText(getResources().getString(
					R.string.message_response_decline));
			break;
		case DELETE:
			tv_result.setVisibility(View.VISIBLE);
			ll_request.setVisibility(View.GONE);
			ll_btn.setVisibility(View.INVISIBLE);
			tv_result.setText(getResources()
					.getString(R.string.message_response_delete));
			break;
		case ADDRESP_ACC:
			tv_result.setVisibility(View.VISIBLE);
			ll_request.setVisibility(View.GONE);
			ll_btn.setVisibility(View.INVISIBLE);
			tv_result.setText(getResources()
					.getString(R.string.message_user_response_accept_invitation));
			break;
		case ADDRESP_DEC:
			tv_result.setVisibility(View.VISIBLE);
			ll_request.setVisibility(View.GONE);
			ll_btn.setVisibility(View.INVISIBLE);
			tv_result.setText(getResources()
					.getString(R.string.message_user_response_decline_invitation));
			break;
		default:
			break;
		}

	}

	private void setListener() {
		btn_accept.setOnClickListener(this);
		btn_reject.setOnClickListener(this);
		titlebar_back.setOnClickListener(this);
	}

	private void initData() {
		username = oauthMsg.getUserName();
		if (TextUtils.isEmpty(username)) {
			username = oauthMsg.getPhone();
			if (TextUtils.isEmpty(username)) {
				username = oauthMsg.getEmail();
			}
		}
		tv_username.setText(username);
		String nickName = oauthMsg.getFromNick();
		if (nickName == null || nickName.equals("")) {
			nickName = oauthMsg.getDevice_id();
		}
		tv_device.setText(nickName);
		showTypeUI(type, oauthMsg.getIsUnread(), oauthMsg.isHandle());
		Utils.sysoInfo("=====Apply id====" + oauthMsg.getId());
	}


	private String showTitle(int type2) {
		switch (type2) {
		case REQUST:
			return getResources().getString(R.string.message_title_reques);
		case ADDRESP_ACC:
		case ADDRESP_DEC:
		case ADD:
			return getResources().getString(R.string.message_title_response_add);
		case RESPONSE_ACCEPT:
			return getResources().getString(R.string.message_title_response_accept);
		case RESPONSE_DECLINE:
			return getResources().getString(R.string.message_title_response_decline);
		case DELETE:
			return getResources().getString(R.string.message_title_response_delete);
		default:
			return "";
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_agree) {
//			showBaseDialog();
			request_type = ACCEPT;
			isHandle = true;
			if (type == REQUST) {
				bindingOauthResponse(false, ACCEPT);
			} else if(type == ADD) {
//				bindingOauthAddResponse(false, ACCEPT);
			}
		} else if (id == R.id.btn_reject) {
//			showBaseDialog();
			request_type = DECLINE;
			isHandle = true;
			if (type == REQUST) {
				bindingOauthResponse(false, DECLINE);
			} 
		} else if (id == R.id.titlebar_back) {
			setResult2Main(type);
			this.finish();
		} else {
		}
	}

	/**
	 * 处理设备授权请求
	 */
	private void bindingOauthResponse(boolean isShowDialog, int type) {
		Interface.getInstance().ShareResponse(oauthMsg.getDevice_id(), username, type == ACCEPT ? "accept" : "decline");
	}

//
//	private Device findDeviceByDeviceId(String device_id) {
//		if (app.getDeviceList() != null && app.getDeviceList().size() > 0) {
//			for (Device d : app.getDeviceList()) {
//				if (d.getDevice_id().equals(device_id))
//					return d;
//			}
//		}
//		return null;
//	}

	private void setResult2Main(int oauth_type) {
		if (oauth_type == REQUST||oauth_type == ADD) {
			setResult(
					RESULTCODE,
					new Intent()
							.putExtra("isUnread", oauthMsg.getIsUnread())
							.putExtra("oauth_id", oauthMsg.getId())
							.putExtra("oauth_type", oauthMsg.getType())
							.putExtra("isAccept",
									request_type == ACCEPT ? true : false)
							.putExtra("isHandle", isHandle));
		} else {
			setResult(
					RESULTCODE,
					new Intent().putExtra("isUnread", oauthMsg.getIsUnread())
							.putExtra("oauth_id", oauthMsg.getId())
							.putExtra("oauth_type", oauthMsg.getType()));
		}
	}

//	/**
//	 * 上传未读消息
//	 */
//	private void uploadOauthRequestMsg(boolean isShowDialog, int type) {
//
//		sendRequest(RouteApiType.BINDING_AUTH_UPLOAD,
//				RouteLibraryParams.BindingNotice(oauthMsg.getDevice_id(),
//						type == ACCEPT ? "response_accept"
//								: "response_decline", username,
//						ICamGlobal.getInstance().getUserinfo()
//								.getAuth(), null), isShowDialog);
//
//	}
	
//	/**
//	 * 上传未读消息
//	 */
//	private void uploadOauthAddMsg(boolean isShowDialog, int type) {
//
//		sendRequest(RouteApiType.BINDING_AUTH_UPLOAD,
//				RouteLibraryParams.BindingNotice(oauthMsg.getDevice_id(),
//						type == ACCEPT ? "addresp_acc"
//								: "addresp_dec", username,
//								ICamGlobal.getInstance().getUserinfo()
//								.getAuth(), null), isShowDialog);
//	}



	public  void DoSucceed(ErrorCode errorCode, RouteApiType apiType, String json) {
		switch (apiType) {
			case V3_SHARE_RESPONSE:
				// msgFragment.handleOauthResponse(processOauthPosition);
//				if (request_type == ACCEPT) {// 接受授权，发送同步消息
//					Device device = findDeviceByDeviceId(oauthMsg
//							.getDevice_id());
//					if (device != null) {
//						Utils.sysoInfo("接受授权，发送同步消息 " + device.getDid());
//						// 同步,添加设备成功
//						SipController.getInstance().sendMessage(
//								device.getSip_username() + "@"
//										+ device.getSip_domain(),
//								SipHandler.NotifySynchroPermission("sip:"
//										+ device.getSip_username() + "@"
//										+ device.getSip_domain(), 1),
//								SipFactory.getInstance().getSipProfile());
//					}
//					uploadOauthRequestMsg(false, ACCEPT);
//				} else {
////					uploadOauthRequestMsg(false, DECLINE);
//				}
				break;
//			case BINDING_AUTH_UPLOAD:
//				if(type == ADD){
//					ICamGlobal.isNeedRefreshDeviceList = true;
//				}
//				mBaseView.dismissBaseDialog();
//				setResult2Main(type);
//				this.finish();
//				break;
			default:
				break;
		}
	}

	@Override
	public void DoFailed(ErrorCode errorCode, RouteApiType apiType, Exception exception) {
		switch (apiType) {
			case V3_SHARE_RESPONSE:
				mBaseView.dismissBaseDialog();
				CustomToast.show(this, R.string.common_send_fail);
				break;
			case V3_SERVER: BINDING_AUTH_UPLOAD:
			mBaseView.dismissBaseDialog();
				CustomToast.show(this, R.string.common_send_fail);
				break;
//		case BINDING_AUTH_ADD_RESPONSE:
//			mBaseView.dismissBaseDialog();
//			CustomToast.show(this, R.string.common_send_fail);
//			break;
			default:
				break;
		}
	}
}
