
# Server-Driven UI (SDUI) и парсинг HTML в Compose

До недавнего времени парсинг HTML-строк в Android был настоящей головной болью: разработчикам приходилось использовать старые инструменты из `View` (вроде `HtmlCompat`) и вручную переводить их в формат Compose.

Но начиная с **Jetpack Compose 1.6 и 1.7**, Google добавил встроенную поддержку HTML. Теперь превратить ответ сервера в красивый кликабельный текст можно буквально одной строчкой кода!

Это идеальный подход для Server-Driven UI (пользовательского интерфейса, управляемого сервером): бэкенд присылает текст с тегами `<b>` (жирный), `<i>` (курсив) и `<a href>` (ссылки), а мобильное приложение само всё это отрисовывает.

Вот как выглядит современное профессиональное решение.

### Шаг 1: Симуляция ответа от сервера

Представьте, что с сервера (по API) вам пришел вот такой JSON:
```json
{
  "message": "Commission paid on bookings and other factors may affect property rankings. <a href='https://www.booking.com/ranking_info'>Find out more</a>"
}
```

### Шаг 2: Утилита для парсинга и интерактивных стилей ссылок

Чтобы управлять тем, будет ли ссылка с подчеркиванием или без, мы воспользуемся новым классом `TextLinkStyles`.

Однако мы не просто раскрасим ссылку, мы добавим **State Layers (Состояния)**. 

Без визуального отклика при нажатии пользователь не поймет, кликнул он по ссылке или нет. Мы настроим `pressedStyle`, чтобы ссылка реагировала на касание.

```kotlin
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.text.AnnotatedString
//import androidx.compose.ui.text.SpanStyle
//import androidx.compose.ui.text.TextLinkStyles
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.fromHtml
//import androidx.compose.ui.text.style.TextDecoration

/**
 * Превращает HTML-строку с сервера в готовый для Compose AnnotatedString.
 * @param html Текст с сервера (с тегами <b>, <i>, <a href> и т.д.)
 * @param underlineLinks Если true — ссылки будут с подчеркиванием, если false — без.
 */
@Composable
fun parseServerHtmlText(
    html: String,
    underlineLinks: Boolean = true
): AnnotatedString {
    
    // 1. Базовый стиль ссылки (в состоянии покоя)
    val linkStyle = SpanStyle(
        color = MaterialTheme.colorScheme.primary, // Берем акцентный цвет из темы
        fontWeight = FontWeight.SemiBold,          // Делаем чуть жирнее
        textDecoration = if (underlineLinks) TextDecoration.Underline else TextDecoration.None
    )
    
    // 2. Стиль при нажатии (визуальный отклик!)
    // Делаем фон ссылки полупрозрачным, когда пользователь держит на ней палец
    val pressedLinkStyle = linkStyle.copy(
        background = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    )

    // 3. Compose 1.6+: встроенный парсер собирает всё вместе
    return AnnotatedString.fromHtml(
        htmlString = html,
        linkStyles = TextLinkStyles(
            style = linkStyle,
            pressedStyle = pressedLinkStyle // Добавили реакцию на клик
        )
    )
}
```

### Шаг 3: Безопасный перехват кликов (LocalUriHandler)

По умолчанию компонент `Text` (в Compose 1.7+) сам перехватывает клик по тегу `<a>` и открывает системный браузер (Chrome или Safari).

**Внимание: Для коммерческого приложения это архитектурная ошибка!** 
Если вы выбросите пользователя из приложения бронирования в браузер, он может отвлечься и не завершить покупку. 
Мы должны перехватить эту ссылку и открыть ее во внутреннем экране (например, в `WebView` или через `DeepLink`).

Для этого мы используем паттерн `CompositionLocal`, подменяя системный обработчик ссылок `LocalUriHandler` на наш собственный.

```kotlin
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.CompositionLocalProvider
//import androidx.compose.ui.platform.LocalUriHandler
//import androidx.compose.ui.platform.UriHandler
//import androidx.compose.ui.res.painterResource

@Composable
fun SmartHtmlMessage() {
    // 1. Строка "пришла с сервера"
    val serverText = "Commission paid on bookings... <a href='https://my-app.com/policy'>Find out more</a>"
    
    // 2. Парсим текст
    val parsedTextAnnotatedString = parseServerHtmlText(
        html = serverText, 
        underlineLinks = true 
    )

    // 3. Создаем кастомный перехватчик ссылок
    val customUriHandler = object : UriHandler {
        override fun openUri(uri: String) {
            // Магия здесь! Вместо открытия внешнего Chrome мы перехватываем URL.
            println("Пользователь кликнул на ссылку: $uri")
            
            // В реальном проекте здесь вы:
            // - Откроете внутренний экран: navController.navigate("webview?url=$uri")
            // - Отправите событие в аналитику: analytics.track("Policy_Link_Clicked")
        }
    }

    // 4. Подменяем обработчик только для этого компонента Message
    CompositionLocalProvider(LocalUriHandler provides customUriHandler) {
        
        Message(
            leadingIcon = { 
                Icon(
                    painter = painterResource(id = R.drawable.ic_close_union), // иконка закрытия
                    contentDescription = "Close"
                ) 
            },
            onCloseClick = { /* Закрыть сообщение */ }
        ) {
            // Этот Text теперь будет отдавать клики в наш customUriHandler!
            Text(
                text = parsedTextAnnotatedString,
                style = MaterialTheme.typography.bodySmall
            )
        }
        
    }
}
```

### Почему эта архитектура работает так круто?

1. **Единый источник истины:** Маркетолог может зайти в админку, написать `Вы <b>обязательно</b> должны прочитать <a href="...">наши правила</a>`, и в приложении слово "обязательно" станет жирным, а "наши правила" — синей ссылкой. Вам не нужно перекомпилировать приложение и выпускать апдейт в Google Play!
2. **Отказ от костылей:** В Compose 1.7 стандартный компонент `Text` получил встроенную поддержку ссылок. Нам больше не нужен устаревший `ClickableText`, не нужно вручную высчитывать координаты клика по пикселям (`offset`). Код стал чистым и декларативным.
3. **Безопасность стилей и Удержание пользователя:** Все базовые цвета берутся из `MaterialTheme.colorScheme`, поэтому при переключении на темную тему текст автоматически перекрасится в читаемые тона. А благодаря `LocalUriHandler` мы надежно удерживаем клиента внутри нашей бизнес-воронки.

---

***
***
***