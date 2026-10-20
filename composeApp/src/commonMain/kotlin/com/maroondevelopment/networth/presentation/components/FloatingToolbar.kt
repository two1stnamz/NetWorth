package com.maroondevelopment.networth.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/**
 * One button in a [FloatingToolbar].
 */
data class ToolbarAction(
    val icon: DrawableResource,
    val contentDescription: String,
    val enabled: Boolean = true,
    val onClick: () -> Unit
)

/**
 * A floating pill of icon actions, meant to sit at the bottom centre of a screen.
 *
 * The caller positions it — typically `Modifier.align(Alignment.BottomCenter)` inside a [androidx.compose.foundation.layout.Box]
 * — but the safe-area inset is handled here so every screen clears the system navigation bar the
 * same way. Scrolling content needs bottom content padding of its own so the last item is not
 * left underneath the toolbar; [TOOLBAR_CONTENT_PADDING] is the amount to reserve.
 */
@Composable
fun FloatingToolbar(
    actions: List<ToolbarAction>,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier =
            modifier
                .padding(bottom = WindowInsets.safeContent.asPaddingValues().calculateBottomPadding())
                .padding(bottom = 16.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 6.dp
    ) {

        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            actions.forEach { action ->

                IconButton(
                    onClick = action.onClick,
                    enabled = action.enabled,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {

                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(action.icon),
                        contentDescription = action.contentDescription
                    )

                }

            }

        }

    }
}

/** Bottom content padding a scrollable needs so its last item clears the toolbar. */
val TOOLBAR_CONTENT_PADDING = 104.dp
