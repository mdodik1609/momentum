# Momentum - Fitness Dashboard

A high-performance, offline-first, and privacy-centric fitness dashboard for Android. Aggregate data from Strava, Garmin, and file imports, providing comprehensive analytics, visualizations, and training insights.

## 🎯 Features

### Core Functionality
- **Offline-First**: 100% functional offline with existing data
- **Data Aggregation**: Import from Strava API, Garmin, and file imports (.fit, .gpx, .tcx)
- **Privacy-Centric**: All data stored locally, no cloud sync required
- **Clean Design**: Data-focused, non-orange design language

### Data Sources
- **Strava Integration**: OAuth2 authentication, automatic sync, background sync
- **File Import**: Support for .gpx, .tcx files (.fit coming soon)
- **Garmin**: File import support

### Analytics & Metrics
- **Activity Statistics**: Distance, time, elevation, pace, power, heart rate
- **Personal Records**: Automatic detection of PRs across all distances
- **Zone Analysis**: Heart rate, pace, and power zone distribution
- **Grade Adjusted Pace (GAP)**: Normalized pace accounting for elevation
- **Relative Effort**: Strava-like effort calculation based on HR zones
- **Fitness & Freshness**: TRIMP-based fitness, fatigue, and form tracking
- **Projected Times**: Predict finish times for 5K, 10K, Half, and Marathon based on recent performance

### Visualizations
- **Interactive Maps**: MapLibre-based maps with GPS track visualization
- **Enhanced Graphs**: 
  - Distance/time-based X-axis
  - Formatted values (pace, HR, power, etc.)
  - Interactive scrubbing (tap/drag to explore)
  - Visual scrubber indicator
  - Multiple graph types: Elevation, HR, Power, Cadence, Pace, Speed
- **Multi-line Charts**: Fitness, Fatigue, and Form trends

### Training Insights
- **Activity Streak**: Track consecutive days with activities
- **Monthly Overview**: Activity count, active days, current streak
- **Training Log**: Calendar view of all activities
- **Advanced Analytics**: Long-term fitness trends and patterns

### Performance Optimizations
- **Map Rendering**: Douglas-Peucker algorithm reduces points by 80-95%
- **Database Management**: Automatic cleanup of old activities, stream compression
- **Responsive Design**: Adapts to phones, tablets, and large screens
- **Memory Management**: Efficient caching, low-memory device support
- **Battery Optimization**: Smart background sync, efficient rendering

## 📱 Screenshots

*Screenshots coming soon*

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 36+ (Android 16)
- Kotlin 1.9.0+
- JDK 17+

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/momentum.git
cd momentum
```

2. Open in Android Studio:
   - File → Open → Select the `momentum` directory

3. Configure Strava API (optional):
   - Get API credentials from [Strava Developers](https://www.strava.com/settings/api)
   - Add to `app/src/main/java/com/momentum/fitness/data/config/AppConfig.kt` or use secure storage

4. Build and run:
   - Click Run or press `Shift+F10`

### First Run

1. **Set Up Heart Rate Zones**: Go to Settings → Heart Rate Zones
   - Configure your 5 HR zones (Zone 1-5 max values)

2. **Set Functional Thresholds** (optional but recommended):
   - **FTP (Functional Threshold Power)**: For cycling activities
   - **FTPa (Functional Threshold Pace)**: For running activities

3. **Connect Strava** (optional):
   - Go to Settings → Connections → Strava
   - Click "Connect" and authorize the app
   - Enable "Auto-Sync" for background updates

4. **Import Activities**:
   - Use "Import Activity File" button on Dashboard
   - Or connect Strava for automatic sync

## 📊 Features in Detail

### Relative Effort Calculation

Momentum uses a Strava-like algorithm to calculate relative effort:

- **HR Zone-Based**: Time spent in each zone contributes differently
- **Exponential Scaling**: Higher HR zones contribute exponentially more
- **Power Support**: For cycling, uses power data when available (TSS-like)
- **Normalized**: Accounts for duration and intensity

**Zone Multipliers**:
- Zone 1 (50-60% max HR): 0.1x
- Zone 2 (60-70% max HR): 0.3x
- Zone 3 (70-80% max HR): 0.6x
- Zone 4 (80-90% max HR): 1.0x
- Zone 5 (90-100% max HR): 1.5x

### Projected Times

Predicts finish times for standard distances based on:
- **Recent Performance**: Last 30-90 days of running activities
- **HR Zone Analysis**: Effort level normalization
- **Distance Curves**: Uses Riegel formula with adjustments
- **FTP Integration**: Uses Functional Threshold Pace when available

**Supported Distances**:
- 5K (5,000m)
- 10K (10,000m)
- Half Marathon (21,097.5m)
- Marathon (42,195m)

**Confidence Levels**:
- **High**: Close distance match + 5+ sample activities
- **Medium**: Close distance match + 2+ sample activities
- **Low**: 1+ sample activity
- **Very Low**: Extrapolated from FTP only

### Enhanced Graphs

**Features**:
- **X-Axis Options**: Distance (km) or Time (hours:minutes)
- **Formatted Values**: All metrics properly formatted with units
- **Interactive Scrubbing**: Tap or drag to explore the activity
- **Visual Feedback**: Vertical line and point indicator
- **Info Card**: Shows formatted values at scrubbed position
- **Color Coding**: Each graph type has distinct colors

**Graph Types**:
- Elevation Profile (m)
- Heart Rate (bpm)
- Power (W)
- Cadence (rpm)
- Pace (min/km) - Running activities
- Speed (km/h) - Cycling activities

### Map Optimization

- **Point Reduction**: Douglas-Peucker algorithm
- **Smart Sampling**: Max 1000 points per map
- **Performance**: 80-95% reduction in rendering load
- **Visual Quality**: Maintains route shape while reducing points
- **Start/End Markers**: Visual indicators for route start (green) and end (red)

### Database Management

- **Automatic Cleanup**: Removes activities older than 2 years
- **Stream Compression**: Old activities (>90 days) get reduced stream data
- **Size Monitoring**: Tracks database size and activity/stream counts
- **Manual Cleanup**: User-triggered cleanup in Settings
- **Batch Processing**: Processes deletions in batches to avoid memory issues

**Configuration**:
- Default retention: 2 years (730 days)
- Max stream points per activity: 2000
- Max database size: 500 MB (auto-cleanup trigger)
- Stream sampling rate: Every 5th point for old activities

### Responsive Design

- **Screen Size Detection**: Small (<600dp), Medium (600-840dp), Large (>840dp)
- **Adaptive Layouts**: Different layouts for phones vs tablets
- **Dynamic Sizing**: Map and graph heights adjust to screen size
- **Density-Independent Units**: Uses `dp` and `sp` throughout
- **Tablet Optimization**: Multi-column layouts for tablets

## 🏗️ Architecture

### Tech Stack
- **Language**: Kotlin 100%
- **UI**: Jetpack Compose 100%
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Database**: Room (SQLite)
- **Networking**: Retrofit + OkHttp
- **Charts**: Vico
- **Maps**: MapLibre Compose
- **Background Tasks**: WorkManager
- **Date/Time**: kotlinx-datetime
- **File Parsing**: SimpleXML (GPX/TCX)

### Project Structure

```
app/src/main/java/com/momentum/fitness/
├── data/
│   ├── api/              # API interfaces (Strava)
│   ├── config/             # App configuration, secure storage
│   ├── database/           # Room entities, DAOs, converters
│   ├── model/              # Data models (SportType, zones, etc.)
│   ├── network/            # Network interceptors (retry logic)
│   ├── parser/             # File parsers (GPX, TCX, FIT)
│   ├── repository/         # Data repositories
│   ├── service/            # Business logic services
│   ├── util/               # Calculators, utilities
│   └── work/               # WorkManager workers
├── di/                     # Dependency injection modules
├── ui/
│   ├── component/          # Reusable UI components
│   │   ├── graph/         # Graph components
│   │   ├── map/           # Map components
│   │   └── zone/          # Zone analysis cards
│   ├── navigation/        # Navigation setup
│   ├── screen/            # Screen composables and ViewModels
│   ├── theme/             # Material Design theme
│   └── util/              # UI utilities, formatters
└── MainActivity.kt         # Main entry point
```

## 🔧 Configuration

### Strava API Setup

1. Create a Strava app at https://www.strava.com/settings/api
2. Set redirect URI: `momentum://strava`
3. Add credentials via Settings → Connections → Strava
   - Or configure in `AppConfig` (not recommended for production)

### Heart Rate Zones

Configure in Settings → Heart Rate Zones:
- Zone 1 Max: Typically 60% of max HR
- Zone 2 Max: Typically 70% of max HR
- Zone 3 Max: Typically 80% of max HR
- Zone 4 Max: Typically 90% of max HR
- Zone 5 Max: Typically 100% of max HR

### Functional Thresholds

**FTP (Functional Threshold Power)**:
- The highest power you can sustain for ~1 hour
- Used for power zone analysis and cycling effort calculation

**FTPa (Functional Threshold Pace)**:
- The fastest pace you can sustain for ~1 hour
- Format: Minutes:Seconds per km
- Used for pace zone analysis and running projections

## 📈 Data Models

### ActivityEntity
- Basic activity information (name, type, date, distance, time, etc.)
- Source tracking (Strava, file import, etc.)
- Summary metrics (avg/max HR, power, cadence, etc.)

### ActivityStreamEntity
- Time-series data points
- GPS coordinates, altitude, HR, power, cadence, speed
- Time offset from activity start

### PersonalRecordEntity
- Automatically detected PRs
- Distance, time, pace records per sport type

### UserSettingsEntity
- Heart rate zones
- Functional thresholds (FTP, FTPa)
- Strava connection info

## 🔐 Privacy & Security

- **Local Storage**: All data stored locally on device
- **Encrypted Credentials**: Strava credentials stored in Android Keystore
- **No Cloud Sync**: Data never leaves your device
- **Offline-First**: Full functionality without internet connection
- **Secure Storage**: Uses EncryptedSharedPreferences for sensitive data

## 🐛 Known Issues

- FIT file import: Coming soon (GPX and TCX fully supported)
- Garmin API: File import only (API integration planned)
- Projected times: Requires recent running activities for accuracy

## 🚧 Roadmap

- [ ] FIT file import support
- [ ] Garmin API integration
- [ ] Export data functionality
- [ ] Custom distance projections
- [ ] Training plans
- [ ] Social features (optional)
- [ ] Wear OS companion app

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📝 License

[Add your license here]

## 🙏 Acknowledgments

- Strava API for activity data
- MapLibre for map rendering
- Vico for chart library
- All open-source contributors

## 📞 Support

For issues, questions, or feature requests, please open an issue on GitHub.

---

**Built with ❤️ for athletes who value privacy and performance**
