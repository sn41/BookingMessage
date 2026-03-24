### 5. Edge-to-Edge: Современный стандарт экрана

Раньше разработчики вручную красили верхний статус-бар (где часы и батарея) и нижнюю панель навигации в цвета приложения. В современном Android (начиная с Android 15 это включено принудительно) золотым стандартом является **Edge-to-Edge**.

Интерфейс растягивается на весь экран, «подлезая» под полностью прозрачные системные панели. Это делает дизайн монолитным, как в лучших современных iOS и Android приложениях. Управление этими панелями вынесено из `Theme.kt` на уровень самой `Activity`.

---

### Практика: Настройка MainActivity и Scaffold для Edge-to-Edge

Чтобы реализовать современный интерфейс из 5 пункта, вам понадобятся два шага.

**Шаг 1. Включаем Edge-to-Edge в `MainActivity`**
Вызовите функцию `enableEdgeToEdge()` до того, как начнете отрисовывать UI.

```kotlin
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Магия здесь: делает панели прозрачными и растягивает UI на весь экран
        enableEdgeToEdge()
        
        setContent {
            AppTheme { // Ваша обновленная тема
                MainScreen()
            }
        }
    }
}
```

**Шаг 2. Защищаем контент с помощью `Scaffold`**
Поскольку экран теперь растянут от края до края устройства, ваш текст может «залезть» под вырез фронтальной камеры или перекрыться системной полоской навигации внизу.

Чтобы этого не произошло, мы используем `Scaffold`. Он автоматически вычисляет размер системных панелей и передает безопасные отступы (`innerPadding`).

```kotlin
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier

@Composable
fun MainScreen() {
    // Scaffold — это строительный лес вашего экрана
    Scaffold(
        modifier = Modifier.fillMaxSize()
        // Здесь также можно добавить topBar = { ... }, bottomBar = { ... },
        // и Scaffold сам раздвинет контент между ними!
    ) { innerPadding ->
        
        // Обязательно применяем innerPadding к главному контейнеру контента
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), // <-- Защищает от наезда на статус-бар и кнопки навигации
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Привет, современный Android!")
        }
        
    }
}
```

## Пример - поведение Top App Bar при скроллинге

В Material Design 3 верхняя панель стала намного умнее. Она больше не отбрасывает жесткую тень.
Вместо этого она использует механизм **Tonal Elevation** (или новые Surface Containers):
когда вы начинаете прокручивать контент, и он «заезжает» под панель, она плавно меняет свой цвет, чтобы визуально отделиться от текста.

Давайте разберем, как сделать такую панель в Compose на примере вашего приложения.

### 3 типа поведения при скролле (Scroll Behaviors)

В Compose M3 есть три готовых сценария того, как панель реагирует на прокрутку списка пользователем:

1. **`pinnedScrollBehavior` (Закрепленная):** Панель всегда остается на экране. При скролле вниз она просто слегка меняет цвет фона, чтобы контент под ней не сливался с заголовком.
2. **`enterAlwaysScrollBehavior` (Плавающая):** Как только пользователь скроллит вниз (читает контент), панель полностью уезжает вверх и прячется, освобождая максимум места на экране. Чуть только пользователь дернет палец вверх — панель сразу же выезжает обратно.
3. **`exitUntilCollapsedScrollBehavior` (Сжимающаяся):** Используется для больших панелей (`LargeTopAppBar` или `MediumTopAppBar`). При скролле вниз огромный заголовок плавно уменьшается и переезжает в стандартную верхнюю строку, а сама панель остается закрепленной.

---

### Практика: Пишем умный экран с плавающей панелью

Самая частая ошибка новичков — они задают поведение для панели, но забывают связать его со списком контента.
Для этого нужен специальный модификатор `nestedScroll`.

Вот  пример экрана с использованием `enterAlwaysScrollBehavior` (плавающая панель, как в браузере или приложении Booking):

```kotlin
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.FavoriteBorder
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.input.nestedscroll.nestedScroll
//import androidx.compose.ui.unit.dp

// В M3 API для TopAppBar всё ещё требует этой аннотации в некоторых версиях библиотеки
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingSearchScreen() {
    
    // 1. Создаем "контроллер" поведения скролла
    // rememberTopAppBarState() запоминает, насколько панель сейчас сдвинута
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    // 2. Scaffold — каркас нашего экрана
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            // 3. КРИТИЧЕСКИ ВАЖНО: Связываем скролл экрана с нашей панелью!
            // Без этой строчки панель не узнает, что пользователь крутит список вниз.
            .nestedScroll(scrollBehavior.nestedScrollConnection),
            
        topBar = {
            // 4. Сама верхняя панель
            TopAppBar(
                title = { 
                    Text("Выберите отель") 
                },
                navigationIcon = {
                    IconButton(onClick = { /* Возврат назад */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    // Иконки действий справа
                    IconButton(onClick = { /* Добавить в избранное */ }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Избранное")
                    }
                },
                // 5. Передаем наш контроллер в саму панель
                scrollBehavior = scrollBehavior,
                
                // 6. Настраиваем цвета (Опционально)
                colors = TopAppBarDefaults.topAppBarColors(
                    // Цвет панели в состоянии покоя (когда список в самом верху)
                    containerColor = MaterialTheme.colorScheme.surface,
                    // Цвет панели, когда под нее заехал контент (становится чуть темнее/насыщеннее)
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            )
        }
    ) { innerPadding ->
        
        // 7. Наш контент (например, список отелей или наши карточки Message)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                // Обязательно применяем отступ от Scaffold, 
                // чтобы первый элемент списка не спрятался ПОД панелью!
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp), // Внутренние отступы списка
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Генерируем 20 заглушек для демонстрации скролла
            items(20) { index ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Отель $index", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
```

### Разбор магии (Как это работает)

* **`nestedScrollConnection`**: Когда пользователь свайпает по `LazyColumn`, список сначала «спрашивает» у `Scaffold`: *"Эй, тут скролл на 50 пикселей вверх, тебе нужно на это отреагировать?"*. Scaffold передает это событие в `scrollBehavior`, панель послушно уезжает вверх, а остаток скролла (если он есть) уже прокручивает сам список. Это называется вложенным скроллом (Nested Scroll).
* **Смена цвета**: Благодаря параметру `scrolledContainerColor` вам не нужно писать логику для смены цвета. Как только первый пиксель карточки с отелем заедет под `TopAppBar`, панель плавно перекрасится из `surface` в `surfaceContainerHigh`, создавая элегантный эффект отслоения от фона.
* **`innerPadding`**: Поскольку панель плавающая, её высота может меняться (от 64.dp до 0.dp). Но `Scaffold` всегда передает в `innerPadding` максимальную высоту панели. Мы применяем этот отступ к `LazyColumn`, чтобы наши карточки начинались ровно под заголовком, а не наслаивались на него.


## LargeTopAppBar
**`LargeTopAppBar`** — это когда заголовок экрана огромный (как в настройках Android или приложении Телефон), но при скролле он сжимается в аккуратную маленькую строку

Он придает приложению премиальный вид и отлично подходит для главных экранов (как в системных настройках, приложении «Контакты» или календаре).

Суть его работы в том, что в состоянии покоя заголовок занимает много места и написан крупным шрифтом (`headlineMedium`).
Но как только пользователь начинает скроллить вниз, огромный заголовок плавно переезжает наверх, уменьшается до размера `titleLarge` и превращается в обычную компактную панель.

Для этого мы используем поведение **`exitUntilCollapsedScrollBehavior`** (скрываться, пока не сожмется).

### Практика: Экран с LargeTopAppBar

Код очень похож на предыдущий пример, но с парой важных отличий в вызове компонентов.

```kotlin
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Menu
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.input.nestedscroll.nestedScroll
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsLargeScreen() {
    
    // 1. Создаем контроллер с поведением "Сжиматься при скролле"
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState()
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            // 2. Обязательно связываем скролл экрана с панелью
            .nestedScroll(scrollBehavior.nestedScrollConnection),
            
        topBar = {
            // 3. Используем именно LargeTopAppBar (или MediumTopAppBar)
            LargeTopAppBar(
                title = { 
                    Text(
                        text = "Настройки приложения",
                        // Защита от слишком длинных заголовков: обрезаем многоточием
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis 
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { /* Открыть боковое меню (Drawer) */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Меню")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Поиск по настройкам */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Поиск")
                    }
                },
                scrollBehavior = scrollBehavior, // 4. Подключаем контроллер
                
                // 5. Настройка цветов
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    // Цвет в развернутом (большом) состоянии
                    containerColor = MaterialTheme.colorScheme.background,
                    // Цвет в свернутом (маленьком) состоянии
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    // Можно также настроить цвет самого текста:
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        
        // 6. Контент экрана
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                // Отступ Scaffold динамически меняется при скролле от ~152.dp до ~64.dp
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(25) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Настройка ${index + 1}", 
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
```

### В чем главная магия этого компонента?

1. **Автоматическая типографика:** Вам не нужно писать анимации для уменьшения размера шрифта. Compose сам плавно интерполирует (рассчитывает промежуточные значения) стиль текста от крупного заголовка к мелкому, пока вы ведете пальцем по экрану.
2. **Безопасность длинных текстов (`maxLines = 1`)**: В развернутом состоянии большой заголовок может занимать две строки, но когда он сжимается в верхнюю панель, ему доступна только одна строка. Параметр `overflow = TextOverflow.Ellipsis` гарантирует, что если текст не влезет в сжатом виде, он аккуратно обрежется многоточием, а не сломает верстку иконок справа.
3. **MediumTopAppBar:** Если `Large` кажется вам слишком гигантским (он занимает около 152 dp в высоту), вы можете просто заменить слово `LargeTopAppBar` на `MediumTopAppBar` в коде (и цвета на `mediumTopAppBarColors`). Поведение скролла останется тем же, но высота в развернутом виде будет более скромной (около 112 dp).

***
***
***