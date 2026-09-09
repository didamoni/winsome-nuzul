package com.winsome.nuzul.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation3.runtime.rememberNavBackStack
import com.winsome.nuzul.presentation.ui.navigation.NuzulNavDisplay
import com.winsome.nuzul.presentation.ui.navigation.Route
import com.winsome.nuzul.presentation.ui.scaffold.NuzulScaffold
import com.winsome.nuzul.presentation.ui.theme.NuzulTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val backStack = rememberNavBackStack(Route.Main.Explore)

            NuzulTheme {
                NuzulScaffold(backStack) {
                    NuzulNavDisplay(backStack)
                }
            }
        }
    }
}