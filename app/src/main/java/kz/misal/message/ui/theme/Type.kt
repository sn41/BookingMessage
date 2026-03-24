package kz.misal.message.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import kz.misal.message.R

// 1. Выносим настройку "без лишнего воздуха" в отдельную константу
// Кстати, начиная с Compose 1.2.0, значение includeFontPadding отключено (false) по умолчанию под капотом библиотеки.
val appPlatformStyle = PlatformTextStyle(
    includeFontPadding = false
)


// Используем системный гротеск (Roboto/Inter)
//val AppFontFamily = FontFamily.Default
val AppFontFamily = FontFamily(Font(R.font.inter))

val defaultTypography = Typography()

val AppTypography = Typography(
    // --- Стили, где мы меняем ТОЛЬКО шрифт и отступ ---
    displayLarge = defaultTypography.displayLarge.copy(
        fontFamily = AppFontFamily,
        platformStyle = appPlatformStyle
    ),
    displayMedium = defaultTypography.displayMedium.copy(
        fontFamily = AppFontFamily,
        platformStyle = appPlatformStyle
    ),
    displaySmall = defaultTypography.displaySmall.copy(
        fontFamily = AppFontFamily,
        platformStyle = appPlatformStyle
    ),

    // ... проделайте то же самое для headline и label ...
    // Иначе для них будет применён стандартный шрифт!

// --- Стили, где мы ДЕТАЛЬНО настраиваем еще и размеры ---
    titleLarge = defaultTypography.titleLarge.copy(
        fontFamily = AppFontFamily,
        platformStyle = appPlatformStyle, // <-- Не забываем добавить и сюда!
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    bodyLarge = defaultTypography.bodyLarge.copy(
        fontFamily = AppFontFamily,
        platformStyle = appPlatformStyle, // <-- И сюда!
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodySmall = defaultTypography.bodyLarge.copy(
        fontFamily = AppFontFamily,
        platformStyle = appPlatformStyle, // <-- И сюда!
        fontSize = 14.sp,
        lineHeight = 23.sp
    )
)


/**

Экран-витрина (или "песочница") — это один из лучших стандартов промышленной разработки.
Дизайнеры называют это
Living Documentation (Живая документация),
UI Catalog (Каталог интерфейса) или
Living UI Kit (Живой UI-кит).

1_ Проверка includeFontPadding:
Посмотрите внимательно на крупные стили (Display Large).
Если вы видите, что над заглавными буквами "N" и "H" есть огромная пустая дыра
до разделительной линии — значит, includeFontPadding = false не сработал.
Если буквы стоят плотно и аккуратно — всё настроено идеально.

2_ Общение с дизайнером:
В реальных командах разработчики делают скриншот такого экрана и отправляют дизайнеру со словами:
"Вот так твоя типографика выглядит на реальном движке Android. Утверждаем?".
*/

// 1. Создаем вспомогательный компонент для отрисовки одного стиля
@Composable
private fun TypographySampleRow(styleName: String, textStyle: TextStyle) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Мелкая подпись с названием стиля (например, "Body Large")
        Text(
            text = styleName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Сам текст, демонстрирующий шрифт
        Text(
            text = "NH Barcelona Eixample 123", // Используем текст из вашего проекта
            style = textStyle,
            color = MaterialTheme.colorScheme.onSurface
        )
        // Разделительная линия для красоты
        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

// 2. Сам Preview-экран
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, showSystemUi = true)
@Composable
fun TypographySystemPreview() {
    // Обязательно оборачиваем в нашу кастомную тему, иначе подтянется Roboto!
    MessageTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Text(
                        text = "Типографика проекта",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // --- Display (Самые крупные) ---
                item { TypographySampleRow("Display Large", MaterialTheme.typography.displayLarge) }
                item { TypographySampleRow("Display Medium", MaterialTheme.typography.displayMedium) }
                item { TypographySampleRow("Display Small", MaterialTheme.typography.displaySmall) }

                // --- Headline (Заголовки экранов) ---
                item { TypographySampleRow("Headline Large", MaterialTheme.typography.headlineLarge) }
                item { TypographySampleRow("Headline Medium", MaterialTheme.typography.headlineMedium) }
                item { TypographySampleRow("Headline Small", MaterialTheme.typography.headlineSmall) }

                // --- Title (Заголовки карточек) ---
                item { TypographySampleRow("Title Large", MaterialTheme.typography.titleLarge) }
                item { TypographySampleRow("Title Medium", MaterialTheme.typography.titleMedium) }
                item { TypographySampleRow("Title Small", MaterialTheme.typography.titleSmall) }

                // --- Body (Основной текст) ---
                item { TypographySampleRow("Body Large", MaterialTheme.typography.bodyLarge) }
                item { TypographySampleRow("Body Medium", MaterialTheme.typography.bodyMedium) }
                item { TypographySampleRow("Body Small", MaterialTheme.typography.bodySmall) }

                // --- Label (Кнопки и бейджи) ---
                item { TypographySampleRow("Label Large", MaterialTheme.typography.labelLarge) }
                item { TypographySampleRow("Label Medium", MaterialTheme.typography.labelMedium) }
                item { TypographySampleRow("Label Small", MaterialTheme.typography.labelSmall) }
            }
        }
    }
}