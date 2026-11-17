package com.nova.sensitivity.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nova.sensitivity.AppViewModel
import com.nova.sensitivity.data.ThemeMode

@Composable
fun SettingsScreen(vm: AppViewModel, pad: PaddingValues) {
    val ctx = LocalContext.current
    var confirmClear by remember { mutableStateOf(false) }
    val version = remember {
        runCatching { ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName }.getOrNull() ?: "1.0"
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = pad.calculateTopPadding() + 16.dp,
            bottom = pad.calculateBottomPadding() + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { Text("Settings", style = MaterialTheme.typography.headlineLarge) }

        item { SectionLabel("THEME") }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeMode.values().forEach { mode ->
                    NovaCard(Modifier.fillMaxWidth(), onClick = { vm.setTheme(mode) }) {
                        Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = vm.theme == mode, onClick = null, modifier = Modifier.padding(12.dp))
                            Text(mode.label, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }

        item { SectionLabel("DATA") }
        item {
            NovaCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Saved profiles: ${vm.profiles.size}", style = MaterialTheme.typography.titleMedium)
                    OutlinedButton(
                        onClick = { confirmClear = true },
                        enabled = vm.profiles.isNotEmpty(),
                    ) { Text("Clear saved profiles") }
                }
            }
        }

        item { SectionLabel("ABOUT NOVA SENSITIVITY") }
        item {
            NovaCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("NOVA Sensitivity  ·  v$version", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Recommended starting sensitivity for mobile shooters, based on your device and game. " +
                            "These are starting points, not perfect settings. Your personal aim style may need some adjustment.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        "Privacy: works offline, no account, no analytics and no permissions. " +
                            "Device details and saved profiles stay on this phone.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("Clear saved profiles?") },
            text = { Text("This deletes all ${vm.profiles.size} saved profiles from this phone. It can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.clearProfiles()
                    confirmClear = false
                }) { Text("Clear") }
            },
            dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("Cancel") } },
        )
    }
}
