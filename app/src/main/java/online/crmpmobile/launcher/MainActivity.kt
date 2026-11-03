package online.crmpmobile.launcher

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.SoundEffectConstants
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Night = Color(0xFF090D12)
private val GlassTop = Color(0xB82D343D)
private val GlassBottom = Color(0xA9161C23)
private val GlassBorder = Color(0x54F4F5F7)
private val Panel = Color(0xC4141A21)
private val PanelRaised = Color(0xD01B222A)
private val Accent = Color(0xFFDA244B)
private val AccentDeep = Color(0xFF941B36)
private val Ink = Color(0xFFF1F2F4)
private val Muted = Color(0xFFACB3BD)
private val Quiet = Color(0xFF7E8793)
private val DividerColor = Color(0x30F0F2F5)
private const val PREFS_NAME = "crmp_launcher_preferences"
private const val SUPPORT_URL = "https://forum.crmp-mobile.online/"

private enum class Destination {
    Play, Servers, Settings
}

private enum class SettingsCategory(
    val title: String,
    val icon: ImageVector
) {
    Appearance("Оформление", Icons.Default.Palette),
    Personal("Личные", Icons.Default.Person),
    Sound("Звук", Icons.Default.VolumeUp),
    Graphics("Графика", Icons.Default.Tune),
    Controls("Управление", Icons.Default.Gamepad),
    Security("Безопасность", Icons.Default.Security)
}

class MainActivity : ComponentActivity() {
    @Suppress("DEPRECATION")
    private fun enterImmersiveMode() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterImmersiveMode()
        setContent { LauncherApp() }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) enterImmersiveMode()
    }
}

@Composable
private fun LauncherApp() {
    val context = LocalContext.current
    val preferences = remember {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var destination by remember { mutableStateOf(Destination.Play) }
    var settingsCategory by remember { mutableStateOf(SettingsCategory.Appearance) }
    var selectedServer by remember {
        mutableStateOf(preferences.getString("selected_server", "Москва") ?: "Москва")
    }
    var rememberServer by remember {
        mutableStateOf(preferences.getBoolean("remember_server", true))
    }
    var showWallpaper by remember {
        mutableStateOf(preferences.getBoolean("show_wallpaper", true))
    }
    var backgroundDim by remember {
        mutableFloatStateOf(preferences.getFloat("background_dim", 0.48f))
    }
    var clickSounds by remember {
        mutableStateOf(preferences.getBoolean("click_sounds", true))
    }
    var haptics by remember {
        mutableStateOf(preferences.getBoolean("haptics", true))
    }
    var animations by remember {
        mutableStateOf(preferences.getBoolean("animations", true))
    }
    var showClientInfo by remember { mutableStateOf(false) }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Accent,
            background = Night,
            surface = Panel,
            onBackground = Ink,
            onSurface = Ink
        )
    ) {
        Box(Modifier.fillMaxSize().background(Night)) {
            if (showWallpaper) {
                Image(
                    painter = painterResource(id = R.drawable.launcher_wallpaper),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0x99090E14),
                            Color(0x42090E14),
                            Color(0x6E090D12),
                            Color(0xD9090D12)
                        )
                    )
                )
            )
            Box(
                Modifier.fillMaxSize().background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0x99080C11),
                            Color.Transparent,
                            Color(0x33080C11)
                        )
                    )
                )
            )
            Box(
                Modifier.fillMaxSize().background(
                    Color(0xFF070B10).copy(alpha = backgroundDim.coerceIn(0.15f, 0.82f))
                )
            )

            when (destination) {
                Destination.Play -> PlayScreen(
                    selectedServer = selectedServer,
                    onOpenServers = { destination = Destination.Servers },
                    onPlay = { showClientInfo = true },
                    onSupport = {
                        openSupport(context)
                    },
                    clickSounds = clickSounds,
                    haptics = haptics
                )

                Destination.Servers -> ServersScreen(
                    selectedServer = selectedServer,
                    onServerSelected = { server ->
                        selectedServer = server
                        if (rememberServer) {
                            preferences.edit().putString("selected_server", server).apply()
                        }
                    },
                    clickSounds = clickSounds,
                    haptics = haptics
                )

                Destination.Settings -> SettingsScreen(
                    selectedCategory = settingsCategory,
                    onCategorySelected = { settingsCategory = it },
                    rememberServer = rememberServer,
                    onRememberServerChanged = {
                        rememberServer = it
                        preferences.edit().putBoolean("remember_server", it).apply()
                        if (it) preferences.edit().putString("selected_server", selectedServer).apply()
                    },
                    showWallpaper = showWallpaper,
                    onShowWallpaperChanged = {
                        showWallpaper = it
                        preferences.edit().putBoolean("show_wallpaper", it).apply()
                    },
                    backgroundDim = backgroundDim,
                    onBackgroundDimChanged = {
                        backgroundDim = it
                        preferences.edit().putFloat("background_dim", it).apply()
                    },
                    clickSounds = clickSounds,
                    onClickSoundsChanged = {
                        clickSounds = it
                        preferences.edit().putBoolean("click_sounds", it).apply()
                    },
                    haptics = haptics,
                    onHapticsChanged = {
                        haptics = it
                        preferences.edit().putBoolean("haptics", it).apply()
                    },
                    animations = animations,
                    onAnimationsChanged = {
                        animations = it
                        preferences.edit().putBoolean("animations", it).apply()
                    },
                    selectedServerName = selectedServer,
                    onOpenServers = { destination = Destination.Servers }
                )
            }

            GlassNavigation(
                selected = destination,
                onSelected = { destination = it },
                clickSounds = clickSounds,
                haptics = haptics,
                animations = animations
            )
        }

        if (showClientInfo) {
            AlertDialog(
                onDismissRequest = { showClientInfo = false },
                containerColor = Color(0xFF1A2028),
                title = {
                    Text(
                        "Игровой клиент ещё не установлен",
                        color = Ink,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        "Лаунчер пока содержит интерфейс и выбор сервера. Для запуска нужны файлы SA-MP/CRMP, CEF Runtime и протокол подключения. Они ещё не интегрированы, поэтому кнопка не имитирует запуск игры.",
                        color = Muted
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

private fun openSupport(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(SUPPORT_URL))
    runCatching { context.startActivity(intent) }
}

@Composable
private fun GlassNavigation(
    selected: Destination,
    onSelected: (Destination) -> Unit,
    clickSounds: Boolean,
    haptics: Boolean,
    animations: Boolean
) {
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current
    val railModifier = Modifier
        .align(Alignment.CenterStart)
        .padding(start = 22.dp)

    Column(
        modifier = railModifier,
        verticalArrangement = Arrangement.spacedBy(13.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlassNavigationButton(
            icon = Icons.Default.PlayArrow,
            label = "Играть",
            selected = selected == Destination.Play,
            animations = animations
        ) {
            playInteraction(view, haptic, clickSounds, haptics)
            onSelected(Destination.Play)
        }
        GlassNavigationButton(
            icon = Icons.Default.Storage,
            label = "Сервера",
            selected = selected == Destination.Servers,
            animations = animations
        ) {
            playInteraction(view, haptic, clickSounds, haptics)
            onSelected(Destination.Servers)
        }
        GlassNavigationButton(
            icon = Icons.Default.Settings,
            label = "Настройки",
            selected = selected == Destination.Settings,
            animations = animations
        ) {
            playInteraction(view, haptic, clickSounds, haptics)
            onSelected(Destination.Settings)
        }
    }
}

private fun playInteraction(
    view: View,
    haptic: androidx.compose.ui.hapticfeedback.HapticFeedback,
    clickSounds: Boolean,
    haptics: Boolean
) {
    if (clickSounds) view.playSoundEffect(SoundEffectConstants.CLICK)
    if (haptics) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
}

@Composable
private fun GlassNavigationButton(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    animations: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        Modifier
            .size(58.dp)
            .shadow(
                elevation = if (selected) 11.dp else 7.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.34f),
                spotColor = Color.Black.copy(alpha = 0.34f)
            )
            .clip(shape)
            .background(
                if (selected) {
                    Brush.linearGradient(
                        listOf(Color(0xD044252F), Color(0xC01D2028))
                    )
                } else {
                    Brush.linearGradient(listOf(GlassTop, GlassBottom))
                }
            )
            .border(
                width = 1.dp,
                color = if (selected) Accent.copy(alpha = 0.78f) else GlassBorder,
                shape = shape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(0.58f)
                .height(1.dp)
                .background(Color.White.copy(alpha = if (selected) 0.26f else 0.14f))
        )
        if (selected) {
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 1.dp)
                    .width(3.dp)
                    .height(25.dp)
                    .clip(CircleShape)
                    .background(Accent)
            )
        }
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) Color(0xFFFF6683) else Color(0xFFE1E5E9),
            modifier = Modifier.size(25.dp)
        )
    }
}

@Composable
private fun PlayScreen(
    selectedServer: String,
    onOpenServers: () -> Unit,
    onPlay: () -> Unit,
    onSupport: () -> Unit,
    clickSounds: Boolean,
    haptics: Boolean
) {
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current

    Box(Modifier.fillMaxSize()) {
        GlassActionButton(
            icon = Icons.Default.HeadsetMic,
            title = "Поддержка",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 25.dp, end = 30.dp),
            onClick = {
                playInteraction(view, haptic, clickSounds, haptics)
                onSupport()
            }
        )

        ServerSelectionPill(
            server = selectedServer,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 30.dp, bottom = 101.dp),
            onClick = {
                playInteraction(view, haptic, clickSounds, haptics)
                onOpenServers()
            }
        )

        Image(
            painter = painterResource(id = R.drawable.crmp_wordmark),
            contentDescription = "CRMP Mobile",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 25.dp)
                .width(180.dp)
                .height(48.dp),
            contentScale = ContentScale.Fit
        )

        PlayActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 30.dp, bottom = 20.dp),
            onClick = {
                playInteraction(view, haptic, clickSounds, haptics)
                onPlay()
            }
        )
    }
}

@Composable
private fun GlassActionButton(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(15.dp)
    Row(
        modifier
            .height(48.dp)
            .clip(shape)
            .background(Brush.linearGradient(listOf(GlassTop, GlassBottom)))
            .border(1.dp, GlassBorder, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = Ink, modifier = Modifier.size(19.dp))
        Spacer(Modifier.width(9.dp))
        Text(title, color = Ink, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ServerSelectionPill(
    server: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier
            .height(38.dp)
            .clip(shape)
            .background(Color(0xB9141A21))
            .border(1.dp, Color(0x4DFFFFFF), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Public, contentDescription = null, tint = Color(0xFFE5A4B1), modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(8.dp))
        Text(server, color = Ink, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.width(9.dp))
        Icon(Icons.Default.ExpandMore, contentDescription = null, tint = Muted, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun PlayActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(15.dp)
    Box(
        modifier
            .width(174.dp)
            .height(58.dp)
            .shadow(14.dp, shape, ambientColor = Accent.copy(alpha = 0.14f), spotColor = Accent.copy(alpha = 0.14f))
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF04767), Accent, AccentDeep)
                )
            )
            .border(1.dp, Color(0x88FF9AAD), shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(0.78f)
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.34f))
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(23.dp))
            Spacer(Modifier.width(8.dp))
            Text("Играть", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun ServersScreen(
    selectedServer: String,
    onServerSelected: (String) -> Unit,
    clickSounds: Boolean,
    haptics: Boolean
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 112.dp, top = 31.dp, end = 32.dp, bottom = 28.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Сервера", color = Ink, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Text("Выбери игровой мир", color = Muted, fontSize = 11.sp)
            }
            Row(
                Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x9C111820))
                    .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFE1A15F)))
                Spacer(Modifier.width(7.dp))
                Text("Статистика онлайна не подключена", color = Muted, fontSize = 9.sp)
            }
        }

        Spacer(Modifier.height(22.dp))

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .widthIn(max = 960.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            ServerRow(
                name = "Москва",
                number = "Сервер 01",
                selected = selectedServer == "Москва",
                onClick = {
                    onServerSelected("Москва")
                },
                clickSounds = clickSounds,
                haptics = haptics
            )
            ServerRow(
                name = "Краснодар",
                number = "Сервер 02",
                selected = selectedServer == "Краснодар",
                onClick = {
                    onServerSelected("Краснодар")
                },
                clickSounds = clickSounds,
                haptics = haptics
            )
        }
    }
}

@Composable
private fun ServerRow(
    name: String,
    number: String,
    selected: Boolean,
    onClick: () -> Unit,
    clickSounds: Boolean,
    haptics: Boolean
) {
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current
    val shape = RoundedCornerShape(15.dp)

    Row(
        Modifier
            .fillMaxWidth()
            .height(86.dp)
            .shadow(6.dp, shape, ambientColor = Color.Black.copy(alpha = 0.22f))
            .clip(shape)
            .background(
                if (selected) {
                    Brush.horizontalGradient(listOf(Color(0xD42A1B24), Color(0xBB171D25)))
                } else {
                    Brush.horizontalGradient(listOf(Color(0xC21B222A), Color(0xB0141A21)))
                }
            )
            .border(
                1.dp,
                if (selected) Accent.copy(alpha = 0.66f) else GlassBorder,
                shape
            )
            .clickable {
                playInteraction(view, haptic, clickSounds, haptics)
                onClick()
            }
            .padding(start = 0.dp, end = 17.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(if (selected) Accent else Color.Transparent)
        )
        Spacer(Modifier.width(18.dp))
        Column(Modifier.weight(1f)) {
            Text(name, color = Ink, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(number, color = Quiet, fontSize = 10.sp)
                Box(Modifier.padding(horizontal = 9.dp).size(3.dp).clip(CircleShape).background(Quiet))
                Text("Онлайн: данные появятся после подключения API", color = Muted, fontSize = 10.sp)
            }
        }
        if (selected) {
            Row(
                Modifier
                    .clip(RoundedCornerShape(9.dp))
                    .background(Color(0x4439A878))
                    .border(1.dp, Color(0x6659C58D), RoundedCornerShape(9.dp))
                    .padding(horizontal = 13.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF8BE0B0), modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text("Выбран", color = Color(0xFFB6E7CA), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
        } else {
            Text("Выбрать", color = Ink, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.width(6.dp))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Muted, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun SettingsScreen(
    selectedCategory: SettingsCategory,
    onCategorySelected: (SettingsCategory) -> Unit,
    rememberServer: Boolean,
    onRememberServerChanged: (Boolean) -> Unit,
    showWallpaper: Boolean,
    onShowWallpaperChanged: (Boolean) -> Unit,
    backgroundDim: Float,
    onBackgroundDimChanged: (Float) -> Unit,
    clickSounds: Boolean,
    onClickSoundsChanged: (Boolean) -> Unit,
    haptics: Boolean,
    onHapticsChanged: (Boolean) -> Unit,
    animations: Boolean,
    onAnimationsChanged: (Boolean) -> Unit,
    selectedServerName: String,
    onOpenServers: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 111.dp, top = 24.dp, end = 30.dp, bottom = 24.dp)
    ) {
        Text("Настройки", color = Ink, fontSize = 23.sp, fontWeight = FontWeight.Bold)
        Text("Параметры лаунчера и игрового клиента", color = Muted, fontSize = 10.sp)
        Spacer(Modifier.height(15.dp))

        Row(
            Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            SettingsCategoryMenu(
                selected = selectedCategory,
                onSelected = onCategorySelected,
                modifier = Modifier.width(188.dp).fillMaxHeight()
            )

            SettingsContentPanel(
                category = selectedCategory,
                modifier = Modifier.weight(1f).fillMaxHeight(),
                rememberServer = rememberServer,
                onRememberServerChanged = onRememberServerChanged,
                showWallpaper = showWallpaper,
                onShowWallpaperChanged = onShowWallpaperChanged,
                backgroundDim = backgroundDim,
                onBackgroundDimChanged = onBackgroundDimChanged,
                clickSounds = clickSounds,
                onClickSoundsChanged = onClickSoundsChanged,
                haptics = haptics,
                onHapticsChanged = onHapticsChanged,
                animations = animations,
                onAnimationsChanged = onAnimationsChanged,
                selectedServerName = selectedServerName,
                onOpenServers = onOpenServers
            )
        }
    }
}

@Composable
private fun SettingsCategoryMenu(
    selected: SettingsCategory,
    onSelected: (SettingsCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        SettingsCategory.entries.forEach { category ->
            val isSelected = category == selected
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(43.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) Color(0xB52D1B25) else Color(0x71141A20))
                    .border(
                        1.dp,
                        if (isSelected) Accent.copy(alpha = 0.5f) else Color.Transparent,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelected(category) }
                    .padding(horizontal = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    category.icon,
                    contentDescription = null,
                    tint = if (isSelected) Color(0xFFFF7892) else Muted,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    category.title,
                    color = if (isSelected) Ink else Color(0xFFD3D7DC),
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
                Spacer(Modifier.weight(1f))
                if (isSelected) {
                    Box(Modifier.size(5.dp).clip(CircleShape).background(Accent))
                }
            }
        }
    }
}

@Composable
private fun SettingsContentPanel(
    category: SettingsCategory,
    modifier: Modifier,
    rememberServer: Boolean,
    onRememberServerChanged: (Boolean) -> Unit,
    showWallpaper: Boolean,
    onShowWallpaperChanged: (Boolean) -> Unit,
    backgroundDim: Float,
    onBackgroundDimChanged: (Float) -> Unit,
    clickSounds: Boolean,
    onClickSoundsChanged: (Boolean) -> Unit,
    haptics: Boolean,
    onHapticsChanged: (Boolean) -> Unit,
    animations: Boolean,
    onAnimationsChanged: (Boolean) -> Unit,
    selectedServerName: String,
    onOpenServers: () -> Unit
) {
    val shape = RoundedCornerShape(15.dp)
    Column(
        modifier
            .clip(shape)
            .background(Brush.linearGradient(listOf(Color(0xD01A2028), Color(0xC111171E))))
            .border(1.dp, GlassBorder, shape)
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(category.icon, contentDescription = null, tint = Color(0xFFFF7892), modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(9.dp))
            Text(category.title, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(DividerColor))
        Spacer(Modifier.height(4.dp))

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            when (category) {
                SettingsCategory.Appearance -> {
                    SettingsToggleRow(
                        "Показывать заставку",
                        "Использовать городское изображение на фоне",
                        showWallpaper,
                        onShowWallpaperChanged
                    )
                    Box(Modifier.fillMaxWidth().height(1.dp).background(DividerColor))
                    Column(Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("Затемнение изображения", color = Ink, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("Настрой яркость фоновой заставки", color = Muted, fontSize = 9.sp)
                            }
                            Text(
                                (backgroundDim * 100).toInt().toString() + "%",
                                color = Color(0xFFE7A0B1),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = backgroundDim,
                            onValueChange = onBackgroundDimChanged,
                            valueRange = 0.15f..0.82f,
                            colors = SliderDefaults.colors(
                                thumbColor = Accent,
                                activeTrackColor = Accent,
                                inactiveTrackColor = Color(0xFF414954)
                            )
                        )
                    }
                }

                SettingsCategory.Personal -> {
                    SettingsToggleRow(
                        "Запоминать сервер",
                        "Открывать лаунчер с последним выбранным миром",
                        rememberServer,
                        onRememberServerChanged
                    )
                    Box(Modifier.fillMaxWidth().height(1.dp).background(DividerColor))
                    SettingsInfoRow(
                        "Текущий сервер",
                        selectedServerName,
                        Icons.Default.Public,
                        actionTitle = "Изменить",
                        onAction = onOpenServers
                    )
                    Box(Modifier.fillMaxWidth().height(1.dp).background(DividerColor))
                    SettingsInfoRow(
                        "Игровой аккаунт",
                        "Не подключён",
                        Icons.Default.Person,
                        detail = "Авторизация появится после подключения серверного API"
                    )
                }

                SettingsCategory.Sound -> {
                    SettingsToggleRow(
                        "Звуки нажатий",
                        "Системный звук при взаимодействии с элементами",
                        clickSounds,
                        onClickSoundsChanged
                    )
                    Box(Modifier.fillMaxWidth().height(1.dp).background(DividerColor))
                    SettingsToggleRow(
                        "Тактильный отклик",
                        "Лёгкая вибрация при нажатии",
                        haptics,
                        onHapticsChanged
                    )
                }

                SettingsCategory.Graphics -> {
                    SettingsToggleRow(
                        "Анимации интерфейса",
                        "Плавное переключение экранов и состояний",
                        animations,
                        onAnimationsChanged
                    )
                    Box(Modifier.fillMaxWidth().height(1.dp).background(DividerColor))
                    SettingsInfoRow(
                        "Заставка",
                        if (showWallpaper) "Городская ночь · 1280 × 720" else "Отключена",
                        Icons.Default.Image
                    )
                    Box(Modifier.fillMaxWidth().height(1.dp).background(DividerColor))
                    SettingsInfoRow(
                        "Игровая графика",
                        "Недоступна до интеграции клиента",
                        Icons.Default.Tune,
                        detail = "Разрешение, эффекты, тени и частота кадров будут управляться самим игровым движком"
                    )
                }

                SettingsCategory.Controls -> {
                    SettingsInfoRow(
                        "Ориентация",
                        "Альбомная",
                        Icons.Default.ScreenRotation,
                        detail = "Лаунчер оптимизирован для горизонтального игрового интерфейса"
                    )
                    Box(Modifier.fillMaxWidth().height(1.dp).background(DividerColor))
                    SettingsInfoRow(
                        "Навигация",
                        "Три иконки слева",
                        Icons.Default.Dashboard,
                        detail = "Играть, сервера и настройки"
                    )
                    Box(Modifier.fillMaxWidth().height(1.dp).background(DividerColor))
                    SettingsInfoRow(
                        "Игровое управление",
                        "Ожидает установки игрового клиента",
                        Icons.Default.Gamepad
                    )
                }

                SettingsCategory.Security -> {
                    SettingsInfoRow(
                        "Статус аккаунта",
                        "Гость",
                        Icons.Default.Security,
                        detail = "Данные авторизации пока не запрашиваются и не сохраняются"
                    )
                    Box(Modifier.fillMaxWidth().height(1.dp).background(DividerColor))
                    SettingsInfoRow(
                        "Защита данных",
                        "Локальные настройки устройства",
                        Icons.Default.Lock,
                        detail = "Выбор сервера и параметры интерфейса хранятся в настройках приложения"
                    )
                    Box(Modifier.fillMaxWidth().height(1.dp).background(DividerColor))
                    SettingsInfoRow(
                        "Привязка аккаунта",
                        "Будет доступна после подключения API",
                        Icons.Default.Link
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current
    Row(
        Modifier.fillMaxWidth().padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, color = Ink, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(3.dp))
            Text(subtitle, color = Muted, fontSize = 9.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        Switch(
            checked = checked,
            onCheckedChange = {
                if (checked.not()) {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                onCheckedChange(it)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Accent,
                uncheckedThumbColor = Color(0xFFB9C0C8),
                uncheckedTrackColor = Color(0xFF444D58),
                uncheckedBorderColor = Color(0xFF65707D)
            )
        )
    }
}

@Composable
private fun SettingsInfoRow(
    title: String,
    value: String,
    icon: ImageVector,
    detail: String? = null,
    actionTitle: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(33.dp).clip(RoundedCornerShape(9.dp)).background(Color(0x3DFFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFFE4A0B0), modifier = Modifier.size(17.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = Ink, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Text(value, color = Muted, fontSize = 9.sp)
            if (detail != null) {
                Spacer(Modifier.height(2.dp))
                Text(detail, color = Quiet, fontSize = 8.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
        if (actionTitle != null && onAction != null) {
            TextButton(onClick = onAction, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                Text(actionTitle, color = Color(0xFFF18AA1), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
