package com.winsome.nuzul.presentation.screen.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.winsome.nuzul.presentation.ui.theme.NuzulTheme

@Composable
fun BookingScreen() {
    var state by remember { mutableStateOf(Unit) }
    BookingScreenContent(state) {}
}

@Composable
private fun BookingScreenContent(
    state: Unit, // use your screen's state
    onAction: () -> Unit
) = Column(
    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        .padding(16.dp).imePadding(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceAround
) {
    Text(
        text = "BookingScreen",
        style = typography.headlineMedium,
        color = colorScheme.onBackground
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BookingScreenPreview() = NuzulTheme {
    Box(Modifier.background(colorScheme.background)) {
        BookingScreenContent(Unit) {}
    }
}