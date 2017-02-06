-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
-dontoptimize
-dontpreverify
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,Synthetic,EnclosingMethod
-keepparameternames
-ignorewarnings

-keep public class com.wulian.sdk.android.oem.honeywell.ipc.model.*{*;}
-keep public class com.wulian.sdk.android.oem.honeywell.ipc.sip.**{*;}
-keep public class com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.**{*;}
-keep public class com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.**{*;}
#-keep public class com.wulian.sdk.android.oem.honeywell.ipc.ui.widget.*{*;}
-keep public class com.wulian.sdk.android.oem.honeywell.ipc.utils.**{*;}
-keep public interface com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.BasePresenter
-keep public class com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.impl.BasePresenterImpl
-keep public interface com.wulian.sdk.android.oem.honeywell.ipc.ui.view.BaseView
-keep public class com.wulian.sdk.android.oem.honeywell.ipc.ui.view.impl.BaseViewImpl
-keep public class com.wulian.routelibrary.common.RouteApiType
-keep public class com.wulian.sdk.android.oem.honeywell.ipc.CallBack
-keep public class  com.wulian.sdk.android.oem.honeywell.ipc.data.repository.UserDataRepository
-keep public class com.wulian.sdk.android.oem.honeywell.ipc.AlarmCntCallBack
-keep public interface com.wulian.sdk.android.oem.honeywell.ipc.AlarmCntCallBack{
	public <fields>;
	public <methods>;
}
-keep public class com.wulian.sdk.android.oem.honeywell.ipc.receiver.MessageCallStateReceiver

-keep public class com.wulian.sdk.android.oem.honeywell.ipc.ui.widget.progress.**{*;}
-keep public interface com.wulian.sdk.android.oem.honeywell.ipc.SDKSipCallBack
-keep public interface com.wulian.sdk.android.oem.honeywell.ipc.SDKSipCallBack{
	public <fields>;
	public <methods>;
}

-keep public enum com.wulian.sdk.android.oem.honeywell.ipc.ErrorCode
-keep public enum com.wulian.sdk.android.oem.honeywell.ipc.ErrorCode{
	public <fields>;
	public <methods>;
}
-keep public class com.wulian.sdk.android.oem.honeywell.ipc.Interface
-keep public class com.wulian.sdk.android.oem.honeywell.ipc.Interface{
	public <fields>;
	public <methods>;
}
-keep public class com.wulian.sdk.android.oem.honeywell.ipc.activity.BaseFragmentActivity
-keep public class com.wulian.sdk.android.oem.honeywell.ipc.APPConfig
-keep public class com.wulian.sdk.android.oem.honeywell.ipc.APPConfig{
	public <fields>;
	public <methods>;
}
-keep public class com.wulian.sdk.android.oem.honeywell.ipc.DeviceListCallBack
-keep public class com.wulian.sdk.android.oem.honeywell.ipc.DeviceListCallBack{
	public <fields>;
	public <methods>;
}
-keep public class com.wulian.sdk.android.oem.honeywell.ipc.UnBindDeviceCallBack
-keep public class com.wulian.sdk.android.oem.honeywell.ipc.UnBindDeviceCallBack{
	public <fields>;
	public <methods>;
}
-keep public class com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.impl.BasePresenterImpl{
	public <fields>;
	public <methods>;
}

-keep public class com.wulian.sdk.android.oem.honeywell.ipc.ui.view.impl.BaseViewImpl{
	<fields>;
	<methods>;
}
#-keep public class com.wulian.sdk.android.oem.honeywell.ipc.utils.*{
#	public <fields>;
#	public <methods>;
#}

-keep class **.R$*{
*;
}
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}






