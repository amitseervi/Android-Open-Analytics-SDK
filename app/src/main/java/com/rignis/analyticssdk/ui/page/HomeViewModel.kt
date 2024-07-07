package com.rignis.analyticssdk.ui.page

import androidx.lifecycle.ViewModel
import com.rignis.analyticssdk.Analytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val analytics: Analytics) : ViewModel() {
    private val _counterState: MutableStateFlow<Int> = MutableStateFlow(0)
    val counterState: StateFlow<Int>
        get() = _counterState

    fun onButtonClick() {
        analytics.sendEvent(
            "home_button_click", mapOf(
                "counter" to _counterState.value.toString()
            )
        )
        _counterState.update {
            it + 1
        }
    }
}
