package com.maroondevelopment.networth.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

/**
 * A sheet anchored to the bottom of the screen over a scrim.
 *
 * Pass [onDismissRequest] to let a tap on the scrim close the sheet; leave it out and the scrim
 * only blocks what is behind it, so the sheet's own buttons are the only way out.
 */
@Composable
fun BottomSheet(
    onDismissRequest: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {

    val scrimInteractionSource = remember { MutableInteractionSource() }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f))
            .then(
                if (onDismissRequest == null) {
                    Modifier
                } else {
                    Modifier.clickable(
                        interactionSource = scrimInteractionSource,
                        indication = null,
                        onClick = onDismissRequest
                    )
                }
            ),
        contentAlignment = Alignment.BottomCenter) {

        Box(
            modifier =
                Modifier
                    .padding(horizontal = 25.dp)
                    .padding(bottom = 25.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(25.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    // A tap on the sheet is not a tap outside it, so it stops here rather than
                    // reaching the scrim behind. Consumed without click semantics, so the sheet
                    // is not announced as one big button.
                    .pointerInput(Unit) { detectTapGestures { } }
        ) {

            content()

        }
    }
}
