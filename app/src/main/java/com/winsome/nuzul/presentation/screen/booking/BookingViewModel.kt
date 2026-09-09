package com.winsome.nuzul.presentation.screen.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = BookingViewModel.Factory::class)
class BookingViewModel @AssistedInject constructor(
    @Assisted val hotelId: String
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(hotelId: String): BookingViewModel
    }

    private val _state = MutableStateFlow(Unit)
    val state = _state.asStateFlow()

    /* Events
	private val _event = MutableSharedFlow<Unit>()
	val event = _event.asSharedFlow()
	 */

    init {
        viewModelScope.launch { }
    }

    /* Intents
	fun processIntent(intent: Unit) {
		when (intent) {
			Unit -> doSomething()
		}
	}

	private fun doSomething() = viewModelScope.launch {  }
	 */
}