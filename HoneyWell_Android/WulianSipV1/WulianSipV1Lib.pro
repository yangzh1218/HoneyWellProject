-dontskipnonpubliclibraryclassmembers
#-dontshrink
-useuniqueclassmembernames
#-keeppackagenames
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,Synthetic,EnclosingMethod
-keepparameternames
-ignorewarnings

-keep class * extends java.lang.annotation.Annotation { *; }

-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep names - Native method names. Keep all native class/method names.
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
-keep public class com.wulian.siplibrary.api.SipController
{
	public <methods>;
}
-keep public class com.wulian.siplibrary.api.SipHandler
{
	public <methods>;
}
-keep public enum com.wulian.siplibrary.api.SipMsgApiType
{
	public <fields>;
	public <methods>;
}

-keep public class com.wulian.siplibrary.manage.SipCallSession
{
	public <fields>;
	public <methods>;
}
-keep public class com.wulian.siplibrary.manage.SipCallSession$InvState {
	public <fields>;
}
-keep public class com.wulian.siplibrary.manage.SipCallSession$MediaState {
	public <fields>;
}
-keep public class com.wulian.siplibrary.manage.SipCallSession$StatusCode {
	public <fields>;
}

-keep public class com.wulian.siplibrary.manage.SipProfile {
	public <fields>;
	public <methods>;
}
-keep public class com.wulian.siplibrary.manage.SipManager {
	public <fields>;
	public <methods>;
}
-keep public class com.wulian.siplibrary.manage.SipMessage {
	public static int MESSAGE_TYPE_INBOX;
	public static int MESSAGE_TYPE_SENT;
	public <methods>;
}

-keep public class  com.wulian.siplibrary.model.cruiseline.* {
	public <methods>;
}
-keep public class  com.wulian.siplibrary.model.linkagedetection.* {
	public <methods>;
}
-keep public enum  com.wulian.siplibrary.model.linkagedetection.WeekModel {
	public <fields>;
	public <methods>;
}

-keep public class  com.wulian.siplibrary.model.prerecordplan.* {
	public <methods>;
}
-keep public class  com.wulian.siplibrary.plugins.codecs.* {
	public <methods>;
}
-keep public class  com.wulian.siplibrary.plugins.video.* {
	public <methods>;
}
-keep public class  com.wulian.siplibrary.service.* {
	public <methods>;
}
-keep public class com.wulian.siplibrary.utils.FileUtils
{
	public <fields>;
	public <methods>;
}
-keep public class com.wulian.siplibrary.utils.WulianLog
{
	public <fields>;
	public <methods>;
}
-keep public class org.pjsip.pjsua.**
{
	public <fields>;
	public <methods>;
}
-keep public class org.webrtc.videoengine.**
{
	public <fields>;
	public <methods>;
}
