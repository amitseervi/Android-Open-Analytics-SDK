package com.rignis.analyticssdk.ui.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rignis.analyticssdk.Analytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val analytics: Analytics) : ViewModel() {
    private val _counterState: MutableStateFlow<Int> = MutableStateFlow(0)
    val counterState: StateFlow<Int>
        get() = _counterState

    fun onButtonClick() {
        viewModelScope.launch(Dispatchers.IO) {
            val newValue = _counterState.updateAndGet {
                it + 1
            }
            analytics.sendEvent(
                "home_button_click", mapOf(
                    "counter" to newValue.toString()
                )
            )
        }
    }
}
