package kz.misal.message.compose_util


import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ripple
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/*
Мы хотим заменить сложный модификатор на самописный - кастомный.

 Icon(
                        painter = painterResource(id = R.drawable.union),
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(13.dp)

                            // вот этот
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(
                                    bounded = false,
                                    radius = 16.dp,
                                ),
                                onClick = onClose
                            )
                            //
                    )

    Когда у вас есть сложный модификатор, который требует
    запоминания состояния (например, remember { MutableInteractionSource() })
    или вызова Composable-функций (как ripple()),
    вы не можете просто написать обычную функцию-расширение.

    Для этого в Compose существует специальный блок
    Modifier.composed { ... }.
    Он позволяет использовать @Composable код внутри кастомного модификатора.
 */


/**
 * Кастомный модификатор для иконок с аккуратным круглым ripple-эффектом.
 * * @param onClick Действие при клике
 * @param rippleRadius Радиус визуального отклика (по умолчанию 16.dp)
 * @param bounded Ограничивать ли эффект границами элемента (по умолчанию false)
 */
fun Modifier.iconClickable(
    rippleRadius: Dp = 16.dp,
    bounded: Boolean = false,
    onClick: () -> Unit
): Modifier =
    composed {              // <<<<<- Ключевое слово для stateful модификаторов!
        this.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(
                bounded = bounded,
                radius = rippleRadius
            ),
            onClick = onClick
        )
    }


/*
    При быстром нажатии на крестик пользователи случайно вызывают событие onClose дважды.
    В кастомный модификатор очень легко добавить таймер,
    который будет игнорировать повторные клики в течение,
    например, 300 миллисекунд.

    Для нашего компонента Message, это не требуется, поскольку он немедленно закроется от
    первого же нажатия. Но мы пишем универсальный компонент.
*/

/**
 * Кастомный модификатор клика для иконок с красивым ripple-эффектом
 * и встроенной защитой от случайных двойных нажатий (Debounce).
 *
 * @param debounceTime Время блокировки повторных кликов в миллисекундах (по умолчанию 300мс)
 * @param rippleRadius Радиус визуального отклика (по умолчанию 16.dp)
 * @param bounded Ограничивать ли эффект границами элемента (по умолчанию false)
 * @param onClick Действие при клике
 */
fun Modifier.customClickable(
    debounceTime: Long = 300L, // Время блокировки нажатия с момента последнего слика
    rippleRadius: Dp = 16.dp,
    bounded: Boolean = false,
    onClick: () -> Unit
): Modifier = composed {
    // Запоминаем время последнего успешного клика
    var lastClickTime by remember { mutableLongStateOf(0L) }

    this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(
            bounded = bounded,
            radius = rippleRadius
        ),
        onClick = {
            // Таймер для игнорирования повторных кликов.
            // Получаем текущее время
            val currentTime = System.currentTimeMillis()
            // Проверяем, прошло ли достаточно времени с прошлого клика
            if (currentTime - lastClickTime >= debounceTime) {
                lastClickTime = currentTime // Обновляем время.
                // Выполняем реальное действие
                onClick()
            }
        }
    )
}