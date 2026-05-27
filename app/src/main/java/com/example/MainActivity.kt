package com.example

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.text.font.FontFamily
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import com.example.ui.MainViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private var hasNotificationPermission by mutableStateOf(false)

    // Handle android 13 runtime request permission gracefully
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            Toast.makeText(this, "Daily 7:00 PM reminder enabled!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Reminder notifications disabled. You can enable them later in system settings.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                HanumanChalisaScreen(
                    viewModel = viewModel,
                    hasPermission = hasNotificationPermission,
                    onRequestPermission = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                )
            }
        }
    }

    private fun checkPermission() {
        hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}

data class Verse(
    val type: String,          // "DOHA" or "CHAUPAI"
    val indexString: String,   // e.g., "दोहा १"
    val text: String
)

// Full holy text of Hanuman Chalisa beautifully laid out
val chalisaVerses = listOf(
    Verse("DOHA", "दोहा १", "श्रीगुरु चरन सरोज रज, निज मनु मुकुरु सुधारि।\nबरनऊँ रघुवर बिमल जसु, जो दायकु फल चारि॥"),
    Verse("DOHA", "दोहा २", "बुद्धिहीन तनु जानिके, सुमिरौं पवन-कुमार।\nबल बुधि बिद्या देहु मोहिं, हरहु कलेस बिकार॥"),
    
    Verse("CHAUPAI", "चौपाई १", "जय हनुमान ज्ञान गुन सागर।\nजय कपीस तिहुँ लोक उजागर॥"),
    Verse("CHAUPAI", "चौपाई २", "रामदूत अतुलित बल धामा।\nअंजनि-पुत्र पवनसुत नामा॥"),
    Verse("CHAUPAI", "चौपाई ३", "महाबीर बिक्रम बजरंगी।\nकुमति निवार सुमति के संगी॥"),
    Verse("CHAUPAI", "चौपाई ४", "कंचन बरन बिराज सुबेसा।\nकानन कुंडल कुंचित केसा॥"),
    Verse("CHAUPAI", "चौपाई ५", "हाथ बज्र औ ध्वजा बिराजै।\nकाँधे मूँज जनेऊ साजै॥"),
    Verse("CHAUPAI", "चौपाई ६", "संकर सुवन केसरीनंदन।\nतेज प्रताप महा जग बंदन॥"),
    Verse("CHAUPAI", "चौपाई ७", "बिद्यावान गुनी अति चातुर।\nराम काज करिबे को आतुर॥"),
    Verse("CHAUPAI", "चौपाई ८", "प्रभु चरित्र सुनिबे को रसिया।\nराम लखन सीता मन बसिया॥"),
    Verse("CHAUPAI", "चौपाई ९", "सूक्ष्म रूप धरि सियहिं दिखावा।\nबिकट रूप धरि लंक जरावा॥"),
    Verse("CHAUPAI", "चौपाई १०", "भीम रूप धरि असुर सँहारे।\nरामचन्द्र के काज सँवारे॥"),
    Verse("CHAUPAI", "चौपाई ११", "लाय सजीवन लखन जियाए।\nश्रीरघुबीर हरषि उर लाए॥"),
    Verse("CHAUPAI", "चौपाई १२", "रघुपति कीन्ही बहुत बड़ाई।\nतुम मम प्रिय भरतहि सम भाई॥"),
    Verse("CHAUPAI", "चौपाई १३", "सहस बदन तुम्हरो जस गावैं।\nअस कहि श्रीपति कंठ लगावैं॥"),
    Verse("CHAUPAI", "चौपाई १४", "सनकादिक ब्रह्मादि मुनीसा।\nनारद सारद सहित अहीसा॥"),
    Verse("CHAUPAI", "चौपाई १५", "जम कुबेर दिगपाल जहाँ ते।\nकबि कोबिद कहि सके कहाँ ते॥"),
    Verse("CHAUPAI", "चौपाई १६", "तुम उपकार सुग्रीवहिं कीन्हा।\nराम मिलाय राज पद दीन्हा॥"),
    Verse("CHAUPAI", "चौपाई १७", "तुम्हरो मंत्र बिभीषन माना।\nलंकेस्वर भए सब जग जाना॥"),
    Verse("CHAUPAI", "चौपाई १८", "जुग सहस्र जोजन पर भानू।\nलील्यो ताहि मधुर फल जानू॥"),
    Verse("CHAUPAI", "चौपाई १९", "प्रभु मुद्रिका मेलि मुख माहीं।\nजलधि लाँघि गये अचरज नाहीं॥"),
    Verse("CHAUPAI", "चौपाई २०", "दुर्गम काज जगत के जेते।\nसुगम अनुग्रह तुम्हरे तेते॥"),
    Verse("CHAUPAI", "चौपाई २१", "राम दुआरे तुम रखवारे।\nहोत न आग्या बिनु पैसारे॥"),
    Verse("CHAUPAI", "चौपाई २२", "सब सुख लहै तुम्हारी सरना।\nतुम रक्षक काहू को डर ना॥"),
    Verse("CHAUPAI", "चौपाई २३", "अपन तेज सम्हारो आपै।\nतीनों लोक हाँक तें कापै॥"),
    Verse("CHAUPAI", "चौपाई २४", "भूत पिसाच निकट नहिं आवै।\nमहाबीर जब नाम सुनावै॥"),
    Verse("CHAUPAI", "चौपाई २५", "नासै रोग हरै सब पीरा।\nजपत निरंतर हनुमत बीरा॥"),
    Verse("CHAUPAI", "चौपाई २६", "संकट तें हनुमान छुड़ावै।\nमन क्रम बचन ध्यान जो लावै॥"),
    Verse("CHAUPAI", "चौपाई २७", "सब पर राम तपस्वी राजा।\nतिन के काज सकल तुम साजा॥"),
    Verse("CHAUPAI", "चौपाई २८", "और मनोरथ जो कोई लावै।\nसोइ अमित जीवन फल पावै॥"),
    Verse("CHAUPAI", "चौपाई २९", "चारों जुग परताप तुम्हारा।\nहै परसिद्ध जगत उजियारा॥"),
    Verse("CHAUPAI", "चौपाई ३०", "साधु-संत के तुम रखवारे।\nअसुर निकंदन राम दुलारे॥"),
    Verse("CHAUPAI", "चौपाई ३१", "अष्ट सिद्धि नौ निधि के दाता।\nअस बर दीन जानकी माता॥"),
    Verse("CHAUPAI", "चौपाई ३२", "राम रसायन तुम्हरे पासा।\nसदा रहो रघुपति के दासा॥"),
    Verse("CHAUPAI", "चौपाई ३३", "तुम्हरे भजन राम को पावै।\nजनम-जनम के दुख बिसरावै॥"),
    Verse("CHAUPAI", "चौपाई ३४", "अंतकाल रघुबर पुर जाई।\nजहाँ जनम हरि-भक्त कहाई॥"),
    Verse("CHAUPAI", "चौपाई ३५", "और देबता चित्त न धरई।\nहनुमत सेइ सर्ब सुख करई॥"),
    Verse("CHAUPAI", "चौपाई ३६", "संकट कटै मिटै सब पीरा।\nजो सुमिरै हनुमत बलबीरा॥"),
    Verse("CHAUPAI", "चौपाई ३७", "जै जै जै हनुमान गोसाईं।\nकृपा करहु गुरुदेव की नाईं॥"),
    Verse("CHAUPAI", "चौपाई ३८", "जो सत बार पाठ कर कोई।\nछूटहि बंदि महा सुख होई॥"),
    Verse("CHAUPAI", "चौपाई ३९", "जो यह पढ़ै हनुमान चलीसा।\nहोइ सिद्ध साखी गौरीसा॥"),
    Verse("CHAUPAI", "चौपाई ४०", "तुलसीदास सदा हरि चेरा।\nकीजै नाथ हृदय मँह डेरा॥"),

    Verse("DOHA", "दोहा ३", "पवनतनय संकट हरन, मंगल मूरति रूप।\nराम लखन सीता सहित, हृदय बसहु सुर भूप॥")
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HanumanChalisaScreen(
    viewModel: MainViewModel,
    hasPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    val stats by viewModel.streakStats.collectAsStateWithLifecycle()
    val fontSizeMultiplier by viewModel.fontSize.collectAsStateWithLifecycle()
    var isSettingsOpen by remember { mutableStateOf(false) }
    
    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val coroutineScope = rememberCoroutineScope()
    
    val currentVerseIndex by remember {
        derivedStateOf {
            val firstVisible = listState.firstVisibleItemIndex
            if (firstVisible <= 0) 0 else (firstVisible - 1).coerceIn(0, chalisaVerses.lastIndex)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_scaffold"),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = Color.Black // Pure AMOLED deep black
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Top Custom Modern Row (Sophisticated Dark App Bar)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hanuman Chalisa",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp,
                                fontFamily = FontFamily.Serif
                            ),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "DAILY DEVOTION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                fontSize = 10.sp
                            ),
                            color = Color(0xFF71717A) // zinc-500
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Streak Pill (bg-zinc-900/50, border zinc-800/50, 12 Days streak block)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFF18181B).copy(alpha = 0.5f))
                                .border(1.dp, Color(0xFF27272A).copy(alpha = 0.5f), CircleShape)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "⚡",
                                fontSize = 14.sp,
                                color = Color(0xFFFF9800)
                            )
                            Text(
                                text = "${stats.currentStreak} Days",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }

                        // Settings font sizing toggler
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSettingsOpen) Color(0xFFFF9800) else Color(0xFF18181B))
                                .clickable { isSettingsOpen = !isSettingsOpen }
                                .border(1.dp, Color(0xFF27272A), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Aa",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSettingsOpen) Color.Black else Color.White
                            )
                        }
                    }
                }

                // Smooth Animation Settings Drawer / Card
                AnimatedVisibility(
                    visible = isSettingsOpen,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF0A0A0A))
                            .border(1.dp, Color(0xFF27272A).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "ADJUST READING FONT SIZE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "A",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF71717A)
                            )
                            Slider(
                                value = fontSizeMultiplier,
                                onValueChange = { viewModel.updateFontSize(it) },
                                valueRange = 16f..32f,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp)
                                    .testTag("font_size_slider"),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFFFF9800),
                                    activeTrackColor = Color(0xFFFF9800),
                                    inactiveTrackColor = Color(0xFF27272A)
                                )
                            )
                            Text(
                                text = "A",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFFF9800)
                            )
                        }
                        
                        Text(
                            text = "Size Preview: ${fontSizeMultiplier.toInt()} sp",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF71717A),
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Notification Alert Permission Card if NOT GRANTED
                if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF0F0E0B))
                            .border(1.dp, Color(0xFFFF9800).copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "🔔",
                                fontSize = 24.sp
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Daily Reminder Enabled",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF9800),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Receive a persistent tracking notification at 7:00 PM if today's reading status is pending.",
                                    color = Color(0xFFD4D4D8),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onRequestPermission,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9800),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("grant_permission_button")
                        ) {
                            Text("Enable 7:00 PM Notification", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Beautiful Visual Dashboard Metrics in Sophisticated Dark Theme
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Active Streak Box
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0A0A0A))
                            .border(1.dp, Color(0xFF27272A).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🔥 STREAK",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 9.sp
                            ),
                            color = Color(0xFFFF9800)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${stats.currentStreak} Days",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    // Best Active Streak Box
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0A0A0A))
                            .border(1.dp, Color(0xFF27272A).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🏆 HIGHEST",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 9.sp
                            ),
                            color = Color(0xFFFFD700)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${stats.highestStreak} Days",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    // Total times Completed Box
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0A0A0A))
                            .border(1.dp, Color(0xFF27272A).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📿 TOTAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 9.sp
                            ),
                            color = Color(0xFF03A9F4)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${stats.totalCompleted}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }

                // Beautiful Scroll sheet of the Chalisa
                LazyColumn(
                    state = listState,
                    flingBehavior = flingBehavior,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("verse_lazy_column"),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 210.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Sacred ॐ logo item at the top of the verses
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF09090B)) // bg-zinc-950
                                    .border(1.dp, Color(0xFF27272A).copy(alpha = 0.5f), CircleShape), // border-zinc-800
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "ॐ",
                                    color = Color(0xFFFF9800), // #FF9800
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    itemsIndexed(chalisaVerses) { index, verse ->
                        val isDoha = verse.type == "DOHA"
                        val isActive = index == currentVerseIndex
                        
                        // Beautiful dynamic animations to focus attention on active verse
                        val cardAlpha by animateFloatAsState(
                            targetValue = if (isActive) 1f else 0.4f,
                            animationSpec = tween(durationMillis = 300),
                            label = "verse_alpha"
                        )
                        
                        val borderWidth by animateDpAsState(
                            targetValue = if (isActive) 1.5.dp else 1.dp,
                            animationSpec = tween(durationMillis = 300),
                            label = "verse_border"
                        )
                        
                        val scaleY by animateFloatAsState(
                            targetValue = if (isActive) 1.04f else 1.0f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                            label = "verse_scale"
                        )
                        
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer(
                                    alpha = cardAlpha,
                                    scaleX = scaleY,
                                    scaleY = scaleY
                                )
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    if (isActive) {
                                        if (isDoha) {
                                            Brush.linearGradient(
                                                colors = listOf(Color(0xFF1E1308), Color(0xFF0F0B04))
                                            )
                                        } else {
                                            Brush.linearGradient(
                                                colors = listOf(Color(0xFF0D0D15), Color(0xFF050508))
                                            )
                                        }
                                    } else {
                                        if (isDoha) {
                                            Brush.linearGradient(
                                                colors = listOf(Color(0xFF0D0906), Color(0xFF050403))
                                            )
                                        } else {
                                            Brush.linearGradient(
                                                colors = listOf(Color(0xFF060608), Color(0xFF020203))
                                            )
                                        }
                                    }
                                )
                                .border(
                                    width = borderWidth,
                                    color = if (isActive) {
                                        Color(0xFFFF9800).copy(alpha = 0.85f)
                                    } else {
                                        if (isDoha) Color(0xFFFF9800).copy(alpha = 0.12f) else Color(0xFF27272A).copy(alpha = 0.3f)
                                    },
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .clickable {
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(index + 1) // +1 due to top Logo item
                                    }
                                }
                                .padding(horizontal = 24.dp, vertical = if (isActive) 40.dp else 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Label of Verse
                            Text(
                                text = verse.indexString.uppercase(),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                ),
                                color = if (isActive) {
                                    if (isDoha) Color(0xFFFF9800) else Color(0xFFFFB74D)
                                } else {
                                    Color(0xFF52525B) // zinc-600
                                },
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Sacred Devanagari text styled with beautiful traditional font Serif
                            Text(
                                text = verse.text,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = if (isActive) (fontSizeMultiplier * 1.05).sp else fontSizeMultiplier.sp,
                                    lineHeight = if (isActive) (fontSizeMultiplier * 1.55 * 1.05).sp else (fontSizeMultiplier * 1.55).sp,
                                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
                                    fontFamily = FontFamily.Serif
                                ),
                                color = if (isActive) {
                                    if (isDoha) Color(0xFFFFD54F) else Color(0xFFFFFBEB)
                                } else {
                                    if (isDoha) Color(0xFFD97706).copy(alpha = 0.5f) else Color(0xFF71717A)
                                },
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.height(14.dp))
                            
                            // Visual Devotional separator symbol
                            Text(
                                text = if (isDoha) "✦ ॐ ✦" else "❖",
                                color = if (isActive) Color(0xFFFF9800).copy(alpha = 0.6f) else Color(0xFF27272A),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Sophisticated Dark "Status & Control Column" persistent at bottom of Box
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.95f),
                                Color.Black
                            )
                        )
                    )
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color(0xFF09090B)) // bg-zinc-950
                        .border(1.dp, Color(0xFF27272A).copy(alpha = 0.5f), RoundedCornerShape(28.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val readToday = stats.readToday
                    val showPrev = currentVerseIndex > 0
                    val showNext = currentVerseIndex < chalisaVerses.lastIndex

                    // Progress info & reminder status (neat single high-density row!)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: progress text
                        val currentVerse = chalisaVerses[currentVerseIndex]
                        Column {
                            Text(
                                text = currentVerse.indexString.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    fontSize = 10.sp
                                ),
                                color = Color(0xFFFF9800)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Verse ${currentVerseIndex + 1} of ${chalisaVerses.size}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = Color(0xFF71717A) // zinc-500
                            )
                        }

                        // Right: today status indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (readToday) Color(0xFF4CAF50) else Color(0xFFFF9800))
                            )
                            Text(
                                text = if (readToday) "Completed" else "Reading Pending",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                color = Color(0xFFD4D4D8)
                            )
                        }
                    }

                    // Progress Miniature Dots/Bar tracker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        val totalDots = 10
                        val progressOnTen = ((currentVerseIndex + 1).toFloat() / chalisaVerses.size * totalDots).toInt().coerceIn(1, totalDots)
                        for (dot in 1..totalDots) {
                            val isActive = dot <= progressOnTen
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(1.5.dp))
                                    .background(if (isActive) Color(0xFFFF9800) else Color(0xFF27272A))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Unified navigation + primary toggle action row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // PREV button
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (showPrev) Color(0xFF18181B) else Color(0xFF040405))
                                .border(1.dp, if (showPrev) Color(0xFF27272A) else Color(0xFF121213), RoundedCornerShape(14.dp))
                                .clickable(enabled = showPrev) {
                                    coroutineScope.launch {
                                        // Scroll to previous verse card
                                        listState.animateScrollToItem(currentVerseIndex)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "❮",
                                color = if (showPrev) Color(0xFFFF9800) else Color(0xFF3F3F46),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        // Big Elegant Primary Toggle Button (BEGIN READING toggle)
                        Button(
                            onClick = { viewModel.toggleTodayReading() },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("toggle_reading_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9800),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(14.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (!readToday) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "BEGIN READING",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        ),
                                        color = Color.Black
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "COMPLETED TODAY",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                lineHeight = 12.sp
                                            ),
                                            color = Color.Black
                                        )
                                        Text(
                                            text = "Tap to Undo",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 8.5.sp,
                                                lineHeight = 10.sp
                                            ),
                                            color = Color.Black.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }

                        // NEXT button
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (showNext) Color(0xFF18181B) else Color(0xFF040405))
                                .border(1.dp, if (showNext) Color(0xFF27272A) else Color(0xFF121213), RoundedCornerShape(14.dp))
                                .clickable(enabled = showNext) {
                                    coroutineScope.launch {
                                        // Scroll to next verse card
                                        listState.animateScrollToItem(currentVerseIndex + 2)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "❯",
                                color = if (showNext) Color(0xFFFF9800) else Color(0xFF3F3F46),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    // A very micro clean offline mode row to protect height
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(10.dp),
                                tint = Color(0xFF52525B)
                            )
                            Text(
                                text = "OFFLINE MODE ACTIVE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = Color(0xFF52525B)
                            )
                        }

                        Text(
                            text = "⚡ BATTERY SAVER ACTIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color(0xFF52525B)
                        )
                    }
                }
            }
        }
    }
}
