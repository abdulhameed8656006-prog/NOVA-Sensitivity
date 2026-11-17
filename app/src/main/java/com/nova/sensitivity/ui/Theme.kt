package com.nova.sensitivity.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nova.sensitivity.data.ThemeMode

val Mint = Color(0xFF38E8B0)

private val DarkScheme = darkColorScheme(
    primary = Mint,
    onPrimary = Color(0xFF00382A),
    secondary = Color(0xFF8AB4FF),
    background = Color(0xFF0E1116),
    onBackground = Color(0xFFE8ECF1),
    surface = Color(0xFF12161C),
    onSurface = Color(0xFFE8ECF1),
    surfaceVariant = Color(0xFF1A1F27),
    onSurfaceVariant = Color(0xFF9AA4B2),
    outline = Color(0xFF2A313B),
    outlineVariant = Color(0xFF222832),
)

private val AmoledScheme = DarkScheme.copy(
    background = Color.Black,
    surface = Color(0xFF080A0D),
    surfaceVariant = Color(0xFF14181E),
)

private val LightScheme = lightColorScheme(
    primary = Color(0xFF00A67A),
    onPrimary = Color.White,
    secondary = Color(0xFF3B6FD6),
    background = Color(0xFFF4F6F9),
    onBackground = Color(0xFF12161C),
    surface = Color.White,
    onSurface = Color(0xFF12161C),
    surfaceVariant = Color.White,
    onSurfaceVariant = Color(0xFF5B6573),
    outline = Color(0xFFCBD2DB),
    outlineVariant = Color(0xFFE1E6ED),
)

private val NovaType = Typography(
    headlineLarge = TextStyle(fontSize = 30.sp, lineHeight = 34.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp),
    titleLarge = TextStyle(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
    labelSmall = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
)

@Composable
fun NovaTheme(mode: ThemeMode, content: @Composable () -> Unit) {
    val scheme = when (mode) {
        ThemeMode.DARK -> DarkScheme
        ThemeMode.AMOLED -> AmoledScheme
        ThemeMode.LIGHT -> LightScheme
    }
    MaterialTheme(colorScheme = scheme, typography = NovaType, content = content)
}

@Composable
fun NovaCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    val shape = RoundedCornerShape(20.dp)
    val border = BorderStroke(
        if (borderColor != null) 2.dp else 1.dp,
        borderColor ?: MaterialTheme.colorScheme.outlineVariant,
    )
    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier, shape = shape, colors = colors, border = border, content = content)
    } else {
        Card(modifier = modifier, shape = shape, colors = colors, border = border, content = content)
    }
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}
