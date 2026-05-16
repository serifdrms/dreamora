package com.isteserif.dreamora.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.isteserif.dreamora.data.local.DreamDatabase
import com.isteserif.dreamora.data.repository.DreamRepository
import com.isteserif.dreamora.domain.model.Dream
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DreamUiState {
    object Idle : DreamUiState()
    object Loading : DreamUiState()
    data class Success(val analysis: String) : DreamUiState()
    data class Error(val message: String) : DreamUiState()
}

class DreamViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DreamRepository()
    private val dao = DreamDatabase.getDatabase(application).dreamDao()

    private val _uiState = MutableStateFlow<DreamUiState>(DreamUiState.Idle)
    val uiState: StateFlow<DreamUiState> = _uiState.asStateFlow()

    val allDreams = dao.getAllDreams()

    fun analyzeUserDream(dreamText: String) {
        if (dreamText.isBlank()) return

        viewModelScope.launch {
            _uiState.value = DreamUiState.Loading

            val result = repository.getDreamAnalysis(dreamText)

            result.fold(
                onSuccess = { analysisText ->
                    _uiState.value = DreamUiState.Success(analysisText)
                    // Başarılı olunca otomatik kaydet
                    dao.insertDream(
                        Dream(
                            dreamText = dreamText,
                            analysis = analysisText
                        )
                    )
                },
                onFailure = { error ->
                    _uiState.value = DreamUiState.Error(
                        error.localizedMessage ?: "Bilinmeyen bir hata oluştu."
                    )
                }
            )
        }
    }

    fun deleteDream(dream: Dream) {
        viewModelScope.launch {
            dao.deleteDream(dream)
        }
    }

    fun resetState() {
        _uiState.value = DreamUiState.Idle
    }
}