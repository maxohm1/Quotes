package max.ohm.quoteapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val accentColor: AccentColor = AccentColor.PURPLE,
    val fontSize: FontSize = FontSize.MEDIUM,
    val notificationEnabled: Boolean = true,
    val notificationTime: String = "08:00" // 24-hour format
)

@Serializable
enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

@Serializable
enum class AccentColor(val displayName: String) {
    PURPLE("Purple"),
    BLUE("Blue"),
    TEAL("Teal"),
    ORANGE("Orange"),
    PINK("Pink")
}

@Serializable
enum class FontSize(val displayName: String, val scale: Float) {
    SMALL("Small", 0.85f),
    MEDIUM("Medium", 1.0f),
    LARGE("Large", 1.15f),
    EXTRA_LARGE("Extra Large", 1.3f)
}
