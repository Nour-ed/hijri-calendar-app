#!/bin/bash
# بناء تطبيق التقويم الهجري للأندرويد
# Hijri Calendar APK Builder

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

echo "=============================================="
echo "  بناء التقويم الهجري للأندرويد"
echo "=============================================="

# Copy latest HTML to assets
echo "📄 نسخ آخر إصدار من HTML..."
cp ../التقويم_الهجري.html app/src/main/assets/

# Clean old builds
echo "🧹 تنظيف البناء القديم..."
./gradlew clean 2>/dev/null || true

# Build debug APK
echo "🔨 بناء APK..."
./gradlew assembleDebug

# Check result
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK_PATH" ]; then
    cp "$APK_PATH" "التقويم_الهجري.apk"
    echo ""
    echo "✅ تم بناء APK بنجاح!"
    echo "   📱 المسار: $APK_PATH"
    SIZE=$(stat -c%s "التقويم_الهجري.apk" 2>/dev/null || stat -f%z "التقويم_الهجري.apk" 2>/dev/null)
    echo "   📦 الحجم: $(echo "scale=1; $SIZE/1048576" | bc) MB"
    echo ""
    echo "🚀 للتثبيت: adb install -r التقويم_الهجري.apk"
else
    echo "❌ فشل بناء APK!"
    exit 1
fi
