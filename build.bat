@echo off
echo Building Momentum APK...
echo.

REM Clean previous builds
echo Cleaning previous builds...
call gradlew.bat clean

REM Build debug APK
echo Building debug APK...
call gradlew.bat assembleDebug

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Build successful!
    echo ========================================
    echo APK location: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo To install on emulator:
    echo   adb install app\build\outputs\apk\debug\app-debug.apk
    echo.
) else (
    echo.
    echo ========================================
    echo Build failed! Check errors above.
    echo ========================================
    exit /b %ERRORLEVEL%
)

pause







