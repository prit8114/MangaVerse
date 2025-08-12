# MangaVerse - Modern Manga Reader App

## Overview
MangaVerse is a production-ready manga reader Android application built with modern Android development practices. The app follows Clean Architecture principles with MVI pattern for predictable state management.

## Tech Stack
- Kotlin 1.9+
- Jetpack Compose with Material 3
- Dynamic colors & AMOLED theme support
- MVI + Clean Architecture
- Coroutines & Flow for asynchronous operations
- Hilt for dependency injection
- Room Database with SQLCipher for encrypted storage
- Retrofit & OkHttp for networking
- Paging 3 for efficient data loading
- Google ML Kit for AI panel detection
- DataStore for preferences
- WorkManager for background tasks
- Coil for image loading

## Features
- Dual reading modes: Webtoon (vertical scroll) and Page (horizontal swipe)
- Offline downloads with encrypted storage
- MangaDex API integration with pagination
- AI-powered panel detection for enhanced reading experience
- Live translation overlay
- MAL/AniList synchronization
- Reading clubs for social interaction
- Secure file storage
- Material You dynamic theming
- AMOLED dark mode

## Architecture
The application follows Clean Architecture principles with three main layers:
1. **Presentation Layer**: Compose UI components, ViewModels, and State management
2. **Domain Layer**: Use cases, domain models, and repository interfaces
3. **Data Layer**: Repository implementations, data sources, and models

The app uses MVI (Model-View-Intent) pattern for unidirectional data flow and predictable state management.

## Project Structure
Detailed project structure and architecture diagrams are available in the `docs` directory.

## Development Plan
A comprehensive development plan is available in the `docs/development-plan.md` file.

## Legal Considerations
Legal guidelines and compliance information are available in the `docs/legal-guidelines.md` file.

## Play Store Submission
A checklist for Play Store submission is available in the `docs/play-store-checklist.md` file.