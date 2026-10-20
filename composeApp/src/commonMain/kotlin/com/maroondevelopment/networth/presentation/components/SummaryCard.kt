package com.maroondevelopment.networth.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.maroondevelopment.networth.presentation.theme.appColors
import networth.composeapp.generated.resources.Res
import networth.composeapp.generated.resources.delete
import networth.composeapp.generated.resources.edit
import org.jetbrains.compose.resources.painterResource

/**
 * A tappable card showing something's name against its value and change.
 *
 * Shared by the portfolio screen (one per institution) and the institution screen (one per
 * account) so both levels of the hierarchy read identically.
 *
 * [onRename] and [onDelete] are optional: pass either and the card grows that icon, while the
 * card body stays tappable for navigation.
 */
@Composable
fun SummaryCard(
    title: String,
    totalValue: String,
    valueChange: String,
    isGain: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRename: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {

    val hasActions = onRename != null || onDelete != null

    Card(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        top = 16.dp,
                        // The icon buttons bring their own padding, so the card tightens up.
                        end = if (hasActions) 4.dp else 16.dp,
                        bottom = 16.dp
                    ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Column(
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = totalValue,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = valueChange,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isGain) appColors.gain else appColors.loss
                )

            }

            onRename?.let { rename ->

                IconButton(onClick = rename) {

                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(Res.drawable.edit),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentDescription = "Rename $title"
                    )

                }

            }

            onDelete?.let { delete ->

                IconButton(onClick = delete) {

                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(Res.drawable.delete),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentDescription = "Delete $title"
                    )

                }

            }

        }

    }
}
