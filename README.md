# Gemma Chat (Android)

Offline-first chat using **Gemma 4 E2B** in LiteRT-LM format (`gemma-4-E2B-it.litertlm`).

## Model

- **Hugging Face:** [litert-community/gemma-4-E2B-it-litert-lm](https://huggingface.co/litert-community/gemma-4-E2B-it-litert-lm)
- **Runtime:** [LiteRT-LM Kotlin API](https://github.com/google-ai-edge/LiteRT-LM/blob/main/docs/api/kotlin/getting_started.md)
- Onboarding downloads the `.litertlm` file once (~2.5 GB) via a plain HTTP GET to the public resolve URL. No Hugging Face account or in-app token is required.

## Build

Open the project in Android Studio with **JDK 17+**, sync Gradle, and run on a device with **Android 12+** and ideally **8 GB+ RAM**.

For debug:

```bash
./gradlew :app:assembleDebug
```

For release verification:

```bash
./gradlew :app:assembleRelease
```

## Distribution Prep

- Review `RELEASE_CHECKLIST.md` before shipping an APK.
- Fill `keystore.properties.example` into a real `keystore.properties` when you are ready to sign a release build.
- If you plan a public release, replace the placeholder `com.example.gemmachat` app ID with your final package name first.

## Install and Auth

- Users can download the APK on their Android phone, tap it, and install it directly.
- Android may ask them to allow `Install unknown apps` for the browser or file manager they used.
- A proper public build should use a signed release APK rather than a debug APK.
- No sign-in, API key, Hugging Face token, or special auth is required inside the app.
- On first launch, the app asks for consent before downloading the local Gemma model (~2.5 GB).
- Internet is only needed for the initial model download and any external links or app handoff actions the user chooses to open.
- Microphone permission is only requested if the user uses voice input.

## Privacy

Inference is on-device. Network is used only for the initial model download and for any external links a user chooses to open. Chat history is saved as `chat_store.json` in app storage. Users can clear chats or delete the model from **Settings**.

## Credits

- Built by [1littlecoder](https://x.com/1littlecoder)
- Gemma 4 by [Google DeepMind](https://deepmind.google/models/gemma/gemma-4/)
- LiteRT model packaging by [LiteRT Community on Hugging Face](https://huggingface.co/litert-community/gemma-4-E2B-it-litert-lm)
