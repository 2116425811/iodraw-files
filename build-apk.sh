#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
APP_DIR="$ROOT_DIR/app"
BUILD_DIR="$ROOT_DIR/build-apk"
PKG_PATH="com/iodraw/beijingclock"

ANDROID_HOME="${ANDROID_HOME:-/usr/local/lib/android/sdk}"
BT_DIR="$ANDROID_HOME/build-tools/34.0.0"
ANDROID_JAR="$ANDROID_HOME/platforms/android-34/android.jar"

AAPT2="$BT_DIR/aapt2"
D8="$BT_DIR/d8"
AAPT="$BT_DIR/aapt"
ZIPALIGN="$BT_DIR/zipalign"
APKSIGNER="$BT_DIR/apksigner"

rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/classes" "$BUILD_DIR/dex" "$BUILD_DIR/gen"

"$AAPT2" compile --dir "$APP_DIR/src/main/res" -o "$BUILD_DIR/compiled-res.zip"

"$AAPT2" link \
  -o "$BUILD_DIR/unsigned.apk" \
  -I "$ANDROID_JAR" \
  --manifest "$APP_DIR/src/main/AndroidManifest.xml" \
  --min-sdk-version 24 \
  --target-sdk-version 34 \
  --auto-add-overlay \
  --java "$BUILD_DIR/gen" \
  "$BUILD_DIR/compiled-res.zip"

javac -source 1.8 -target 1.8 \
  -classpath "$ANDROID_JAR" \
  -d "$BUILD_DIR/classes" \
  "$BUILD_DIR/gen/$PKG_PATH/R.java" \
  "$APP_DIR/src/main/java/$PKG_PATH/MainActivity.java"

"$D8" --lib "$ANDROID_JAR" --output "$BUILD_DIR/dex" "$BUILD_DIR/classes/$PKG_PATH/MainActivity.class"

cp "$BUILD_DIR/dex/classes.dex" "$BUILD_DIR/classes.dex"
"$AAPT" add "$BUILD_DIR/unsigned.apk" "$BUILD_DIR/classes.dex"

"$ZIPALIGN" -f 4 "$BUILD_DIR/unsigned.apk" "$BUILD_DIR/aligned.apk"

keytool -genkeypair -v \
  -keystore "$BUILD_DIR/debug.keystore" \
  -storepass android \
  -alias androiddebugkey \
  -keypass android \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -dname "CN=Android Debug,O=Android,C=US" >/dev/null 2>&1

"$APKSIGNER" sign \
  --ks "$BUILD_DIR/debug.keystore" \
  --ks-pass pass:android \
  --key-pass pass:android \
  --out "$BUILD_DIR/beijing-clock-debug.apk" \
  "$BUILD_DIR/aligned.apk"

"$APKSIGNER" verify "$BUILD_DIR/beijing-clock-debug.apk"

echo "APK built: $BUILD_DIR/beijing-clock-debug.apk"
