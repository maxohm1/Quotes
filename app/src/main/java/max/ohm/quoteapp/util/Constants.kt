package max.ohm.quoteapp.util

object Constants {
    // Supabase Tables
    const val TABLE_QUOTES = "quotes"
    const val TABLE_FAVORITES = "favorites"
    const val TABLE_COLLECTIONS = "collections"
    const val TABLE_COLLECTION_QUOTES = "collection_quotes"
    const val TABLE_USER_PROFILES = "user_profiles"
    const val TABLE_USER_SETTINGS = "user_settings"
    
    // Storage Buckets
    const val BUCKET_AVATARS = "avatars"

    // DataStore Keys
    const val DATASTORE_SETTINGS = "settings"
    const val KEY_THEME_MODE = "theme_mode"
    const val KEY_ACCENT_COLOR = "accent_color"
    const val KEY_FONT_SIZE = "font_size"
    const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
    const val KEY_NOTIFICATION_TIME = "notification_time"
    const val KEY_DAILY_QUOTE_ID = "daily_quote_id"
    const val KEY_DAILY_QUOTE_DATE = "daily_quote_date"

    // Notification
    const val NOTIFICATION_CHANNEL_ID = "daily_quote_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Daily Quote"
    const val NOTIFICATION_ID = 1001
    const val DAILY_QUOTE_WORK_NAME = "daily_quote_work"

    // Widget
    const val WIDGET_PREFS = "widget_prefs"
    const val WIDGET_QUOTE_ID = "widget_quote_id"

    // Share Card Styles
    const val CARD_STYLE_GRADIENT = "gradient"
    const val CARD_STYLE_MINIMAL = "minimal"
    const val CARD_STYLE_ELEGANT = "elegant"

    // Pagination
    const val PAGE_SIZE = 20
}
