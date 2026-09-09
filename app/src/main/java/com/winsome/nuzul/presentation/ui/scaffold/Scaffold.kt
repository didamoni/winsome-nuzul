package com.winsome.nuzul.presentation.ui.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.winsome.nuzul.presentation.ui.navigation.NuzulNavDisplay
import com.winsome.nuzul.presentation.ui.navigation.Route
import com.winsome.nuzul.presentation.ui.theme.NuzulTheme

@Composable
fun NuzulScaffold(
    backStack: NavBackStack<NavKey>,
    content: @Composable () -> Unit = {}
) = Scaffold(
    content = { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            content()
        }
    },
    bottomBar = {
        val currentRoute = backStack.lastOrNull()
        if (currentRoute is Route.Main) {
            NuzulBottomBar(currentRoute) { route ->
                backStack.clear()
                backStack.add(Route.Main.Explore)
                if (route != Route.Main.Explore) backStack.add(route)
            }
        }
    }
)

@Preview(
    showBackground = true, showSystemUi = true,
    device = "spec:parent=pixel_5,navigation=buttons",
)
@Composable
private fun ScaffoldPreview() = NuzulTheme {
    val backStack = rememberNavBackStack(Route.Main.Explore)
    NuzulScaffold(backStack) {
        NuzulNavDisplay(backStack)
    }
}