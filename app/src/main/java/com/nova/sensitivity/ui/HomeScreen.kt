package com.nova.sensitivity.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nova.sensitivity.AppViewModel
import com.nova.sensitivity.data.GameCatalog
import com.nova.sensitivity.data.GameDef

@Composable
fun HomeScreen(vm: AppViewModel, pad: PaddingValues) {
    val game = GameCatalog.byId(vm.selectedGameId)
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = pad.calculateTopPadding() + 16.dp,
            bottom = pad.calculateBottomPadding() + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item { Header() }
        item { DeviceCard(vm) }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionLabel("GAME")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(GameCatalog.games, key = { it.id }) { g ->
                        GameChip(g, g.id == vm.selectedGameId) { vm.selectGame(g.id) }
                    }
                }
            }
        }
        item { SelectedGameCard(game) }
        item {
            Button(
                onClick = { vm.generate() },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text("GENERATE SENSITIVITY", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun Header() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
            Spacer(Modifier.width(8.dp))
            SectionLabel("NOVA")
        }
        Text("NOVA Sensitivity", style = MaterialTheme.typography.headlineLarge)
        Text(
            "Your device. Your game. Your sensitivity.",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            "Get a recommended sensitivity setup based on your device and game.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DeviceCard(vm: AppViewModel) {
    val d = vm.device
    val source = if (d.custom) "Custom device" else if (d.detected) "Detected on this phone" else "Selected manually"
    NovaCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionLabel("DEVICE")
            Text(d.name, style = MaterialTheme.typography.titleLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(
                "$source  ·  ${d.tier.label}  ·  ${"%.1f".format(d.screenInches)}\"",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (d.detected) {
                Text(
                    "Based on the model name, memory and screen size Android shares with apps.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(4.dp))
            OutlinedButton(onClick = { vm.openPicker() }, shape = RoundedCornerShape(12.dp)) {
                Text("Change Device")
            }
        }
    }
}

@Composable
private fun GameChip(game: GameDef, selected: Boolean, onClick: () -> Unit) {
    val target = if (selected) game.accent else MaterialTheme.colorScheme.outlineVariant
    val border by animateColorAsState(target, label = "chipBorder")
    NovaCard(modifier = Modifier.width(148.dp), onClick = onClick, borderColor = border) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            GameBadge(game)
            Text(
                game.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(44.dp),
            )
        }
    }
}

@Composable
private fun SelectedGameCard(game: GameDef) {
    NovaCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionLabel("YOU'LL GET")
            Text(game.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                game.settings.joinToString("  ·  ") { it.label },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
