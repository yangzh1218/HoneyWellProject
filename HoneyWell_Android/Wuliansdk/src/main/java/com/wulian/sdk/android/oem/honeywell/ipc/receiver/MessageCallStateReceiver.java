package com.wulian.sdk.android.oem.honeywell.ipc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wulian.sdk.android.oem.honeywell.ipc.Interface;
import com.wulian.siplibrary.api.SipHandler;
import com.wulian.siplibrary.api.SipMsgApiType;
import com.wulian.siplibrary.manage.SipManager;
import com.wulian.siplibrary.manage.SipMessage;

/**
 * 作者：Administrator on 2016/8/10 09:22
 * 邮箱：huihui@wuliangroup.com
 */
public class MessageCallStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(SipManager.GET_ACTION_SIP_MESSAGE_RECEIVED())) {
            SipMessage sm = (SipMessage) intent
                    .getParcelableExtra("SipMessage");
            if (sm != null) {
                SipMsgApiType apiType = SipHandler.parseXMLData(sm
                        .getBody());
                if (sm.getType() == SipMessage.MESSAGE_TYPE_SENT) {
                    if (!sm.getContact().equalsIgnoreCase("200")) {
                        Interface.getInstance().SipDataReturn(false, apiType, sm.getBody(),
                                sm.getFrom(), sm.getTo());
                    } else if (apiType == SipMsgApiType.NOTIFY_WEB_ACCOUNT_INFO) {
                        Interface.getInstance().SipDataReturn(false, apiType, sm.getBody(),
                                sm.getFrom(), sm.getTo());
                    }
                } else if (sm.getType() == SipMessage.MESSAGE_TYPE_INBOX) {
                    Interface.getInstance().SipDataReturn(true, apiType, sm.getBody(),
                            sm.getFrom(), sm.getTo());
                }
            }
        }
    }
}
