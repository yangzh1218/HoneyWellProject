/**
 * Project Name:  iCam
 * File Name:     BarCodeWiFiSettingActivity.java
 * Package Name:  com.wulian.icam.view.device
 *
 * @Date: 2015年6月30日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.config;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.common.RouteLibraryParams;
import com.wulian.sdk.android.oem.honeywell.ipc.CallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.ErrorCode;
import com.wulian.sdk.android.oem.honeywell.ipc.Interface;
import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.model.ConfigWiFiInfoModel;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.DeviceListActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.navigation.Navigator;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.DialogUtils;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.DirectUtils;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.MessageUtil;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;


/**
 * @author Puml
 * @ClassName: BarCodeSettingActivity
 * @Function: 二维码配置Wi-Fi
 * @Date: 2015年6月30日
 * @email puml@wuliangroup.cn
 */
public class BarCodeSettingActivity extends BaseFragmentActivity implements CallBack {
    private Button btn_hear_scan_voice;
    private ImageView iv_barcode;
    //    private RelativeLayout rl_barcode_layout;
    private LinearLayout ll_barcode;
    private String deviceId;
    private String wifi_info;
    private int QrWidth;
    private Dialog mTipDialog;
    private ConfigWiFiInfoModel mData;
    private static final int RETRY_QR_SEED = 1;
    private TextView tv_help;
    static final int SHOW_TOAST = 4;
    //    @BindView(R.id.btn_next_step)
    Button btn_next_step;
    //    @BindView(R.id.rl_barcode_layout)
    RelativeLayout rl_barcode_layout;

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_barcode_setting);
        initView();
        initData();
        setBrightestScreen();
        Interface.getInstance().setContext(this);
        Interface.getInstance().setCallBack(this);
    }


    private void initView() {
        rl_barcode_layout = (RelativeLayout) findViewById(R.id.rl_barcode_layout);
        ll_barcode = (LinearLayout) findViewById(R.id.ll_barcode);
        btn_next_step = (Button) findViewById(R.id.btn_next_step);
        btn_next_step.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mData.isAddDevice()) {
                    Intent it = new Intent(BarCodeSettingActivity.this,
                            CheckBindingStateActivity.class);
                    it.putExtra("configInfo", mData);
                    startActivity(it);
                    finish();
                } else {
                    CustomToast.show(BarCodeSettingActivity.this, R.string.setting_wifi_success);
                    Navigator navigator = new Navigator(BarCodeSettingActivity.this);
                    navigator.navigateToDeviceSetting(BarCodeSettingActivity.this);
                }
            }
        });
        ImageView titlebarBack = (ImageView) findViewById(R.id.titlebar_back);
        titlebarBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                BarCodeSettingActivity.this.finish();
            }
        });
        iv_barcode = (ImageView) findViewById(R.id.iv_barcode);
        tv_help = (TextView) findViewById(R.id.tv_help);
        tv_help.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showCommonInstructionsWebViewTipDialog(BarCodeSettingActivity.this,
                        getResources()
                                .getString(R.string.config_not_hear_tip_voice),
                        "scan_no_voice");
            }
        });
        tv_help.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    private void initData() {
        Bundle bd = getIntent().getExtras();
        if (bd != null) {
            mData = getIntent().getParcelableExtra("configInfo");
            if (mData == null) {
                this.finish();
                return;
            } else {
                deviceId = mData.getDeviceId();
                if (TextUtils.isEmpty(deviceId)) {
                    this.finish();
                    return;
                }
            }
        } else {
            this.finish();
            return;
        }
        QrWidth = Utils.getDeviceSize(this).widthPixels;
        ViewGroup.LayoutParams lp = ll_barcode.getLayoutParams();
        float left_right_width = getResources().getDimension(
                R.dimen.margin_little);
        float linearWidth = QrWidth - Utils.px2dip(this, left_right_width * 2);
        lp.height = (int) linearWidth;
        lp.width = (int) linearWidth;
        ll_barcode.setLayoutParams(lp);
        left_right_width = getResources().getDimension(R.dimen.margin_little)
                + getResources().getDimension(R.dimen.margin_little);
        QrWidth -= Utils.px2dip(this, left_right_width * 2);

        DialogUtils.showBarcodeConfigTipDialog(this);
//        rl_barcode_layout.setVisibility(View.GONE);
        if (mData.isAddDevice()) {
            BindCheck(false);
        } else {
            handlePicture(null);
        }
    }

    @Override
    protected void onDestroy() {
        myHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void BindCheck(boolean showDialog) {
        myHandler.removeMessages(RETRY_QR_SEED);
        Interface.getInstance().BindingCheck(deviceId);
    }

    private void handlePicture(String seed) {
//        rl_barcode_layout.setVisibility(View.VISIBLE);
        String originSSid = mData.getWifiName();
        String originSecurity = mData.getSecurity();
        String pwd = mData.getWifiPwd();
        StringBuilder sb = new StringBuilder();

        int secType = DirectUtils.getTypeSecurityByCap(originSecurity);
        if (secType == DirectUtils.SECURITY_OPEN) {
            sb.append(secType + "\n");
            sb.append(originSSid);
        } else {
            sb.append(secType + "\n");
            sb.append(originSSid + "\n");
            sb.append(RouteLibraryParams.EncodeMappingString(pwd, pwd.length()));
        }
        if (mData.isAddDevice()) {
            sb.append("\n");
            sb.append(RouteLibraryParams.EncodeMappingString(seed, seed.length()));
//            Utils.sysoInfo("##生成二维码加密的seed-->" + RouteLibraryParams.EncodeMappingString(seed, seed.length()));
        }
        wifi_info = sb.toString();
        Utils.sysoInfo("##createQRImage");
        createQRImage(wifi_info, QrWidth, QrWidth);// 暂时
    }

    Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case RETRY_QR_SEED:
                    mTipDialog = DialogUtils.showCommonTipDialog(
                            BarCodeSettingActivity.this,
                            false,
                            "",
                            getResources()
                                    .getString(R.string.config_barcode_expire),
                            getResources().getString(
                                    R.string.config_barcode_get_retry),
                            new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mTipDialog.dismiss();
                                    BindCheck(true);
                                }
                            });
                    break;
                case SHOW_TOAST:
                    CustomToast.show(BarCodeSettingActivity.this, msg.getData().getString("toast"));
                    break;
                case 2:
                    iv_barcode.setImageBitmap((Bitmap) msg.getData().getParcelable("bitmap"));
                    break;
                default:
                    break;
            }
        }
    };

    private Bitmap createQRImage(String qrdata, int qrwidth, int qrheight) {
        Bitmap bitmap = null;
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(qrdata,
                    BarcodeFormat.QR_CODE, qrwidth, qrheight, hints);
            int[] pixels = new int[qrwidth * qrheight];
            for (int y = 0; y < qrheight; y++) {
                for (int x = 0; x < qrwidth; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * qrwidth + x] = 0xff000000;
                    } else {
                        pixels[y * qrwidth + x] = 0xffffffff;
                    }
                }
            }
            bitmap = Bitmap.createBitmap(qrwidth, qrheight,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, qrwidth, 0, 0, qrwidth, qrheight);
            Message message = new Message();
            message.what = 2;
            Bundle bundle = new Bundle();
            bundle.putParcelable("bitmap", bitmap);
            message.setData(bundle);
            myHandler.sendMessage(message);

//            Utils.sysoInfo("##bitmap-->" + bitmap);
//            iv_barcode.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

//    @OnClick(R.id.btn_next_step)
//    void next() {
//        Intent it = new Intent(BarCodeSettingActivity.this,
//                CheckBindingStateActivity.class);
//        it.putExtra("configInfo", mData);
//
//        startActivity(it);
//    }

    /**
     * 项目名称：iCam
     * 类描述：
     * 创建人：huihui
     * 创建时间：2016年5月9日 上午10:36:37
     * 修改人：Administrator
     * 修改时间：2016年5月9日 上午10:36:37
     * 修改备注：
     *
     * @version
     */
    private void setBrightestScreen() {
        WindowManager.LayoutParams wl = getWindow().getAttributes();
        wl.screenBrightness = 1;
        getWindow().setAttributes(wl);
    }

    @Override
    public void DoSucceed(ErrorCode errorCode, RouteApiType apiType, String json) {
        switch (apiType) {
            case V3_BIND_CHECK:
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String seed = jsonObject.isNull("seed") ? "" : jsonObject
                            .getString("seed");
//                    Utils.sysoInfo("##服务器上获取的seed-->" + seed);
                    if (!TextUtils.isEmpty(seed)) {
                        String timestamp = jsonObject.isNull("timestamp") ? ""
                                : jsonObject.getString("timestamp");
//                        Utils.sysoInfo("##timestamp-->" + timestamp);
                        seed = RouteLibraryParams.getDecodeString(seed,
                                timestamp);
//                        Utils.sysoInfo("##从服务器解密的seed-->" + seed);
                        handlePicture(seed);
                        myHandler.sendEmptyMessageDelayed(RETRY_QR_SEED, 300000);
                    } else {
                        myHandler.sendMessage(MessageUtil.set(SHOW_TOAST, "toast", getResources().getString(R.string.config_device_bind_others)));
                        Intent it = new Intent(BarCodeSettingActivity.this, DeviceListActivity.class);
                        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(it);
                    }
                } catch (JSONException e) {
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void DoFailed(ErrorCode errorCode, RouteApiType apiType, Exception exception) {

    }
}
