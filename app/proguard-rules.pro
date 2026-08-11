# ═══════════════════════════════════════════════════════════════
#  SpellType Keyboard — ProGuard / R8 Rules
#  Optimized for Play Store release with minification
# ═══════════════════════════════════════════════════════════════

# ─── Keep Kotlin Metadata ───
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-dontwarn kotlin.**

# ─── Keep Coroutines ───
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# ─── Keep Room Entities & DAOs ───
-keep class com.spelltype.keyboard.data.db.** { *; }
-keep class com.spelltype.keyboard.data.datastore.** { *; }
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# ─── Keep Domain Models ───
-keep class com.spelltype.keyboard.domain.model.** { *; }
-keep class com.spelltype.keyboard.domain.theme.** { *; }
-keep class com.spelltype.keyboard.domain.language.** { *; }

# ─── Keep IME Service ───
-keep class com.spelltype.keyboard.presentation.ime.SpellTypeIME { *; }

# ─── Keep AdMob ───
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.** { *; }
-dontwarn com.google.android.gms.**

# ─── Keep DataStore ───
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

# ─── Keep ViewBinding ───
-keep class com.spelltype.keyboard.databinding.** { *; }

# ─── Keep Serializable ───
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ─── Keep Enums ───
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ─── Keep Parcelable ───
-keep class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ─── Optimize ───
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification

# ─── Remove Logging in Release ───
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
