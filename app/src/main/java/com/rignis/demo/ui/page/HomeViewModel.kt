package com.rignis.demo.ui.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rignis.analyticssdk.RignisAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _counterState: MutableStateFlow<Int> = MutableStateFlow(0)
    val counterState: StateFlow<Int>
        get() = _counterState

    fun onButtonClick() {
        viewModelScope.launch(Dispatchers.IO) {
            val newValue = _counterState.updateAndGet {
                it + 1
            }
            RignisAnalytics.sendEvent(
                "home_button_click", mapOf(
                    "counter" to newValue.toString()
                )
            )
        }
    }
}
