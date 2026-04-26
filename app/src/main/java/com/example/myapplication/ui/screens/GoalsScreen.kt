package com.example.myapplication.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

// --- Professional Data Model ---
data class BadgeInfo(
    val title: String,
    val icon: ImageVector,
    val meaning: String,
    val significance: String,
    val co2Saved: Double,
    val energySaved: Double,
    val iconColor: Color = Color(0xFF4CAF50)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen() {
    var isDarkMode by remember { mutableStateOf(false) }
    var selectedBadge by remember { mutableStateOf<BadgeInfo?>(null) }
    var sharedBadge by remember { mutableStateOf<BadgeInfo?>(null) }
    var selectedTab by remember { mutableStateOf("Goals") }

    val badgesData = remember {
        listOf(
            BadgeInfo("First Steps", Icons.Default.Spa, "Initial commitment to sustainability.", "Laying the foundation for a sustainable lifestyle.", 1.2, 5.0, Color(0xFFAED581)),
            BadgeInfo("Green Week", Icons.Default.Eco, "7 consecutive days of eco-conscious habits.", "Consistent small habits lead to significant long-term impact.", 8.5, 32.0, Color(0xFF81C784)),
            BadgeInfo("Earth Hero", Icons.Default.Public, "Major milestone in energy reduction.", "Reducing consumption directly lowers reliance on fossil fuels.", 25.0, 110.5, Color(0xFF64B5F6)),
            BadgeInfo("Power Saver", Icons.Default.Bolt, "Reduced monthly energy usage by 20%.", "Lower demand helps stabilize local power grids.", 15.2, 65.0, Color(0xFFFFD54F)),
            BadgeInfo("Active Streak", Icons.Default.Timeline, "30 days of consistent eco-actions.", "Environmental protection requires dedicated, long-term discipline.", 42.0, 180.0, Color(0xFFFF8A65)),
            BadgeInfo("Eco Warrior", Icons.Default.WorkspacePremium, "Reached the top 5% of community savers.", "You are a recognized leader in the local sustainability movement.", 60.5, 250.0, Color(0xFFBA68C8))
        )
    }

    MaterialTheme(
        colorScheme = if (isDarkMode) darkColorScheme(
            primary = Color(0xFF81C784),
            background = Color(0xFF0F110F),
            surface = Color(0xFF1A1D1A)
        ) else lightColorScheme(
            primary = Color(0xFF1B5E20),
            background = Color(0xFFF1F4F1),
            surface = Color(0xFFFFFFFF)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    Surface(
                        shadowElevation = 2.dp,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Eco, 
                                    contentDescription = null, 
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    "EcoStep", 
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            
                            IconButton(
                                onClick = { isDarkMode = !isDarkMode },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                            ) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "Theme",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                },
                bottomBar = {
                    Surface(
                        shadowElevation = 8.dp,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val navItems = listOf(
                                "Home" to Icons.Outlined.Home,
                                "Devices" to Icons.Outlined.Memory,
                                "Analytics" to Icons.Outlined.BarChart,
                                "Goals" to Icons.Default.EmojiEvents,
                                "Tips" to Icons.Outlined.Lightbulb
                            )

                            navItems.forEach { (label, icon) ->
                                val isSelected = selectedTab == label
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { selectedTab = label },
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(28.dp)
                                            .height(3.dp)
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                RoundedCornerShape(2.dp)
                                            )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Icon(
                                        imageVector = if (isSelected) {
                                            when(label) {
                                                "Home" -> Icons.Filled.Home
                                                "Devices" -> Icons.Filled.Memory
                                                "Analytics" -> Icons.Filled.BarChart
                                                "Goals" -> Icons.Filled.EmojiEvents
                                                "Tips" -> Icons.Filled.Lightbulb
                                                else -> icon
                                            }
                                        } else icon,
                                        contentDescription = label,
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(if (selectedBadge != null || sharedBadge != null) 12.dp else 0.dp),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                "Performance Overview", 
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Track your sustainability milestones.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(20.dp))
                            CarbonCreditsCard()
                            Spacer(modifier = Modifier.height(14.dp))
                            MonthlyGoalCard()
                            Spacer(modifier = Modifier.height(14.dp))
                            EarthChallengeCard()

                            Spacer(modifier = Modifier.height(32.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MilitaryTech, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Achievements", 
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            BadgeGrid(
                                badges = badgesData,
                                onBadgeClick = { badge -> selectedBadge = badge }
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }

                    BadgeDetailModal(
                        badge = selectedBadge,
                        onDismiss = { selectedBadge = null },
                        onShareClick = { badge ->
                            sharedBadge = badge
                            selectedBadge = null
                        }
                    )
                }
            }

            // Move SharePreview out of Dialog to ensure it is in the same view hierarchy for capture
            SharePreviewOverlay(
                badge = sharedBadge,
                onDismiss = { sharedBadge = null }
            )
        }
    }
}

@Composable
fun SharePreviewOverlay(badge: BadgeInfo?, onDismiss: () -> Unit) {
    AnimatedVisibility(
        visible = badge != null,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        if (badge != null) {
            val context = LocalContext.current
            val view = LocalView.current
            val scope = rememberCoroutineScope()
            var caption by remember { mutableStateOf("") }
            var isCapturing by remember { mutableStateOf(false) }
            var captureRect by remember { mutableStateOf<androidx.compose.ui.geometry.Rect?>(null) }

            // Opaque background to hide the main screen completely during capture
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header Actions (EXCLUDED from capture area)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                        Text(
                            "Share Achievement",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(48.dp))
                    }

                    // Content Area (This specific box will be captured)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                            .onGloballyPositioned { coordinates ->
                                captureRect = coordinates.boundsInWindow()
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Badge Visual
                        Box(contentAlignment = Alignment.Center) {
                            Surface(
                                shape = CircleShape,
                                color = badge.iconColor.copy(alpha = 0.15f),
                                modifier = Modifier.size(160.dp)
                            ) {}
                            Icon(
                                imageVector = badge.icon,
                                contentDescription = null,
                                tint = badge.iconColor,
                                modifier = Modifier.size(80.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = badge.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = badge.meaning,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(48.dp))

                        // Highlight Stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatHighlight(
                                value = "${String.format("%.1f", badge.co2Saved)} kg",
                                label = "CO₂ saved"
                            )
                            StatHighlight(
                                value = "${String.format("%.1f", badge.energySaved)} kWh",
                                label = "energy saved"
                            )
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        // Editable Caption
                        OutlinedTextField(
                            value = caption,
                            onValueChange = { caption = it },
                            placeholder = { Text("Write your caption here...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            textStyle = MaterialTheme.typography.bodyMedium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            "#EcoStep #Sustainability #ClimateAction #GreenLiving",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(40.dp))
                        
                        Text(
                            "Powered by EcoStep",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 2.sp
                        )
                    }

                    // Share Button (EXCLUDED from capture area)
                    Box(modifier = Modifier.padding(24.dp)) {
                        Button(
                            onClick = {
                                scope.launch {
                                    isCapturing = true
                                    delay(300) // Ensure UI is rendered
                                    captureAndShare(context, view, captureRect)
                                    isCapturing = false
                                    onDismiss()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            enabled = !isCapturing
                        ) {
                            if (isCapturing) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Icon(Icons.Default.Share, contentDescription = null)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Share Achievement", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Logic to capture ONLY the area defined by captureRect
private fun captureAndShare(context: Context, view: android.view.View, rect: androidx.compose.ui.geometry.Rect?) {
    try {
        // 1. Capture the entire view bitmap
        val fullBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(fullBitmap)
        view.draw(canvas)

        // 2. Crop to the achievement card if rect is available
        val finalBitmap = if (rect != null) {
            Bitmap.createBitmap(
                fullBitmap,
                rect.left.toInt().coerceAtLeast(0),
                rect.top.toInt().coerceAtLeast(0),
                rect.width.toInt().coerceAtMost(view.width - rect.left.toInt()),
                rect.height.toInt().coerceAtMost(view.height - rect.top.toInt())
            )
        } else {
            fullBitmap
        }

        // 3. Save to cache
        val cachePath = File(context.cacheDir, "shared_images").apply { mkdirs() }
        val file = File(cachePath, "achievement_${System.currentTimeMillis()}.png")
        val stream = FileOutputStream(file)
        finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()

        // 4. Launch Share Intent
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Achievement"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Composable
fun StatHighlight(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BadgeDetailModal(
    badge: BadgeInfo?, 
    onDismiss: () -> Unit,
    onShareClick: (BadgeInfo) -> Unit
) {
    if (badge != null) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                var isVisible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { isVisible = true }

                AnimatedVisibility(
                    visible = isVisible,
                    enter = scaleIn(animationSpec = tween(300)) + fadeIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .clickable(enabled = false) {},
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(onClick = { onShareClick(badge) }) {
                                    Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = onDismiss) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(bottom = 20.dp)) {
                                Surface(
                                    shape = CircleShape,
                                    color = badge.iconColor.copy(alpha = 0.12f),
                                    modifier = Modifier.size(90.dp)
                                ) {}
                                Icon(
                                    imageVector = badge.icon,
                                    contentDescription = null,
                                    tint = badge.iconColor,
                                    modifier = Modifier.size(44.dp)
                                )
                            }

                            Text(
                                text = badge.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = badge.meaning,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )

                            Divider(
                                modifier = Modifier.padding(vertical = 20.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )

                            Text(
                                text = "ENVIRONMENTAL SIGNIFICANCE",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = badge.significance,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 10.dp),
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.BarChart, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "VERIFIED IMPACT",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))
                                    ImpactRow(label = "CO2 Offset", value = "${String.format("%.1f", badge.co2Saved)} kg")
                                    Spacer(modifier = Modifier.height(6.dp))
                                    ImpactRow(label = "Energy Saved", value = "${String.format("%.1f", badge.energySaved)} kWh")
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Your daily choices are shaping a better world.",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImpactRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun CarbonCreditsCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Carbon Credits", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelLarge)
                Text("520.00", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Equivalent to 12 trees planted", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
            }
            Surface(
                color = Color.White.copy(alpha = 0.15f),
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.AccountBalanceWallet, null, tint = Color.White, modifier = Modifier.padding(12.dp))
            }
        }
    }
}

@Composable
fun MonthlyGoalCard() {
    var maxKwh by remember { mutableStateOf(319) }
    val actualKwh = 245
    val actualProgress = (actualKwh.toFloat() / maxKwh.toFloat()).coerceIn(0f, 1f)
    
    var previewProgress by remember { mutableStateOf(actualProgress) }
    var isDragging by remember { mutableStateOf(false) }
    var componentWidth by remember { mutableStateOf(0f) }
    
    val limitedPreviewProgress = if (isDragging) previewProgress.coerceAtMost(actualProgress) else actualProgress
    val previewKwh = (limitedPreviewProgress * maxKwh).roundToInt()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = maxKwh.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Icon(
                                    Icons.Default.ArrowDropUp, 
                                    contentDescription = "Increase",
                                    modifier = Modifier.size(20.dp).clickable { maxKwh += 1 },
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    Icons.Default.ArrowDropDown, 
                                    contentDescription = "Decrease",
                                    modifier = Modifier.size(20.dp).clickable { if (maxKwh > 1) maxKwh -= 1 },
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "kWh target this month",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                this@Row.AnimatedVisibility(visible = isDragging) {
                    Text(
                        text = "Day ${(limitedPreviewProgress * 30).coerceIn(1f, 30f).roundToInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$actualKwh / $maxKwh kWh used", 
                    style = MaterialTheme.typography.bodySmall, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "On track \u2713", 
                    color = Color(0xFF2E7D32),
                    style = MaterialTheme.typography.bodySmall, 
                    fontWeight = FontWeight.ExtraBold
                )
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .onGloballyPositioned { componentWidth = it.size.width.toFloat() }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { isDragging = true },
                            onDragEnd = { isDragging = false },
                            onDragCancel = { isDragging = false },
                            onDrag = { change, _ ->
                                change.consume()
                                previewProgress = (change.position.x / componentWidth).coerceIn(0f, 1f)
                            }
                        )
                    }
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(8.dp).align(Alignment.Center)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                )
                
                Box(
                    modifier = Modifier.fillMaxWidth(actualProgress).height(8.dp).align(Alignment.CenterStart)
                        .background(
                            Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))),
                            RoundedCornerShape(4.dp)
                        )
                )
                
                val inspectorX = (limitedPreviewProgress * componentWidth).roundToInt()
                
                Box(
                    modifier = Modifier.offset { 
                        IntOffset(
                            x = (inspectorX - 40.dp.toPx().roundToInt()).coerceAtLeast(0), 
                            y = -35.dp.toPx().roundToInt()
                        ) 
                    }
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isDragging,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 6.dp
                        ) {
                            Text(
                                text = "$previewKwh kWh",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
                
                Surface(
                    modifier = Modifier
                        .offset { IntOffset(x = inspectorX - 2.dp.toPx().roundToInt(), y = 0) }
                        .size(width = 4.dp, height = 28.dp)
                        .align(Alignment.CenterStart),
                    shape = RoundedCornerShape(2.dp),
                    color = if (isDragging) MaterialTheme.colorScheme.primary else Color.Transparent
                ) {}
            }
        }
    }
}

@Composable
fun EarthChallengeCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(Brush.horizontalGradient(listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))))
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Public, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Global Challenge", color = Color.White, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Objective: Reduce grid dependency by 15%", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = 0.6f,
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.2f),
                    strokeCap = StrokeCap.Round
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Community Progress: 60%", color = Color.White, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun BadgeGrid(
    badges: List<BadgeInfo>,
    onBadgeClick: (BadgeInfo) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        badges.chunked(2).forEach { rowBadges ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowBadges.forEach { badge ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = if (rowBadges.size > 1) 4.dp else 0.dp)
                            .clickable { onBadgeClick(badge) }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(badge.iconColor.copy(alpha = 0.12f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = badge.icon,
                                    contentDescription = null,
                                    tint = badge.iconColor,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                badge.title, 
                                style = MaterialTheme.typography.labelLarge, 
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "View Details", 
                                style = MaterialTheme.typography.labelSmall, 
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                if (rowBadges.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}