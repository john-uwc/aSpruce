# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\adt\adt-bundle-windows-x86_64-20140702\sdk/tools/proguard/proguard-android.txt
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
-flattenpackagehierarchy 'obs'
-keepattributes Signature, InnerClasses, SourceFile, LineNumberTable, LocalVariableTable, Exceptions

#===================== support design =====================
#@link http://stackoverflow.com/a/31028536
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }
#==========================================================


-keep ,allowoptimization public class uwc.android.spruce.** {
    public *;
    protected *;
}