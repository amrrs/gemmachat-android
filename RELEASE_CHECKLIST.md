# Release Checklist

## Already prepared in this repo

- Clear onboarding consent before downloading the ~2.5 GB model
- About section with creator credit, LiteRT Community credit, privacy, runtime details, and external links
- `allowBackup="false"` in the manifest
- Debug and release Gradle build paths verified

## Required before distributing an APK

1. Create a release keystore.
2. Copy `keystore.properties.example` to `keystore.properties` and fill in the real values.
3. Bump `versionCode` and `versionName` in `app/build.gradle.kts`.
4. Replace the placeholder app ID `com.example.gemmachat` with your final package name if this is going public.
5. Run `./gradlew :app:assembleRelease`.
6. Sign the release APK with your release key if your build output is unsigned.
7. Test on a real device:
   - fresh install onboarding
   - large model download flow
   - low-RAM warning visibility
   - image attachment and camera attach
   - microphone permission and recording
   - external app handoff confirmation
   - clear chats / delete model from settings
8. Verify app icon, app name, About links, and creator credit in the final build.

## Recommended before public release

- Add a privacy policy URL if distributing through an app store
- Capture screenshots for the store listing
- Prepare short release notes
- Keep a backup of the release keystore outside the project folder
