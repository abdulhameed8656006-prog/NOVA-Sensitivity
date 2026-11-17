package com.nova.sensitivity.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nova.sensitivity.AppViewModel
import com.nova.sensitivity.data.Recommender
import com.nova.sensitivity.data.SettingDef

@Composable
fun ResultScreen(vm: AppViewModel) {
    val game = vm.resultGame
    val ctx = LocalContext.current
    val clipboard = LocalClipboardManager.current

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize().systemBarsPadding()) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { vm.closeOverlay() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text("Recommended Sensitivity", style = MaterialTheme.typography.titleLarge)
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    NovaCard(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            GameBadge(game)
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text("Device: ${vm.resultDevice.name}", style = MaterialTheme.typography.bodyMedium)
                                Text("Game: ${game.name}", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
                item {
                    Text(
                        "These settings are a starting point based on your device and game. Your personal aim style may require some adjustment.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                item { TuneCard(vm) }
                items(game.settings, key = { it.key }) { def -> SettingCard(vm, def, game.accent) }
                item {
                    Text(
                        game.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Surface(tonalElevation = 3.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = {
                            val text = Recommender.exportText(game, vm.resultDevice, vm.values.toMap())
                            clipboard.setText(AnnotatedString(text))
                            Toast.makeText(ctx, "Settings copied", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                    ) { Text("Copy Settings") }
                    Button(
                        onClick = {
                            vm.saveProfile()
                            Toast.makeText(ctx, "Profile saved", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                    ) { Text("Save Profile") }
                }
            }
        }
    }
}

@Composable
private fun TuneCard(vm: AppViewModel) {
    NovaCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Fine-tune", style = MaterialTheme.typography.titleMedium)
            Text(
                "Try it in a training match, then nudge everything at once or adjust each setting below.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilledTonalButton(
                    onClick = { vm.nudgeAll(1) },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                ) { Text("Too slow?\nIncrease", textAlign = TextAlign.Center) }
                FilledTonalButton(
                    onClick = { vm.nudgeAll(-1) },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                ) { Text("Too fast?\nDecrease", textAlign = TextAlign.Center) }
            }
            TextButton(onClick = { vm.resetToRecommended() }) { Text("Reset to Recommended") }
        }
    }
}

@Composable
private fun SettingCard(vm: AppViewModel, def: SettingDef, accent: Color) {
    val v = vm.values[def.key] ?: def.base
    val rec = vm.recommended[def.key] ?: def.base
    NovaCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(def.label, style = MaterialTheme.typography.titleMedium)
                    Text(
                        def.hint,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    Recommender.format(def, v),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 26.sp,
                    color = accent,
                )
            }
            Slider(
                value = v,
                onValueChange = { vm.setValue(def, it) },
                valueRange = def.min..def.max,
                colors = SliderDefaults.colors(
                    thumbColor = accent,
                    activeTrackColor = accent,
                    inactiveTrackColor = MaterialTheme.colorScheme.outline,
                ),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Recommended: ${Recommender.format(def, rec)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                FilledTonalIconButton(onClick = { vm.bump(def, -1) }) { Text("−", fontSize = 20.sp) }
                Spacer(Modifier.width(8.dp))
                FilledTonalIconButton(onClick = { vm.bump(def, 1) }) { Text("+", fontSize = 20.sp) }
            }
        }
    }
}
