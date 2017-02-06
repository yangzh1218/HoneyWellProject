/**
 * Project Name:  iCam
 * File Name:     AlarmMsgFragment.java
 * Package Name:  com.wulian.icam.view.main
 *
 * @Date: 2015年9月5日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.ui.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;
import com.wulian.sdk.android.oem.honeywell.ipc.model.OauthMessage;
import com.wulian.sdk.android.oem.honeywell.ipc.service.AuthMsgService;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.DeviceListActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.message.ApplyDetailActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.adapter.AuthMsgAdapter;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.navigation.Navigator;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.widget.RefreshableView;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

/**
 * @ClassName: AlarmMsgFragment
 * @Function: TODO
 * @Date: 2015年9月5日
 * @author: yuanjs
 * @email: yuanjsh@wuliangroup.cn
 */
public class UserMsgFragment extends Fragment implements OnScrollListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private View outView;
    private RadioGroup rGroup_msg;
    private ListView listview_oauth;
    private TextView tv_oauth_empty;
    private RefreshableView viewItem_oauth;
    private Button btn_titlebar_right;
    private RelativeLayout rl_right;
    private LinearLayout deletePanel;
    private TextView tv_select_count;
    private CheckBox cb_delete_all;
    private Button btn_delete;
    private String uuid;
    private DataSource mDataSource;
    private AuthMsgAdapter mOauthAdapter;
    private List<OauthMessage> mOAuthMsgData;
    private boolean isServiceConnected = false;
    private boolean isRefreshing = false;// 是否在刷新
    private AuthMsgService mAuthMsgService;
    public final static int REQUESTCODE = 0X3;
    public final static String BINDING_MESSAGE_RECEIVED_ACTION = "com.wulian.icam.JPUSH_BINDING_MESSAGE_RECEIVED_ACTION";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        outView = inflater.inflate(R.layout.fragment_user_msg_jpush, container,
                false);
        return outView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        setListener();
        initData();
        bindOauthMsgService();// 轮询授权消息的服务
        getOauthMessagesFromDB();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() == null) {
            return;
        }
        stopOauthMsgService();// 授权轮询
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Navigator navigator = new Navigator(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODE
                && resultCode == ApplyDetailActivity.RESULTCODE) {
            boolean isUnread = data.getExtras().getBoolean("isUnread");
            long oauth_id = data.getExtras().getLong("oauth_id");
            if (isUnread) {
                Iterator<OauthMessage> it = mOAuthMsgData.iterator();
                while (it.hasNext()) {
                    OauthMessage oauthMessage = (OauthMessage) it.next();
                    if (oauthMessage.getId() == oauth_id) {
                        oauthMessage.setIsUnread(false);
                    }
                }
                refreshOauthAdapter(false);
            }
        }
    }


    private void initView() {
        listview_oauth = (ListView) outView.findViewById(R.id.list_oauth_msg);
        tv_oauth_empty = (TextView) outView.findViewById(R.id.tv_oauth_empty);
        viewItem_oauth = (RefreshableView) outView
                .findViewById(R.id.device_oauth_refresh);
        btn_titlebar_right = (Button) outView
                .findViewById(R.id.titlebar_right);
        rl_right = (RelativeLayout) outView.findViewById(R.id.rl_right);
        deletePanel = (LinearLayout) outView
                .findViewById(R.id.deletePanel);
        tv_select_count = (TextView) outView
                .findViewById(R.id.tv_select_count);
        cb_delete_all = (CheckBox) outView
                .findViewById(R.id.cb_delete_all);
        btn_delete = (Button) outView.findViewById(R.id.btn_delete);
        tv_select_count = (TextView) outView
                .findViewById(R.id.tv_select_count);
    }

    private void setListener() {
        listview_oauth.setOnScrollListener(this);// 标题随动
        viewItem_oauth.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                if (getActivity() != null) {
//                    ((DeviceListActivity) getActivity()).getBindingNotices(false);
                }
            }
        }, 0);
        listview_oauth
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (getActivity() == null) {
                            return;
                        }
                        Intent intent = new Intent(getActivity(),
                                ApplyDetailActivity.class);
                        intent.putExtra("oauthmsg", mOAuthMsgData.get(position));
                        UserMsgFragment.this.startActivityForResult(intent,
                                REQUESTCODE);
                        getActivity().overridePendingTransition(
                                R.anim.push_right_in, R.anim.push_left_out);
                    }
                });

        btn_titlebar_right.setOnClickListener(this);
        rl_right.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        cb_delete_all.setOnCheckedChangeListener(this);
    }

    private void initData() {
        uuid = getUUID();
        if (getActivity() == null) {
            return;
        }

        mOAuthMsgData = Collections
                .synchronizedList(new ArrayList<OauthMessage>());
        mOauthAdapter = new AuthMsgAdapter(getActivity());
        listview_oauth.setAdapter(mOauthAdapter);
//        tv_select_count.setText(Html.fromHtml(String.format(
//                getString(R.string.common_check_count), 0)));
    }

    private void bindOauthMsgService() {
        Intent msgIntent = new Intent();
        if (getActivity() == null) {
            return;
        }
        msgIntent.setClass(getActivity(), AuthMsgService.class);
        getActivity().bindService(msgIntent, serviceConnection,
                Context.BIND_AUTO_CREATE);
    }

    AuthMsgService.OauthMsgCallBack oauthMsgCallBack = new AuthMsgService.OauthMsgCallBack() {
        @Override
        public void updateOauthMsg(String oauthMsg) {
            Utils.sysoInfo("oauth:" + oauthMsg);
//            List<OauthMessage> oauthList = JsonHandler
//                    .getBindingNoticeList(oauthMsg);
//            addOauthMsg2DBandUI(oauthList);
        }
    };

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Utils.sysoInfo("onServiceConnected");
            isServiceConnected = true;
            mAuthMsgService = ((AuthMsgService.MsgBinder) service).getService();
            mAuthMsgService.setOauthMsgCallBack(oauthMsgCallBack);
            mAuthMsgService.beginOauthTask();// 开始轮询
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Utils.sysoInfo("onServiceDisconnected");
            isServiceConnected = false;
        }
    };

    private void stopOauthMsgService() {
        getActivity().unbindService(serviceConnection);
//        if (serviceConnection != null) {// && isServiceConnected
//            if (getActivity() != null) {
//
//                isServiceConnected = false;
//            }
//        }
    }

    public void addOauthMsg2DBandUI(List<OauthMessage> bindingList) {
        if (bindingList != null && bindingList.size() > 0) {
            mAuthMsgService.setAuthLastTime(String.valueOf(bindingList.get(0)
                    .getTime()));
            addOauthMsg2DB(bindingList);
            addOauthMsg2UI(bindingList);
        }
    }

    public void finishRefreshView() {
        if (viewItem_oauth.canFinishRefresh()) {
            viewItem_oauth.finishRefreshing();
        }
        isRefreshing = false;
    }

    public RefreshableView getRefreshView() {
        return viewItem_oauth;
    }

    public List<OauthMessage> getOauthList() {
        return mOAuthMsgData;
    }

    private void addOauthMsg2DB(List<OauthMessage> data) {
//        new InsertOauthMsgList2DBTask(data).execute();
    }

    private void addOauthMsg2UI(List<OauthMessage> bindingList) {
        mOAuthMsgData.addAll(0, bindingList);
        // updateNickName(); //refresh中会执行
        refreshOauthAdapter(false);
    }

    private String getUUID() {
        if (TextUtils.isEmpty(uuid)) {
//            uuid = ICamGlobal.getInstance().getUserinfo().getUuid();
        }
        return uuid;
    }

    private void updateNickName() {
        if (mOAuthMsgData != null && getActivity() != null)
            for (OauthMessage om : mOAuthMsgData) {
                if (TextUtils.isEmpty(om.getFromNick())) {
//                    Device device = ((DeviceListActivity) getActivity())
//                            .findDeviceByDeviceId(om.getDevice_id());
//                    if (device != null) {
//                        om.setFromNick(device.getDevice_nick());
//                    }
                }
            }
    }


    public void refreshOauthAdapter(boolean isShowDelete) {
        if (mOAuthMsgData.size() == 0) {
            tv_oauth_empty.setVisibility(View.VISIBLE);
        } else {
            tv_oauth_empty.setVisibility(View.GONE);
        }
        updateNickName();
        mOauthAdapter.refresh(mOAuthMsgData, isShowDelete);
        for (OauthMessage oauthMessage : mOAuthMsgData) {
            if (oauthMessage.getIsUnread()) {
                return;
            }
        }
        //如果没有节点未读，红点消失
        if (getActivity() != null) {
//            ((DeviceListActivity) getActivity()).tv_msg_count
//                    .setVisibility(View.INVISIBLE);
        }
    }

    public void getOauthMessagesFromDB() {
//        new GetAllOauthMsgFromDBTask().execute();
    }

    public void deleteAllOauthMsgFromDB() {
//        new DeleteAllOauthMsgFromDBTask().execute();
    }

    public void deleteOauthMsgsFromDB(Long[] ids) {
//        new DeleteBindingNoticesMsgFromDBTask().execute(ids);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // 第一项与第二项标题不同，说明标题需要移动
        if (mOAuthMsgData != null) {
            if (mOAuthMsgData.size() == 0) {
//				((TitledListView) view).hideTitle();
            } else if (mOAuthMsgData.size() == 1) {
//                ((TitledListView) view).updateTitle(mOAuthMsgData.get(
//                        firstVisibleItem).getTimeYMD());
                if (firstVisibleItem == 0) {// 肯定满足了
//                    ((TitledListView) view).moveTitleDown();
                }
            } else if (mOAuthMsgData.size() > 1) {// >1是为了避免只有1条数据时的get(1)越界

            }
        }
    }

    // 标记授权信息为已读
    public void updateUserMsgStatus(long oauth_id, boolean isAccept,
                                    boolean isHandle) {
        Iterator<OauthMessage> it = mOAuthMsgData.iterator();
        OauthMessage bindingNotice = null;
        while (it.hasNext()) {
            OauthMessage oauthMessage = (OauthMessage) it.next();
            if (oauthMessage.getId() == oauth_id) {
                oauthMessage.setIsUnread(false);
                oauthMessage.setAccept(isAccept);
                oauthMessage.setHandle(isHandle);
                bindingNotice = oauthMessage;
                break;
            }
        }
        Utils.sysoInfo("=====UserMsg id====" + bindingNotice.getId());
        // 更新数据库,标记为已查看
//        new UpdateBindingNoticesMsgFromDBTask(bindingNotice).execute();
        refreshOauthAdapter(false);
    }

    // 遍历，是否有未读信息
    private boolean isOauthAllReaded() {
        if (mOAuthMsgData != null && mOAuthMsgData.size() > 0) {
            Iterator<OauthMessage> it = mOAuthMsgData.iterator();
            while (it.hasNext()) {
                if (it.next().getIsUnread()) {
                    return false;
                }
            }
        }
        return true;
    }


    private boolean isShowDelete = false;
    private boolean ignore_cb_delete_all = false;

    public void toggleShowDelete() {
        isShowDelete = !isShowDelete;

        if (isShowDelete) {
            // 1、隐藏主菜单
//            ((DeviceListActivity) getActivity()).main_menu.setVisibility(View.GONE);
            // 2、设置按钮样式
            btn_titlebar_right.setBackgroundResource(0);// 无背景
            btn_titlebar_right.setText(getString(R.string.common_cancel));// 显示文字
            // 3、显示删除面板
            deletePanel.setVisibility(View.VISIBLE);
            refreshOauthAdapter(isShowDelete);
            // 6、再次恢复初始值：不忽略
            ignore_cb_delete_all = false;
        } else {
            // 1、显示主菜单
//            ((DeviceListActivity) getActivity()).main_menu
//                    .setVisibility(View.VISIBLE);
            // 2、设置按钮样式
            btn_titlebar_right
                    .setBackgroundResource(R.drawable.selector_msg_delete);// 有背景
            btn_titlebar_right.setText("");// 不显示文字
            // 3、隐藏删除面板
            deletePanel.setVisibility(View.GONE);
            // 4、刷新页面,隐藏删除
            refreshOauthAdapter(isShowDelete);

            // 隐藏后，勾选状态并不会更新，比如全选中，即使新消息来了，仍然是全选中。
        }
    }

    @Override
    public void onClick(View v) {
        if (getActivity() == null)
            return;
        int id = v.getId();
        if (id == R.id.titlebar_right || id == R.id.rl_right) {
            showDeleteView();
        } else if (id == R.id.btn_delete) {
            deleteSeletedMsg();
        }
    }

    public void showDeleteView() {
        if (getOauthList() != null
                && getOauthList().size() > 0) {
            toggleShowDelete();
        }
        return;
    }

    private void deleteSeletedMsg() {
        if (cb_delete_all.isChecked()) {// 删除所有
            toggleShowDelete();// 全部删除之后也要恢复状态
            deleteAllOauthMsgFromDB();

        } else {// 批量删除
            List<OauthMessage> deleteItems = new ArrayList<OauthMessage>();
            List<Long> deleteIds = new ArrayList<Long>();
            if (getOauthList() != null) {
                for (OauthMessage om : getOauthList()) {
                    if (om.isDelete()) {
                        deleteItems.add(om);
                        deleteIds.add(om.getId());
                    }
                }
            }
            int len = deleteIds.size();
            if (len > 0) {
                getOauthList().removeAll(deleteItems);
                toggleShowDelete();
                Long[] ids = new Long[len];
                for (int i = 0; i < len; i++) {
                    ids[i] = deleteIds.get(i);
                }
                deleteOauthMsgsFromDB(ids);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (getActivity() != null) {
            int id = buttonView.getId();
            if (id == R.id.rb_oauth_msg_choose) {
                if (isChecked)
                    Utils.sysoInfo("rb_oauth_msg_choose");
            } else if (id == R.id.cb_delete_all) {
                if (getOauthList() != null
                        && getOauthList().size() > 0) {
                    for (OauthMessage om : getOauthList()) {
                        om.setDelete(isChecked);
                    }
                    refreshOauthAdapter(isShowDelete);
                }
                updateDeleteCount();
            }
        }
    }

    public void updateDeleteCount() {
        int count = 0;
        count = getOauthDeleteCount();
        if (count == getOauthList().size()) {
            if (!cb_delete_all.isChecked()) {// 为了避免影响正常的check判断，需要加个if判断
                ignore_cb_delete_all = true;// 忽略checkchange
                cb_delete_all.setChecked(true);// 避免false到true的重新设置
            }
        } else {
            if (cb_delete_all.isChecked()) {// 为了避免影响正常的check判断，需要加个if判断
                ignore_cb_delete_all = true;// 忽略checkchange
                cb_delete_all.setChecked(false);// 避免true到false的全部取消勾选
            }
        }
//        tv_select_count.setText(Html.fromHtml(String.format(
//                getString(R.string.common_check_count), count)));
        Utils.sysoInfo("updateDeleteCount");
    }


    public int getOauthDeleteCount() {
        int i = 0;
        for (OauthMessage om : getOauthList()) {
            if (om.isDelete()) {
                i++;
            }
        }
        return i;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
