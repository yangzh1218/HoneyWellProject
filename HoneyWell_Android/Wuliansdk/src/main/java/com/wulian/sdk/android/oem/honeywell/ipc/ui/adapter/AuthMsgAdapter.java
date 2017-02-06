/**
 * Project Name:  iCam
 * File Name:     JpushAuthMsgAdapter.java
 * Package Name:  com.wulian.icam.adpter
 * @Date:         2015年9月7日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.model.OauthMessage;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.DeviceListActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: JpushAuthMsgAdapter
 * @Function: 新版消息中心适配器
 * @date: 2015年9月7日
 * @author: yuanjs
 * @email: JianSheng.Yuan@wuliangroup.com
 */
public class AuthMsgAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<OauthMessage> mList = null;
	private boolean isShowDelete;

	public AuthMsgAdapter(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		mList = new ArrayList<OauthMessage>();
	}

	public void refresh(List<OauthMessage> data, boolean isShowDelete) {
		this.mList.clear();
		if (data != null && data.size() > 0) {
			this.mList.addAll(data);
		}
		this.isShowDelete = isShowDelete;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public OauthMessage getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class Holder {
		TextView tv_request_account;// 授权用户名
		TextView tv_request_time;// 授权时间
		TextView tv_request_desc;// 授权描述
		TextView tv_oauth_mark;// 未查看标示
		TextView tv_time_head;// 头部时间
		CheckBox cb_delete;// 选择框
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder mHolder;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(
					R.layout.item_oauth_request_new, parent, false);
			mHolder = new Holder();
			mHolder.tv_request_account = (TextView) convertView
					.findViewById(R.id.tv_request_account);
			mHolder.tv_request_time = (TextView) convertView
					.findViewById(R.id.tv_request_time);
			mHolder.tv_request_desc = (TextView) convertView
					.findViewById(R.id.tv_request_desc);
			mHolder.tv_oauth_mark = (TextView) convertView
					.findViewById(R.id.tv_oauth_mark);
			mHolder.cb_delete = (CheckBox) convertView
					.findViewById(R.id.cb_delete);
			mHolder.tv_time_head = (TextView) convertView
					.findViewById(R.id.tv_time_head);
			convertView.setTag(mHolder);
		} else {
			mHolder = (Holder) convertView.getTag();
		}
		final OauthMessage item = getItem(position);
		String username = item.getUserName();
		if (TextUtils.isEmpty(username)) {
			username = item.getPhone();
			if (TextUtils.isEmpty(username)) {
				username = item.getEmail();
			}
		}
		mHolder.tv_request_account.setText(username);
		if (TextUtils.isEmpty(username)) {
			username = mContext.getResources().getString(
					R.string.share_username_unknown);
		}
		mHolder.tv_request_time.setText(item.getTimeHMS());
		mHolder.tv_time_head.setText(item.getTimeYMD());
		// 第一项 和 首个不同的项 需要显示标题，否则隐藏
		if (position == 0) {
			mHolder.tv_time_head.setVisibility(View.VISIBLE);
		} else if (position < getCount()
				&& !getItem(position).getTimeYMD().equals(
						getItem(position - 1).getTimeYMD())) {
			mHolder.tv_time_head.setVisibility(View.VISIBLE);
		} else {
			mHolder.tv_time_head.setVisibility(View.GONE);
		}
		mHolder.cb_delete
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (item.isDelete() != isChecked) {// 加个判断可以减少updateDeleteCount()，确保只是手动点击时才调用，忽略初始化重用的控件照成的CheckedChange
							item.setDelete(isChecked);// item是final的瞬间固定值
							if (((DeviceListActivity) mContext).userMsgFragment != null) {
								((DeviceListActivity) mContext).userMsgFragment
										.updateDeleteCount();
							}
						}
					}
				});// 更新为最新的监听事件
		mHolder.cb_delete.setChecked(item.isDelete());
		if(item.getIsUnread()){
			mHolder.tv_oauth_mark.setVisibility(View.VISIBLE);
		}else{
			mHolder.tv_oauth_mark.setVisibility(View.INVISIBLE);
		}
		// 复选框
		if (this.isShowDelete) {
			mHolder.cb_delete.setVisibility(View.VISIBLE);
		} else {
			mHolder.cb_delete.setVisibility(View.GONE);
		}
		int type = item.getType();
		// 1:request,2:add,3:response_accept,4:response_decline,5:delete,6:addresp_acc,7:addresp_dec,0:unknown
		switch (type) {
		case 0:
			break;
		case 1:
			if(!item.isHandle()){
				mHolder.tv_request_desc.setText(mContext.getResources().getString(
						R.string.message_user_reques));
			}else{
				if(item.isAccept()){
					mHolder.tv_request_desc.setText(mContext.getResources().getString(
							R.string.message_agree_reques));
				}else{
					mHolder.tv_request_desc.setText(mContext.getResources().getString(
							R.string.message_reject_reques));
				}
			}
			break;
		case 2:
			if(!item.isHandle()){
				mHolder.tv_request_desc.setText(mContext.getResources().getString(
						R.string.message_user_response_add));
			}else{
				if(item.isAccept()){
					mHolder.tv_request_desc.setText(mContext.getResources().getString(
							R.string.message_response_accept_invitation));
				}else{
					mHolder.tv_request_desc.setText(mContext.getResources().getString(
							R.string.message_response_decline_invitation));
				}
			}
			break;
		case 3:
			mHolder.tv_request_desc.setText(mContext.getResources().getString(
					R.string.message_user_response_accept));
			break;
		case 4:
			mHolder.tv_request_desc.setText(mContext.getResources().getString(
					R.string.message_user_response_decline));
			break;
		case 5:
			mHolder.tv_request_desc.setText(mContext.getResources().getString(
					R.string.message_user_response_delete));
			break;
		case 6:
			mHolder.tv_request_desc.setText(mContext.getResources().getString(
					R.string.message_user_response_accept_invitation));
			break;
		case 7:
			mHolder.tv_request_desc.setText(mContext.getResources().getString(
					R.string.message_user_response_decline_invitation));
			break;
		default:
			break;
		}
		return convertView;
	}
}
