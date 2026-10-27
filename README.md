# Personal Expense Tracker — Android App

A midterm Android project built with Kotlin, MVVM, Room Database, and Navigation Component.

## Tech Stack
- **Language:** Kotlin
- **Architecture:** MVVM + Repository
- **Database:** Room (SQLite) — `expense_tracker_database`
- **Navigation:** Navigation Component with Safe Args
- **List display:** RecyclerView with ListAdapter + DiffUtil
- **UI:** XML layouts, Material Design 3, ViewBinding

## Project Structure
```
app/src/main/java/com/example/personalexpensetracker/
├── data/
│   ├── local/
│   │   ├── TransactionEntity.kt   — Room @Entity
│   │   ├── TransactionDao.kt      — Room @Dao
│   │   └── AppDatabase.kt         — Room @Database singleton
│   └── repository/
│       └── TransactionRepository.kt
├── ui/
│   ├── dashboard/
│   │   ├── DashboardFragment.kt
│   │   └── DashboardViewModel.kt
│   ├── addtransaction/
│   │   ├── AddTransactionFragment.kt
│   │   └── AddTransactionViewModel.kt
│   ├── history/
│   │   ├── TransactionHistoryFragment.kt
│   │   ├── TransactionHistoryViewModel.kt
│   │   └── TransactionAdapter.kt
│   └── detail/
│       ├── TransactionDetailFragment.kt
│       └── TransactionDetailViewModel.kt
├── utils/
│   └── Utils.kt                   — DateUtils, CurrencyUtils, Constants
└── MainActivity.kt
```

## Screens
1. **DashboardFragment** — Monthly summary + recent transactions
2. **AddTransactionFragment** — Insert / Edit transactions
3. **TransactionHistoryFragment** — Full list with filters
4. **TransactionDetailFragment** — Full record + Edit/Delete

## How to Open
1. Install [Android Studio](https://developer.android.com/studio)
2. Open this folder (`PersonalExpenseTracker/`) as an existing project
3. Let Gradle sync complete
4. Run on an emulator (API 26+) or a physical device

## GitHub
[YOUR GITHUB URL HERE]
