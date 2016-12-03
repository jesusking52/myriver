# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/kimjongho/Development/adt-bundle-mac-x86_64-20130917/sdk/tools/proguard/proguard-android.txt
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
-keepattributes *Annotation*
-keepattributes Signature

-include ./proguard/annotations.pro
-include ./proguard/binding.pro
-include ./proguard/dagger.pro
-include ./proguard/okio.pro
-include ./proguard/retrofit.pro
-include ./proguard/roundedimageview.pro
-include ./proguard/googleplayservices.pro
-include ./proguard/butterknife.pro
-include ./proguard/jackson.pro

-dontwarn org.apache.**
-dontwarn com.google.common.**

-keep class * implements java.lang.annotation.Annotation { *; }

# M legacy
-dontwarn android.webkit.**

# retrolambda
-dontwarn java.lang.invoke.*

# Needed to use javascript in webview
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

-keepclassmembers class ** {
    public void onEvent*(**);
}
