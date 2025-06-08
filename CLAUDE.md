# CLAUDE.md
すべてのやりとりは日本語で行ってください。
app_requirement.mdにはこのアプリの要件定義書になっています。
実装が終わったら以下のコマンドでビルドが通るか確認し、エラーが発生したら、その原因を解消してください。
```bash
 ./gradlew assembleDebug
```
作業は`claude-code`ブランチから作成したブランチで作業してください。
作業は細かくコミットしてください。
実装が終わったら`claude-code`向けのRPを作成してください。

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

```bash
# Build the project
./gradlew build

# Run debug build on connected device/emulator
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Clean build artifacts
./gradlew clean

# Generate APK
./gradlew assembleDebug

# Lint code
./gradlew lint
```

## Architecture Overview

This is an Android weather forecast application built with Kotlin and Jetpack Compose that displays tomorrow's weather based on the user's current location.

### Key Architecture Components

**MVVM Pattern with Repository:**
- `WeatherViewModel`: Manages UI state and business logic
- `WeatherRepository`: Coordinates between location service and weather API
- `LocationService`: Handles GPS location requests using FusedLocationProviderClient
- `WeatherApiService`: Retrofit interface for Open-Meteo weather API

**Data Flow:**
1. User grants location permission → `LocationService` gets GPS coordinates
2. `WeatherRepository` fetches weather data from Open-Meteo API using coordinates
3. API response is mapped to `WeatherData` model (tomorrow's forecast only)
4. `WeatherViewModel` exposes `WeatherUiState` to Compose UI

**Key Data Models:**
- `WeatherData`: Core weather information with Japanese weather descriptions
- `LocationData`: GPS coordinates
- `WeatherResponse/DailyWeather`: API response models for Open-Meteo

**UI Components:**
- `WeatherScreen`: Main screen with permission handling and state management
- `WeatherCard`, `LoadingComponent`, `ErrorComponent`: Reusable UI components
- Uses Material3 design system

### Technology Stack
- **UI**: Jetpack Compose with Material3
- **Network**: Retrofit + OkHttp with kotlinx.serialization
- **Location**: Google Play Services FusedLocationProviderClient
- **Architecture**: MVVM with Repository pattern
- **Async**: Kotlin Coroutines with StateFlow

### API Integration
Uses Open-Meteo (api.open-meteo.com) free weather API - no API key required. Fetches 2-day forecast but only displays tomorrow's weather with Japanese descriptions.

### Permissions Required
- `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION` for GPS
- `INTERNET` for weather API calls

## Code Style Guidelines

### Comment Conventions
- **Future Work**: Use proper TODO format with clear description
  ```kotlin
  // TODO: Replace with FavoriteLocationRepository when available
  // TODO: Add reverse geocoding for location names
  ```
- **Avoid informal comments**: Don't use informal comments like "(will be replaced with Repository)" or "For now, use mock data"
- **Be specific**: TODO comments should clearly indicate what needs to be done and when