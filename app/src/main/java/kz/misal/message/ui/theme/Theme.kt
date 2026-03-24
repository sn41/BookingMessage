package kz.misal.message.ui.theme

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp


private val DarkColorScheme = darkColorScheme(
    // Синий цвет бренда. Значок Genius, кнопка «Find out more». В тёмной теме он светлее.
    primary = BrandBlueActionLight,
    // Тёмный текст на светлом синем акценте.
    onPrimary = Charcoal,
    // Тёмно-синий контейнер (для кнопок с меньшим акцентом).
    primaryContainer = BrandBlueDarkContainer,
    // Светло-синий текст на тёмно-синем контейнере.
    onPrimaryContainer = OnBrandBlueDarkContainer,

    // Звезды рейтинга. Желтый обычно хорошо смотрится и на светлом, и на тёмном, оставляем его.
    secondary = StarYellow,
    // Тёмный текст на желтых элементах.
    onSecondary = Charcoal,
    // Тёмно-желтый контейнер
    secondaryContainer = YellowDarkContainer,
    // Светло-желтый текст на тёмном контейнере.
    onSecondaryContainer = OnYellowDarkContainer,

    // Светло-зеленый цвет тега («Limited-time Deal»).
    tertiary = DealGreenLight,
    // Тёмный текст на зеленом теге.
    onTertiary = Charcoal,

    // Реальный тёмный цвет фона всего приложения.
    background = DarkBaseBackground,
    // Светлый текст на тёмном фоне приложения («748 properties»).
    onBackground = LightTextPrimary,

    // Фон карточек отелей. Чуть светлее фона (elevation).
    surface = DarkCardSurface,
    // Светлый текст на карточках отелей (Заголовки отелей).
    onSurface = LightTextPrimary,

    // Альтернативная поверхность (фон предупреждения вверху).
    surfaceVariant = DarkVariantSurface,
    // Менее приоритетный светлый текст на карточке: адрес («Elxample»), «Includes taxes...».
    onSurfaceVariant = DarkTextSecondary,

    // Светло-красный цвет. Зачеркнутая цена, «Only 1 left».
    error = AlertRedDark,
    // Тёмный текст на error элементах.
    onError = Charcoal
)


private val LightColorScheme = lightColorScheme(
    // Основные кнопки.
    primary = BrandBlueAction,
    // Текст и иконки на фоне бренда.
    onPrimary = White,
    // Темно-синяя верхняя панель
    primaryContainer = BrandBlueLightContainer,
    // Белые иконки на темно-синей панели
    onPrimaryContainer = White,

    // Жёлтый акцент - рамочка, тело звёзд
    secondary = StarYellow,
    // Текст на желтом
    onSecondary = White,
    // Светло-желтый контейнер, рамочка звезды, рамочка текста
    secondaryContainer = YellowLightContainer,
    // Текст в светло-желтом контейнере
    onSecondaryContainer = OnYellowLightContainer,

    // Зеленый для позитивных статусов
    tertiary = DealGreenDark,
    // Белый цвет на зеленом теге.
    onTertiary = White,

    // Карточки отелей
    background = MercuryGray,
    // Текст на фоне
    onBackground = Charcoal,
    //Фон карточек отелей. В M3 фон карточек часто светлее фона приложения для создания эффекта возвышения.

    surface = LightCardSurface,
    //
    onSurface = Black,
    // Серые обводки неактивных кнопок
    surfaceVariant = LightVariantSurface,
    // Менее приоритетный текст на карточке: адрес («Elxample»), «Includes taxes...», «Booking.com» внизу.
    onSurfaceVariant = LightTextSecondary,
    // Красный цвет. Зачеркнутая цена, «Only 1 left», предупреждающий текст «Booking.com» внизу.

    error = AlertRedLight,
    // Текст на error элементах (не используется).
    onError = White
)

val LocalMessageIconSize = compositionLocalOf { 20.dp }

@Composable
fun MessageTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Интегрируем дополнительные цвета в MaterialTheme
    val customColors = if (darkTheme) darkCustomColors else lightCustomColors

    val borders = remember(colorScheme) {
        AppBorders(
            thin = BorderStroke(1.dp, colorScheme.outlineVariant),
            active = BorderStroke(2.dp, colorScheme.primary),
            error = BorderStroke(2.dp, colorScheme.error)
        )
    }

    CompositionLocalProvider(
        LocalAppBorders provides borders,
        LocalAppSpacing provides AppSpacing(),
        LocalCustomColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content,
            shapes = AppShapes // <-- Подключаем формы
        )
    }
}