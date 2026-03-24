package kz.misal.message.frame

// Файл: MainViewModel.kt
import androidx.compose.foundation.Image
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Файл: SduiMessageRenderer.kt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import kz.misal.message.R
// Файл: FinalMessage.kt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import kz.misal.message.compose_util.alignCenterToFirstLine
import kz.misal.message.compose_util.customClickable
import kz.misal.message.ui.theme.LocalMessageIconSize
import kz.misal.message.ui.theme.borders
import kz.misal.message.ui.theme.spacing
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.CompositionLocalProvider
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Файл: MainScreen.kt
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
//import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay

/*
 Message6 — это базовый примитив (UI-кирпичик).
 В парадигме SDUI экраны не строятся путем ручного вызова примитивов и ручной передачи туда лямбд.
 Экраны строятся на основе данных, пришедших с сервера. Нам нужен "мост", который переведет JSON-ответ в ваш Message.

 Ниже представлена образцовая организация кода (по паттерну Unidirectional Data Flow), которая связывает серверные данные, ViewModel и ваш UI-компонент.
 */

// Уровень 1: Модели данных (SDUI Contract) ========================================================
// Эти классы описывают то, что присылает сервер. Они должны быть независимы от Compose.

// Файл: SduiModels.kt
// Базовый класс для всех действий в приложении
sealed class SduiAction {
    data class OpenUrl(val url: String) : SduiAction()
    data class DismissMessage(val messageId: String) : SduiAction()
}

// Описание доступных иконок (словарь приложения)
enum class SduiIcon {
    INFO,

    // WARNING,
    LOCATION,
    CLOSE,

    // SUCCESS,
    HEART
}

// Модель самого сообщения
data class SduiMessageModel(
    val id: String,
    val textHtml: String,
    val leadingIcon: SduiIcon? = null,
    val trailingIcon: SduiIcon? = null,
    val trailingAction: SduiAction? = null // Что делать при клике на правую иконку
)

// Уровень 2: Управление состоянием (ViewModel) ===================================================
// ViewModel ничего не знает про цвета и отступы. Ее задача — загрузить данные, сохранить их в StateFlow и обработать клики (действия).

// Файл: MainViewModel.kt ----------------------
// Состояние экрана
data class MainScreenState(
    val messages: List<SduiMessageModel> = emptyList(),
    val isLoading: Boolean = true
)


class MainViewModel : ViewModel() {

    private val _state = MutableStateFlow(MainScreenState())
    val state: StateFlow<MainScreenState> = _state.asStateFlow()

    init {
        loadServerData()
    }

    private fun loadServerData() {
        // Имитация ответа от бэкенда (SDUI)
        viewModelScope.launch {
            val serverMock = listOf(
                SduiMessageModel(
                    id = "msg_1",
                    textHtml = "Commission paid on bookings. <a href='https://booking.com'>Find out more</a>",
                    leadingIcon = SduiIcon.INFO,
                    trailingIcon = SduiIcon.CLOSE,
                    trailingAction = SduiAction.DismissMessage("msg_1")
                )
            )
            _state.value = MainScreenState(messages = serverMock, isLoading = false)
        }

        viewModelScope.launch {
            // Имитируем сетевую задержку
            delay(2500)

            //  Имитация SDUI-ответа с ТРЕМЯ карточками
            val serverResponseMock = listOf(
                // Карточка 1: Инфо о комиссии (Синяя иконка INFO)
                SduiMessageModel(
                    id = "id_commission",
                    textHtml = "Commission paid on bookings and other factors may affect property rankings. <a href='https://booking.com'>Find out more</a>",
                    leadingIcon = SduiIcon.INFO,
                    trailingIcon = SduiIcon.CLOSE,
                    trailingAction = SduiAction.DismissMessage("id_commission")
                ),
                // Карточка 2: Предупреждение о цене (Желтая WARNING)
                SduiMessageModel(
                    id = "id_price_warning",
                    textHtml = "Price may increase due to low availability in NH Barcelona. <a href='https://booking.com/offers'>View details</a>",
//                    leadingIcon = SduiIcon.WARNING,
                    leadingIcon = SduiIcon.LOCATION,
                    trailingIcon = SduiIcon.CLOSE,
                    trailingAction = SduiAction.DismissMessage("id_price_warning")
                ),
                // Карточка 3: Успех Genius (Зеленая SUCCESS)
                SduiMessageModel(
                    id = "id_genius_success",
                    textHtml = "Congratulations! You've unlocked the <b>Genius Level 2</b> discount.",
//                    leadingIcon = SduiIcon.SUCCESS,
                    leadingIcon = SduiIcon.HEART,
                    trailingIcon = SduiIcon.CLOSE,
                    trailingAction = SduiAction.DismissMessage("id_genius_success")
                )
            )
            // Обновляем состояние списка
            _state.value = MainScreenState(messages = serverResponseMock, isLoading = false)
        }
    }

    // Единая точка входа для всех действий из UI
    fun onAction(action: SduiAction) {
        when (action) {
            is SduiAction.DismissMessage -> {
                // Удаляем сообщение из состояния
                _state.update { currentState ->
                    currentState.copy(
                        messages = currentState.messages.filter { it.id != action.messageId }
                    )
                }
            }

            is SduiAction.OpenUrl -> {
                // Логика перехода (обычно делегируется в Router/Navigator)
                println("Аналитика: переход по ссылке ${action.url}")
            }
        }
    }
}

// Уровень 3: Фабрика рендеринга (Мост SDUI) =======================================================
// Это важнейший слой в SDUI. Мы создаем компонент-посредник, который берет "глупую" модель данных и распаковывает ее в ваш "умный" Message.

// Файл: SduiMessageRenderer.kt
/**
 * Рендерер читает модель и преобразует ее в слоты для Message6
 */
@Composable
fun SduiMessageRenderer(
    model: SduiMessageModel,
    onAction: (SduiAction) -> Unit // Прокидываем события наверх во ViewModel
) {
    Message(
        leadingIcon = model.leadingIcon?.let { iconType ->
            {
                Image(painter = painterResource(iconType.toDrawableRes()), contentDescription = null)
            }
        },

        trailingIcon = model.trailingIcon?.let { iconType ->
            {
                Icon(
                    painter = painterResource(iconType.toDrawableRes()),
                    contentDescription = "Action"
                )
            }
        },
        // Если сервер прислал действие для правой иконки — вешаем его на клик
        onTrailingIconClick = {
            model.trailingAction?.let { action -> onAction(action) }
        }
    ) {
        // Используем нашу функцию парсинга HTML из предыдущих заданий
        val parsedText = parseServerHtmlText(html = model.textHtml)
        Text(text = parsedText)
    }
}

// Вспомогательный маппер (Mapper) серверных иконок в локальные ресурсы
private fun SduiIcon.toDrawableRes(): Int = when (this) {
    SduiIcon.INFO -> R.drawable.ic_info
    // SduiIcon.WARNING-> R.drawable.ic_warning
    SduiIcon.LOCATION -> R.drawable.ic_location
    SduiIcon.CLOSE -> R.drawable.ic_close_union
    // SduiIcon.SUCCESS -> R.drawable.ic_success
    SduiIcon.HEART -> R.drawable.ic_heart
}

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


// Файл: FinalMessage.kt
@Composable
fun Message(
    modifier: Modifier = Modifier,
    iconSize: Dp = 20.dp,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    onTrailingIconClick: (() -> Unit)? = null, // Уточнил название
    content: @Composable ColumnScope.() -> Unit
) {
    val defaultTextStyle = MaterialTheme.typography.bodySmall

    CompositionLocalProvider(LocalMessageIconSize provides iconSize) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.small,
            border = MaterialTheme.borders.thin
        ) {
            Row(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                verticalAlignment = Alignment.Top
            ) {
                // Иконка слева
                if (leadingIcon != null) {
                    Box(
                        modifier = Modifier
                            .size(LocalMessageIconSize.current)
                            .alignCenterToFirstLine(textStyle = defaultTextStyle),
                        contentAlignment = Alignment.Center
                    ) {

                        // Оборачиваем лямбду в блок, задающий правила цвета
                        CompositionLocalProvider(
                            LocalContentColor provides MaterialTheme.colorScheme.onSecondaryContainer
                        ) {
                            leadingIcon()
                        }
                    }
                }

                // Контентная часть
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        ProvideTextStyle(value = defaultTextStyle) {
                            content()
                        }
                    }
                }

                // Иконка справа
                if (trailingIcon != null) {
                    Box(
                        modifier = Modifier
                            .size(LocalMessageIconSize.current)
                            .alignCenterToFirstLine(textStyle = defaultTextStyle)
                            // Применяем клик только если передано действие
                            .then(
                                if (onTrailingIconClick != null) {
                                    Modifier.customClickable(
                                        debounceTime = 300L,
                                        rippleRadius = LocalMessageIconSize.current,
                                        bounded = false,
                                        onClick = onTrailingIconClick
                                    )
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides MaterialTheme.colorScheme.onSecondaryContainer
                        ) {
                            trailingIcon()
                        }
                    }
                }
            }
        }
    }
}

// Уровень 5: Сборка экрана (Точка входа)
// Теперь посмотрим, как элегантно выглядит итоговый экран. Он абсолютно реактивен и управляется данными из ViewModel.

// Файл: MainScreen.kt
@Composable
fun MainScreen(viewModel: MainViewModel, modifier: Modifier) {
    // Безопасно подписываемся на StateFlow
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        // Показываем Shimmer или лоадер
        CircularProgressIndicator()
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            // Магия SDUI: Рендерим список сообщений, не зная их содержимого!
            items(
                items = state.messages,
                key = { message -> message.id } // Оптимизация для LazyColumn
            ) { messageModel ->

                SduiMessageRenderer(
                    model = messageModel,
                    onAction = viewModel::onAction // Передаем ссылку на функцию
                )
            }
        }
    }
}

/*
    Резюме архитектуры для студентов:

    Разделение ответственности (Separation of Concerns):
        - Message отвечает только за пиксели и отступы.
        - SduiMessageRenderer отвечает только за маппинг данных в UI.
        - ViewModel отвечает только за логику.

    Масштабируемость:
        Если завтра бэкенд начнет присылать 5 новых
        типов карточек (Banner, Alert, Promo), Message не изменится вообще!

        Вы просто добавите новые дата-классы и новые рендереры.
        Это и есть настоящая мощь Jetpack Compose в enterprise-разработке.

 */
