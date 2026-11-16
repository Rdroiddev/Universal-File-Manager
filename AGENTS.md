# AGENTS.md

## Project

Universal Download Manager — an all-in-one Android download manager for videos, music, images, documents, archives, and other files. Built with Kotlin and Jetpack Compose.

- Package / applicationId: `com.technologia.universaldownloadmanager`
- Product requirements: `PRD.md`
- Roadmap source image: `images/Universal Download Manager Roadmap.png`

## Tech Stack

| Concern | Choice |
|---|---|
| Language | Kotlin (`kotlin.code.style=official`) |
| UI | Jetpack Compose + Material 3 |
| Build | Gradle Kotlin DSL, AGP 9.4.1 |
| SDK | minSdk 26, targetSdk/compileSdk 37 |
| Java | 11 source/target |
| Dependencies | Version catalog `gradle/libs.versions.toml`, Compose BOM |
| Testing | JUnit4, AndroidX Test, Espresso, Compose UI Test |

Planned (per PRD, not yet added): OkHttp/Retrofit, Room, WorkManager, MediaStore/SAF storage. Add these through the version catalog, not hardcoded versions.

## Commands

Run from the project root.

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK (R8 optimization enabled)
./gradlew test                   # Unit tests (app/src/test)
./gradlew connectedAndroidTest   # Instrumented tests (requires device/emulator)
./gradlew lint                   # Android lint
./gradlew :app:testDebugUnitTest # Unit tests for debug variant only
./gradlew clean                  # Clean build outputs
```

- Gradle configuration cache is enabled; avoid tasks that mutate the build directory during configuration.
- `local.properties` holds the machine-specific SDK path and must not be committed.

## Source Layout

```
app/src/main/java/com/technologia/universaldownloadmanager/
├── MainActivity.kt              # Entry point (edge-to-edge Compose host)
├── ui/theme/                    # Material 3 theme, colors, typography
└── (add as the app grows)
    ├── data/                    # Room, OkHttp resolvers, repositories, storage
    ├── domain/                  # Models, use cases, repository interfaces
    ├── ui/                      # Screens, components, navigation, view models
    └── worker/                  # WorkManager download worker

app/src/test/                    # Local unit tests
app/src/androidTest/             # Instrumented/Compose UI tests
app/src/main/keepRules/rules.keep # R8 keep rules
```

Target architecture is Clean Architecture + MVVM: Compose → ViewModel (`StateFlow`) → use cases → repositories/engine. Keep domain free of Android framework types where practical.

## Conventions

- Compose-first: build screens with Compose and Material 3 components; no XML layouts.
- Follow the existing theme entry point `UniversalDownloadManagerTheme` (supports dark/light + dynamic color).
- Use `enableEdgeToEdge()` and consume insets via `Scaffold` padding.
- Externalize user-facing strings to `res/values/strings.xml`.
- Stream downloads to disk; never buffer full files in memory. Write to `.part` files and finalize on success.
- Use MediaStore/SAF for storage on API 29+; never assume raw filesystem paths.
- Long-running work belongs in WorkManager, not in `Activity`/`ViewModel` coroutines.
- Do not add code comments unless explicitly requested.
- Register new dependencies in `gradle/libs.versions.toml` and reference them via `libs.*` aliases.
- Keep R8 rules minimal and targeted; library consumer rules are preferred over broad package wildcards.

## Testing

- Add unit tests for link validation, platform detection, format/MIME mapping, and download state transitions.
- Add Compose UI tests for share-intake flow, download list, file manager, and settings.
- Verify changes with `./gradlew test lint` at minimum; run `connectedAndroidTest` when UI behavior changes.

## Definition of Done

- Code compiles with `./gradlew assembleDebug`.
- Unit tests and lint pass.
- No regressions to existing share-intent, download, or storage behavior.
- New user-facing strings are localized and accessible (content descriptions, 48dp touch targets).
- Behavior matches the relevant section of `PRD.md`.
