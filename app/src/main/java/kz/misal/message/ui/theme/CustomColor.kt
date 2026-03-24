package kz.misal.message.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Специфичные элементы UI
// Определите логические группы для ваших нестандартных цветов.

// Специфичные элементы UI
// Фоны и поверхности
// Тексты
// Семантические статусы

data class CustomColors(
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val onWarning: Color,
    val premiumBadge: Color,
    val brand: Color, // Цвета бренда
    val accent: Color,
    val iconGray: Color, // Серые иконки в полях ввода (кровать, календарь, человечек)
    val mapButtonBackground: Color,
    val textPrimary: Color,
    val textSecondary: Color,
)

// Определяем глобальный ключ для этих цветов
val LocalCustomColors = staticCompositionLocalOf {
    CustomColors(
        success = Color.Unspecified,
        onSuccess = Color.Unspecified,
        warning = Color.Unspecified,
        onWarning = Color.Unspecified,
        premiumBadge = Color.Unspecified,
        brand = Color.Unspecified,
        accent = Color.Unspecified,
        iconGray = Color.Unspecified,
        mapButtonBackground = Color.Unspecified,
        textPrimary = Color.Unspecified,
        textSecondary = Color.Unspecified,
    )
}

// Создаём extension-свойство, чтобы обращаться к новым цветам так же, как к стандартным.
// Пример использования в верстке:
//Text(
//  text = "Операция успешна",
//  color = MaterialTheme.customColors.onSuccess,
//  modifier = Modifier.background(MaterialTheme.customColors.success)
//)

val MaterialTheme.customColors: CustomColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomColors.current


// Задаем палитры для светлой и темной тем
val lightCustomColors = CustomColors(
    success = DealGreenDark,
    onSuccess = White,
    warning = Color(0xFFFF9800),
    onWarning = Black,
    premiumBadge = Color(0xFF9C27B0),
    brand = Brand,
    accent = AlertRedLight,
    iconGray = Color(0xFF757575),
    mapButtonBackground = Color(0xFF242424),
    textPrimary = Color(0xFF1A1A1A),
    textSecondary = Color(0xFF595959),
)

val darkCustomColors = CustomColors(
    success = DealGreenLight,
    onSuccess = Black,
    warning = Color(0xFFFFB74D),
    onWarning = Black,
    premiumBadge = Color(0xFFCE93D8),
    brand = Brand, // Оставляем фирменный цвет неизменным
    accent = AlertRedDark,
    iconGray = Color(0xFF757575),
    mapButtonBackground = Color(0xFF242424),
    textPrimary = Color(0xFF1A1A1A),
    textSecondary = Color(0xFF595959),
)