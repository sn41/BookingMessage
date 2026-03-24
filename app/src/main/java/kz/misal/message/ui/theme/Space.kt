package kz.misal.message.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// 1. Описываем нашу 8-пиксельную сетку
data class AppSpacing(
    val default: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,  // Базовый отступ от краев экрана
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val huge: Dp = 48.dp
)

// 2. Создаем Local-провайдер.
// Так как отступы не меняются от светлой/темной темы, мы сразу передаем готовый объект.
val LocalAppSpacing = staticCompositionLocalOf { AppSpacing() }

// 3. Добавляем расширение к MaterialTheme
val MaterialTheme.spacing: AppSpacing
    @Composable
    @ReadOnlyComposable
    get() = LocalAppSpacing.current