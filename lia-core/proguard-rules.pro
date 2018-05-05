# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/sumit/Library/Android/sdk/tools/proguard/proguard-android.txt
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
-dontwarn **
-target 1.7
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-keep class **
-keepclassmembers class *{*;}
-keepattributes *
-dontshrink
-dontoptimize
-dontobfuscate

-assumenosideeffects class android.util.Log {
   public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-assumenosideeffects class java.io.PrintStream {
    public void println(...);
    public void print(...);
}