package com.isteserif.dreamora.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.isteserif.dreamora.ui.viewmodel.DreamUiState
import com.isteserif.dreamora.ui.viewmodel.DreamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamScreen(
    onHistoryClick: () -> Unit = {},
    viewModel: DreamViewModel = viewModel()
) {

    // 1. ViewModel'deki durumu anlık olarak dinliyoruz
    val uiState by viewModel.uiState.collectAsState()

    // 2. Kullanıcının klavyeden girdiği rüya metnini hafızada tutuyoruz
    var dreamText by remember { mutableStateOf("") }

    // Klavye odağını kontrol etmek için
    val focusManager = LocalFocusManager.current

    // Scaffold, Android ekranlarında üst bar (TopBar) ve alt bar gibi yapıları kolayca kurmamızı sağlar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dreamora") },
                actions = {
                    TextButton(onClick = onHistoryClick) {
                        Text("Geçmişim")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { paddingValues ->
        // Ekranın içindeki elemanları alt alta (Column) diziyoruz
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // Ekran kaydırılabilir olsun
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus() // Ekranın boş yerine tıklanınca klavyeyi kapat
                    })
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // Elemanlar arasına 16dp boşluk
        ) {

            // Rüya Giriş Alanı
            OutlinedTextField(
                value = dreamText,
                onValueChange = { dreamText = it },
                label = { Text("Rüyanızı detaylıca anlatın...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 5,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() } // Enter/Tamam tuşuna basınca klavyeyi kapat
                )
            )

            // Gönder Butonu (Eğer yükleniyorsa butona basılamasın)
            Button(
                onClick = {
                    focusManager.clearFocus() // Butona basınca klavyeyi kapat
                    viewModel.analyzeUserDream(dreamText)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is DreamUiState.Loading && dreamText.isNotBlank()
            ) {
                Text("Rüyamı Yorumla")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. EKRANIN DURUMUNA GÖRE (STATE) FARKLI GÖRSELLER ÇIKARTIYORUZ
            when (val currentState = uiState) {
                // YENİ:
                is DreamUiState.Idle -> {
                    val quotes = listOf(
                        "\"Rüyalar, bilinçdışının kraliyet yoludur.\" — Sigmund Freud",
                        "\"Rüya görmek cesarettir.\" — Anaïs Nin",
                        "\"Rüyalar gerçeğin gizli kapılarıdır.\" — Carl Jung",
                        "\"En derin düşünceler rüyalarda gizlidir.\" — Aristo",
                        "\"Rüyalar, ruhun sessiz çığlıklarıdır.\" — Victor Hugo",
                        "\"Gece rüya gören, gündüz güneşi fark eder.\" — William Blake",
                        "\"Rüyalarınızı hatırlayın, çünkü onlar yolunuzu gösterir.\" — Carl Jung",
                        "\"Rüya görmeden uyumak, yıldızsız gökyüzü gibidir.\" — Malcolm de Chazal"
                    )
                    val randomQuote = remember { quotes.random() }

                    Text(
                        text = randomQuote,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                is DreamUiState.Loading -> {
                    CircularProgressIndicator() // Dönen yükleme animasyonu
                    Text("Evrenin mesajları dinleniyor...")
                }

                is DreamUiState.Success -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Text(
                            text = currentState.analysis,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    // Yeni Rüya Butonu
                    TextButton(onClick = {
                        dreamText = ""
                        viewModel.resetState()
                    }) {
                        Text("Başka Bir Rüya Anlat")
                    }
                }

                is DreamUiState.Error -> {
                    Text(
                        text = "Bir Hata Oluştu:\n${currentState.message}",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}