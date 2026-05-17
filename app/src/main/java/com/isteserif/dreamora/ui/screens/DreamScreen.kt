package com.isteserif.dreamora.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.isteserif.dreamora.ui.viewmodel.DreamUiState
import com.isteserif.dreamora.ui.viewmodel.DreamViewModel
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamScreen(
    onHistoryClick: () -> Unit = {},
    viewModel: DreamViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var dreamText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull() ?: ""
            if (spokenText.isNotBlank()) {
                dreamText = if (dreamText.isBlank()) spokenText
                else "$dreamText $spokenText"
            }
        }
    }

    fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "tr-TR")
            putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf("en-US"))
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Rüyanızı anlatın...")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        speechLauncher.launch(intent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("✦ Dreamora") },
                actions = {
                    TextButton(onClick = onHistoryClick) {
                        Text("Geçmiş Rüyalarım")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MoonWithStars()

            OutlinedTextField(
                value = dreamText,
                onValueChange = { dreamText = it },
                label = { Text("Rüyanızı detaylıca anlatın...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 5,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )

            // Yorum butonu + mikrofon yan yana
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.analyzeUserDream(dreamText)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = uiState !is DreamUiState.Loading && dreamText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("✨ Rüyamı Yorumla")
                }

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        if (SpeechRecognizer.isRecognitionAvailable(context)) {
                            startSpeechRecognition()
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Sesli yaz",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            when (val currentState = uiState) {
                is DreamUiState.Idle -> {
                    Spacer(modifier = Modifier.height(24.dp))

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    val quotes = listOf(
                        "\"Rüyalar, bilinçdışının kraliyet yoludur.\"\n— Sigmund Freud",
                        "\"Rüya görmek cesarettir.\"\n— Anaïs Nin",
                        "\"Rüyalar gerçeğin gizli kapılarıdır.\"\n— Carl Jung",
                        "\"En derin düşünceler rüyalarda gizlidir.\"\n— Aristo",
                        "\"Rüyalar, ruhun sessiz çığlıklarıdır.\"\n— Victor Hugo",
                        "\"Gece rüya gören, gündüz güneşi fark eder.\"\n— William Blake",
                        "\"Rüyalarınızı hatırlayın, onlar yolunuzu gösterir.\"\n— Carl Jung",
                        "\"Rüya görmeden uyumak, yıldızsız gökyüzü gibidir.\"\n— Malcolm de Chazal",
                        "\"Rüya, ruhun en derin köşesindeki gizli küçük kapıdır.\"\n— Carl Jung",
                        "\"Uyurken hayaller görürüz; uyanıkken hayal kurarız.\"\n— Victor Hugo",
                        "\"Rüyalar gündüz dilinde konuşmaz.\"\n— Gail Godwin",
                        "\"Rüyalar yarının sorularına bugünün cevaplarıdır.\"\n— Edgar Cayce",
                        "\"Rüya, ruhun yazdığı kitabın illüstrasyonlarıdır.\"\n— Marsha Norman",
                        "\"Uyku en iyi meditasyondur.\"\n— Dalai Lama",
                        "\"Kelebek miyim rüyamda insan olan, yoksa insan mıyım rüyamda kelebek olan?\"\n— Zhuangzi",
                        "\"Uyku, umutsuzlukla umut arasındaki en iyi köprüdür.\"\n— E. Joseph Cossman",
                        "\"Rüyalar, gündüzün bıraktığı izlerin geceki yankısıdır.\"\n— Rabindranath Tagore",
                        "\"Bir kelebek gibi uçup giden rüyalar, sabah ışığında iz bırakır.\"\n— Khalil Gibran",
                        "\"Her şey uyanıkken hayal edilen, uyurken yaşanır.\"\n— Edgar Allan Poe",
                        "\"Rüya görmeden uyumak, yıldızsız gökyüzü gibidir.\"\n— Malcolm de Chazal"
                    )
                    val randomQuote = remember { quotes.random() }

                    Text(
                        text = randomQuote,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Italic
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    WaveAnimation(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                    )
                }

                is DreamUiState.Loading -> {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Text(
                        "Rüyanız İnceleniyor...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                is DreamUiState.Success -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(
                            text = currentState.analysis,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    TextButton(onClick = {
                        dreamText = ""
                        viewModel.resetState()
                    }) {
                        Text("Başka Bir Rüya Anlat")
                    }
                }

                is DreamUiState.Error -> {
                    Text(
                        text = "Bir Hata Oluştu:\nTekrar Deneyin",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun MoonWithStars() {
    val infiniteTransition = rememberInfiniteTransition(label = "stars")

    val alphas = (0..11).map { i ->
        infiniteTransition.animateFloat(
            initialValue = 0.15f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = (2000 + i * 300),
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset(i * 200)
            ),
            label = "star$i"
        )
    }

    val moonOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "moon"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val starPositions = listOf(
                Offset(size.width * 0.20f, size.height * 0.25f) to 4f,
                Offset(size.width * 0.30f, size.height * 0.13f) to 3f,
                Offset(size.width * 0.13f, size.height * 0.55f) to 3f,
                Offset(size.width * 0.70f, size.height * 0.20f) to 4f,
                Offset(size.width * 0.80f, size.height * 0.50f) to 3f,
                Offset(size.width * 0.75f, size.height * 0.12f) to 3f,
                Offset(size.width * 0.25f, size.height * 0.75f) to 2.5f,
                Offset(size.width * 0.77f, size.height * 0.72f) to 2.5f,
                Offset(size.width * 0.40f, size.height * 0.10f) to 2.5f,
                Offset(size.width * 0.62f, size.height * 0.78f) to 2.5f,
                Offset(size.width * 0.15f, size.height * 0.80f) to 2f,
                Offset(size.width * 0.87f, size.height * 0.30f) to 2f,
            )

            starPositions.forEachIndexed { i, (pos, radius) ->
                drawCircle(
                    color = Color(0xFFE0C8FF),
                    radius = radius,
                    center = pos,
                    alpha = alphas[i].value
                )
            }
        }

        Text(
            text = "🌙",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.offset(y = (-moonOffset * 6).dp)
        )
    }
}

@Composable
fun WaveAnimation(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")

    val wave1Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing)
        ),
        label = "wave1"
    )
    val wave2Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing)
        ),
        label = "wave2"
    )
    val wave3Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing)
        ),
        label = "wave3"
    )

    val sparkleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle"
    )

    Canvas(modifier = modifier) {
        drawWave(wave1Offset, Color(0xFF4E3080), 10f, size.height * 0.5f, 0.55f)
        drawWave(wave2Offset, Color(0xFF3D2470), 8f, size.height * 0.65f, 0.6f)
        drawWave(wave3Offset, Color(0xFF2A1850), 6f, size.height * 0.78f, 0.75f)

        val sparklePositions = listOf(
            Offset(size.width * 0.15f, size.height * 0.35f),
            Offset(size.width * 0.42f, size.height * 0.25f),
            Offset(size.width * 0.75f, size.height * 0.4f),
            Offset(size.width * 0.88f, size.height * 0.2f),
        )
        sparklePositions.forEachIndexed { i, pos ->
            drawCircle(
                color = Color(0xFFD4B4FF),
                radius = 2.5f,
                center = pos,
                alpha = if (i % 2 == 0) sparkleAlpha else 1f - sparkleAlpha
            )
        }
    }
}

fun DrawScope.drawWave(
    offset: Float,
    color: Color,
    amplitude: Float,
    yBase: Float,
    alpha: Float
) {
    val path = Path()
    val width = size.width
    val height = size.height
    var x = 0f
    while (x <= width) {
        val y = yBase + amplitude * sin(offset + (x / width) * 2 * Math.PI).toFloat()
        if (x == 0f) path.moveTo(x, y) else path.lineTo(x, y)
        x += 4f
    }
    path.lineTo(width, height)
    path.lineTo(0f, height)
    path.close()
    drawPath(path = path, color = color, alpha = alpha)
}