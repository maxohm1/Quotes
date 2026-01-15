# QuoteVault 📱✨

A full-featured quote discovery and collection app built with Kotlin and Jetpack Compose.

![QuoteVault Banner](https://via.placeholder.com/800x400?text=QuoteVault)

## 🌟 Features

### Authentication & User Accounts
- ✅ Sign up with email/password
- ✅ Login/logout functionality
- ✅ Password reset flow
- ✅ User profile screen (name, avatar)
- ✅ Session persistence (stay logged in)

### Quote Browsing & Discovery
- ✅ Home feed displaying quotes (paginated)
- ✅ Browse quotes by category (5 categories: Motivation, Love, Success, Wisdom, Humor)
- ✅ Search quotes by keyword
- ✅ Search/filter by author
- ✅ Pull-to-refresh functionality
- ✅ Loading states and empty states handled gracefully

### Favorites & Collections
- ✅ Save quotes to favorites (heart/bookmark)
- ✅ View all favorited quotes in a dedicated screen
- ✅ Create custom collections (e.g., "Morning Motivation", "Work Quotes")
- ✅ Add/remove quotes from collections
- ✅ Cloud sync — favorites persist across devices when logged in

### Daily Quote & Notifications
- ✅ "Quote of the Day" prominently displayed on home screen
- ✅ Quote of the day changes daily
- ✅ Local push notification for daily quote
- ✅ User can set preferred notification time in settings

### Sharing & Export
- ✅ Share quote as text via system share sheet
- ✅ Generate shareable quote card (quote + author on styled background)
- ✅ Save quote card as image to device
- ✅ 6 different card styles/templates to choose from

### Personalization & Settings
- ✅ Dark mode / Light mode toggle
- ✅ 5 accent color themes (Purple, Blue, Teal, Orange, Pink)
- ✅ Font size adjustment for quotes (Small, Medium, Large, Extra Large)
- ✅ Settings persist locally

### Widget
- ✅ Home screen widget displaying current quote of the day
- ✅ Widget updates daily
- ✅ Tapping widget opens the app

## 🛠️ Tech Stack

| Category | Technology |
|----------|------------|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose |
| **Architecture** | MVVM + Clean Architecture |
| **Dependency Injection** | Hilt |
| **Local Database** | Room |
| **Remote Backend** | Supabase (Auth + Database) |
| **Networking** | Ktor Client |
| **State Management** | Kotlin Flow + StateFlow |
| **Navigation** | Navigation Compose (Type-Safe) |
| **Preferences** | DataStore |
| **Image Loading** | Coil |
| **Background Tasks** | WorkManager |
| **Widget** | Glance |
| **Serialization** | Kotlinx Serialization |

## 📋 Project Structure

```
max.ohm.quoteapp/
├── data/
│   ├── local/
│   │   ├── dao/          # Room DAOs
│   │   ├── entity/       # Room Entities
│   │   └── QuoteDatabase.kt
│   ├── remote/
│   │   └── dto/          # Data Transfer Objects
│   └── repository/       # Repository Implementations
├── di/
│   └── AppModule.kt      # Hilt DI Module
├── domain/
│   ├── model/            # Domain Models
│   └── repository/       # Repository Interfaces
├── presentation/
│   ├── auth/             # Login, SignUp, ForgotPassword
│   ├── home/             # Home Screen
│   ├── favorites/        # Favorites Screen
│   ├── collections/      # Collections Screen
│   ├── profile/          # Profile Screen
│   ├── settings/         # Settings Screen
│   ├── share/            # Share Quote Screen
│   ├── components/       # Reusable UI Components
│   └── navigation/       # Navigation Graph
├── ui/theme/             # Theme, Colors, Typography
├── util/                 # Utilities & Constants
├── widget/               # Home Screen Widget
├── worker/               # Background Workers
├── MainActivity.kt
└── QuoteVaultApp.kt
```

## 🚀 Setup Instructions

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 35

### Supabase Configuration

1. Create a new project at [supabase.com](https://supabase.com)

2. Create the following tables in your Supabase database:

```sql
-- Quotes table
CREATE TABLE quotes (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    text TEXT NOT NULL,
    author TEXT NOT NULL,
    category TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Favorites table
CREATE TABLE favorites (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    quote_id UUID REFERENCES quotes(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE(user_id, quote_id)
);

-- Collections table
CREATE TABLE collections (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT DEFAULT '',
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Collection quotes junction table
CREATE TABLE collection_quotes (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    collection_id UUID REFERENCES collections(id) ON DELETE CASCADE,
    quote_id UUID REFERENCES quotes(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE(collection_id, quote_id)
);

-- User profiles table
CREATE TABLE user_profiles (
    id UUID REFERENCES auth.users(id) ON DELETE CASCADE PRIMARY KEY,
    email TEXT NOT NULL,
    display_name TEXT NOT NULL,
    avatar_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

3. Enable Row Level Security (RLS) for all tables

4. Add your Supabase credentials to `local.properties`:

```properties
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-anon-key
```

### Seed Data
Run the included SQL script to populate your database with 100+ quotes:
```bash
psql -h your-project.supabase.co -U postgres -d postgres -f seed_quotes.sql
```

### Build & Run

```bash
# Clone the repository
git clone https://github.com/maxohm1/QuoteApp.git

# Open in Android Studio
# Sync Gradle files
# Run on emulator or device
```

## 🤖 AI Coding Approach & Workflow

This project was built with extensive use of AI tools to accelerate development and produce cleaner code.

### AI Tools Used
- **Claude Code** (Primary) - Architecture design, code generation, debugging
- **GitHub Copilot** - Code completion and suggestions

### Workflow Highlights
1. **Architecture Planning** - Used Claude to design the MVVM + Clean Architecture structure
2. **Code Generation** - Generated boilerplate code for repositories, ViewModels, and Composables
3. **UI Design** - Created polished UI components with AI assistance
4. **Debugging** - Fixed issues efficiently with AI-powered debugging
5. **Documentation** - Generated comprehensive README with AI

### Effective Prompts Used
- "Create a complete authentication flow with Supabase using Hilt for DI"
- "Design a beautiful quote card component with gradient backgrounds and animations"
- "Implement a type-safe navigation system with Jetpack Compose"

## 🎨 Design

The app features a modern, polished design with:
- Vibrant gradient backgrounds
- Smooth animations and transitions
- Custom accent color themes
- Responsive layouts
- Glassmorphism elements

## 📱 Screenshots

| Home | Favorites | Settings |
|------|-----------|----------|
| ![Home](screenshots/home.png) | ![Favorites](screenshots/favorites.png) | ![Settings](screenshots/settings.png) |

## ⚠️ Known Limitations

- Widget requires manual refresh after quote update
- Time picker for notifications uses 24-hour format only
- Avatar upload not yet implemented

## 📄 License

This project is for assessment purposes only.

---

Made with ❤️ using AI-powered development
