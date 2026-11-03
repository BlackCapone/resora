package online.crmpmobile.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF090B0E)
private val SidebarBg = Color(0xFF0D1014)
private val Panel = Color(0xFF151A20)
private val PanelRaised = Color(0xFF1B222A)
private val PanelSelected = Color(0xFF271720)
private val Accent = Color(0xFFE51D52)
private val AccentSoft = Color(0xFF461B2B)
private val TextMain = Color(0xFFF2F3F5)
private val TextMuted = Color(0xFF89919B)
private val Line = Color(0xFF252C34)
private val Warning = Color(0xFFFFB45D)

private enum class LauncherSection { Play, Servers, Settings }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LauncherApp() }
    }
}

@Composable
private fun LauncherApp() {
    var section by remember { mutableStateOf(LauncherSection.Play) }
    var selectedServer by remember { mutableStateOf("Москва") }
    var showClientInfo by remember { mutableStateOf(false) }
    var interfaceSound by remember { mutableStateOf(true) }
    var vibration by remember { mutableStateOf(false) }
    var animations by remember { mutableStateOf(true) }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Accent,
            background = Bg,
            surface = Panel,
            onBackground = TextMain,
            onSurface = TextMain
        )
    ) {
        Row(Modifier.fillMaxSize().background(Bg)) {
            Sidebar(section = section, onSectionSelected = { section = it })
            Box(Modifier.width(1.dp).fillMaxHeight().background(Line))
            Box(Modifier.weight(1f).fillMaxHeight()) {
                when (section) {
                    LauncherSection.Play -> PlayScreen(
                        selectedServer = selectedServer,
                        onServerSelected = { selectedServer = it },
                        onPlay = { showClientInfo = true },
                        onOpenServers = { section = LauncherSection.Servers }
                    )
                    LauncherSection.Servers -> ServersScreen(
                        selectedServer = selectedServer,
                        onServerSelected = { selectedServer = it }
                    )
                    LauncherSection.Settings -> SettingsScreen(
                        interfaceSound = interfaceSound,
                        onInterfaceSoundChanged = { interfaceSound = it },
                        vibration = vibration,
                        onVibrationChanged = { vibration = it },
                        animations = animations,
                        onAnimationsChanged = { animations = it }
                    )
                }
            }
        }

        if (showClientInfo) {
            AlertDialog(
                onDismissRequest = { showClientInfo = false },
                containerColor = PanelRaised,
                title = { Text("Игровой клиент пока не подключён", fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Интерфейс лаунчера уже готов, но для запуска игры нужны сами файлы SA-MP/CRMP, CEF Runtime и механизм подключения к серверу. Они пока не входят в APK. Поэтому кнопка не имитирует запуск несуществующего клиента."
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showClientInfo = false }) {
                        Text("Понятно", color = Accent)
                    }
                }
            )
        }
    }
}

@Composable
private fun Sidebar(section: LauncherSection, onSectionSelected: (LauncherSection) -> Unit) {
    Column(
        Modifier.width(190.dp).fillMaxHeight().background(SidebarBg).padding(horizontal = 14.dp)
    ) {
        Row(
            Modifier.padding(start = 7.dp, top = 22.dp, bottom = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(39.dp).clip(RoundedCornerShape(11.dp))
                    .background(Brush.linearGradient(listOf(Accent, Color(0xFF9E143B)))),
                contentAlignment = Alignment.Center
            ) {
                Text("C", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text("CRMP", color = TextMain, fontSize = 16.sp, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
                Text("MOBILE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            }
        }

        Text("МЕНЮ", Modifier.padding(start = 11.dp, bottom = 10.dp), color = Color(0xFF66707B), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.7.sp)

        SidebarItem("Играть", "Главная", Icons.Default.PlayArrow, section == LauncherSection.Play) {
            onSectionSelected(LauncherSection.Play)
        }
        Spacer(Modifier.height(6.dp))
        SidebarItem("Сервера", "Выбор игрового мира", Icons.Default.Storage, section == LauncherSection.Servers) {
            onSectionSelected(LauncherSection.Servers)
        }
        Spacer(Modifier.height(6.dp))
        SidebarItem("Настройки", "Параметры приложения", Icons.Default.Settings, section == LauncherSection.Settings) {
            onSectionSelected(LauncherSection.Settings)
        }

        Spacer(Modifier.weight(1f))
        Box(Modifier.fillMaxWidth().height(1.dp).background(Line))
        Spacer(Modifier.height(15.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(34.dp).clip(CircleShape).background(PanelRaised), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, tint = TextMuted, modifier = Modifier.size(19.dp))
            }
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                Text("Гость", color = TextMain, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text("Аккаунт не подключён", color = TextMuted, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Text("CRMP MOBILE  •  v0.1.0", Modifier.padding(start = 5.dp, top = 13.dp, bottom = 17.dp), color = Color(0xFF59616B), fontSize = 8.sp, letterSpacing = 0.7.sp)
    }
}

@Composable
private fun SidebarItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().height(57.dp).clip(RoundedCornerShape(11.dp))
            .background(if (selected) PanelSelected else Color.Transparent)
            .clickable(onClick = onClick).padding(horizontal = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(3.dp).height(27.dp).clip(RoundedCornerShape(4.dp)).background(if (selected) Accent else Color.Transparent))
        Spacer(Modifier.width(10.dp))
        Icon(icon, contentDescription = null, tint = if (selected) Accent else TextMuted, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = if (selected) TextMain else Color(0xFFC0C5CB), fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
            Text(subtitle, color = TextMuted, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun PageHeader(title: String, subtitle: String) {
    Row(Modifier.fillMaxWidth().height(42.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, color = TextMain, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold)
            Text(subtitle, color = TextMuted, fontSize = 10.sp)
        }
        StatusChip()
    }
}

@Composable
private fun StatusChip() {
    Row(
        Modifier.clip(RoundedCornerShape(20.dp)).background(Color(0xFF262016))
            .border(1.dp, Color(0xFF4B3924), RoundedCornerShape(20.dp))
            .padding(horizontal = 11.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(Warning))
        Spacer(Modifier.width(7.dp))
        Text("КЛИЕНТ НЕ УСТАНОВЛЕН", color = Color(0xFFE6B978), fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
    }
}

@Composable
private fun PlayScreen(
    selectedServer: String,
    onServerSelected: (String) -> Unit,
    onPlay: () -> Unit,
    onOpenServers: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(Bg).padding(horizontal = 22.dp, vertical = 15.dp)) {
        PageHeader("Игровой центр", "Всё необходимое для входа в CRMP Mobile")
        Spacer(Modifier.height(12.dp))
        Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(13.dp)) {
            HeroPanel(Modifier.weight(1.55f).fillMaxHeight(), onPlay, onOpenServers)
            SessionPanel(Modifier.weight(0.85f).fillMaxHeight(), selectedServer, onOpenServers)
        }
        Spacer(Modifier.height(11.dp))
        QuickServerRow(selectedServer, onServerSelected)
    }
}

@Composable
private fun HeroPanel(modifier: Modifier, onPlay: () -> Unit, onOpenServers: () -> Unit) {
    val shape = RoundedCornerShape(19.dp)
    Box(
        modifier.clip(shape)
            .background(Brush.linearGradient(listOf(Color(0xFF24212A), Color(0xFF161C25), Color(0xFF101820))))
            .border(1.dp, Color(0xFF34313B), shape)
    ) {
        CityArtwork(Modifier.matchParentSize())
        Column(
            Modifier.fillMaxWidth(0.78f).fillMaxHeight().padding(19.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.width(19.dp).height(2.dp).background(Accent))
                    Spacer(Modifier.width(7.dp))
                    Text("CRMP MOBILE  /  ROLE PLAY", color = Color(0xFFE7A0B2), fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp)
                }
                Spacer(Modifier.height(10.dp))
                Text("ТВОЯ ИСТОРИЯ.\nТВОЙ ГОРОД.", color = TextMain, fontSize = 27.sp, lineHeight = 29.sp, fontWeight = FontWeight.Black, letterSpacing = 0.1.sp)
                Spacer(Modifier.height(5.dp))
                Text("Выбери сервер и возвращайся в игру.", color = Color(0xFFB0B6BF), fontSize = 10.sp, maxLines = 2)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onPlay,
                    modifier = Modifier.height(39.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 19.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.White)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(17.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("ИГРАТЬ", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                }
                Spacer(Modifier.width(12.dp))
                TextButton(onClick = onOpenServers, contentPadding = PaddingValues(horizontal = 3.dp)) {
                    Text("ВЫБРАТЬ СЕРВЕР", color = Color(0xFFE5DDE1), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Spacer(Modifier.width(3.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Accent, modifier = Modifier.size(13.dp))
                }
            }
        }
    }
}

@Composable
private fun CityArtwork(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        drawCircle(Color(0xFFE51D52).copy(alpha = 0.11f), radius = h * 0.62f, center = Offset(w * 0.83f, h * 0.20f))
        drawCircle(Color(0xFFFFA5B9).copy(alpha = 0.12f), radius = h * 0.27f, center = Offset(w * 0.83f, h * 0.20f))

        val buildingHeights = listOf(0.28f, 0.47f, 0.36f, 0.62f, 0.42f, 0.72f, 0.50f, 0.39f, 0.60f, 0.33f)
        val buildingWidth = w / 10.5f
        buildingHeights.forEachIndexed { index, heightFactor ->
            val x = w * 0.43f + index * buildingWidth * 0.72f
            val buildingHeight = h * heightFactor
            drawRect(Color(0xFF090D14).copy(alpha = 0.78f), Offset(x, h - buildingHeight), Size(buildingWidth * 0.73f, buildingHeight))
            for (row in 0..2) {
                drawRect(Color(0xFFFF7194).copy(alpha = 0.17f), Offset(x + buildingWidth * 0.14f, h - buildingHeight + h * (0.07f + row * 0.09f)), Size(buildingWidth * 0.12f, h * 0.025f))
                drawRect(Color(0xFFFF7194).copy(alpha = 0.12f), Offset(x + buildingWidth * 0.42f, h - buildingHeight + h * (0.07f + row * 0.09f)), Size(buildingWidth * 0.12f, h * 0.025f))
            }
        }

        val road = Path().apply {
            moveTo(w * 0.43f, h)
            lineTo(w * 0.65f, h * 0.81f)
            lineTo(w, h * 0.81f)
            lineTo(w, h)
            close()
        }
        drawPath(road, Color(0xFF080B10).copy(alpha = 0.9f))
        drawLine(Color(0xFFE51D52).copy(alpha = 0.42f), Offset(w * 0.66f, h * 0.91f), Offset(w, h * 0.91f), strokeWidth = 2.dp.toPx())
        drawLine(Color.White.copy(alpha = 0.09f), Offset(w * 0.60f, h * 0.98f), Offset(w * 0.78f, h * 0.81f), strokeWidth = 1.dp.toPx())
    }
}

@Composable
private fun SessionPanel(modifier: Modifier, selectedServer: String, onOpenServers: () -> Unit) {
    val serverNumber = if (selectedServer == "Москва") "01" else "02"
    val shape = RoundedCornerShape(19.dp)
    Column(modifier.clip(shape).background(Panel).border(1.dp, Line, shape).padding(16.dp)) {
        Text("ВАШ СЕАНС", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(34.dp).clip(RoundedCornerShape(9.dp)).background(AccentSoft), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Public, contentDescription = null, tint = Color(0xFFFF7999), modifier = Modifier.size(19.dp))
            }
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                Text(selectedServer, color = TextMain, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text("СЕРВЕР  " + serverNumber, color = TextMuted, fontSize = 8.sp, letterSpacing = 1.sp)
            }
        }
        Spacer(Modifier.height(14.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(Line))
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(Warning))
            Spacer(Modifier.width(7.dp))
            Text("Клиент не установлен", color = Color(0xFFE6B978), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.weight(1f))
        Text("ГОТОВНОСТЬ К ИГРЕ", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.9.sp)
        Spacer(Modifier.height(7.dp))
        Box(Modifier.fillMaxWidth().height(4.dp).clip(CircleShape).background(Color(0xFF303740)))
        Spacer(Modifier.height(6.dp))
        Text("Ожидает установки файлов", color = TextMuted, fontSize = 9.sp)
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onOpenServers,
            modifier = Modifier.fillMaxWidth().height(35.dp),
            shape = RoundedCornerShape(7.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF414852)),
            contentPadding = PaddingValues(horizontal = 7.dp, vertical = 0.dp)
        ) {
            Text("СМЕНИТЬ СЕРВЕР", fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.6.sp, color = TextMain)
        }
    }
}

@Composable
private fun QuickServerRow(selectedServer: String, onServerSelected: (String) -> Unit) {
    Row(Modifier.fillMaxWidth().height(54.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.width(142.dp)) {
            Text("БЫСТРЫЙ ВЫБОР", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
            Text("Игровой мир", color = TextMain, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.weight(1f))
        QuickServerButton("Москва", "SERVER 01", selectedServer == "Москва") { onServerSelected("Москва") }
        Spacer(Modifier.width(8.dp))
        QuickServerButton("Краснодар", "SERVER 02", selectedServer == "Краснодар") { onServerSelected("Краснодар") }
    }
}

@Composable
private fun QuickServerButton(name: String, number: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.width(139.dp).height(43.dp).clip(RoundedCornerShape(8.dp))
            .background(if (selected) PanelSelected else Panel)
            .border(1.dp, if (selected) Accent.copy(alpha = 0.65f) else Line, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick).padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(if (selected) Accent else TextMuted))
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(name, color = TextMain, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(number, color = TextMuted, fontSize = 7.sp, letterSpacing = 0.8.sp)
        }
        if (selected) Icon(Icons.Default.Check, contentDescription = null, tint = Accent, modifier = Modifier.size(14.dp))
    }
}

@Composable
private fun ServersScreen(selectedServer: String, onServerSelected: (String) -> Unit) {
    Column(Modifier.fillMaxSize().background(Bg).padding(horizontal = 24.dp, vertical = 17.dp)) {
        PageHeader("Выбор сервера", "Выбери игровой мир, в который хочешь войти")
        Spacer(Modifier.height(18.dp))
        Text("ИГРОВЫЕ МИРЫ", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
        Spacer(Modifier.height(10.dp))
        Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(13.dp)) {
            ServerDetailCard("Москва", "01", selectedServer == "Москва", Modifier.weight(1f)) { onServerSelected("Москва") }
            ServerDetailCard("Краснодар", "02", selectedServer == "Краснодар", Modifier.weight(1f)) { onServerSelected("Краснодар") }
        }
        Spacer(Modifier.height(11.dp))
        Text("Список серверов и их онлайн будут загружаться после подключения серверного API.", color = TextMuted, fontSize = 9.sp)
    }
}

@Composable
private fun ServerDetailCard(name: String, number: String, selected: Boolean, modifier: Modifier, onSelect: () -> Unit) {
    val shape = RoundedCornerShape(17.dp)
    Column(
        modifier.clip(shape).background(if (selected) Color(0xFF1D171E) else Panel)
            .border(1.dp, if (selected) Accent.copy(alpha = 0.7f) else Line, shape)
            .clickable(onClick = onSelect).padding(18.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text("CRMP MOBILE  /  SERVER " + number, color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                Spacer(Modifier.height(8.dp))
                Text(name, color = TextMain, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold)
                Text("Игровой сервер №" + number, color = TextMuted, fontSize = 10.sp)
            }
            Box(Modifier.size(37.dp).clip(RoundedCornerShape(10.dp)).background(if (selected) AccentSoft else PanelRaised), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Public, contentDescription = null, tint = if (selected) Color(0xFFFF7999) else TextMuted, modifier = Modifier.size(20.dp))
            }
        }
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(if (selected) Accent else TextMuted))
                Spacer(Modifier.width(7.dp))
                Text(if (selected) "ВЫБРАН ДЛЯ ВХОДА" else "ДОСТУПЕН ДЛЯ ВЫБОРА", color = if (selected) Color(0xFFE98CA5) else TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.7.sp)
            }
            Spacer(Modifier.height(10.dp))
            if (selected) {
                Button(
                    onClick = onSelect,
                    modifier = Modifier.fillMaxWidth().height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("ВЫБРАН", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.8.sp)
                }
            } else {
                OutlinedButton(
                    onClick = onSelect,
                    modifier = Modifier.fillMaxWidth().height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF49515B))
                ) {
                    Text("ВЫБРАТЬ СЕРВЕР", color = TextMain, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    interfaceSound: Boolean,
    onInterfaceSoundChanged: (Boolean) -> Unit,
    vibration: Boolean,
    onVibrationChanged: (Boolean) -> Unit,
    animations: Boolean,
    onAnimationsChanged: (Boolean) -> Unit
) {
    Column(Modifier.fillMaxSize().background(Bg).padding(horizontal = 24.dp, vertical = 17.dp)) {
        PageHeader("Настройки", "Настрой лаунчер под себя")
        Spacer(Modifier.height(18.dp))
        Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(13.dp)) {
            SettingsCard(Modifier.weight(1.1f).fillMaxHeight(), "ПОВЕДЕНИЕ ПРИЛОЖЕНИЯ") {
                SettingToggleRow("Звук интерфейса", "Звуки нажатий и действий", interfaceSound, onInterfaceSoundChanged)
                Box(Modifier.fillMaxWidth().height(1.dp).background(Line))
                SettingToggleRow("Вибрация", "Тактильный отклик", vibration, onVibrationChanged)
                Box(Modifier.fillMaxWidth().height(1.dp).background(Line))
                SettingToggleRow("Анимации", "Плавные переходы интерфейса", animations, onAnimationsChanged)
            }
            SettingsCard(Modifier.weight(0.9f).fillMaxHeight(), "КОМПОНЕНТЫ КЛИЕНТА") {
                ComponentStatusRow("SA-MP / CRMP", "Игровой модуль")
                ComponentStatusRow("CEF Runtime", "Веб-интерфейс игры")
                ComponentStatusRow("Игровые ресурсы", "Файлы выбранного сервера")
                ComponentStatusRow("Обновления", "Загрузчик и проверка файлов")
            }
        }
        Spacer(Modifier.height(10.dp))
        Text("Версия лаунчера 0.1.0  •  Некоторые функции станут доступны после подключения клиента и API.", color = TextMuted, fontSize = 9.sp)
    }
}

@Composable
private fun SettingsCard(modifier: Modifier, title: String, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Column(modifier.clip(shape).background(Panel).border(1.dp, Line, shape).padding(horizontal = 16.dp, vertical = 14.dp)) {
        Text(title, color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp)
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun SettingToggleRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, color = TextMain, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = TextMuted, fontSize = 9.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.height(30.dp),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Accent,
                uncheckedThumbColor = Color(0xFFB0B7C0),
                uncheckedTrackColor = Color(0xFF414951)
            )
        )
    }
}

@Composable
private fun ComponentStatusRow(title: String, subtitle: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(29.dp).clip(RoundedCornerShape(8.dp)).background(PanelRaised), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Inventory2, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = TextMain, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = TextMuted, fontSize = 8.sp)
        }
        Text("НЕТ", color = Color(0xFFD99A69), fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
    }
}
