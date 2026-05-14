package com.isteserif.dreamora.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isteserif.dreamora.data.repository.DreamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. Ekranın o an içinde bulunabileceği 4 farklı durumu (State) tanımlıyoruz
sealed class DreamUiState {
    object Idle : DreamUiState() // Başlangıç durumu, kullanıcı henüz bir şey yazmadı
    object Loading : DreamUiState() // İstek atıldı, API'den cevap bekleniyor (Dönen animasyon göstereceğiz)
    data class Success(val analysis: String) : DreamUiState() // İşlem başarılı, rüya yorumu geldi!
    data class Error(val message: String) : DreamUiState() // Eyvah, bir hata oldu
}

class DreamViewModel : ViewModel() {

    // Az önce yazdığımız Garsonu (Repository) çağırıyoruz
    private val repository = DreamRepository()

    // 2. Ekranın (Compose) anlık olarak dinleyeceği durum değişkeni (Varsayılan olarak Idle başlar)
    private val _uiState = MutableStateFlow<DreamUiState>(DreamUiState.Idle)
    val uiState: StateFlow<DreamUiState> = _uiState.asStateFlow()

    // 3. Kullanıcı "Gönder" butonuna bastığında çalışacak fonksiyon
    fun analyzeUserDream(dreamText: String) {
        // Eğer boş metin gönderdiyse hiçbir şey yapma
        if (dreamText.isBlank()) return

        // ViewModel içinde arka plan işlemi (Coroutine) başlatıyoruz
        viewModelScope.launch {
            // Önce durumu "Yükleniyor" yapıyoruz
            _uiState.value = DreamUiState.Loading

            // Garsona (Repository) rüyayı verip cevabı bekliyoruz
            val result = repository.getDreamAnalysis(dreamText)

            // Dönen cevaba göre durumu Güncelliyoruz (Başarılı veya Hata)
            result.fold(
                onSuccess = { analysisText ->
                    _uiState.value = DreamUiState.Success(analysisText)
                },
                onFailure = { error ->
                    _uiState.value = DreamUiState.Error(error.localizedMessage ?: "Bilinmeyen bir hata oluştu.")
                }
            )
        }
    }

    // Kullanıcı "Yeni Rüya Analiz Et" derse ekranı başa saran fonksiyon
    fun resetState() {
        _uiState.value = DreamUiState.Idle
    }
}