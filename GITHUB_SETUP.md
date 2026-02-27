# 🚀 GitHub Upload + Auto APK Build Guide

## Overview
Every time you push code → GitHub Actions automatically builds Debug & Release APKs.
When you create a version tag → a GitHub Release is created with the APK attached.

---

## STEP 1 — Install Git (if not installed)

Download from https://git-scm.com/downloads and install.

---

## STEP 2 — Create GitHub Repository

1. Go to https://github.com → click **"New"**
2. Repository name: `BorrowLendManager`
3. Set to **Public** or **Private**
4. ❌ Do NOT check "Add README" (we already have one)
5. Click **"Create repository"**

---

## STEP 3 — Upload Project to GitHub

Open terminal / command prompt in your `BorrowLendApp` folder:

```bash
# Initialize git
git init

# Add all files
git add .

# First commit
git commit -m "Initial commit: Borrow & Lend Manager"

# Add your GitHub repo as remote (replace YOUR_USERNAME)
git remote add origin https://github.com/YOUR_USERNAME/BorrowLendManager.git

# Push to GitHub
git branch -M main
git push -u origin main
```

✅ After this, GitHub Actions will **automatically trigger** and build your APK!

---

## STEP 4 — Watch the Build

1. Go to your repo on GitHub
2. Click the **"Actions"** tab
3. You'll see **"Build APK"** workflow running ⏳
4. After ~3–5 minutes it will show ✅ green checkmark
5. Click the workflow run → scroll down → **"Artifacts"**
6. Download **BorrowLend-Debug-APK** or **BorrowLend-Release-APK**

---

## STEP 5 — Add Secrets for Signed Release APK (Optional)

A signed APK is required for Play Store. To sign via Actions:

### 5a. Generate a Keystore (one time only)

Run in terminal:
```bash
keytool -genkeypair \
  -alias borrowlend \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -keystore borrowlend.jks
```
Enter a password when prompted. **Save this file and password safely!**

### 5b. Convert keystore to Base64

**Mac/Linux:**
```bash
base64 -i borrowlend.jks | pbcopy
```

**Windows (PowerShell):**
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("borrowlend.jks")) | clip
```

### 5c. Add Secrets to GitHub

Go to your repo → **Settings** → **Secrets and variables** → **Actions** → **New repository secret**

Add these 4 secrets:

| Secret Name        | Value                              |
|--------------------|------------------------------------|
| `KEYSTORE_BASE64`  | (paste the Base64 string)          |
| `KEYSTORE_PASSWORD`| your keystore password             |
| `KEY_ALIAS`        | `borrowlend`                       |
| `KEY_PASSWORD`     | your key password (same or different) |

✅ Now every build will produce a **signed** Release APK!

---

## STEP 6 — Create a Release with APK attached

When your app is ready for a new version:

```bash
# Bump versionCode and versionName in app/build.gradle first, then:

git add .
git commit -m "Release v1.1"

# Create a version tag
git tag v1.1

# Push tag to GitHub
git push origin v1.1
```

GitHub Actions will:
1. Build the APK
2. Sign it
3. Automatically create a **GitHub Release** at `Releases` tab
4. Attach the APK as a downloadable file 🎉

---

## STEP 7 — Future Updates

Whenever you make changes:

```bash
git add .
git commit -m "Fix: description of what you changed"
git push
```

GitHub Actions builds a new APK automatically every push!

---

## 📁 What's in `.github/workflows/build.yml`

```
push to main/master
    │
    ├── ✅ Checkout code
    ├── ☕ Setup JDK 17
    ├── 📦 Cache Gradle (speeds up builds)
    ├── 🔨 Build Debug APK
    ├── 🔨 Build Release APK
    ├── 🔑 Sign Release APK (if secrets set)
    ├── 📤 Upload Debug APK  → Artifacts
    └── 📤 Upload Release APK → Artifacts

push tag v*.*
    │
    └── 🚀 Create GitHub Release + attach APKs
```

---

## ⚠️ Important Rules

- **NEVER commit** `.jks` or `.keystore` files to GitHub
- **NEVER commit** passwords — always use GitHub Secrets
- The `.gitignore` file already blocks keystore files from being committed

---

## 🔗 Quick Links

- GitHub Actions docs: https://docs.github.com/en/actions
- Android signing docs: https://developer.android.com/studio/publish/app-signing
