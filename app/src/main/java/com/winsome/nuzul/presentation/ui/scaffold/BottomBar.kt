package com.winsome.nuzul.presentation.ui.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.winsome.nuzul.presentation.ui.navigation.Route
import com.winsome.nuzul.presentation.ui.theme.NuzulTheme

@Composable
fun NuzulBottomBar(
    currentRoute: Route,
    onNavItemClick: (Route) -> Unit
) = NavigationBar {
    Route.Main.entries.forEach { route ->
        val selected = currentRoute == route

        NavigationBarItem(
            selected = selected,
            enabled = !selected,
            onClick = { onNavItemClick(route) },
            icon = {
                Icon(
                    painter = painterResource(
                        if (selected) route.filledIcon else route.outlinedIcon
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = stringResource(route.label),
                    maxLines = 1
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomBarPreview() = NuzulTheme {
    Box(
        modifier = Modifier.padding(top = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        NuzulBottomBar(Route.Main.Explore) {}
    }
}