package com.nova.sensitivity.ui

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nova.sensitivity.AppViewModel
import com.nova.sensitivity.NavTab
import com.nova.sensitivity.data.GameCatalog

@Composable
fun GamesScreen(vm: AppViewModel, pad: PaddingValues) {
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
                Text("Games", style = MaterialTheme.typography.headlineLarge)
                Text(
                    "Each game gets its own set of controls. Tap one to select it.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        items(GameCatalog.games, key = { it.id }) { g ->
            val selected = g.id == vm.selectedGameId
            NovaCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    vm.selectGame(g.id)
                    vm.tab = NavTab.Home
                },
                borderColor = if (selected) g.accent else null,
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    GameBadge(g)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(g.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            g.settings.joinToString("  ·  ") { it.label },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
