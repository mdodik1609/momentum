# Momentum - Fitness Dashboard

<div align="center">

![Momentum Logo](https://via.placeholder.com/200x200/FC4C02/FFFFFF?text=M)

**A powerful, offline-first fitness tracking app built with Flutter**

[![Flutter](https://img.shields.io/badge/Flutter-3.38.5-02569B?logo=flutter)](https://flutter.dev)
[![Dart](https://img.shields.io/badge/Dart-3.10.4-0175C2?logo=dart)](https://dart.dev)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

</div>

## 📱 Overview

Momentum is a comprehensive fitness dashboard that aggregates data from Strava, file imports, and provides detailed analytics, visualizations, and training insights. Built with Flutter for cross-platform support, it offers a beautiful, Strava-inspired interface with powerful offline capabilities.

## 📸 Screenshots

<div align="center">

### Home Screen
![Home Screen](docs/screenshots/home.png)
*Dashboard with weekly stats and activity trends*

### Activity Feed
![Activity Feed](docs/screenshots/feed.png)
*List of all activities with quick stats*

### Activity Detail
![Activity Detail](docs/screenshots/detail.png)
*Detailed view with maps and charts*

### Analytics
![Analytics](docs/screenshots/analytics.png)
*Heart rate zones and personal records*

### Strava Auth
![Strava Auth](docs/screenshots/strava.png)
*Easy OAuth integration*

</div>

> **Note**: Screenshots coming soon! To add your own:
> 1. Take screenshots of the app running
> 2. Save them in `docs/screenshots/` directory
> 3. Update the paths above

### ✨ Key Features

- 🚴 **Strava Integration** - OAuth2 authentication with automatic activity sync
- 📊 **Interactive Charts** - Elevation, heart rate, pace, power, and cadence graphs
- 🗺️ **GPS Maps** - Visualize your routes with interactive maps
- 📈 **Analytics Dashboard** - Weekly, monthly, and yearly statistics
- ❤️ **Heart Rate Zones** - Zone distribution analysis with visual charts
- 🏆 **Personal Records** - Automatic PR detection for standard distances
- 📁 **File Import** - Support for GPX and TCX files
- 🔒 **Privacy-First** - All data stored locally, no cloud sync required
- 🌙 **Dark Mode** - Full dark theme support

## 🎯 App Structure

The app uses a bottom navigation bar with five main screens:

| Screen | Icon | Description |
|--------|------|-------------|
| **Home** | 🏠 | Dashboard with weekly stats and activity trends |
| **Feed** | 🏃 | Chronological list of all activities |
| **Record** | ➕ | Record new activities (coming soon) |
| **Explore** | 🔍 | Analytics, heart rate zones, and personal records |
| **Profile** | 👤 | User profile, settings, and statistics |

### 🏠 Home Screen
**Features:**
- 📊 Weekly statistics cards (distance, time, elevation, activities)
- 📈 Monthly overview with totals
- 📉 Activity trend chart (last 30 days)
- ⚡ Quick actions:
  - 📁 Import Files (GPX/TCX)
  - 🔄 Sync with Strava

**Data Displayed:**
- Total distance (km)
- Total time (hours/minutes)
- Elevation gain (meters)
- Activity count
- Daily distance trend graph

### 📋 Activity Feed Screen
**Features:**
- 📜 Chronological list of all activities
- 🔍 Search functionality
- 🔽 Filter by sport type
- 📅 Date and time display
- 📏 Distance and duration summary
- 👆 Tap to view full details

**Activity Cards Show:**
- Sport type icon
- Activity name
- Date and time
- Distance
- Duration
- Quick stats

### 🗺️ Activity Detail Screen
**Features:**
- 🗺️ **GPS Route Map**: Interactive map with route polyline
  - Start marker (green)
  - End marker (red)
  - Auto-zoom to route bounds
  
- 📊 **Interactive Charts**:
  - Elevation Profile (meters over distance)
  - Heart Rate (bpm over distance)
  - Pace/Speed (min/km or km/h)
  - Power (watts, if available)
  
- 📈 **Statistics Grid**:
  - Distance, Time, Elevation
  - Average Pace
  - Average/Max Heart Rate
  - Average/Max Power

### 📊 Analytics Screen (Explore)
**Features:**
- ❤️ **Heart Rate Zones Chart**: Bar chart showing time in each zone
  - Zone 1 (50-60%): Recovery
  - Zone 2 (60-70%): Aerobic
  - Zone 3 (70-80%): Tempo
  - Zone 4 (80-90%): Threshold
  - Zone 5 (90-100%): VO2 Max
  
- 📈 **Activity Trend**: Time series chart of daily distance
- 🏆 **Personal Records**: List of all PRs with:
  - Distance label
  - Time achieved
  - Pace
  - Activity name and date

### 👤 Profile Screen
**Features:**
- 👤 User profile header
- 📊 Activity statistics cards
- ❤️ Heart rate zones visualization
- 🏆 Personal records display
- ⚙️ Settings access

## 🚀 Quick Start

### Prerequisites

- ✅ **Flutter SDK**: 3.38.5 or later ([Install Flutter](https://flutter.dev/docs/get-started/install))
- ✅ **Dart SDK**: 3.10.4 or later (included with Flutter)
- ✅ **Android Studio** or **VS Code** with Flutter extensions
- ✅ **Android SDK** (for Android builds)
- ✅ **Xcode** (for iOS builds, macOS only)

### Installation (5 minutes)

1. **Clone and enter directory:**
   ```bash
   git clone https://github.com/yourusername/momentum.git
   cd momentum
   ```

2. **Install dependencies:**
   ```bash
   flutter pub get
   ```

3. **Generate database code:**
   ```bash
   flutter pub run build_runner build --delete-conflicting-outputs
   ```

4. **Run the app:**
   ```bash
   # Connect Android device or start emulator
   flutter run
   ```

### Building APK

**Release build (for distribution):**
```bash
flutter build apk --release
```
📱 APK location: `build/app/outputs/flutter-apk/app-release.apk`

**Debug build (for testing):**
```bash
flutter build apk --debug
```
📱 APK location: `build/app/outputs/flutter-apk/app-debug.apk`

**Install on connected device:**
```bash
adb install build/app/outputs/flutter-apk/app-release.apk
```

## 🔧 Configuration

### Strava API Setup

**Step 1: Create Strava Application**

1. Go to [Strava API Settings](https://www.strava.com/settings/api)
2. Click **"Create App"**
3. Fill in:
   - **Application Name**: Momentum (or your choice)
   - **Category**: Training
   - **Website**: (optional)
   - **Application Description**: (optional)
4. **Important**: Set **Authorization Callback Domain** to: `momentum`
5. Click **"Create"**
6. Copy your **Client ID** and **Client Secret**

**Step 2: Connect in App**

1. Open Momentum app
2. Navigate to **Home** screen
3. Tap **"Sync with Strava"** button
4. Enter your **Client ID** and **Client Secret**
5. Tap **"Connect to Strava"**
6. Browser will open - authorize the app
7. Copy the authorization code from the URL
8. Paste code in the app dialog
9. Activities will start syncing automatically

**Step 3: Verify Sync**

- Check **Activity Feed** screen for imported activities
- Activities include: GPS tracks, heart rate, power, cadence data
- Sync happens automatically after initial setup

### File Import

**Import GPX/TCX/FIT Files:**

1. Tap **"Import Files"** on Home screen
2. Select one or multiple files from your device (GPX, TCX, or FIT format)
3. Files will be automatically parsed and imported
4. Check Activity Feed to see imported activities

**Note:** FIT file import currently creates activity entries. Full GPS track and sensor data parsing from FIT files is in progress.

**Supported Formats:**
- ✅ `.gpx` - GPS Exchange Format (fully supported)
- ✅ `.tcx` - Training Center XML (fully supported)
- ✅ `.fit` - Garmin FIT format (basic support, full parsing coming soon)

**File Requirements:**
- Files must contain valid GPS track data
- Heart rate, power, and cadence data are optional
- Activities are automatically detected and categorized

## 🏗️ Architecture

### Tech Stack

- **Framework**: Flutter 3.38.5
- **Language**: Dart 3.10.4
- **State Management**: Riverpod 2.6.1
- **Database**: Drift 2.29.0 (SQLite)
- **Charts**: fl_chart 0.66.2
- **Maps**: flutter_map 7.0.2
- **HTTP**: Dio 5.4.1
- **OAuth**: Custom implementation with url_launcher

### Project Structure

```
lib/
├── database/           # Drift database setup
│   ├── database.dart
│   └── database.g.dart
├── models/             # Data models
│   └── activity.dart
├── providers/          # Riverpod providers
│   ├── activities_provider.dart
│   ├── analytics_provider.dart
│   ├── database_provider.dart
│   ├── file_import_provider.dart
│   ├── personal_records_provider.dart
│   └── strava_provider.dart
├── screens/            # App screens
│   ├── activity_detail/
│   ├── explore/
│   ├── feed/
│   ├── home/
│   ├── profile/
│   ├── strava_auth/
│   └── training/
├── services/           # Business logic
│   ├── file_import_service.dart
│   ├── strava_service.dart
│   └── export_service.dart
├── widgets/            # Reusable widgets
│   ├── activity_chart.dart
│   ├── activity_map.dart
│   └── heart_rate_zones_chart.dart
├── theme/             # App theming
│   └── app_theme.dart
└── navigation/        # Navigation setup
    └── main_navigation.dart
```

## 📊 Features in Detail

### Strava Integration

**OAuth2 Authentication:**
- Secure token-based authentication
- No password storage required
- Automatic token refresh (tokens valid ~1 year)

**Data Synchronization:**
- ✅ Import all activities from Strava
- ✅ Full activity metadata (distance, time, elevation, etc.)
- ✅ GPS track data (lat/lng coordinates)
- ✅ Heart rate streams
- ✅ Power data (for cycling)
- ✅ Cadence data
- ✅ Speed/pace data
- ✅ Elevation profiles

**Sync Features:**
- Manual sync trigger
- Automatic sync after authentication
- Duplicate detection (won't re-import existing activities)
- Batch import (handles multiple activities efficiently)

### Activity Export

Export activities to standard formats:

- **GPX Export**: Export activity with GPS track, elevation, and heart rate data
- **TCX Export**: Export in Training Center XML format with full activity details
- **Export Location**: Files saved to app's documents/exports directory
- **Access**: Use the export menu in Activity Detail screen

### Activity Search & Filtering

Find activities quickly:

- **Search**: Search by activity name or sport type
- **Filter by Sport**: Filter activities by sport type (Run, Ride, Walk, etc.)
- **Quick Access**: Search icon in Activity Feed screen
- **Real-time Results**: Results update as you type

### Settings

Configure app preferences:

- **Strava Integration**: Connect/disconnect Strava account
- **Background Sync**: Enable automatic Strava sync every 6 hours
- **Data Management**: View app information and version
- **Privacy**: All data stored locally, no cloud sync

### Activity Charts

Interactive charts powered by `fl_chart`:

- **Elevation Profile**: GPS elevation over distance
- **Heart Rate**: HR zones and trends
- **Pace/Speed**: Running and cycling pace visualization
- **Power**: Cycling power data (if available)
- **Time Series**: Activity trends over time
- **Multi-Metric**: Compare multiple metrics simultaneously

### GPS Maps

- **Route Visualization**: Interactive maps with OpenStreetMap
- **Start/End Markers**: Visual indicators for activity start (green) and end (red)
- **Route Polyline**: Colored route line following GPS track
- **Auto Zoom**: Automatic bounds calculation

### Analytics

- **Weekly Statistics**: Distance, time, elevation, activity count
- **Monthly Statistics**: Monthly totals and averages
- **Activity Trends**: 30-day activity trend visualization
- **Heart Rate Zones**: Zone distribution with bar charts
- **Personal Records**: Automatic PR detection for:
  - 1K, 5K, 10K
  - Half Marathon (21.1K)
  - Marathon (42.2K)

### Personal Records

Automatically detects and tracks:
- Best times for standard distances
- Pace records
- Distance-based PRs
- Sport-specific records

## 🔐 Privacy & Security

- **Local Storage**: All data stored locally using SQLite (Drift)
- **No Cloud Sync**: Data never leaves your device
- **Offline-First**: Full functionality without internet
- **Secure Credentials**: Strava tokens stored securely using SharedPreferences
- **No Tracking**: No analytics or tracking services

## 📦 Dependencies

### Core
- `flutter_riverpod` - State management
- `drift` - Local database
- `drift_dev` - Database code generation

### UI & Visualization
- `fl_chart` - Beautiful charts
- `flutter_map` - Map visualization
- `latlong2` - Geographic coordinates

### Data & Networking
- `dio` - HTTP client
- `xml` - XML parsing (GPX/TCX)
- `shared_preferences` - Local storage
- `url_launcher` - OAuth flow

### Utilities
- `intl` - Internationalization
- `file_picker` - File selection
- `path_provider` - File system access
- `permission_handler` - Android permissions
- `workmanager` - Background task scheduling

## 🛠️ Development

### Setup Development Environment

1. **Install Flutter:**
   ```bash
   # Check Flutter installation
   flutter doctor
   
   # Install missing dependencies as shown
   ```

2. **Clone and Setup:**
   ```bash
   git clone https://github.com/yourusername/momentum.git
   cd momentum
   flutter pub get
   flutter pub run build_runner build --delete-conflicting-outputs
   ```

3. **Run the App:**
   ```bash
   # Debug mode
   flutter run
   
   # Release mode
   flutter run --release
   
   # Specific device
   flutter run -d <device-id>
   ```

### Code Generation

The app uses code generation for the database. After modifying models:

```bash
# Watch mode (auto-regenerate on changes)
flutter pub run build_runner watch

# One-time generation
flutter pub run build_runner build --delete-conflicting-outputs

# Clean and regenerate
flutter clean
flutter pub get
flutter pub run build_runner build --delete-conflicting-outputs
```

### Building

```bash
# Android APK
flutter build apk --release

# Android App Bundle (for Play Store)
flutter build appbundle --release

# iOS (macOS only)
flutter build ios --release
```

### Database Migrations

When modifying database schema:

1. Update `lib/models/activity.dart`
2. Increment `schemaVersion` in `lib/database/database.dart`
3. Add migration logic in `migration` getter
4. Regenerate code: `flutter pub run build_runner build --delete-conflicting-outputs`

## 🐛 Troubleshooting

### Build Issues

**Android SDK not found:**
```bash
# macOS/Linux
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$PATH:$ANDROID_HOME/platform-tools"

# Then build
flutter build apk --release
```

**Database code not generated:**
```bash
flutter clean
flutter pub get
flutter pub run build_runner build --delete-conflicting-outputs
```

**Dependencies conflicts:**
```bash
flutter pub upgrade
flutter pub get
```

**File picker errors:**
- Ensure `file_picker` version is 8.0.0 or higher
- Run `flutter pub upgrade file_picker`

### Strava Integration

**OAuth not working:**
- ✅ Verify callback domain is set to `momentum` in Strava app settings
- ✅ Check that Client ID and Secret are correct
- ✅ Ensure internet connection is available
- ✅ Check that `url_launcher` package is properly configured

**Activities not syncing:**
- ⚠️ Check Strava API rate limits (600 requests per 15 minutes)
- ⚠️ Verify token hasn't expired (tokens last ~1 year)
- ⚠️ Check app logs: `flutter logs`
- ⚠️ Try manual sync from Home screen

**Token expired:**
- Re-authenticate via "Sync with Strava" button
- Enter authorization code when prompted
- Tokens will auto-refresh when near expiration

### Database Issues

**Database errors:**
```bash
# Reset database (WARNING: Deletes all data)
# Delete the database file on device or emulator
# App will recreate it on next launch
```

**Migration errors:**
- Increment `schemaVersion` in `database.dart`
- Add migration logic in `migration` getter
- Regenerate code with build_runner

### Performance

**Slow chart rendering:**
- Charts are optimized for up to 2000 data points
- Large activities may take a moment to render
- Consider reducing stream data for old activities

**Large database:**
- Database automatically manages size
- Old activities (>2 years) can be cleaned up
- Stream data is compressed for old activities

## 🗺️ Roadmap

### Short Term
- [x] FIT file import support
- [x] Background sync for Strava
- [x] Export activities to GPX/TCX
- [x] Activity search and filtering
- [x] Settings screen implementation

### Medium Term
- [ ] Training plans
- [ ] Workout builder
- [ ] Advanced analytics (power zones, training load)
- [ ] Custom distance projections
- [ ] Activity sharing

### Long Term
- [ ] Wear OS companion app
- [ ] iOS improvements
- [ ] Web support
- [ ] Social features (optional)
- [ ] Cloud backup (optional, privacy-preserving)

## 📊 Feature Comparison

| Feature | Momentum | Strava | Garmin Connect |
|---------|----------|--------|----------------|
| Offline-First | ✅ | ❌ | ❌ |
| Privacy (Local Storage) | ✅ | ❌ | ❌ |
| Free Analytics | ✅ | Limited | Limited |
| Heart Rate Zones | ✅ | ✅ | ✅ |
| Personal Records | ✅ | ✅ | ✅ |
| GPS Maps | ✅ | ✅ | ✅ |
| Activity Charts | ✅ | ✅ | ✅ |
| File Import | ✅ | Limited | ✅ |
| Strava Sync | ✅ | N/A | Limited |
| No Subscription | ✅ | ❌ | ❌ |

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- [Strava API](https://developers.strava.com/) for activity data
- [fl_chart](https://github.com/imaNNeoFighT/fl_chart) for beautiful charts
- [flutter_map](https://github.com/fleaflet/flutter_map) for map rendering
- [Drift](https://drift.simonbinder.eu/) for database management
- [Riverpod](https://riverpod.dev/) for state management

## 📞 Support

For issues, questions, or feature requests:
- Open an issue on [GitHub](https://github.com/yourusername/momentum/issues)
- Check existing issues and discussions

---

<div align="center">

**Built with ❤️ using Flutter**

*For athletes who value privacy, performance, and beautiful design*

</div>
