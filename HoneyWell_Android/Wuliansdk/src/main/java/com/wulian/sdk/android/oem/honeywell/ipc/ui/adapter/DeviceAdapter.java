/**
 * Project Name:  iCam
 * File Name:     DeviceAdapter.java
 * Package Name:  com.wulian.icam.adpter
 * @Date:         2014年10月21日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.ui.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.wulian.sdk.android.oem.honeywell.ipc.APPConfig;
import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.common.Common;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.UserDataRepository;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;
import com.wulian.sdk.android.oem.honeywell.ipc.sip.SipFactory;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.setting.DeviceSettingActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.play.PlayVideoActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.share.DeviceShareActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.DeviceListPresenter;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.view.DeviceListView;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.SnapCache;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;
import com.wulian.siplibrary.api.SipController;

import java.util.ArrayList;
import java.util.List;


/**
 * @Function: 新版设备适配器V2
 * @date: 2015年6月24日
 * @author Wangjj
 */

public class DeviceAdapter extends BaseAdapter {
	private Context context;
	private List<Device> deviceList;// 可能为空？内部维护一个实体还是一个外部引用？
	private OnClickListener clickListener;
	SharedPreferences sp;
	AlertDialog dialogOffline;
	Dialog dialog;
	View dialogContentView, dialogOfflineView;
	ConnectivityManager connectivity;
	NetworkInfo mobileNetworkInfo;
	NetworkInfo wifiNetworkInfo;
	public SnapCache snapCache;
	public View clickView;
	private Device jumpDevice;
	DeviceListView deviceListView;
	DeviceListPresenter deviceListPresenter;
	private static class ViewHolder {
		public ImageView iv_preview;
		public TextView tv_device_name, tv_isonline, tv_authby;
		public Button btn_device_setting, btn_device_share;
		public LinearLayout ll_bind_setting;
	}

	public DeviceAdapter(final Context context, DeviceListView deviceListView, final DeviceListPresenter deviceListPresenter, List<Device> deviceList) {
		this.context = context;
		this.deviceListView = deviceListView;
		this.deviceListPresenter = deviceListPresenter;
		connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		this.deviceList=new ArrayList<Device>();
		this.deviceList.clear();
		if(deviceList!=null) {
			this.deviceList.addAll(deviceList);
		}

//		this.deviceList = deviceList;// MainActivity中的引用可能有空指针异常;DeviceFragment中的引用解决问题了？
		sp = context.getSharedPreferences(APPConfig.SP_CONFIG,
				Context.MODE_PRIVATE);
		snapCache = new SnapCache(12 * 1024 * 1024);
		clickListener = new View.OnClickListener() {
			int position = 0;
			@Override
			public void onClick(View v) {
				clickView = v;
				int id = v.getId();
				if (id == R.id.iv_preview) {
					Utils.sysoInfo("item click");
					mobileNetworkInfo = connectivity
							.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);// 此瞬间的状态
					wifiNetworkInfo = connectivity
							.getNetworkInfo(ConnectivityManager.TYPE_WIFI);// 此瞬间的状态
					if ((mobileNetworkInfo == null || !mobileNetworkInfo
							.isConnected()) && !wifiNetworkInfo.isConnected()) {
						CustomToast.show(context, R.string.error_no_network);
						return;
					}
					int lastCode = SipController.getInstance().getAccountInfo(
							SipFactory.getInstance().getSipProfile());
					SipFactory.getInstance().getAccountInfo();
					Utils.sysoInfo("lastCode:" + lastCode);
					if (lastCode == 100) {
						CustomToast.show(context,
								R.string.play_sip_processing_waiting);
					} else if (lastCode == 401) {
						CustomToast.show(context,
								R.string.play_sip_unauthorized);
					} else if (lastCode == 404) {
						CustomToast.show(context, R.string.play_sip_offline);
					} else if (lastCode == 407) {
						CustomToast
								.show(context,
										R.string.play_sip_proxy_authentication_required);
					} else if (lastCode == 408) {
						CustomToast.show(context,
								R.string.play_sip_request_timeout);
					} else if (lastCode == 486) {
						CustomToast
								.show(context, R.string.play_sip_device_busy);
					} else if (lastCode == 503) {
						CustomToast.show(context,
								R.string.play_sip_service_unavailable);
					} else if (lastCode > 500) {
						CustomToast.show(context, context.getResources()
								.getString(R.string.play_sip_serve_error)
								+ "("
								+ lastCode + ")");
					} else if (lastCode != 200) {// 超大范围
						CustomToast.show(context, "account_info:" + lastCode);
					}
					if (lastCode != 200) {// 超大墙
						Utils.sysoInfo("lastCode!=200 registerAccountForce");
						// sipProfile = app.registerAccountForce();
						return;
					}

					Device device = DeviceAdapter.this.deviceList.get((int) v.getTag());
					if ((device != null && device.getOnline() == 1)
							&& (mobileNetworkInfo != null && mobileNetworkInfo
							.isConnected())
							&& !wifiNetworkInfo.isConnected()
							&& sp.getBoolean(UserDataRepository.getInstance().uuid()
							+ APPConfig.NETWORK_PROTECT, true)) {
						Utils.sysoInfo("开启流量保护,需要确认。");
						jumpToPlay(clickView);
						CustomToast.show(context, R.string.main_network_protect_info);
					} else if ((device != null && device.getOnline() == 1)
							&& (mobileNetworkInfo != null && mobileNetworkInfo
							.isConnected())
							&& !wifiNetworkInfo.isConnected()) {
						jumpToPlay(clickView);
						CustomToast.show(context, R.string.main_network_protect_info);
					} else {
						// 跳转播放页
						Utils.sysoInfo("不满足流量保护的条件,直接跳转");
						jumpToPlay(clickView);
					}
				} else if (id == R.id.btn_device_setting) {
					position = (int) v.getTag();
					context.startActivity(new Intent(context,
							DeviceSettingActivity.class).putExtra("device",
							DeviceAdapter.this.deviceList.get(position)));
				} else if(id == R.id.btn_device_share){
					position = (int) v.getTag();
					if (DeviceAdapter.this.deviceList.get(position).getIs_BindDevice()) {
						Intent intent = new Intent(context,
								DeviceShareActivity.class);
						intent.putExtra("device", DeviceAdapter.this.deviceList.get(position));
						context.startActivity(intent);
					}
				}
			}
		};
	}

	/**
	 *
	 * @Function 跳转到播放页
	 * @author Wangjj
	 * @date 2014年12月10日
	 * @param v
	 */
	public void jumpToPlay(View v) {

		// 跳转播放页
		int position = (int) v.getTag();
		jumpDevice = deviceList.get(position);
//		if (TextUtils.isEmpty(jumpDevice.getSip_username())) {
//			CustomToast.show(context, R.string.common_none_account);
//			return;
//		}

		context.startActivity(new Intent(context.getApplicationContext(),
				PlayVideoActivity.class).putExtra("device", jumpDevice));// onDestory中

	}

	@Override
	public int getCount() {
		return deviceList.size();
	}

	@Override
	public Object getItem(int i) {
		return deviceList.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		final ViewHolder holder;
		if (null == view) {
			view = LayoutInflater.from(context).inflate(
					R.layout.adapter_device, viewGroup, false);
			holder = new ViewHolder();
			holder.btn_device_setting = (Button) view
					.findViewById(R.id.btn_device_setting);
			holder.iv_preview = (ImageView) view
					.findViewById(R.id.iv_preview);
			holder.tv_authby = (TextView) view
					.findViewById(R.id.tv_authby);
			holder.tv_device_name = (TextView) view
					.findViewById(R.id.tv_device_name);
			holder.tv_isonline = (TextView) view
					.findViewById(R.id.tv_isonline);
			holder.btn_device_share = (Button) view.findViewById(R.id.btn_device_share);
			holder.ll_bind_setting = (LinearLayout) view.findViewById(R.id.ll_bind_setting);
			// 固定尺寸为16:9
			LayoutParams lp = (LayoutParams) holder.iv_preview
					.getLayoutParams();
			// 屏幕宽度-2边的padding
			lp.width = Utils.getDeviceSize(context).widthPixels
					- 2
					* context.getResources().getDimensionPixelSize(
					R.dimen.device_list_padding);

			lp.height = (int) (lp.width * 9.0 / 16.0);
			holder.iv_preview.setLayoutParams(lp);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		// 设备信息绑定->视图重用->完整的if、else判断
		Device tempDevice = deviceList.get(i);
		holder.tv_device_name.setText(tempDevice.getNick());
		// 是否主绑定设备
		if (tempDevice.getIs_BindDevice()) {
			holder.tv_authby.setVisibility(View.GONE);
			holder.btn_device_share.setVisibility(View.VISIBLE);
			// 授权
			if (tempDevice.getShares() > 0) {
				holder.btn_device_share.setText(Html.fromHtml(String.format(
						context.getString(R.string.share_already),
						tempDevice.getShares())));
				holder.btn_device_share
						.setCompoundDrawablesWithIntrinsicBounds(
								R.drawable.v2_share_yes, 0, 0, 0);
			}else {
				holder.btn_device_share
						.setCompoundDrawablesWithIntrinsicBounds(
								R.drawable.v2_share_no, 0, 0, 0);
				holder.btn_device_share.setText(R.string.share_others);
			}
		} else {
			holder.tv_authby.setVisibility(View.VISIBLE);
			holder.btn_device_share.setVisibility(View.GONE);
			// 显示授权者
			String owner = Common.getUnPreUserName(tempDevice.getOwner());
			owner = owner.replace("--","@");
			holder.tv_authby
					.setText(Html.fromHtml(String.format(
							context.getString(R.string.share_by),
							owner)));
		}
		// 是否在线
		if (tempDevice.getOnline() == 1) {
			// 文字显示
			holder.tv_isonline.setText(context.getResources().getString(
					R.string.main_online));
			holder.tv_isonline.setBackgroundColor(context.getResources()
					.getColor(R.color.theme_color));

		} else {
			// 文字显示
			holder.tv_isonline.setText(context.getResources().getString(
					R.string.main_offline));
			holder.tv_isonline.setBackgroundColor(context.getResources()
					.getColor(R.color.offline_color));
			// 背景显示
			// holder.iv_preview.setImageResource(R.drawable.shape_bg_offline);

		}
		// 背景显示
		Bitmap bitmap = snapCache.get(tempDevice.getDid());
		if (bitmap == null) {
			bitmap = Utils.getBitmap(tempDevice.getDid(), context).get();
			if (bitmap != null) {
				Utils.sysoInfo("snap放入缓存");
				snapCache.put(tempDevice.getDid(), bitmap);
			}
		} else {
			Utils.sysoInfo("从缓存中取snap");// 缓存如果不更新,截图会一直不变
		}

		if (bitmap != null) {
			holder.iv_preview.setImageBitmap(bitmap);

		} else {
			holder.iv_preview.setImageResource(R.drawable.v2_device_default);
		}
		holder.btn_device_setting.setTag(i);
		holder.btn_device_setting.setOnClickListener(clickListener);
		holder.iv_preview.setTag(i);
		holder.iv_preview.setOnClickListener(clickListener);
		holder.btn_device_setting.setTag(i);
		holder.btn_device_setting.setOnClickListener(clickListener);
		holder.btn_device_share.setTag(i);
		holder.btn_device_share.setOnClickListener(clickListener);
		return view;
	}

	public void setDeviceList(List<Device> deviceList) {
		if(this.deviceList!=null) {
			this.deviceList.clear();
		}
		if(deviceList!=null) {
			this.deviceList.addAll(deviceList);
		}
	}
}