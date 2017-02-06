-dontskipnonpubliclibraryclassmembers
#-dontshrink
-useuniqueclassmembernames
#-keeppackagenames
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,Synthetic,EnclosingMethod
-keepparameternames
-ignorewarnings
-keep class * extends java.lang.annotation.Annotation { *; }
-keep class  okhttp3.**{*;}
-keep class  okio.**{*;}
#-keep class  com.alibaba.sdk.android.**{*;}

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
-keep public class com.wulian.oss.callback.ConnectDataCallBack
{
	public <methods>;
}
-keep public class com.wulian.oss.model.FederationToken
{
	public <methods>;
}
-keep public class com.wulian.oss.model.GetObjectDataModel
{
	public <methods>;
}
-keep public class com.wulian.oss.service.WulianOssClient
{
	public <methods>;
}

-keep public class com.wulian.oss.Utils.OSSXMLHandler
{
	public <methods>;
}
