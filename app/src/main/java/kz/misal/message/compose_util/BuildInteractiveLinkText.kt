package kz.misal.message.compose_util

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview

// Строим аннотированную строку
@Composable
fun buildInteractiveLinkText1(
    text: String = "",
    linkText: String,
    url: String
) = buildAnnotatedString {
    // Основной текст
    append("${text.trim()} ")

    // Ссылка
    val link = LinkAnnotation.Url(url)

    // Добавляем ссылку с текстом.
    // Меняем обычные пробелы на неразрывные, чтобы строка ссылки переносилась целиком.
    val nonBreakingLinkText = linkText.replace(" ", "\u00A0")

    withLink(link) {
        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)) {
            append(nonBreakingLinkText)
        }
    }

    /*
    Если вы передаете эту строку вручную из ресурсов или прямо в коде,
    вы можете сразу написать её с неразрывными пробелами, без использования функции .replace():
        // Вариант в коде:
        val myLink = "Find\u00A0out\u00A0more"

        // Вариант в strings.xml:
        // <string name="find_out_more">Find&#160;out&#160;more</string>
     */
}


@Preview
@Composable
private fun PreviewText() {
    val annotatedString = buildInteractiveLinkText1(
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

//