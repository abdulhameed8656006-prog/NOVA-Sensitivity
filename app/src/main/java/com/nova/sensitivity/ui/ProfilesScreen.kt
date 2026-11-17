package com.nova.sensitivity.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.nova.sensitivity.AppViewModel
import com.nova.sensitivity.data.GameCatalog
import com.nova.sensitivity.data.Recommender
import java.text.DateFormat
import java.util.Date

@Composable
fun ProfilesScreen(vm: AppViewModel, pad: PaddingValues) {
    val ctx = LocalContext.current
    val clipboard = LocalClipboardManager.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = pad.calculateTopPadding() + 16.dp,
            bottom = pad.calculateBottomPadding() + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("My Profiles", style = MaterialTheme.typography.headlineLarge)
                Text(
                    "Saved only on this phone. No account needed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (vm.profiles.isEmpty()) {
            item {
                NovaCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Nothing saved yet", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Generate a sensitivity, fine-tune it, then tap Save Profile.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
        items(vm.profiles, key = { it.id }) { p ->
            val game = GameCatalog.byId(p.gameId)
            NovaCard(Modifier.fillMaxWidth(), onClick = { vm.openProfile(p) }) {
                Column(Modifier.padding(start = 16.dp, top = 16.dp, end = 8.dp, bottom = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GameBadge(game, size = 40.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("${game.name} — ${p.device.name}", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Saved " + DateFormat.getDateInstance().format(Date(p.savedAt)),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        IconButton(onClick = { vm.deleteProfile(p.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete profile")
                        }
                    }
                    Row {
                        TextButton(onClick = { vm.openProfile(p) }) { Text("Open") }
                        TextButton(onClick = {
                            clipboard.setText(AnnotatedString(Recommender.exportText(game, p.device, p.values)))
                            Toast.makeText(ctx, "Settings copied", Toast.LENGTH_SHORT).show()
                        }) { Text("Copy Settings") }
                    }
                }
            }
        }
    }
}
