package kz.misal.message.compose_util

import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import kz.misal.message.ui.theme.MessageTheme

// Финальный вариант buildInteractiveLinkText
/*
В Jetpack Compose 1.7+ появилась потрясающая возможность
гибко настраивать состояния ссылок внутри текста.
Вы можете задать разный внешний вид для ссылки в состоянии покоя,
при наведении курсора мыши (Hover) и при нажатии пальцем (Pressed).

Появление подчеркивания при наведении реализуется с помощью класса TextLinkStyles и его параметра hoveredStyle.

Важный нюанс для Android:
    Состояние "Hover" (наведение) работает только если к устройству подключена мышь, стилус,
    или если это приложение запускается на Desktop/Web.

    Для мобильных телефонов с сенсорным экраном главным интерактивным состоянием
    является pressedStyle (удержание пальцем).

Поэтому мы настроим их оба.
 */
@Composable
fun buildInteractiveLinkText(
    text: String,
    linkText: String,
    url: String,
    isUnderlinedByDefault: Boolean = false // Управление из SDUI - с подчёркиваним или без
): AnnotatedString {

    // 1. Базовый стиль (Состояние покоя)
    val defaultStyle = SpanStyle(
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        // Выбираем: подчеркивать или нет
        textDecoration = if (isUnderlinedByDefault) TextDecoration.Underline else TextDecoration.None
    )

    // 2. Стиль при наведении (Hover - для мыши/стилуса)
    val hoveredStyle = SpanStyle(
        // При наведении всегда показываем подчеркивание для обратной связи
        textDecoration = TextDecoration.Underline
    )

    // 3. Стиль при нажатии (Pressed - для сенсорных экранов)
    val pressedStyle = SpanStyle(
        textDecoration = TextDecoration.Underline,
        // Добавляем полупрозрачный фон (Ripple-эффект для текста)
        background = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    )

    // 4. Собираем всё в единый объект стилей ссылки
    val linkStyles = TextLinkStyles(
        style = defaultStyle,
        hoveredStyle = hoveredStyle,
        pressedStyle = pressedStyle
    )

    return buildAnnotatedString {
        append("$text ")

        val linkAnnotation = LinkAnnotation.Url(
            url = url,
            styles = linkStyles
        )

        withLink(linkAnnotation) {
            // Меняем обычные пробелы на неразрывные, чтобы строка ссылки переносилась целиком.
            val nonBreakingLinkText = linkText.replace(" ", "\u00A0")
            append(nonBreakingLinkText)
        }
    }
}

@Preview
@Composable
private fun PreviewText() {
    MessageTheme(){
        val annotatedString = buildInteractiveLinkText(
            text = "Commission paid on bookings and other factors may affect property rankings. Learn about these ranking parameteters and how to select and modify them.",
            linkText = "Find out more",
            url = "https://www.booking.com/ranking_info"
        )

        // Используется обычный Text. Он сам обработает клик и откроет браузер
        Text(
            text = annotatedString,
            modifier = Modifier,
            // Обязательно задаем базовый стиль для обычного текста (иначе он будет черным по умолчанию)
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}