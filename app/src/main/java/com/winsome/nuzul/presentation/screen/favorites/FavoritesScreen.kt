package com.winsome.nuzul.presentation.screen.favorites

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.winsome.nuzul.presentation.ui.theme.NuzulTheme

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onHotelClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    FavoritesScreenContent(state, onHotelClick)
}

@Composable
private fun FavoritesScreenContent(
    state: Unit, // use your screen's state
    onHotelClick: (String) -> Unit
) = Column(
    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        .padding(16.dp).imePadding(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceAround
) {
    Text(
        text = "FavoritesScreen",
        style = typography.headlineMedium,
        color = colorScheme.onBackground
    )

    repeat(5) {
        OutlinedButton(
            onClick = { onHotelClick("hotel_$it") }
        ) {
            Text("Hotel $it")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FavoritesScreenPreview() = NuzulTheme {
    Box(Modifier.background(colorScheme.background)) {
        FavoritesScreenContent(Unit) {}
    }
}