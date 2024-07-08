package com.rignis.demo.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomePage(testFunction: () -> Unit) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val state = viewModel.counterState.collectAsState()
    HomePageLayout(viewModel::onButtonClick, state, testFunction)
}

@Composable
private fun HomePageLayout(
    onButtonClick: () -> Unit,
    counterState: State<Int>,
    testFunction: () -> Unit,
) {
    Scaffold { innerPadding ->
        Column(
            modifier =
            Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Counter value = ${counterState.value}")
            Spacer(Modifier.height(12.dp))
            Button(onButtonClick) {
                Text("Button")
            }
            Spacer(Modifier.height(12.dp))
            Button(testFunction) {
                Text("Test")
            }
        }
    }
}

@Preview
@Composable
private fun PreviewHomePageLayout() {
    val state =
        remember {
            mutableIntStateOf(0)
        }
    HomePageLayout({ state.intValue++ }, state, {})
}
