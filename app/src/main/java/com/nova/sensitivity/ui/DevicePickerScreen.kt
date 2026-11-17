package com.nova.sensitivity.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nova.sensitivity.AppViewModel
import com.nova.sensitivity.data.DeviceCatalog
import com.nova.sensitivity.data.DeviceInfo
import com.nova.sensitivity.data.Tier

@Composable
fun DevicePickerScreen(vm: AppViewModel) {
    var query by remember { mutableStateOf("") }
    var customTier by remember { mutableStateOf(Tier.MID) }
    val results = remember(query) { DeviceCatalog.search(query) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize().systemBarsPadding().imePadding()) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { vm.closeOverlay() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text("Choose Device", style = MaterialTheme.typography.titleLarge)
            }
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                placeholder = { Text("Search brand or model") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (query.isBlank()) {
                    item {
                        DeviceRow(vm.detected, "This phone (detected)") { vm.chooseDevice(vm.detected) }
                    }
                } else {
                    item {
                        NovaCard(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Can't find it? Use a custom device", style = MaterialTheme.typography.titleMedium)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Tier.values().forEach { t ->
                                        FilterChip(
                                            selected = customTier == t,
                                            onClick = { customTier = t },
                                            label = { Text(t.label) },
                                        )
                                    }
                                }
                                Button(
                                    onClick = {
                                        vm.chooseDevice(
                                            DeviceInfo(
                                                name = query.trim(),
                                                brand = "",
                                                tier = customTier,
                                                screenInches = 6.5f,
                                                custom = true,
                                            ),
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                ) { Text("Use \"${query.trim()}\"") }
                            }
                        }
                    }
                }
                items(results, key = { it.brand + it.name }) { d ->
                    DeviceRow(d, null) { vm.chooseDevice(d) }
                }
            }
        }
    }
}

@Composable
private fun DeviceRow(d: DeviceInfo, caption: String?, onClick: () -> Unit) {
    NovaCard(Modifier.fillMaxWidth(), onClick = onClick) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            if (caption != null) SectionLabel(caption.uppercase())
            Text(d.name, style = MaterialTheme.typography.titleMedium)
            Text(
                listOf(d.brand, d.tier.label, "%.1f\"".format(d.screenInches)).filter { it.isNotBlank() }.joinToString("  ·  "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
