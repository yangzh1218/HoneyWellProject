-dontskipnonpubliclibraryclassmembers
-dontshrink
-useuniqueclassmembernames
-keeppackagenames
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,Synthetic,EnclosingMethod
-keepparameternames
-ignorewarnings

-keep class * extends java.lang.annotation.Annotation { *; }
-keep public class org.apache.http.entity.mime.*{*;}

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

-keep public class com.wulian.lanlibrary.LanController 
{
	public <methods>;
}
-keep public class com.wulian.lanlibrary.MulticastContinueLanClient 
{
	public static void receivedData(java.lang.String[]);
	public <methods>;
}
-keep public enum com.wulian.routelibrary.common.RouteApiType {
	public <fields>;
	public <methods>;
}
-keep public enum com.wulian.routelibrary.common.ErrorCode {
	public <fields>;
	public <methods>;
}
-keep public enum com.wulian.routelibrary.common.RequestType {
	public <fields>;
	public <methods>;
}
-keep public class com.wulian.routelibrary.common.RouteLibraryParams {
	public <methods>;
}
-keep public class com.wulian.routelibrary.common.LibraryConstants {
	public static java.lang.String SOFT_TYPE_ICAM;
	public static java.lang.String SOFT_TYPE;
}
-keep public class com.wulian.routelibrary.common.LibraryConstants$ThirdType {
	public <fields>;
}
-keep public class com.wulian.routelibrary.utils.LibraryLoger {
	public <methods>;
}
-keep public class  com.wulian.routelibrary.exception.* {
	public <methods>;
}
-keep public class com.wulian.routelibrary.utils.FileUtils {
	public <methods>;
}
-keep public class com.wulian.routelibrary.utils.LibraryPhoneStateUtil {
	public <methods>;
}
-keep public class com.wulian.routelibrary.utils.MD5 {
	public <methods>;
}
-keep public class com.wulian.routelibrary.controller.RouteLibraryController {
	public <methods>;
}
-keep public class com.wulian.routelibrary.controller.RouteLibraryHandler {
	public <methods>;
}
-keep public class com.wulian.routelibrary.ConfigLibrary {
	public static int TIMEOUT;
	public <methods>;
}
-keep public class com.wulian.routelibrary.controller.TaskResultListener 
{
	public <methods>;
}
-keep public class com.wulian.routelibrary.model.HttpRequestModel
{
	public <methods>;
}
