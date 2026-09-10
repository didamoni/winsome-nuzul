package com.winsome.nuzul.presentation.screen.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.winsome.nuzul.domain.usecase.favorites.GetFavoriteHotelsUseCase
import com.winsome.nuzul.domain.usecase.favorites.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteHotelsUseCase: GetFavoriteHotelsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeFavorites()
    }

    fun onIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.OnRemoveFavorite -> removeFavorite(intent.hotelId)
        }
    }

    private fun observeFavorites() {
        getFavoriteHotelsUseCase()
            .onEach { hotels ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        favorites = hotels,
                        errorMessage = null
                    )
                }
            }
            .catch { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Could not load favorites"
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun removeFavorite(hotelId: String) {
        viewModelScope.launch {
            toggleFavoriteUseCase(hotelId, isFavorite = false)
        }
    }
}