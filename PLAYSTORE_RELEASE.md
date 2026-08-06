# SpellType Keyboard — Play Store Release Guide

## 📦 Build Outputs

After CI builds, you get:

| Artifact | Description | Use |
|----------|-------------|-----|
| `spelltype-debug.apk` | Debug APK | Testing |
| `spelltype-release.apk` | Signed Release APK | Direct install |
| `spelltype-playstore.aab` | Android App Bundle | **Play Store upload** |
| `mapping.txt` | ProGuard mapping | Crash symbolication |

## 🏪 Play Store Upload Steps

1. Go to [Google Play Console](https://play.google.com/console)
2. Select your app (com.spelltype.keyboard)
3. Go to **Production** → **Create new release**
4. Upload the `.aab` file from CI artifacts
5. Fill in release notes
6. Review and roll out

## 🔐 Release Signing (Production)

For production Play Store, use your own keystore:

1. Generate keystore:
```bash
keytool -genkey -v -keystore spelltype-release.jks \
  -alias spelltype -keyalg RSA -keysize 2048 -validity 10000
```

2. Add to GitHub Secrets:
   - `KEYSTORE_BASE64` — `base64 -w0 spelltype-release.jks`
   - `KEYSTORE_PASSWORD` — your password
   - `KEY_ALIAS` — your alias
   - `KEY_PASSWORD` — your key password

3. Update CI workflow to decode keystore from secrets

## 📱 Version Management

Current version: **5.00.0** (versionCode 5)

Update in `app/build.gradle.kts`:
```kotlin
versionCode = 6  // Increment for each release
versionName = "5.01.0"  // Semantic versioning
```
