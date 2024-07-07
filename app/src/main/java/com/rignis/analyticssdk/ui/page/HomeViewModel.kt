package com.rignis.analyticssdk.ui.page

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor() : ViewModel() {
        private val _counterState: MutableStateFlow<Int> = MutableStateFlow(0)
        val counterState: StateFlow<Int>
            get() = _counterState

        fun onButtonClick() {
            _counterState.update {
                it + 1
            }
        }
    }
