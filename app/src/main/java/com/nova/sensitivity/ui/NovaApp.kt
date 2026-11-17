package com.nova.sensitivity.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.nova.sensitivity.AppViewModel
import com.nova.sensitivity.NavTab
import com.nova.sensitivity.Overlay

private fun iconFor(tab: NavTab): ImageVector = when (tab) {
    NavTab.Home -> Icons.Default.Home
    NavTab.Games -> Icons.Default.PlayArrow
    NavTab.Profiles -> Icons.Default.Favorite
    NavTab.Settings -> Icons.Default.Settings
}

@Composable
fun NovaApp(vm: AppViewModel) {
    BackHandler(enabled = vm.overlay != Overlay.None) { vm.closeOverlay() }
    BackHandler(enabled = vm.overlay == Overlay.None && vm.tab != NavTab.Home) { vm.tab = NavTab.Home }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    NavTab.values().forEach { t ->
                        NavigationBarItem(
                            selected = vm.tab == t,
                            onClick = { vm.tab = t },
                            icon = { Icon(iconFor(t), contentDescription = t.label) },
                            label = { Text(t.label, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                            ),
                        )
                    }
                }
            },
        ) { pad ->
            when (vm.tab) {
                NavTab.Home -> HomeScreen(vm, pad)
                NavTab.Games -> GamesScreen(vm, pad)
                NavTab.Profiles -> ProfilesScreen(vm, pad)
                NavTab.Settings -> SettingsScreen(vm, pad)
            }
        }

        AnimatedVisibility(
            visible = vm.overlay == Overlay.Result,
            enter = fadeIn() + slideInVertically { it / 12 },
            exit = fadeOut(),
        ) { ResultScreen(vm) }

        AnimatedVisibility(
            visible = vm.overlay == Overlay.Picker,
            enter = fadeIn() + slideInVertically { it / 12 },
            exit = fadeOut(),
        ) { DevicePickerScreen(vm) }
    }
}
