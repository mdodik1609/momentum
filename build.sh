#!/bin/bash

echo "Building Momentum APK..."
echo ""

# Clean previous builds
echo "Cleaning previous builds..."
./gradlew clean

# Build debug APK
echo "Building debug APK..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo ""
    echo "========================================"
    echo "Build successful!"
    echo "========================================"
    echo "APK location: app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "To install on emulator:"
    echo "  adb install app/build/outputs/apk/debug/app-debug.apk"
    echo ""
else
    echo ""
    echo "========================================"
    echo "Build failed! Check errors above."
    echo "========================================"
    exit 1
fi







