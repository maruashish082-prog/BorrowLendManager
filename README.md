# 💰 Borrow & Lend Manager — Android App

A professional Android app to track your borrow and lend transactions.

---

## 📱 Features

- **Add** borrow/lend transactions with name, amount, date & remarks
- **Edit** any transaction
- **Delete** with confirmation dialog
- **Filter** by All / Borrowed / Lent using tabs
- **Summary cards** — total borrowed, total lent, net balance
- **Total footer** at the bottom of the list
- **Persistent SQLite** database — data survives app restarts
- Material Design 3 dark theme UI

---

## 🛠️ Tech Stack

| Layer       | Technology                        |
|-------------|-----------------------------------|
| Language    | Kotlin                            |
| Database    | SQLite (native, no ORM)           |
| UI          | Material Components 3 + ViewBinding |
| Min SDK     | Android 7.0 (API 24)             |
| Target SDK  | Android 14 (API 34)              |

---

## 🚀 How to Open in Android Studio

1. **Extract** this zip folder
2. Open **Android Studio** (Hedgehog or newer)
3. Click **"Open"** → select the `BorrowLendApp` folder
4. Wait for Gradle sync to complete
5. Click **Run ▶** (or `Shift+F10`)

> No extra setup needed. SQLite is built into Android — no internet required.

---

## 📁 Project Structure

```
BorrowLendApp/
├── app/src/main/
│   ├── kotlin/com/borrowlend/manager/
│   │   ├── Transaction.kt          ← Data model
│   │   ├── DatabaseHelper.kt       ← SQLite CRUD
│   │   ├── TransactionAdapter.kt   ← RecyclerView adapter
│   │   ├── MainActivity.kt         ← List screen
│   │   └── AddEditActivity.kt      ← Add / Edit form
│   └── res/
│       ├── layout/
│       │   ├── activity_main.xml
│       │   ├── activity_add_edit.xml
│       │   └── item_transaction.xml
│       ├── values/
│       │   ├── colors.xml
│       │   ├── strings.xml
│       │   └── themes.xml
│       └── drawable/               ← Icons & shapes
└── build.gradle
```

---

## 🎨 UI Screens

### List Screen (MainActivity)
- Header with summary cards (Borrowed / Lent / Net)
- Tab filter: All | Borrowed | Lent
- RecyclerView with card items (name, amount, date, badge, remarks)
- Edit ✏️ and Delete 🗑️ buttons on each card
- Footer showing total for current filter
- FAB (+) to add new transaction

### Form Screen (AddEditActivity)
- Toggle: "I Borrowed" / "I Lent"
- Fields: Name, Amount (₹), Date (DatePicker), Remarks
- Save/Update button
- Full form validation

---

## 📦 APK Size

- Debug: ~4–5 MB  
- Release (minified): ~2–3 MB

---

*Built with ❤️ using Kotlin + Material Design 3*
