package dev.lackluster.mihelper.app.screen.systemui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.lackluster.hyperx.R
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.ColorPicker
import top.yukonga.miuix.kmp.basic.ColorSpace
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperDialog
import androidx.core.graphics.toColorInt

@Composable
fun ColorPickerDialog(
    visible: Boolean,
    title: String,
    initialColor: Color,
    onConfirm: (Color) -> Unit,
    onDismissRequest: () -> Unit,
    onDismissFinished: () -> Unit = {},
    supportAlpha: Boolean = true,
    message: String? = null,
    negativeText: String = stringResource(R.string.button_cancel),
    positiveText: String = stringResource(R.string.button_ok),
) {
    val hapticFeedback = LocalHapticFeedback.current

    var selectedColor by remember(initialColor, visible) {
        mutableStateOf(initialColor)
    }
    var colorHex by remember(initialColor, visible) {
        val argb = initialColor.toArgb()
        val hexString = if (supportAlpha) {
            String.format("%08X", argb)
        } else {
            String.format("%06X", argb and 0xFFFFFF) // 强行剥离 Alpha 通道，只留 RGB
        }
        mutableStateOf(hexString)
    }

    SuperDialog(
        show = visible,
        title = title,
        summary = message,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ColorPicker(
                    color = selectedColor,
                    onColorChanged = { newColor ->
                        selectedColor = newColor
                        val argb = newColor.toArgb()
                        colorHex = if (supportAlpha) {
                            String.format("%08X", argb)
                        } else {
                            String.format("%06X", argb and 0xFFFFFF)
                        }
                    },
                    colorSpace = ColorSpace.HSV,
                    showPreview = true,
                )
                TextField(
                    value = colorHex,
                    onValueChange = { newHex ->
                        val filteredHex = newHex.filter {
                            it.isDigit() || it in 'a'..'f' || it in 'A'..'F'
                        }.uppercase()
                        val maxLength = if (supportAlpha) 8 else 6
                        if (filteredHex.length <= maxLength) {
                            colorHex = filteredHex
                            val isValidLength = if (supportAlpha) {
                                filteredHex.length == 6 || filteredHex.length == 8
                            } else {
                                filteredHex.length == 6
                            }
                            if (isValidLength) {
                                try {
                                    val parseableHex = if (filteredHex.length == 6) "#FF$filteredHex" else "#$filteredHex"
                                    selectedColor = Color(parseableHex.toColorInt())
                                } catch (_: Exception) {}
                            }
                        }
                    },
                    leadingIcon = {
                        Text(
                            "HEX: #",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(start = 16.dp),
                        )
                    },
                    modifier = Modifier.padding(top = 12.dp),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        modifier = Modifier.weight(1f),
                        text = negativeText,
                        minHeight = 50.dp,
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                            onDismissRequest()
                        }
                    )

                    TextButton(
                        modifier = Modifier.weight(1f),
                        text = positiveText,
                        minHeight = 50.dp,
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                            val finalColor = if (supportAlpha) {
                                selectedColor
                            } else {
                                selectedColor.copy(alpha = 1f)
                            }
                            onConfirm(finalColor)
                            onDismissRequest()
                        }
                    )
                }
            }
        }
    )
}