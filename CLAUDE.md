# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**NetWorth** is a Kotlin Multiplatform (KMP) project targeting Android and iOS, built with Compose Multiplatform. It tracks investment portfolio values by combining locally stored holdings with live market quotes fetched from the CNBC API.

## Build Commands

```bash
# Build Android debug APK
./gradlew :composeApp:assembleDebug

# Build Android release APK
./gradlew :composeApp:assembleRelease

# Run all unit tests (commonTest)
./gradlew :composeApp:testDebugUnitTest

# Run a single test class
./gradlew :composeApp:testDebugUnitTest --tests "com.maroondevelopment.networth.ComposeAppCommonTest"

# Android lint
./gradlew :composeApp:lint

# Clean build outputs
./gradlew clean
```

iOS builds must be done via Xcode by opening `/iosApp/`.

## Architecture

Clean Architecture with three layers, all in `composeApp/src/commonMain/kotlin/com/maroondevelopment/networth/`:

```
Presentation  →  Domain  →  Data  →  Network/DB
```

- **`domain/entity/`** — Pure Kotlin data models (`Portfolio`, `Asset`, `Holding`, `Quote`, `CachePolicy`)
- **`domain/repository/`** — Repository interfaces (`HoldingsRepository`, `QuoteRepository`)
- **`domain/usecase/FetchPortfolioUseCase`** — Combines holdings + quotes into a `Portfolio`
- **`data/repository/`** — Repository implementations that wire data sources to domain interfaces
- **`data/datasource/QuoteDataSource`** — Two implementations: `LocalQuoteDataSourceImpl` (SQLDelight cache) and `RemoteQuoteDataSourceImpl` (CNBC HTTP)
- **`presentation/`** — `SnapshotViewModel` + `SnapshotView` (Compose UI) + `App.kt` (root composable)
- **`di/Factory.kt`** — Manual service locator; call `Factory.initialize(DriverFactory())` at app startup

## Key Tech Stack

| Concern | Library |
|---|---|
| UI | Compose Multiplatform 1.10.1, Material 3 |
| Networking | Ktor Client 2.3.0 (OkHttp on Android, Darwin on iOS) |
| Database | SQLDelight 2.2.1 — single `Quote` table (symbol PK, price) |
| Serialization | Kotlinx Serialization 1.10.0 |
| DI | Manual factory (`di/Factory.kt`) — no Hilt/Koin |
| ViewModel | `androidx.lifecycle:lifecycle-viewmodel-compose` |

## Source Set Layout

```
composeApp/src/
├── commonMain/          # All shared business logic, UI, and domain code
│   ├── kotlin/com/maroondevelopment/networth/
│   └── sqldelight/      # .sq schema files (Quote table)
│   └── composeResources/files/investments.json  # Static holdings data
├── androidMain/         # Android entry point (App.kt, MainActivity, DriverFactory)
└── iosMain/             # iOS DriverFactory implementation
```

## Data Flow

1. `MainActivity` → `App` composable → `Factory.initialize(DriverFactory())`
2. `SnapshotView` observes `SnapshotViewModel.uiState`
3. ViewModel calls `FetchPortfolioUseCase`:
   - Reads holdings from `investments.json` via `HoldingsRepositoryImpl`
   - Fetches quotes via `QuoteRepositoryImpl` using `CachePolicy.PREFER_CACHE` (local SQLDelight first, remote CNBC fallback) or `CachePolicy.REFRESH` (always remote)
   - Merges data into `Asset` objects and returns a `Portfolio`
4. Remote quotes hit: `https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol`

## Dependency Versions

All versions are centrally managed in `gradle/libs.versions.toml`. Key versions:
- Kotlin: 2.3.10, AGP: 8.11.2, compileSdk/targetSdk: 36, minSdk: 30, JVM target: 11
