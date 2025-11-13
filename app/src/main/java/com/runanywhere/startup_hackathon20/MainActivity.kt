package com.runanywhere.startup_hackathon20

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.runanywhere.startup_hackathon20.ui.theme.Startup_hackathon20Theme
import com.runanywhere.startup_hackathon20.ui.theme.SurfaceDark
import com.runanywhere.startup_hackathon20.ui.theme.TrueBlack
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if onboarding should be shown
        val prefs = getSharedPreferences("email_rewriter_prefs", Context.MODE_PRIVATE)
        val hasCompletedOnboarding = prefs.getBoolean("onboarding_completed", false)

        setContent {
            Startup_hackathon20Theme {
                if (!hasCompletedOnboarding) {
                    OnboardingFlow(
                        onComplete = {
                            prefs.edit().putBoolean("onboarding_completed", true).apply()
                        }
                    )
                } else {
                    EmailRewriterScreen()
                }
            }
        }
    }
}

// Example emails for quick demo
val EXAMPLE_EMAILS = listOf(
    "Hi, I need the report by today. This is the third time I'm asking. Please send it ASAP.",
    "Hey! Would love to catch up over coffee sometime next week if you're free?",
    "I'm sorry for missing the deadline. I had some personal issues. Can we reschedule?"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailRewriterScreen(viewModel: ChatViewModel = viewModel()) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val selectedTone by viewModel.selectedTone.collectAsState()
    val rewrittenEmail by viewModel.rewrittenEmail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val availableModels by viewModel.availableModels.collectAsState()
    val downloadProgress by viewModel.downloadProgress.collectAsState()
    val currentModelId by viewModel.currentModelId.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    var emailText by remember { mutableStateOf("") }
    var showModelSelector by remember { mutableStateOf(false) }
    var showResultSheet by remember { mutableStateOf(false) }
    var showExamplesMenu by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Staggered animation states
    var titleVisible by remember { mutableStateOf(false) }
    var subtitleVisible by remember { mutableStateOf(false) }
    var toneChipsVisible by remember { mutableStateOf(false) }
    var inputVisible by remember { mutableStateOf(false) }
    var buttonVisible by remember { mutableStateOf(false) }

    // Launch staggered animation on composition
    LaunchedEffect(Unit) {
        delay(50)
        titleVisible = true
        delay(100)
        subtitleVisible = true
        delay(150)
        toneChipsVisible = true
        delay(200)
        inputVisible = true
        delay(250)
        buttonVisible = true
    }

    // Show result sheet when rewrite completes
    LaunchedEffect(rewrittenEmail) {
        if (rewrittenEmail.isNotEmpty() && !isLoading) {
            showResultSheet = true
        }
    }

    // Result Bottom Sheet
    if (showResultSheet && rewrittenEmail.isNotEmpty()) {
        ModalBottomSheet(
            onDismissRequest = {
                showResultSheet = false
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            },
            containerColor = SurfaceDark,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .width(32.dp)
                        .height(4.dp)
                        .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                )
            }
        ) {
            ResultSheetContent(
                originalEmail = emailText,
                rewrittenEmail = rewrittenEmail,
                tone = selectedTone,
                onCopy = {
                    copyToClipboard(context, rewrittenEmail)
                    Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )
        }
    }

    // Examples Menu
    if (showExamplesMenu) {
        DropdownMenu(
            expanded = showExamplesMenu,
            onDismissRequest = { showExamplesMenu = false }
        ) {
            EXAMPLE_EMAILS.forEachIndexed { index, example ->
                DropdownMenuItem(
                    text = {
                        Text(
                            "Example ${index + 1}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        emailText = example
                        showExamplesMenu = false
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SurfaceDark,
                        TrueBlack
                    ),
                    radius = 1500f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Top navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(48.dp))

                IconButton(
                    onClick = {
                        showExamplesMenu = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Examples",
                        tint = Color.White
                    )
                }
            }

            // Title with animation
            AnimatedVisibility(
                visible = titleVisible,
                enter = fadeIn(
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                ) + slideInVertically(
                    animationSpec = tween(400, easing = FastOutSlowInEasing),
                    initialOffsetY = { 20 }
                )
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Email Tone\nRewriter",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 42.sp,
                            lineHeight = 48.sp
                        ),
                        color = Color.White
                    )
                }
            }

            // Subtitle with animation
            AnimatedVisibility(
                visible = subtitleVisible,
                enter = fadeIn(
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                ) + slideInVertically(
                    animationSpec = tween(400, easing = FastOutSlowInEasing),
                    initialOffsetY = { 20 }
                )
            ) {
                Text(
                    text = "On-device AI. Zero tracking.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            // Status message
            if (currentModelId == null && downloadProgress == null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF2C2C2E),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = statusMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )

                        TextButton(
                            onClick = {
                                showModelSelector = !showModelSelector
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        ) {
                            Text(
                                if (showModelSelector) "Hide Model Setup" else "Show Model Setup",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Download progress
            downloadProgress?.let { progress ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF2C2C2E),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Downloading AI Model...",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Model selector (collapsible)
            if (showModelSelector) {
                ModelSelector(
                    models = availableModels,
                    currentModelId = currentModelId,
                    onDownload = { modelId ->
                        viewModel.downloadModel(modelId)
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    onLoad = { modelId ->
                        viewModel.loadModel(modelId)
                        showModelSelector = false
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    onRefresh = { viewModel.refreshModels() }
                )
            }

            // Tone selector with animation
            AnimatedVisibility(
                visible = toneChipsVisible,
                enter = fadeIn(
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                ) + slideInVertically(
                    animationSpec = tween(400, easing = FastOutSlowInEasing),
                    initialOffsetY = { 20 }
                )
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Choose Your Tone",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )

                    ToneSelector(
                        selectedTone = selectedTone,
                        onToneSelected = {
                            viewModel.setTone(it)
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        enabled = !isLoading && currentModelId != null
                    )
                }
            }

            // Email Input with animation
            AnimatedVisibility(
                visible = inputVisible,
                enter = fadeIn(
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                ) + slideInVertically(
                    animationSpec = tween(400, easing = FastOutSlowInEasing),
                    initialOffsetY = { 20 }
                )
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Your Email",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )

                    Box {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF2C2C2E),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            TextField(
                                value = emailText,
                                onValueChange = {
                                    if (it.length <= 300) {
                                        emailText = it
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 200.dp),
                                placeholder = {
                                    Text(
                                        "Paste or type your email here...",
                                        color = Color.White.copy(alpha = 0.4f)
                                    )
                                },
                                enabled = !isLoading && currentModelId != null,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    disabledTextColor = Color.White.copy(alpha = 0.5f),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                ),
                                textStyle = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Character counter
                        AnimatedVisibility(
                            visible = emailText.isNotEmpty(),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp),
                            enter = fadeIn(animationSpec = tween(300)),
                            exit = fadeOut(animationSpec = tween(300))
                        ) {
                            Text(
                                text = "${emailText.length}/300",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            // Rewrite Button with animation
            AnimatedVisibility(
                visible = buttonVisible,
                enter = fadeIn(
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                ) + slideInVertically(
                    animationSpec = tween(400, easing = FastOutSlowInEasing),
                    initialOffsetY = { 20 }
                )
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = {
                            viewModel.rewriteEmail(emailText, selectedTone)
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !isLoading && emailText.isNotBlank() && currentModelId != null,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = Color(0xFF3A3A3C)
                        )
                    ) {
                        if (isLoading) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                                Text(
                                    "Rewriting...",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontSize = 16.sp
                                )
                            }
                        } else {
                            Text(
                                "Rewrite Email",
                                style = MaterialTheme.typography.labelLarge,
                                fontSize = 16.sp
                            )
                        }
                    }

                    // Loading progress bar
                    if (isLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = Color.White.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ToneSelector(
    selectedTone: String,
    onToneSelected: (String) -> Unit,
    enabled: Boolean = true
) {
    val tones = listOf("Professional", "Friendly", "Concise", "Formal")
    val haptic = LocalHapticFeedback.current

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tones) { tone ->
            FilterChip(
                selected = selectedTone == tone,
                onClick = {
                    onToneSelected(tone)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                label = {
                    Text(
                        tone,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                enabled = enabled,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFF2C2C2E),
                    labelColor = Color.White.copy(alpha = 0.7f),
                    disabledContainerColor = Color(0xFF2C2C2E).copy(alpha = 0.5f),
                    disabledLabelColor = Color.White.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
fun ResultSheetContent(
    originalEmail: String,
    rewrittenEmail: String,
    tone: String,
    onCopy: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Rewritten Email",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )

        // Original Email
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF2C2C2E),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Original",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = originalEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // Rewritten Email
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "$tone Tone",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = rewrittenEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }

        // Copy Button
        Button(
            onClick = onCopy,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = Color.White
                )
                Text(
                    "Copy to Clipboard",
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun OnboardingFlow(onComplete: () -> Unit) {
    var currentPage by remember { mutableStateOf(0) }
    val viewModel: ChatViewModel = viewModel()

    val availableModels by viewModel.availableModels.collectAsState()
    val downloadProgress by viewModel.downloadProgress.collectAsState()
    val currentModelId by viewModel.currentModelId.collectAsState()
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SurfaceDark,
                        TrueBlack
                    ),
                    radius = 1500f
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        when (currentPage) {
            0 -> OnboardingModelDownload(
                models = availableModels,
                currentModelId = currentModelId,
                downloadProgress = downloadProgress,
                onDownload = { modelId ->
                    viewModel.downloadModel(modelId)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                onLoad = { modelId ->
                    viewModel.loadModel(modelId)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                onNext = {
                    currentPage = 1
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                onSkip = {
                    onComplete()
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )
            1 -> OnboardingTutorial(
                onNext = {
                    currentPage = 2
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                onSkip = {
                    onComplete()
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )
            2 -> OnboardingComplete(
                onGetStarted = {
                    onComplete()
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )
        }
    }
}

@Composable
fun OnboardingModelDownload(
    models: List<com.runanywhere.sdk.models.ModelInfo>,
    currentModelId: String?,
    downloadProgress: Float?,
    onDownload: (String) -> Unit,
    onLoad: (String) -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Email Tone Rewriter",
            style = MaterialTheme.typography.displayLarge,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "On-device AI. Zero tracking.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(48.dp))

        if (currentModelId != null) {
            // Model loaded - show next button
            Text(
                text = "Ready to go!",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    "Next",
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 16.sp
                )
            }
        } else if (models.isNotEmpty()) {
            // Show first model for download
            val firstModel = models.first()

            Text(
                text = "Download AI Model",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (downloadProgress != null) {
                LinearProgressIndicator(
                    progress = { downloadProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${(downloadProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            } else if (firstModel.isDownloaded) {
                Button(
                    onClick = { onLoad(firstModel.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Load Model",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 16.sp
                    )
                }
            } else {
                Button(
                    onClick = { onDownload(firstModel.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Download Model",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "~374 MB · One-time setup",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onSkip) {
            Text(
                "Skip",
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun OnboardingTutorial(
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Choose Your Tone",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Select from Professional, Friendly, Concise, or Formal to match your needs.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                "Next",
                style = MaterialTheme.typography.labelLarge,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onSkip) {
            Text(
                "Skip",
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun OnboardingComplete(
    onGetStarted: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Rewrite Instantly",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your email will be transformed in seconds, ready to copy and send.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                "Get Started",
                style = MaterialTheme.typography.labelLarge,
                fontSize = 16.sp
            )
        }
    }
}

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Rewritten Email", text)
    clipboard.setPrimaryClip(clip)
}

@Composable
fun ModelSelector(
    models: List<com.runanywhere.sdk.models.ModelInfo>,
    currentModelId: String?,
    onDownload: (String) -> Unit,
    onLoad: (String) -> Unit,
    onRefresh: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF2C2C2E),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Available Models",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                TextButton(
                    onClick = {
                        onRefresh()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                ) {
                    Text(
                        "Refresh",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (models.isEmpty()) {
                Text(
                    text = "No models available. Initializing...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f)
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    models.forEach { model ->
                        ModelItem(
                            model = model,
                            isLoaded = model.id == currentModelId,
                            onDownload = {
                                onDownload(model.id)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            onLoad = {
                                onLoad(model.id)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModelItem(
    model: com.runanywhere.sdk.models.ModelInfo,
    isLoaded: Boolean,
    onDownload: () -> Unit,
    onLoad: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isLoaded)
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        else
            Color(0xFF3A3A3C),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = model.name,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White
            )

            if (isLoaded) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "✓ Currently Loaded",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDownload,
                        modifier = Modifier.weight(1f),
                        enabled = !model.isDownloaded,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = Color(0xFF2C2C2E)
                        )
                    ) {
                        Text(
                            if (model.isDownloaded) "Downloaded" else "Download",
                            style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp)
                        )
                    }

                    Button(
                        onClick = onLoad,
                        modifier = Modifier.weight(1f),
                        enabled = model.isDownloaded,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = Color(0xFF2C2C2E)
                        )
                    ) {
                        Text(
                            "Load",
                            style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Startup_hackathon20Theme {
        EmailRewriterScreen()
    }
}
