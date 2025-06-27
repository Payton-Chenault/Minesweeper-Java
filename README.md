# 🧨 Minesweeper

A cross-platform Minesweeper game built in Java with a retro-style UI using Swing and custom fonts.  
Supports flagging, timed games, difficulty levels, and runs on Linux & Windows.

---

## 🎮 Features

- ⏱ Timer and mines-left counter
- ✅ Flagging support (Right-click to place/remove)
- 🚫 Guaranteed first click is never a mine
- 🎨 Custom fonts and icons
- 🔥 Built-in `.deb`, `.msi`, and AppImage installers
- 🧪 Built and released automatically with GitHub Actions

---

## 📦 Download

Installers are automatically generated on every release!

| Platform | Installer |
|----------|-----------|
| 🐧 Linux (Ubuntu/Debian) | [Download `.deb`](https://github.com/Payton-Chenault/minesweeper/releases/latest) |
| 🐧 Linux (AppImage) | [Download AppImage](https://github.com/Payton-Chenault/minesweeper/releases/latest) |
| 🪟 Windows | [Download `.msi`](https://github.com/Payton-Chenault/minesweeper/releases/latest) |

---

## 🚀 How to Run (Dev)

### Requirements

- Java 21+
- Gradle (or use the included wrapper `./gradlew`)

### Run via Gradle

```bash
./gradlew run
