# AI Bell — ChatGPT-style Android UI

A clean Android Jetpack Compose starter for **AI Bell**, inspired by the early 2024 ChatGPT conversation layout without using OpenAI/ChatGPT branding or assets.

## Included

- Android app module ready for Android Studio
- Jetpack Compose UI
- Left conversation sidebar
- New-chat action
- Chat composer with send button
- Welcome screen
- Settings screen placeholder
- Internet permission for a future AI provider
- No API key hard-coded into the project
- Gradle wrapper configuration for Gradle 9.1.0

## Important

The current build is a **UI + architecture starter**. The chat button intentionally shows a provider-setup message instead of pretending to have a live model connection.

For production AI responses, connect a current API/provider through a small backend or a carefully designed client layer. Never commit a real secret/API key to this repository.

## Build

Open the project folder in Android Studio and let Gradle sync. The project uses:

- Android Gradle Plugin 9.0.1
- Gradle 9.1.0
- Kotlin 2.2.10 (via AGP built-in Kotlin + Compose compiler plugin)
- Jetpack Compose BOM 2026.09.00
- compileSdk 36
- minSdk 24

Then run the `app` configuration on an emulator or Android device.

## Branding

The app is named **AI Bell** and does not copy OpenAI logos, official ChatGPT assets, or claim to be the official ChatGPT app.
