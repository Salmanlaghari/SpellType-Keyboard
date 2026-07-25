# Google Play Services Ads Keep Rules
-keep class com.google.android.gms.ads.** { *; }
-keep interface com.google.android.gms.ads.** { *; }

# Android App Standard Keep Rules
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# Jetpack Room Rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Datastore & Preferences Keep Rules
-keep class androidx.datastore.preferences.protobuf.** { *; }

# Kotlin Coroutines Rules
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
-dontwarn javax.naming.**
