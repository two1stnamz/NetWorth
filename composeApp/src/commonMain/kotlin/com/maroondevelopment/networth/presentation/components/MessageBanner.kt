package com.maroondevelopment.networth.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** How long transient feedback stays on screen before the view asks for it to be cleared. */
const val MESSAGE_DURATION_MS = 2_500L

/** Transient feedback from the last add / rename / delete attempt. */
@Composable
fun MessageBanner(
    message: String,
    modifier: Modifier = Modifier
) {

    Text(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
}
