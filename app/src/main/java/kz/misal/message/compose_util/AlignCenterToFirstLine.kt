package kz.misal.message.compose_util


import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import kz.misal.message.ui.theme.LocalMessageIconSize

//Универсальная Composable-функция
//Мы создадим функцию, которая принимает стиль текста и размер иконки, а возвращает готовый отступ в Dp. Я также добавил важную проверку на isUnspecified (на случай, если lineHeight в теме вообще не задан — тогда мы возьмем размер шрифта).


/**
 * Вычисляет отступ сверху для элемента (например, иконки),
 * чтобы его геометрический центр совпадал с центром первой строки текста.
 * * @param textStyle Стиль текста, относительно которого идет выравнивание
 * @param itemSize Внешний размер выравниваемого элемента (например, 24.dp)
 * @return Отступ сверху (Dp), который нужно применить к элементу
 */
@Composable
fun calculateFirstLineCenterPadding1(textStyle: TextStyle, itemSize: Dp): Dp {

    val density = LocalDensity.current

    // Если межстрочный интервал не задан, берем размер самого шрифта
    val effectiveLineHeight = when {
        textStyle.lineHeight.isSpecified -> textStyle.lineHeight
        textStyle.fontSize.isSpecified -> textStyle.fontSize
        else -> return 0.dp // Если вообще ничего не задано, возвращаем 0
    }

    val lineHeightDp = with(density) {
        when {
            effectiveLineHeight.isSp -> effectiveLineHeight.toDp()
            else -> 16.dp // Фолбэк на крайний случай
        }
    }

    // (Высота строки - Высота элемента) / 2
    val topPaddingValue = ((lineHeightDp - itemSize) / 2)

    // не должен быть меньше 0
    val topPadding = topPaddingValue.coerceAtLeast(0.dp)

    return topPadding
}


// Как использовать:

/*
val textStyle = MaterialTheme.typography.bodySmall
val iconPadding = calculateFirstLineCenterPadding(textStyle, 24.dp)

Icon(
// ...
    modifier = Modifier
        .padding(top = iconPadding)
        .size(24.dp)
)
*/


// -----------

// Кастомный Modifier
// Хороший тон прятать математику прямо в расширения модификаторов.
// Это делает код верстки чистым и читаемым (в стиле декларативного программирования).

/**
 * Модификатор для сдвига элемента вниз так, чтобы его центр
 * визуально совпадал с оптическим центром первой строки переданного стиля текста.
 */
@Composable
fun Modifier.alignCenterToFirstLine1(
    textStyle: TextStyle,
    itemSize: Dp
): Modifier {
    // Вызываем нашу универсальную функцию из Варианта 1
    val topPadding = calculateFirstLineCenterPadding1(textStyle, itemSize)

    // Применяем высчитанный отступ
    return padding(top = topPadding)
}



// --------------------------------------

/*
Но может оказаться, что calculateFirstLineCenterPadding не точно вычисляет отступ для иконки.
Иконка располагается чуть выше, чем требуется.

Мы столкнулись с одной из самых известных проблем в UI-разработке:
разницей между математическим и оптическим центрированием.

Функция calculateFirstLineCenterPadding считает всё абсолютно правильно с точки зрения математики,
но она опирается на lineHeight (полную высоту строки).

А проблема кроется в том, как устроены шрифты.

Вот три причины, почему иконка кажется выше, и как это исправить.

Причина 1: Анатомия шрифта (Line Height vs Cap Height)

        Когда вы задаете lineHeight (например, 20.sp),
    эта высота включает в себя не только сами буквы,
    но и невидимое пространство над ними (для диакритических знаков вроде Й или Ё)
    и под ними (для хвостиков букв y, p, q).

    Обычно "воздуха" сверху больше, чем снизу.
    В результате реальные буквы смещены чуть ниже математического центра контейнера lineHeight.
    Функция ставит иконку ровно по центру невидимого контейнера,
    поэтому относительно самих букв иконка выглядит задранной вверх.

Причина 2: Наследие Android (includeFontPadding)

        Исторически в Android у текстов включена настройка includeFontPadding = true.
    Она добавляет дополнительный невидимый отступ сверху текста,
    чтобы старые кривые шрифты не обрезались.
    Из-за этого контейнер текста становится еще выше, а сами буквы съезжают еще ниже.

Как это исправить?
Шаг 1: Отключаем includeFontPadding в вашей теме

// файл Type.kt
import androidx.compose.ui.text.PlatformTextStyle

val bodySmall = TextStyle(
    // ... ваши настройки шрифта
    platformStyle = PlatformTextStyle(
        includeFontPadding = false // Убирает лишний "воздух" сверху!
    )
)

Шаг 2: Добавляем параметр оптической компенсации в функцию
    В профессиональных дизайн-системах к математическому расчету всегда добавляют
оптический сдвиг (Optical Tweak).
Это константа (обычно от 1 до 3 dp),
которая компенсирует особенности конкретного шрифта (Roboto, Inter, SF Pro — у всех они разные).

Шаг 2: Подобрать opticalOffset?
У вас есть макет в Figma, сделайте следующее:
Выставите opticalOffset = 0.dp. Запустите Preview.
Если иконка выше текста, меняйте значение на 1.dp, 2.dp или 3.dp,
пока центр крестика не совпадет визуально с центром заглавной буквы вашей первой строки.
Оставьте это значение по умолчанию. Для одного шрифта оно всегда будет одинаковым.

Дизайнеры в Figma делают ровно то же самое: они выравнивают элементы не математическими инструментами,
а сдвигают иконки на пару пикселей вниз стрелочками на клавиатуре, чтобы "смотрелось ровно".
 */


@Composable
fun calculateFirstLineCenterPadding(
    textStyle: TextStyle,
    itemSize: Dp,
    opticalOffset: Dp = 0.dp // <<<<<-- Добавили компенсацию!
): Dp {

    val density = LocalDensity.current

    // Если межстрочный интервал не задан, берем размер самого шрифта
    val effectiveLineHeight = when {
        textStyle.lineHeight.isSpecified -> textStyle.lineHeight
        textStyle.fontSize.isSpecified -> textStyle.fontSize
        else -> return 0.dp // Если вообще ничего не задано, возвращаем 0
    }

    val lineHeightDp = with(density) {
        when {
            effectiveLineHeight.isSp -> effectiveLineHeight.toDp()
            else -> 16.dp // Фолбэк на крайний случай
        }
    }

    // (Высота строки - Высота элемента) / 2
    val topPaddingValue = ((lineHeightDp - itemSize) / 2)

    // не должен быть меньше 0
    val topPadding = topPaddingValue.coerceAtLeast(0.dp)

    val topPaddingPlusOpticalOffset = topPadding + opticalOffset

    return topPaddingPlusOpticalOffset
}

// Кастомный Modifier
/**
 * Модификатор для сдвига элемента вниз так, чтобы его центр
 * визуально совпадал с оптическим центром первой строки переданного стиля текста.
 */
@Composable
fun Modifier.alignCenterToFirstLine(
    textStyle: TextStyle,
    itemSize: Dp,
    opticalOffset: Dp = 0.dp // Прокидываем параметр
): Modifier  {
    val topPadding = calculateFirstLineCenterPadding(textStyle, itemSize, opticalOffset)
    return offset ( y = topPadding)
}


// Как использовать:

/*
val textStyle = MaterialTheme.typography.bodySmall
val iconPadding = calculateFirstLineCenterPadding(textStyle, 24.dp)

Icon(
// ...
    modifier = Modifier
        .padding(top = iconPadding)
        .size(24.dp)
)
*/

// Вариант, использующий локальную переменную LocalMessageIconSize
@Composable
fun Modifier.alignCenterToFirstLine(
    textStyle: TextStyle,
    opticalOffset: Dp = 0.dp
): Modifier {
    // Читаем размер из провайдера
    val itemSize = LocalMessageIconSize.current
    val topPadding = calculateFirstLineCenterPadding(textStyle, itemSize, opticalOffset)
    return offset ( y = topPadding)
}