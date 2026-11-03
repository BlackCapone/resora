package online.crmpmobile.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF090B10)
private val Panel = Color(0xFF141821)
private val Panel2 = Color(0xFF1B2230)
private val Accent = Color(0xFF6C8CFF)
private val Muted = Color(0xFF929BAE)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LauncherApp() }
    }
}

@Composable
private fun LauncherApp() {
    var tab by remember { mutableStateOf(0) }
    var server by remember { mutableStateOf("Москва") }
    var showClientInfo by remember { mutableStateOf(false) }
    MaterialTheme(colorScheme = darkColorScheme(background = Bg, surface = Panel, primary = Accent, onBackground = Color.White, onSurface = Color.White)) {
        Scaffold(containerColor = Bg, bottomBar = {
            NavigationBar(containerColor = Color(0xFF10141C), contentColor = Color.White) {
                NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Главная") })
                NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Icon(Icons.Default.CloudDownload, null) }, label = { Text("Файлы") })
                NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Профиль") })
            }
        }) { padding ->
            when (tab) {
                0 -> HomeScreen(server, { server = it }, { showClientInfo = true }, padding)
                1 -> FilesScreen(padding, { showClientInfo = true })
                else -> ProfileScreen(padding)
            }
        }
        if (showClientInfo) AlertDialog(onDismissRequest = { showClientInfo = false }, containerColor = Panel2,
            title = { Text("Игровой клиент не подключён") },
            text = { Text("Это рабочая основа лаунчера. Для запуска игры нужны легально распространяемые файлы SA-MP/CRMP-клиента и сведения о способе их интеграции. Сейчас APK содержит интерфейс лаунчера, но не игровой движок.") },
            confirmButton = { TextButton(onClick = { showClientInfo = false }) { Text("Понятно") } })
    }
}

@Composable
private fun HomeScreen(server: String, onServer: (String) -> Unit, onStart: () -> Unit, padding: PaddingValues) {
    Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).clip(RoundedCornerShape(14.dp)).background(Accent), contentAlignment = Alignment.Center) {
                Text("C", fontSize = 25.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("CRMP MOBILE", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp)
                Text("ОФИЦИАЛЬНЫЙ ЛАУНЧЕР", fontSize = 9.sp, color = Muted, letterSpacing = 1.3.sp)
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {}) { Icon(Icons.Default.NotificationsNone, "Уведомления", tint = Muted) }
        }
        Spacer(Modifier.height(28.dp))
        Text("ДОБРО ПОЖАЛОВАТЬ", color = Accent, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Text("Твоя игра.", fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
        Text("Твои правила.", fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = Muted)
        Spacer(Modifier.height(22.dp))
        Box(Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF263B72), Color(0xFF171E31), Color(0xFF11151E))))
            .border(1.dp, Color.White.copy(alpha = .08f), RoundedCornerShape(24.dp))) {
            Box(Modifier.align(Alignment.TopEnd).padding(22.dp).size(115.dp).clip(CircleShape).background(Accent.copy(alpha = .13f)))
            Column(Modifier.align(Alignment.BottomStart).padding(22.dp)) {
                Text("НОВЫЙ СЕЗОН", color = Color(0xFFB8C7FF), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                Spacer(Modifier.height(7.dp))
                Text("Начни свою\nисторию", fontSize = 27.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(24.dp))
        SectionTitle("Выбор сервера", "Выбери игровой мир")
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ServerCard("Москва", server == "Москва", { onServer("Москва") }, Modifier.weight(1f))
            ServerCard("Краснодар", server == "Краснодар", { onServer("Краснодар") }, Modifier.weight(1f))
        }
        Spacer(Modifier.height(22.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("ГОТОВНОСТЬ К ИГРЕ", fontSize = 10.sp, color = Muted, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                Spacer(Modifier.height(5.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(7.dp).clip(CircleShape).background(Color(0xFFFFB454)))
                    Spacer(Modifier.width(7.dp))
                    Text("Игровой клиент не установлен", fontSize = 12.sp, color = Color(0xFFFFC477))
                }
            }
            Text("v0.1.0", color = Muted, fontSize = 11.sp)
        }
        Spacer(Modifier.height(14.dp))
        Button(onClick = onStart, modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(17.dp), colors = ButtonDefaults.buttonColors(containerColor = Accent)) {
            Icon(Icons.Default.PlayArrow, null)
            Spacer(Modifier.width(8.dp))
            Text("ПОДКЛЮЧИТЬ КЛИЕНТ", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
        }
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Shield, null, tint = Muted, modifier = Modifier.size(15.dp))
            Spacer(Modifier.width(7.dp))
            Text("Безопасное подключение • Android 8.0+", color = Muted, fontSize = 10.sp)
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ServerCard(name: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(17.dp)).background(if (selected) Color(0xFF202C4B) else Panel)
        .border(1.dp, if (selected) Accent else Color.White.copy(alpha = .07f), RoundedCornerShape(17.dp))
        .clickable(onClick = onClick).padding(15.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Public, null, tint = if (selected) Accent else Muted, modifier = Modifier.size(18.dp))
            Spacer(Modifier.weight(1f))
            if (selected) Icon(Icons.Default.CheckCircle, null, tint = Accent, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.height(15.dp))
        Text(name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text("Сервер " + if (name == "Москва") "01" else "02", color = Muted, fontSize = 11.sp)
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column { Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold); Text(subtitle, color = Muted, fontSize = 12.sp) }
}

@Composable
private fun FilesScreen(padding: PaddingValues, onInfo: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
        Text("Игровые файлы", fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
        Text("Управление компонентами клиента", color = Muted, fontSize = 13.sp)
        Spacer(Modifier.height(22.dp))
        listOf("Игровой движок", "SA-MP / CRMP модуль", "CEF-компонент", "Игровые ресурсы").forEach { item ->
            Row(Modifier.fillMaxWidth().padding(bottom = 10.dp).clip(RoundedCornerShape(16.dp)).background(Panel).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Inventory2, null, tint = Accent)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(item, fontWeight = FontWeight.SemiBold)
                    Text("Ожидает подключения исходных файлов", color = Muted, fontSize = 11.sp)
                }
                Icon(Icons.Default.Lock, null, tint = Muted, modifier = Modifier.size(17.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onInfo, modifier = Modifier.fillMaxWidth()) { Text("Подробнее о компонентах") }
    }
}

@Composable
private fun ProfileScreen(padding: PaddingValues) {
    Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
        Text("Профиль", fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
        Text("Управление игровым аккаунтом", color = Muted, fontSize = 13.sp)
        Spacer(Modifier.height(24.dp))
        Box(Modifier.size(76.dp).clip(CircleShape).background(Panel2), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Person, null, tint = Accent, modifier = Modifier.size(38.dp))
        }
        Spacer(Modifier.height(14.dp))
        Text("Гость", fontSize = 21.sp, fontWeight = FontWeight.Bold)
        Text("Аккаунт ещё не подключён", color = Muted, fontSize = 12.sp)
        Spacer(Modifier.height(22.dp))
        OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth(), enabled = false) {
            Icon(Icons.Default.Login, null)
            Spacer(Modifier.width(8.dp))
            Text("Авторизация будет добавлена позже")
        }
    }
}
