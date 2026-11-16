# Product Requirements Document (PRD)

## Universal Download Manager

| Field | Value |
|---|---|
| Product | Universal Download Manager |
| Platform | Android (Jetpack Compose) |
| Package | `com.technologia.universaldownloadmanager` |
| Version | 1.0 (initial release) |
| Min SDK | 26 |
| Target/Compile SDK | 37 |
| Language | Kotlin |
| Source | `images/Universal Download Manager Roadmap.png` |
| Status | Draft |

---

## 1. Overview

Universal Download Manager is an all-in-one Android download manager that lets users download videos, music, images, documents, archives, and other files from virtually any platform through a single app. It combines share-sheet intake, smart link detection, a resumable multi-connection download engine, built-in format support, a file manager, and background downloading.

**Tagline:** One App. All Formats. All Platforms.

**Vision:** Build once, download everything — a simple, fast, and reliable downloader that works across all major platforms and file types.

---

## 2. Problem Statement

Users currently juggle multiple apps to save content from different platforms and in different formats. Existing downloaders are often format-limited, ad-heavy, unreliable on slow networks, or lack background downloading and resume support. There is no single, trustworthy Android app that handles links from any platform and any common file type with speed, reliability, and a clean UI.

---

## 3. Goals and Non-Goals

### Goals

- Accept a link from any app via Android share intent and auto-detect the platform, content type, and file type.
- Provide a reliable download engine with progress, pause/resume, cancel, retry, and multi-connection speed.
- Support 100+ file types across video, audio, image, document, archive, and other categories.
- Guarantee background downloads that survive app closure and screen-off states.
- Offer a clear file manager with download history and share/open actions.
- Ship a polished, responsive Material 3 interface with dark and light themes.
- Prepare the app for Play Store release with performance and storage optimization.

### Non-Goals (v1.0)

- In-app video/audio player beyond basic open-with actions.
- Batch queue downloads of multiple links at once (post-launch).
- Playlist/YouTube channel downloads (post-launch).
- Cloud backup and sync (post-launch).
- Premium tiers, subscriptions, or ad monetization (post-launch).
- DRM-protected content or login-restricted pages requiring authentication.
- iOS or desktop clients.

---

## 4. Target Users

| Persona | Need |
|---|---|
| Casual saver | Quickly save videos, reels, and images from social apps via share sheet. |
| Media collector | Download music, video, and images in preferred formats with reliable resume. |
| Student / professional | Save PDFs, office documents, and archives for offline access. |
| Power user | Manage large queues, choose custom download locations, and control storage. |

---

## 5. Key Features

| Feature | Description |
|---|---|
| Multi-platform support | Links from YouTube, Facebook, Instagram, TikTok, X (Twitter), and general web. |
| All file types | Video, music, images, documents, archives, APK, ZIP, and more. |
| High-speed download | Multiple connections and resume support. |
| Download queue | Multiple concurrent and queued downloads with progress. |
| Smart link detection | Auto-detect source platform and file type from a shared link. |
| Built-in browser | Open links in-app to trigger downloads. |
| Download manager | Pause, resume, cancel, retry, and track status. |
| File manager | View, organize, and share downloaded files. |
| Background downloads | Long-running work via WorkManager with notifications. |
| Format support | 100+ file types across categories. |
| Theme support | Dark and light mode. |

---

## 6. Supported Formats

| Category | Formats |
|---|---|
| Video | MP4, WEBM, MKV, AVI, MOV, 3GP, FLV, M3U8, and similar |
| Audio | MP3, M4A, AAC, WAV, FLAC, OGG, and similar |
| Images | JPG, PNG, WEBP, BMP, GIF, and similar |
| Documents | PDF, DOC, DOCX, XLS, XLSX, PPT, PPTX, TXT, and similar |
| Archives | ZIP, RAR, 7Z, TAR, GZ, and similar |
| Other | APK, EXE, ISO, and similar |

The app must remain format-agnostic: unknown types are saved as raw files with best-effort MIME detection.

---

## 7. Technology Stack

| Layer | Choice |
|---|---|
| UI | Jetpack Compose, Material 3, responsive layouts |
| Language | Kotlin |
| Architecture | Clean Architecture + MVVM |
| Networking | OkHttp with Retrofit for metadata/resolution |
| Storage | Room for download records, MediaStore for public files |
| Background | WorkManager for long-running downloads |
| Minimum SDK | 26 |
| Target SDK | 37 |

Dependencies are managed through `gradle/libs.versions.toml` and the Compose BOM.

---

## 8. Functional Requirements

### 8.1 Link Intake and Detection

- **FR-1** Register an `ACTION_SEND` intent filter for `text/plain` so users can share links from any app.
- **FR-2** Extract the first valid URL from shared text and validate its scheme (HTTP/HTTPS only).
- **FR-3** Detect the source platform from the URL host and map it to a supported resolver.
- **FR-4** Determine content type and probable file extension via HEAD request, `Content-Type`, and `Content-Disposition`.
- **FR-5** Display a pre-download confirmation sheet showing source, filename, size, type, and destination.
- **FR-6** Reject unsupported or malformed links with a clear, non-blocking error message.
- **FR-7** Support direct link entry/paste inside the app as an alternative to sharing.

### 8.2 Download Engine

- **FR-8** Download via OkHttp with support for HTTP range requests and multi-connection segmentation where the server allows.
- **FR-9** Persist download state (queued, running, paused, completed, failed) so it survives process death.
- **FR-10** Emit progress updates (bytes, percent, speed, ETA) to the UI and notification.
- **FR-11** Support pause, resume, cancel, and retry; resume from the last byte offset when possible.
- **FR-12** Handle redirects, retries with exponential backoff, timeouts, and network loss.
- **FR-13** Detect insufficient storage before and during download and fail gracefully.
- **FR-14** Write to a temporary `.part` file and finalize only on success; clean up partial files on cancel.
- **FR-15** Enforce a configurable maximum number of concurrent downloads (default: 3).

### 8.3 Background and Notifications

- **FR-16** Run downloads through WorkManager with foreground notification and progress.
- **FR-17** Continue downloads when the app is backgrounded or the screen is off, subject to OS restrictions.
- **FR-18** Provide notification actions for pause, resume, and cancel where supported.
- **FR-19** Honor battery optimization and data-saver constraints with a user-visible explanation.

### 8.4 Storage and File Management

- **FR-20** Save completed files to public collections via MediaStore (Downloads/Movies/Music/Pictures/Documents) on Android 10+.
- **FR-21** Support a user-selected custom download location via the Storage Access Framework.
- **FR-22** Provide a file manager listing downloads with name, size, type, date, and status.
- **FR-23** Support open, share, rename, delete, and view-in-folder actions.
- **FR-24** Maintain searchable and sortable download history backed by Room.
- **FR-25** Enforce scoped storage; request only the permissions actually required.

### 8.5 UI and Navigation

- **FR-26** Provide bottom navigation across Home, Downloads/Queue, Files, and Settings.
- **FR-27** Show an empty state with link-paste and share-sheet guidance when no downloads exist.
- **FR-28** Show active download cards with progress, speed, ETA, and inline controls.
- **FR-29** Provide a built-in browser/web view to open links and start downloads in-app.
- **FR-30** Support dark and light themes following the system by default, with a manual override.
- **FR-31** Use Material 3 theming, dynamic color where available, and accessible touch targets.

### 8.6 Settings

- **FR-32** Configure default download location, concurrent downloads, and Wi-Fi-only mode.
- **FR-33** Toggle notifications and theme preference.
- **FR-34** Show storage usage and a clear-cache/clear-history action.

---

## 9. Non-Functional Requirements

| Area | Requirement |
|---|---|
| Performance | UI remains responsive during active downloads; no main-thread I/O. |
| Reliability | Downloads resume after network loss and process death; no corrupt finalized files. |
| Storage | Stream to disk; never buffer entire files in memory. |
| Battery | Use WorkManager constraints and avoid polling; batch progress updates. |
| Security | HTTPS by default; validate URLs; no arbitrary file writes outside scoped storage. |
| Privacy | No telemetry or link logging without explicit consent. |
| Compatibility | Android 8.0 (API 26) through Android 17 (API 37). |
| Accessibility | Content descriptions, scalable text, 48dp minimum touch targets. |
| Localization | English at launch; strings externalized for future translation. |
| Code quality | Clean Architecture, MVVM, unit-testable engine, lint-clean build. |

---

## 10. Architecture Overview

```
UI (Compose + Material 3)
    |
ViewModel (StateFlow UI state)
    |
Use Cases / Domain (link resolution, download orchestration)
    |
+----------------+----------------+----------------+
| DownloadEngine | Repository     | FileStorage    |
| (OkHttp)       | (Room)         | (MediaStore/SAF)|
+----------------+----------------+----------------+
    |
WorkManager (foreground worker + notifications)
```

- **Presentation:** Compose screens, navigation, theme, reusable components.
- **Domain:** models (`DownloadRequest`, `DownloadTask`, `MediaType`, `DownloadStatus`), use cases, interfaces.
- **Data:** Room entities/DAOs, OkHttp-based source resolvers, MediaStore/SAF storage, repositories.
- **Background:** `DownloadWorker` bridges WorkManager to the domain engine.

### Core Data Model

| Entity | Key Fields |
|---|---|
| `DownloadTask` | id, url, sourcePlatform, fileName, mimeType, totalBytes, downloadedBytes, status, localUri, createdAt, updatedAt, error |
| `DownloadSource` | platform, displayName, urlPattern, supported (resolver metadata) |
| `AppSettings` | downloadLocation, maxConcurrent, wifiOnly, theme, notificationsEnabled |

---

## 11. Milestones and Delivery Plan (Roadmap)

### Phase 1 — Project Setup & Core Structure (Day 1–5)
- Set up Android project with Jetpack Compose.
- Configure permissions and dependencies (OkHttp, Room, WorkManager, MediaStore).
- Design base UI theme (Material 3, dark/light) and navigation.
- **Exit:** App builds, runs, and navigates between placeholder screens.

### Phase 2 — Share Receiver & Link Processing (Day 6–12)
- Implement `ACTION_SEND` receiver.
- Extract and validate shared links.
- Detect platform, content type, and file extension.
- Handle unsupported links gracefully.
- **Exit:** Sharing a link from another app opens the app with a correct pre-download summary.

### Phase 3 — Download Engine (Day 13–20)
- Implement download manager with progress and retry.
- Save files to device storage.
- Handle errors and network issues.
- Support multiple file types and URLs.
- **Exit:** A direct link downloads successfully with pause/resume and survive process death.

### Phase 4 — Format Support & Converters (Day 21–28)
- Support major video formats (MP4, WEBM, etc.).
- Support audio (MP3, M4A, etc.).
- Support images (JPG, PNG, etc.).
- Support documents (PDF, DOCX, TXT, etc.).
- **Exit:** 100+ file types download correctly with correct MIME and save location.

### Phase 5 — Advanced Features & UI Polish (Day 29–38)
- Download queue and multi-download support.
- Background downloads.
- Notifications and status bar progress.
- Download history and file manager.
- Dark/light theme polish.
- **Exit:** Concurrent downloads run in background with accurate notifications and history.

### Phase 6 — Testing, Optimization & Release (Day 39–45)
- Test supported platforms and formats.
- Fix bugs and performance issues.
- Optimize storage and memory usage.
- Prepare Play Store listing.
- Release to Play Store.
- **Exit:** Release build passes QA, lint, and is published.

---

## 12. Success Metrics

- Share-to-download success rate >= 90% on supported platforms.
- Download resume success rate >= 95% after network interruption.
- Crash-free sessions >= 99.5%.
- Cold start < 2 seconds on mid-range devices.
- Average download completion within 5% of theoretical network throughput.
- Play Store rating >= 4.3 within the first quarter.
- File manager actions (open/share/delete) complete without errors.

---

## 13. Acceptance Criteria (Release Gate)

- [ ] Link shared from a supported app opens UDM with correct metadata.
- [ ] Direct URL downloads to the expected public folder.
- [ ] Pause/resume works across app restart and network loss.
- [ ] Three concurrent downloads run in the background with notifications.
- [ ] All six format categories save with correct MIME types.
- [ ] Files appear in the file manager and can be opened, shared, and deleted.
- [ ] Dark and light themes render correctly across all screens.
- [ ] No crashes or ANRs in a 48-hour QA soak test.
- [ ] Lint and type checks pass; release build is optimized.
- [ ] Play Store listing assets and privacy policy are complete.

---

## 14. Risks and Mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| Target sites change URL patterns or block downloads | Broken resolution | Pluggable source resolvers, graceful fallback to direct download, telemetry on failures. |
| Scoped storage restrictions on Android 10+ | Save failures | Use MediaStore + SAF; avoid raw file paths. |
| Aggressive background limits (Doze, OEM killers) | Interrupted downloads | WorkManager foreground service; user guidance on battery optimization. |
| Large files exhaust storage mid-download | Corrupt/incomplete files | Pre-check free space; `.part` files; failure cleanup. |
| Legal/ToS concerns for platform downloads | Store rejection or takedown | Download only user-authorized publicly accessible content; clear in-app disclaimer. |
| Multi-connection ranges unsupported | Slow downloads | Detect `Accept-Ranges`; fall back to single connection. |

---

## 15. Future Enhancements (Post v1.0)

- Batch download of multiple links.
- Playlist and channel downloads (e.g., YouTube).
- Built-in video/audio player.
- Video-to-audio (MP3) conversion.
- Custom download location presets.
- Cloud backup and sync.
- Ad-free experience (one-time purchase).
- Optional premium features.

---

## 16. Open Questions

1. Which video platforms will be officially supported at launch versus best-effort?
2. Should link resolution for gated platforms use server-side helpers, or remain fully on-device?
3. What is the default download destination policy per file category?
4. Is monetization in scope for v1.0 or strictly post-launch?
5. What privacy policy and legal disclaimer wording is required for Play Store approval?

---

## 17. Appendix — Glossary

- **Resolver:** Component that turns a platform-specific share link into a downloadable media URL.
- **Segment:** A byte range of a file downloaded in parallel for speed.
- **Scoped storage:** Android storage model restricting direct access to shared storage.
- **SAF:** Storage Access Framework, used for user-chosen destination folders.
- **WorkManager:** Android API for deferrable, guaranteed background work.
