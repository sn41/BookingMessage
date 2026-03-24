package kz.misal.message.frame

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kz.misal.message.R
import kz.misal.message.compose_util.alignCenterToFirstLine
import kz.misal.message.compose_util.buildInteractiveLinkText
import kz.misal.message.compose_util.buildInteractiveLinkText1
import kz.misal.message.compose_util.iconClickable
import kz.misal.message.compose_util.customClickable
import kz.misal.message.ui.theme.LocalMessageIconSize
import kz.misal.message.ui.theme.MessageTheme
import kz.misal.message.ui.theme.borders
import kz.misal.message.ui.theme.spacing

// Выполним несколько шагов преобразования

// Базовая версия
@Composable
private fun Message0(text: AnnotatedString, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White, // <<<--- зададим в теме, используем по умолчанию
        shape = RoundedCornerShape(6.dp), // <<<--- зададим в теме
        border = BorderStroke(1.dp, Color(0xFFE9E9E9)) // <<<--- зададим в теме
    ) {
        Box(
            modifier = Modifier.Companion.padding(17.dp) // <<<--- зададим в теме
        ) {
            // Используется обычный Text. Он сам обработает клик и откроет браузер
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black, //<<<--- зададим в теме, используем по умолчанию
                modifier = Modifier.Companion
                    .width(256.dp)
                    .align(Alignment.Companion.CenterStart)
            )
        }
    }
}

@Composable
private fun Message1(text: AnnotatedString, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        border = MaterialTheme.borders.thin
    ) {
        Box(
            //
            modifier = Modifier.padding(MaterialTheme.spacing.medium)
        ) {
            // Используется обычный Text. Он сам обработает клик и откроет браузер
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .width(256.dp)
                    .align(Alignment.CenterStart)
            )
        }
    }
}

// Пропускаем версию где используем Row и IconButton для кнопки, она тривиальна.
// Теперь мы создаём универсальный компонент, где можно задать начальную и завершающую иконки
// и задать колонку строк в поле текста
// Мы пробуем использовать baseLine чтобы иконки сами выстроились по уровню текста
// Они разместятся по нижней линии текста

@Composable
private fun Message2(
    modifier: Modifier = Modifier,
    // 1. Слот для начальной иконки (сделан nullable, чтобы иконка была необязательной)
    leadingIcon: (@Composable () -> Unit)? = null,
    // 2. Слот для кнопки закрытия (тоже необязательный)
    onClose: (() -> Unit)? = null,
    // 3. Слот для контента (текстов). Мы используем ColumnScope,
    // чтобы внутри лямбды вам были доступны модификаторы колонки
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small, // наша тема
        border = MaterialTheme.borders.thin, // наша тема
        color = MaterialTheme.colorScheme.secondaryContainer // наша тема
    ) {
        Row(
            modifier = Modifier.padding(MaterialTheme.spacing.medium), // наша тема
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small), // 8.dp
            //-- verticalAlignment = Alignment.Top --
            // будем использовать BaseLine и
            // нижний край иконки совпадёт с базовой линией текста
        ) {
            // --- НАЧАЛЬНАЯ ИКОНКА ---
            if (leadingIcon != null) {
                Box(
                    modifier = Modifier
                        .padding(end = MaterialTheme.spacing.medium)
                        // Выровнять нижний край иконки по базовой линии текста
                        .alignByBaseline()
                ) {
                    leadingIcon()
                }
            }

            // --- КОНТЕНТ (Колонка текстов) ---
            // Оборачиваем переданный контент в Column
            Column(
                modifier = Modifier
                    // Занимает всё доступное место между иконками
                    .weight(1f)
                    // Колонка автоматически "отдаст" базовую линию своего первого Text
                    .alignByBaseline(),

                verticalArrangement = Arrangement.spacedBy(4.dp) // Отступ между строками текста
            ) {
                content()
            }

            // --- КНОПКА ЗАКРЫТИЯ ---
            if (onClose != null) {
                Box(
                    modifier = Modifier
                        .padding(start = MaterialTheme.spacing.medium)
                        // Крестик тоже ставим на базовую линию
                        .alignByBaseline()
                ) {


                    //Стандартный IconButton из M3 имеет невидимую зону 48.dp,
                    // которая раздвигает Row и ломает макеты.
                    // Мы заменим его на Icon с модификатором .clickable,
                    // который позволяет точно контролировать зону клика и визуальный эффект.

//                    IconButton(
//                        onClick = onCloseClick,
//                        modifier = Modifier.size(13.dp) // 13 - из макета
//                    ) {
//                        Icon(
//                            // Замените на R.drawable.union, если используете свой ресурс
//                            painter = painterResource(id = R.drawable.union),
//                            contentDescription = "Close",
//                            tint = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                    }

                    Icon(
                        painter = painterResource(id = R.drawable.ic_close_union),
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            // Ставим размер холста (Viewport), а не самого крестика!
                            // Размер можно посмотреть в файле ic_close_union.xml
                            .size(14.dp)
                            // Следующий кусок кода неплохо бы заменить на кастомный модификатор
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                // Добавляем аккуратный эффект нажатия без огромного радиуса
                                indication = ripple(
                                    bounded = false,
                                    radius = MaterialTheme.spacing.medium,
                                    // color = Color.Unspecified // Можно задать свой цвет при желании
                                ),
                                onClick = onClose
                            )
                    )
                }
            }
        }


    }
}

/*
    Почему не просто .clickable{}?

    Простой .clickable { ... } по умолчанию создает прямоугольный серый фон, который
строго ограничен размерами элемента. Для текстовых кнопок или больших карточек это нормально,
но для маленькой иконки крестика это выглядит топорно.

Расширенная конструкция используется для создания идеального,
кастомного визуального отклика (Ripple-эффекта).

1. indication = ripple(bounded = false, radius = 16.dp)
    Это визуальная часть эффекта (тот самый расходящийся круг при нажатии).

bounded = false (без границ):
    Если поставить
    - true (или использовать обычный clickable), то круг пульсации резко обрежется по краям вашей иконки
        (создав уродливый квадратный эффект).
    - false позволяет кругу плавно выходить за пределы иконки (эффект "пузыря").

radius = 16.dp:
    Мы жестко задаем размер этого "пузыря".
    Так как сама иконка маленькая (около 13-24 dp),
    круг радиусом 16 dp (диаметром 32 dp) будет смотреться очень аккуратно под пальцем пользователя.

2. interactionSource = remember { MutableInteractionSource() }
    Это "сенсор" или "память" нашего компонента.

    Чтобы нарисовать красивую пульсацию, системе нужно знать:
    - нажал ли сейчас пользователь палец?
    - Держит ли он его? Или уже отпустил?

    MutableInteractionSource собирает все эти события (Press, Release, Hover, Focus).

    Мы оборачиваем его в remember { ... },
    чтобы компонент не забыл состояние нажатия при перерисовке экрана (рекомпозиции).

Технический нюанс:
    Если вы задаете кастомный indication (наш ripple),
    Compose обязывает вас передать ему interactionSource, чтобы эффект знал, на какие события реагировать.

3. onClick = { onCloseClick?.invoke() }
    Это просто само действие, которое произойдет при клике (вызов функции, которую мы передали извне).
*/


@Composable
private fun CommissionMessageCard1(modifier: Modifier = Modifier) {
    // Применим Message2
    // Обратите внимание на нашу функцию buildDisclaimerText - разберите, как она работает
    val disclaimerText = buildInteractiveLinkText1(
        text = "Commission paid on bookings and other factors may affect property rankings. Learn about these ranking parameteters and how to select and modify them.",
        linkText = "Find out more",
        url = "https://www.booking.com/ranking_info"
    )

    Message2(leadingIcon = { }, onClose = { }) {
        Text(
            text = disclaimerText,
            modifier = Modifier,

            style = MaterialTheme.typography.bodySmall
            // Если нужно, задаем стиль для обычного текста (иначе он будет черным по умолчанию)
            // .copy(
            //      color = MaterialTheme.colorScheme.onSurfaceVariant
            //  )
        )
    }
}

// Превью для проверки в Android Studio
@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun CommissionMessageCard1Preview() {
    // Оборачиваем в тему (замените на вашу AppTheme)
    MaterialTheme {
        Box(modifier = Modifier.padding(24.dp)) {
            CommissionMessageCard1()
        }
    }
}

/*
    Нас не устраивает размещение иконок.
    Пробуем другую стратегию.

    Нужно прижать элементы к верху (Alignment.Top),
    но сдвинуть иконку вниз так, чтобы её центр иконок совпал с центром первой строки текста.

    Это делается с помощью простой математики, встроенной прямо в Compose.
    Мы
     - берем высоту строки текста (lineHeight),
     - вычитаем высоту иконки,
     - и делим остаток пополам.
     - Это и будет наш идеальный отступ сверху!

 // 1. Получаем стиль текста
    val textStyle = MaterialTheme.typography.bodySmall

    // 2. Достаем высоту одной строки (Line Height)
    val lineHeight = textStyle.lineHeight

    // 3. Переводим размер иконки (24.dp) и высоту строки в одинаковые единицы (Dp)
    val density = LocalDensity.current
    val iconSizeDp = 24.dp

    val lineHeightDp = with(density) {
        // Если lineHeight задан в sp, конвертируем в dp
        if (lineHeight.isSp) lineHeight.toDp() else 16.dp // 16.dp как фоллбэк
    }

    // 4. Считаем идеальный отступ: (Высота строки - Высота иконки) / 2
    // Если иконка больше строки, отступ будет 0 (coerceAtLeast)
    val iconTopPadding = ((lineHeightDp - iconSizeDp) / 2).coerceAtLeast(0.dp)

    Помещаем этот код в кастомный модификатор, смотри файл compose_util/AlignCenterToFirstLine
*/


@Composable
private fun Message3(
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        border = MaterialTheme.borders.thin,
        color = MaterialTheme.colorScheme.surface
    ) {

        // Как это не универсально!
        // задавать жёстко стиль текста MaterialTheme.typography.bodySmall
        val defaultTextStyle = MaterialTheme.typography.bodySmall

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {

            if (leadingIcon != null) {
                Box(
                    modifier = Modifier
                        .padding(end = MaterialTheme.spacing.medium)
                        // Используем наш модификатор alignCenterToFirstLine
                        .alignCenterToFirstLine(textStyle = defaultTextStyle, itemSize = 24.dp)
                ) {
                    leadingIcon()
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp) // Отступ между строками текста
            ) {
                content()
            }


            if (onClose != null) {
                Box(
                    modifier = Modifier
                        .padding(start = MaterialTheme.spacing.medium)
                        // Снова используем тот же источник истины
                        .alignCenterToFirstLine(
                            textStyle = defaultTextStyle,
                            itemSize = 14.dp,
                            opticalOffset = 0.dp
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close_union),
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(14.dp)
                            // Используем наш кастомный модификатор
                            .iconClickable(onClick = onClose)
                    )
                }
            }
        }
    }
}

/*
    Нам приходится задавать стиль текста для иконок.
    Но мы не можем знать заранее, какой стиль будет использоваться для текста в content!!!
    Наша функция никак не видит стиля этого текста!

    Как получить один источник истины
    и для стиля текста в content и для вызова calculateFirstLineCenterPadding
    при вычислении отступа иконок?

    В Jetpack Compose для этого существует элегантный механизм — ProvideTextStyle (или напрямую CompositionLocalProvider).

    Этот механизм берет стиль,
    делает его "дефолтным" для всего, что находится внутри его блока,
    и избавляет вас от необходимости вручную писать style = ...
    в каждом компоненте Text внутри контента.
*/

@Composable
private fun Message4(
    modifier: Modifier = Modifier,
    // 1. ЕДИНЫЙ ИСТОЧНИК ИСТИНЫ для этого компонента
    defaultTextStyle: TextStyle = MaterialTheme.typography.bodySmall,
    // (Опционально) Единый цвет текста для компонента
    defaultTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    leadingIcon: (@Composable () -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {


    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        border = MaterialTheme.borders.thin,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
            verticalAlignment = Alignment.Top
        ) {

            if (leadingIcon != null) {
                Box(
                    modifier = Modifier
                        .padding(end = MaterialTheme.spacing.medium)
                        // Используем наш источник истины и
                        .alignCenterToFirstLine(textStyle = defaultTextStyle, itemSize = 14.dp)
                ) {
                    leadingIcon()
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp) // Отступ между строками текста
            ) {
                // 2. ПЕРЕДАЕМ ИСТОЧНИК ИСТИНЫ ВНИЗ ПО ДЕРЕВУ
                // ProvideTextStyle делает defaultTextStyle стилем по умолчанию
                // для всех Text(), которые будут вызваны внутри content()
                // Это означает, что в content мы должны передавать текст без стиля и цвета!!!
                ProvideTextStyle(value = defaultTextStyle) {
                    // А CompositionLocalProvider передаст цвет по умолчанию
                    CompositionLocalProvider(LocalContentColor provides defaultTextColor) {
                        content()
                    }
                }
            }

            // --- КНОПКА ЗАКРЫТИЯ ---
            if (onClose != null) {
                Box(
                    modifier = Modifier
                        .padding(start = MaterialTheme.spacing.medium)
                        // Снова используем тот же источник истины
                        .alignCenterToFirstLine(
                            textStyle = defaultTextStyle,
                            itemSize = 14.dp,
                            opticalOffset = 0.dp
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close_union),
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(14.dp)
                            // Наш кастомный модификатор
                            .iconClickable(onClick = onClose)
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun PreviewMessage3() {
    val disclaimerText = buildInteractiveLinkText1(
        text = "Commission paid on bookings and other factors may affect property rankings. Learn about these ranking parameteters and how to select and modify them.",
        linkText = "Find out more",
        url = "https://www.booking.com/ranking_info"
    )

    Message3(
        Modifier.width(360.dp), onClose = {}
    ) {
        Text(
            text = disclaimerText,
            modifier = Modifier.width(256.dp),
            style = MaterialTheme.typography.bodySmall,
            // выравнивание по ширине
            textAlign = TextAlign.Justify

        )
    }
}

/*
    Попробуем выравнять текст по ширине

    Проблема "рек пробелов"

    Выравнивание по ширине на узких экранах мобильных телефонов часто создает проблему:
    чтобы края были ровными, система вставляет между словами огромные дыры
    (дизайнеры называют это "реками пробелов"). Читать такой текст становится физически тяжело.

    Если вы включаете TextAlign.Justify,
    строго рекомендуется включить автоматический перенос слов (Hyphenation),
    чтобы текст распределялся красиво и плотно!

    Современный подход Compose 1.3+
    В новых версиях Compose добавили мощную типографику для правильного выравнивания.
    Вам нужно добавить настройки hyphens и lineBreak прямо в ваш стиль текста:

        import androidx.compose.ui.text.style.Hyphens
        import androidx.compose.ui.text.style.LineBreak
        import androidx.compose.ui.text.style.TextAlign

        Text(
            text = "Commission paid on bookings and other factors may affect...",
            // 1. Включаем выравнивание по ширине
            textAlign = TextAlign.Justify,
            // 2. Копируем ваш стиль и добавляем правила переноса
            style = MaterialTheme.typography.bodySmall.copy(
                // Включаем автоматическую расстановку дефисов
                hyphens = Hyphens.Auto,
                // Включаем алгоритм верстки абзацев (как в книгах), он делает пробелы равномернее
                lineBreak = LineBreak.Paragraph
            ),
            modifier = Modifier.fillMaxWidth()
        )

    Что это даст: Теперь длинное слово "parameters" в конце строки не будет целиком
    прыгать на новую строку (создавая огромную дыру выше), а аккуратно перенесется: "param-eters".
    Текст будет выглядеть монолитно и профессионально

 */

@Preview(name = "message 04", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewMessage4() {
    MessageTheme {
        val disclaimerText = buildInteractiveLinkText1(
            text = "Commission paid on bookings and other factors may affect property rankings. Learn about these ranking parameteters and how to select and modify them.",
            linkText = "Find out more",
            url = "https://www.booking.com/ranking_info"
        )

        Message4(
            Modifier.width(360.dp), onClose = {}
        ) {
            Text(
                text = disclaimerText,
                modifier = Modifier.width(256.dp),
                // 2. Копируем ваш стиль и добавляем правила переноса
                style = MaterialTheme.typography.bodySmall.copy(
                    // Включаем автоматическую расстановку дефисов
                    hyphens = Hyphens.Auto,
                    // Включаем алгоритм верстки абзацев (как в книгах), он делает пробелы равномернее
                    lineBreak = LineBreak.Paragraph
                ),
                // выравнивание по ширине
                textAlign = TextAlign.Justify

            )
        }
    }
}

// Используем CompositionLocalProvider
/*
# Зачем здесь
    ```
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onSecondaryContainer
    ) {...}
    ```

    Это одна из самых изящных фишек при создании профессиональных UI-компонентов (Slot API) в Compose.

    Если коротко: **этот код автоматически задает правильный цвет по умолчанию
    для всех текстов и иконок внутри блока,
    чтобы разработчику не приходилось прописывать его вручную каждый раз

    Разберем подробнее, какую именно проблему архитектуры это решает.

### Проблема: "Непредсказуемые" слоты (Slot API)

    В вашем компоненте `Message` фон (`Surface`) имеет цвет `secondaryContainer`.
    По правилам Material Design 3, любой текст или иконка, лежащие на этом фоне,
    должны окрашиваться в контрастный цвет `onSecondaryContainer`, чтобы их было легко читать.

    Однако ваш компонент спроектирован очень гибко —
    он принимает контент извне через лямбду (`content: @Composable ColumnScope.() -> Unit`).
    Вы как создатель компонента не знаете, что именно другой разработчик положит внутрь.

    Что будет, если разработчик напишет так:
    ```kotlin
        Message {
            Text("Политика конфиденциальности") // Забыл указать color!
            Icon(Icons.Default.Warning, contentDescription = null) // Забыл указать tint!
        }
    ```

    Без переопределения цветов текст и иконка
    взяли бы дефолтный цвет всего экрана (например, черный `onBackground`).
    На цветном фоне карточки `Message` это могло бы выглядеть грязно или вообще слиться с фоном.

### Решение: Подмена `LocalContentColor`

    В Jetpack Compose есть встроенный механизм невидимой передачи данных — `CompositionLocal`.
    Одной из таких переменных является `LocalContentColor`.

    Когда стандартные компоненты `Text` или `Icon` отрисовываются на экране,
    они работают по такой логике: *"Если программист не передал мне цвет напрямую через параметры,
    я возьму тот цвет, который сейчас лежит в `LocalContentColor`"*.

    Когда вы оборачиваете вызов `content()` (или `leadingIcon()`) в этот провайдер:
    ```kotlin
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            content()
        }
    ```
    Вы буквально отдаете приказ движку Compose:
    > *"Внимание! Для всех компонентов, которые будут отрисованы внутри этого блока, назначь цвет по умолчанию `onSecondaryContainer`"*.

### Почему это признак качественного (Senior) кода?

    Именно так написана сама библиотека Material 3 от Google. Вспомните стандартную кнопку:
    ```kotlin
        Button(onClick = { }) {
            Text("Нажми меня")
        }
    ```
    Вы ведь не пишете `color = MaterialTheme.colorScheme.onPrimary` для текста внутри кнопки?
    Кнопка делает это за вас "под капотом",
    используя точно такой же `CompositionLocalProvider(LocalContentColor provides ...)`.

    Вы применили этот же паттерн в своем `Message`,
    сделав его **защищенным от ошибок ("защита от дурака")**.
    Любой, кто будет использовать вашу карточку `Message`,
    автоматически получит идеальный контрастный дизайн, даже если напишет просто `Text("Привет")`.

---
    Рядом с этим блоком есть еще одна мощная конструкция —
    `ProvideTextStyle(value = MaterialTheme.typography.bodySmall) { content() }`.

    Конструкция `ProvideTextStyle` — это родной брат `LocalContentColor`,
    но её работа гораздо умнее простого переопределения.

    ```kotlin
        ProvideTextStyle(value = MaterialTheme.typography.bodySmall) {
            content() // Лямбда, куда передается верстка от другого разработчика
        }
    ```

    Как и в случае с цветом, эта обертка задает типографику по умолчанию
    для всех компонентов `Text` внутри нее.
    Но имеется особенность **в механизме слияния (Merge)**.

### В чем отличие от работы с цветом?

    Цвет — это примитив. У текста может быть только один цвет.
    Если вы задали `LocalContentColor = Color.Blue`,
    а программист внутри написал `Text(color = Color.Red)`,
    то красный полностью затрет синий. Всё просто.

    А вот **Типографика (`TextStyle`) — это огромный контейнер свойств**.
    Там лежат: размер шрифта, межстрочный интервал, жирность,
    семейство шрифтов (ваша папка с `.ttf`), кернинг и многое другое.

    Когда вы оборачиваете контент в `ProvideTextStyle`,
    под капотом Compose не просто заменяет один стиль на другой.
    Он использует функцию **`merge()` (слияние)**.

### Как работает магия слияния (Merge)

    Вы задали базовый стиль карточки: `bodySmall` (например, это 12sp, обычный вес, шрифт Inter).

    Смотрите, что произойдет, если другой разработчик начнет использовать вашу карточку `Message`:

    ```kotlin
        Message {
            // Пример 1: Разработчик ничего не указал.
            // Текст автоматически станет 12sp, Inter (возьмет всё из ProvideTextStyle)
            Text("Политика конфиденциальности")

            // Пример 2: Разработчик захотел выделить слово жирным.
            Text(
                text = "Важное предупреждение",
                fontWeight = FontWeight.Bold // <-- Указал ТОЛЬКО жирность
            )
        }
    ```

    В **Примере 2** происходит настоящая магия Slot API.
    Compose видит, что разработчик указал только `fontWeight`.
    Вместо того чтобы сбросить остальные настройки, Compose **сольет** их.
    Он возьмет размер 12sp и шрифт Inter из вашего `ProvideTextStyle`, добавит к ним `FontWeight.Bold`
    от разработчика и выдаст идеальный результат.

    Текст останется маленьким (как и положено в вашей карточке), но станет жирным!

### Почему это архитектурный шедевр?

1. **Диктатура дизайна с человеческим лицом:**
Вы, как создатель компонента `Message`, жестко диктуете правило:
*"Всё внутри этой карточки должно быть написано мелким шрифтом (`bodySmall`)"*.

2. **Гибкость:**
При этом вы не отбираете у других программистов свободу точечно менять стили
(сделать текст жирным, курсивом или зачеркнутым).
Их локальные правки изящно наслаиваются на ваш фундамент.

Если бы вы не использовали `ProvideTextStyle`, а просто оставили `content()`,
то любой стандартный `Text` внутри карточки отрисовался бы огромным базовым шрифтом `bodyLarge` (16sp),
сломав вам всю компактную верстку всплывающего сообщения.
 */

@Composable
private fun Message5(
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val defaultTextStyle = MaterialTheme.typography.bodySmall

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.small,
        border = MaterialTheme.borders.thin
    ) {
        Row(
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small), // 8.dp
            verticalAlignment = Alignment.Top
        ) {
            // Иконка слева
            if (leadingIcon != null) {
                Box(
                    modifier = Modifier.alignCenterToFirstLine(
                        textStyle = defaultTextStyle,
                        itemSize = 20.dp
                    )
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
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall) // 4.dp
            ) {
                // Оборачиваем лямбду в блок, задающий правила цвета
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    ProvideTextStyle(
                        value = defaultTextStyle
                    ) {
                        content()
                    }
                }
            }

            // Иконка закрытия
            if (onClose != null) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close_union),
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .size(20.dp)
                        .alignCenterToFirstLine(
                            textStyle = defaultTextStyle,
                            itemSize = 20.dp
                        )
                        .customClickable(
                            debounceTime = 300L,
                            rippleRadius = 20.dp,
                            bounded = false,
                            onClick = onClose
                        )
                )
            }
        }
    }
}

@Preview(name = "message 05", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewMessage5() {
    MessageTheme {
        val disclaimerText = buildInteractiveLinkText1(
            text = "Commission paid on bookings and other factors may affect property rankings. Learn about these ranking parameteters and how to select and modify them.",
            linkText = "Find out more",
            url = "https://www.booking.com/ranking_info"
        )

        Message5(
            Modifier.width(360.dp), onClose = {}
        ) {
            Text(
                text = disclaimerText,
                modifier = Modifier.width(256.dp),
                // 2. Копируем ваш стиль и добавляем правила переноса
                style = MaterialTheme.typography.bodySmall.copy(
                    // Включаем автоматическую расстановку дефисов
                    hyphens = Hyphens.Auto,
                    // Включаем алгоритм верстки абзацев (как в книгах), он делает пробелы равномернее
                    lineBreak = LineBreak.Paragraph
                ),
                // выравнивание по ширине
                textAlign = TextAlign.Justify

            )
        }
    }
}


/*

 Шестой вариант Message предполагает задать завершающую иконку

 Рассмотрим еще одно применение глобальных значений, иногда такое решение может нам пригодится.

 " А могу ли я задать какой-то размер, как глобальную переменную?
 А то мне не нравиться, что приходится указывать размер изображения и для расчёта смещения
 в Message, и при вызове Message в лямбде, в Icon или Image. "

1. Создаем LocalMessageIconSize
В файле с темой создаем ключ, который будет хранить размер иконки внутри компонента Message.
По умолчанию установим 20.dp (стандарт для макета Booking).

    val LocalMessageIconSize = compositionLocalOf { 20.dp }

2. Обновляем модификатор alignCenterToFirstLine
Теперь модификатору не нужно передавать itemSize в параметрах — он сам возьмет его из контекста.

    @Composable
    fun Modifier.alignCenterToFirstLine(
        textStyle: TextStyle,
        opticalOffset: Dp = 2.dp
    ): Modifier {
        // Читаем размер из провайдера
        val itemSize = LocalMessageIconSize.current
        val topPadding = calculateFirstLineCenterPadding(textStyle, itemSize, opticalOffset)
        return this.padding(top = topPadding)
    }

Добавим к Message параметр
    fun Message6(
        modifier: Modifier = Modifier,
        iconSize: Dp = 20.dp, // Задаем размер ОДИН РАЗ здесь
    ){
        // Оборачиваем всё содержимое в провайдер размера
        CompositionLocalProvider(LocalMessageIconSize provides iconSize) {
        ...
            // Используем
            Box(modifier = Modifier.size(LocalMessageIconSize.current)){}
        }

    Теперь Message становится «умным»: он сам объявляет, какой размер иконок он ожидает,
    и предоставляет его всем внутренним элементам. В том числе и функции alignCenterToFirstLine.
 */

@Composable
fun Message6(
    modifier: Modifier = Modifier,
    iconSize: Dp = 20.dp, // Задаем размер ОДИН РАЗ здесь
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val defaultTextStyle = MaterialTheme.typography.bodySmall
    // Оборачиваем всё содержимое в провайдер размера
    CompositionLocalProvider(LocalMessageIconSize provides iconSize) {

        Surface(
            modifier = modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.small,
            border = MaterialTheme.borders.thin
        ) {
            Row(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium), // 8.dp
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
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall) // 4.dp
                ) {
                    // Оборачиваем лямбду в блок, задающий правила цвета
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        ProvideTextStyle(
                            value = defaultTextStyle
                        ) {
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
                            .customClickable(
                                debounceTime = 300L,
                                rippleRadius = LocalMessageIconSize.current,
                                bounded = false,
                                onClick = { if (onClick != null) onClick() }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Оборачиваем лямбду в блок, задающий правила цвета
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


@Preview(name = "message 06", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewMessage6() {
    MessageTheme {
        Message6(
            modifier = Modifier.width(360.dp),
            iconSize = 20.dp, // размер!
            leadingIcon = {
                Image(
                    painterResource(R.drawable.group_28),
                    "",
                )
            },
            trailingIcon = {
                Icon(
                    painterResource(R.drawable.ic_check),
                    "",
                )
            },
            onClick = {}
        ) {
            Text(
                text = "Manage personalised recommendations",
                modifier = Modifier.fillMaxWidth(),
                // 2. Копируем ваш стиль и добавляем правила переноса
                style = MaterialTheme.typography.bodyMedium.copy(
                    // Включаем автоматическую расстановку дефисов
                    hyphens = Hyphens.Auto,
                    // Включаем алгоритм верстки абзацев (как в книгах), он делает пробелы равномернее
                    lineBreak = LineBreak.Paragraph
                ),
                // выравнивание по ширине
                textAlign = TextAlign.Justify
            )

        }
    }
}

@Preview(name = "message 06", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewMessage7() {
    MessageTheme {
        val disclaimerText = buildInteractiveLinkText(
            text = "Commission paid on bookings and other factors may affect property rankings. Learn about these ranking parameteters and how to select and modify them.",
            linkText = "Find out more",
            url = "https://www.booking.com/ranking_info",
            true
        )

        Message6(
            iconSize = 20.dp,
            modifier = Modifier.width(360.dp),
            trailingIcon = {
                Icon(
                    painterResource(R.drawable.ic_close_union),
                    "",
                )
            },
            onClick = {}
        ) {
            Text(
                text = disclaimerText,
                modifier = Modifier.width(300.dp),
                // 2. Копируем ваш стиль и добавляем правила переноса
                style = MaterialTheme.typography.bodySmall.copy(
                    // Включаем автоматическую расстановку дефисов
                    hyphens = Hyphens.Auto,
                    // Включаем алгоритм верстки абзацев (как в книгах), он делает пробелы равномернее
                    lineBreak = LineBreak.Paragraph
                ),
                // выравнивание по ширине
                textAlign = TextAlign.Justify
            )

        }
    }
}

@Preview(name = "message 06", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewMessage8() {
    MessageTheme {

        val text = "Properties with these icons have been awarded Booking.com´s quality rating for homes"

        val disclaimerText = buildInteractiveLinkText(
            text = "",
            linkText = "Learn more",
            url = "https://www.booking.com/ranking_info",
        )

        Message6(
            iconSize = 20.dp,
            modifier = Modifier.width(360.dp),
            leadingIcon =  {
                Image(painterResource(R.drawable.group_16), "")
            },
            trailingIcon = {
                Icon(
                    painterResource(R.drawable.ic_close_union),
                    "",
                )
            },
            onClick = {}
        ) {

            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    // Включаем автоматическую расстановку дефисов
                    hyphens = Hyphens.Auto,
                    // Включаем алгоритм верстки абзацев (как в книгах), он делает пробелы равномернее
                    lineBreak = LineBreak.Paragraph
                ),
            )

            Text(
                text = disclaimerText,
                modifier = Modifier.width(256.dp),
                // 2. Копируем ваш стиль и добавляем правила переноса
                style = MaterialTheme.typography.bodySmall.copy(
                    // Включаем автоматическую расстановку дефисов
                    hyphens = Hyphens.Auto,
                    // Включаем алгоритм верстки абзацев (как в книгах), он делает пробелы равномернее
                    lineBreak = LineBreak.Paragraph
                ),
                // выравнивание по ширине
                textAlign = TextAlign.Justify
            )

        }
    }
}