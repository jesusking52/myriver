# http://stackoverflow.com/questions/24871219/problems-using-latest-jars-from-square-retrofit-okhttp-okio-and-okhttp-urlco
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-keep class retrofit.** { *; }
-dontwarn retrofit.**
-dontwarn rx.**
-dontwarn com.squareup.okhttp.**
