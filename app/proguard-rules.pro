# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/fei/Dev/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#retrofit
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class okio.** {*;}

-dontwarn okhttp3.**
-dontwarn okio.**

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-keep class com.phillipsong.gittrending.data.models.** {*;}
-dontwarn com.phillipsong.gittrending.data.models.**

# retrolambda
-dontwarn java.lang.invoke.*

# rx
-dontwarn rx.**
-keep class rx.** { *; }

-dontwarn com.jakewharton.rxbinding.**
-keep class com.jakewharton.rxbinding.** {*;}

-dontwarn com.trello.rxlifecycle.**
-keep class com.trello.rxlifecycle.** {*;}

#realm
-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class * { *; }
-dontwarn javax.**
-dontwarn io.realm.**

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

