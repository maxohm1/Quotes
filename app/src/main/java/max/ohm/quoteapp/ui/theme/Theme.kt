package max.ohm.quoteapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import max.ohm.quoteapp.domain.model.AccentColor
import max.ohm.quoteapp.domain.model.ThemeMode

// Purple Color Scheme (Default)
private val PurpleDarkColorScheme = darkColorScheme(
    primary = PurplePrimary,
    onPrimary = White,
    primaryContainer = PurpleDark,
    onPrimaryContainer = PurpleLight,
    secondary = PurpleSecondary,
    onSecondary = White,
    secondaryContainer = PurpleDark,
    onSecondaryContainer = PurpleLight,
    tertiary = PurpleTertiary,
    onTertiary = White,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    error = ErrorRed,
    onError = White
)

private val PurpleLightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = White,
    primaryContainer = PurpleLight,
    onPrimaryContainer = PurpleDark,
    secondary = PurpleSecondary,
    onSecondary = White,
    secondaryContainer = PurpleLight,
    onSecondaryContainer = PurpleDark,
    tertiary = PurpleTertiary,
    onTertiary = White,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    error = ErrorRed,
    onError = White
)

// Blue Color Scheme
private val BlueDarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    onPrimary = White,
    primaryContainer = BlueDark,
    onPrimaryContainer = BlueLight,
    secondary = BlueSecondary,
    onSecondary = White,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    error = ErrorRed,
    onError = White
)

private val BlueLightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = White,
    primaryContainer = BlueLight,
    onPrimaryContainer = BlueDark,
    secondary = BlueSecondary,
    onSecondary = White,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    error = ErrorRed,
    onError = White
)

// Teal Color Scheme
private val TealDarkColorScheme = darkColorScheme(
    primary = TealPrimary,
    onPrimary = White,
    primaryContainer = TealDark,
    onPrimaryContainer = TealLight,
    secondary = TealSecondary,
    onSecondary = White,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    error = ErrorRed,
    onError = White
)

private val TealLightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = White,
    primaryContainer = TealLight,
    onPrimaryContainer = TealDark,
    secondary = TealSecondary,
    onSecondary = White,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    error = ErrorRed,
    onError = White
)

// Orange Color Scheme
private val OrangeDarkColorScheme = darkColorScheme(
    primary = OrangePrimary,
    onPrimary = White,
    primaryContainer = OrangeDark,
    onPrimaryContainer = OrangeLight,
    secondary = OrangeSecondary,
    onSecondary = White,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    error = ErrorRed,
    onError = White
)

private val OrangeLightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = White,
    primaryContainer = OrangeLight,
    onPrimaryContainer = OrangeDark,
    secondary = OrangeSecondary,
    onSecondary = White,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    error = ErrorRed,
    onError = White
)

// Pink Color Scheme
private val PinkDarkColorScheme = darkColorScheme(
    primary = PinkPrimary,
    onPrimary = White,
    primaryContainer = PinkDark,
    onPrimaryContainer = PinkLight,
    secondary = PinkSecondary,
    onSecondary = White,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    error = ErrorRed,
    onError = White
)

private val PinkLightColorScheme = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = White,
    primaryContainer = PinkLight,
    onPrimaryContainer = PinkDark,
    secondary = PinkSecondary,
    onSecondary = White,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    error = ErrorRed,
    onError = White
)

@Composable
fun QuoteAppTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    accentColor: AccentColor = AccentColor.PURPLE,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when (accentColor) {
        AccentColor.PURPLE -> if (darkTheme) PurpleDarkColorScheme else PurpleLightColorScheme
        AccentColor.BLUE -> if (darkTheme) BlueDarkColorScheme else BlueLightColorScheme
        AccentColor.TEAL -> if (darkTheme) TealDarkColorScheme else TealLightColorScheme
        AccentColor.ORANGE -> if (darkTheme) OrangeDarkColorScheme else OrangeLightColorScheme
        AccentColor.PINK -> if (darkTheme) PinkDarkColorScheme else PinkLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}