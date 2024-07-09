/*
 * Copyright (c) [2024] Amitkumar Chaudhary
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
