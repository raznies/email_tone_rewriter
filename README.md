# 📧 Email Tone Rewriter - On-Device AI Assistant

Transform your email drafts with AI-powered tone adjustment. All processing happens on-device for complete privacy.

**Built with RunAnywhere SDK** | **Powered by Qwen 2.5 0.5B Instruct**

---

## 🎯 What This App Does

An intelligent email rewriting assistant that:

An intelligent email rewriting assistant that:

- Analyzes your email drafts
- Rewrites them in your chosen tone (Professional, Friendly, Concise, Formal)
- Runs **100% offline** - no cloud, no APIs, no data collection
- Processes everything on-device using a tiny 374 MB AI model

Perfect for professionals, non-native speakers, and anyone who wants to communicate more effectively.

---

## ✨ Features

### 🎨 **4 Tone Options**

- **Professional** - Polished business language
- **Friendly** - Warm and approachable
- **Concise** - Brief and to-the-point
- **Formal** - Executive-level correspondence

### 🚀 **Smart Input**

- Multi-line email editor with 300-character limit
- Real-time character counter
- 3 pre-loaded example emails for instant demo

### 🔒 **Privacy-First**

- All AI processing happens on your device
- No internet required after model download
- Zero data collection or tracking
- Perfect for sensitive business communications

### 📋 **One-Tap Copy**

- Copy rewritten emails to clipboard instantly
- Toast notification confirms copy action
- Ready to paste into any email client

### 🤖 **On-Device AI**

- Powered by Qwen 2.5 0.5B Instruct model
- Streaming response generation
- Optimized for mobile devices

---

## 📱 Installation

### Download APK

1. Go to [Releases](https://github.com/RunanywhereAI/Hackss/releases)
2. Download `app-debug.apk`
3. On your Android device:
   - Settings → Security → Enable "Install from Unknown Sources"
   - Open the downloaded APK
   - Tap "Install"

### First Launch

1. Open the app
2. Tap "Models" to show model selector
3. Tap "Download" on Qwen 2.5 0.5B model (374 MB)
4. Wait for download to complete
5. Tap "Load Model"
6. Wait 30-90 seconds for model to load
7. Start rewriting emails!

---

## 🎬 How to Use

1. **Select a Tone** - Choose from Professional, Friendly, Concise, or Formal
2. **Enter Email** - Paste or type your email draft (max 300 characters)
   - _Or tap an example email for instant demo_
3. **Tap Rewrite** - Wait 5-20 seconds for AI generation
4. **Copy Result** - Tap the 📋 Copy button
5. **Paste Anywhere** - Use in Gmail, Outlook, WhatsApp, etc.

---

## 🛠️ Technical Details

### Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose + Material3
- **AI Runtime**: RunAnywhere SDK
- **Model**: Qwen 2.5 0.5B Instruct (Q6_K GGUF)
- **Architecture**: MVVM (ViewModel + StateFlow)

### Key Components

| File               | Purpose                                                     |
| ------------------ | ----------------------------------------------------------- |
| `ChatViewModel.kt` | Email rewriting logic, prompt engineering, state management |
| `MainActivity.kt`  | UI components, tone selector, result display                |
| `MyApplication.kt` | SDK initialization and model registration                   |

### Prompt Engineering

Each tone uses a carefully crafted prompt:

**Professional Tone Example:**

```
Rewrite this email in a professional and polished tone.
Keep the core message but use formal business language:

[User's email]

Rewritten email:
```

The model streams tokens in real-time, providing a responsive user experience.

---

## 📊 Performance

| Metric              | Value                      |
| ------------------- | -------------------------- |
| Model Size          | 374 MB                     |
| Model Load Time     | 30-90 seconds (first time) |
| Generation Time     | 5-20 seconds               |
| App Size            | ~15 MB                     |
| Min Android Version | 7.0 (API 24)               |

**Note**: Performance varies by device. Modern devices (2020+) run significantly faster.

---

## 🎯 Use Cases

- **Sales Professionals** - Adjust tone for different clients
- **Customer Support** - Maintain consistent professional communication
- **Non-Native Speakers** - Improve English email fluency
- **Business Owners** - Quick professional responses
- **Anyone** - Overcome writer's block or message anxiety

---

## 🔧 Build from Source

### Prerequisites

- Android Studio (latest version)
- JDK 17+
- Android SDK 24-36

### Steps

```bash
# Clone repository
git clone https://github.com/RunanywhereAI/Hackss.git
cd Hackss

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

---

## 🐛 Troubleshooting

### Model won't download

- Check internet connection
- Ensure 500MB+ free storage
- Try tapping "Refresh" in Models section

### App is slow/crashes

- Close other apps to free RAM
- Model requires ~1.5GB RAM during inference
- Older devices may struggle with generation

### Rewrite quality is poor

- Keep emails under 300 characters
- Be specific in your email content
- Try different tones for better results

### Copy button doesn't work

- Grant clipboard permissions if prompted
- Look for "Copied to clipboard!" toast message

---

## 🚀 Future Improvements

Potential enhancements for v2.0:

- [ ] Multiple tone comparison (side-by-side view)
- [ ] Email templates (Meeting request, Follow-up, Apology)
- [ ] History/favorites system
- [ ] Adjustable character limit
- [ ] More tone options (Urgent, Apologetic, Persuasive)
- [ ] Character personality modes

---

## 🏆 Hackathon Submission

This project was built for [Hackathon Name] demonstrating:

✅ **On-Device AI** - No cloud dependencies
✅ **Privacy-First Design** - Zero data collection  
✅ **Real-World Value** - Solves actual communication problems  
✅ **Clean Architecture** - Modular, maintainable code  
✅ **Modern Stack** - Kotlin, Compose, MVVM

---

## 📄 License

This project uses the RunAnywhere SDK. See individual component licenses for details.

---

## 🙏 Acknowledgments

- **RunAnywhere SDK** - On-device LLM runtime
- **Qwen Team** - Qwen 2.5 0.5B Instruct model
- **Jetpack Compose** - Modern Android UI toolkit

---

## 📞 Contact

For questions or feedback:

- GitHub Issues: [Create an issue](https://github.com/RunanywhereAI/Hackss/issues)
- Repository: [RunanywhereAI/Hackss](https://github.com/RunanywhereAI/Hackss)

---

**Made with ❤️ using On-Device AI**
