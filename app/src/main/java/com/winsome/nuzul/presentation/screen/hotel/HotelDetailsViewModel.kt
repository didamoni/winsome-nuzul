package com.winsome.nuzul.presentation.screen.hotel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotelDetailsViewModel @Inject constructor(
    // private val useCase: Unit
) : ViewModel() {

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