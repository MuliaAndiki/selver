#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../.." && pwd)"
export PATH="$HOME/.npm-global/bin:$PATH"

if [ -f "$ROOT_DIR/local.properties" ]; then
  SDK_DIR="$(grep '^sdk.dir=' "$ROOT_DIR/local.properties" | cut -d= -f2)"
  export ANDROID_HOME="$SDK_DIR"
  export ANDROID_SDK_ROOT="$SDK_DIR"
fi

echo "==> Cek perangkat Android"
adb devices

echo "==> Cek Appium server"
if ! curl -sf http://127.0.0.1:4723/status >/dev/null; then
  echo "Appium belum jalan. Menjalankan Appium dengan ANDROID_HOME..."
  nohup env ANDROID_HOME="$SDK_DIR" ANDROID_SDK_ROOT="$SDK_DIR" appium --address 127.0.0.1 --port 4723 > /tmp/appium.log 2>&1 &
  sleep 6
fi

if ! curl -sf http://127.0.0.1:4723/status >/dev/null; then
  echo "Appium gagal start. Jalankan manual:"
  echo "  export ANDROID_HOME=$SDK_DIR"
  echo "  appium"
  exit 1
fi

echo "==> Cek driver UiAutomator2"
if ! appium driver list --installed 2>/dev/null | grep -q uiautomator2; then
  echo "Driver belum terpasang. Jalankan: appium driver install uiautomator2"
  echo "Lalu RESTART Appium server setelah install driver."
  exit 1
fi

UDID="${APPIUM_UDID:-$(adb devices | awk 'NR>1 && $2==\"device\" {print $1; exit}')}"
if [ -z "$UDID" ]; then
  echo "Tidak ada emulator/perangkat yang terhubung."
  exit 1
fi

echo "==> Build APK"
cd "$ROOT_DIR"
./gradlew :app:assembleDebug

echo "==> Jalankan test Appium (udid=$UDID)"
./gradlew :app:appium-test:test \
  -Dappium.device.udid="$UDID" \
  -Dappium.app.path="$ROOT_DIR/app/build/outputs/apk/debug/app-debug.apk"
