package com.maroondevelopment.networth.presentation.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.maroondevelopment.networth.presentation.account.ErrorView
import com.maroondevelopment.networth.presentation.account.LoadingView
import com.maroondevelopment.networth.presentation.components.ValueLineChart
import com.maroondevelopment.networth.presentation.model.PortfolioHistoryUiModel
import networth.composeapp.generated.resources.Res
import networth.composeapp.generated.resources.back
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

private val GainGreen = Color(0xFF1B873F)
private val LossRed = Color(0xFFC62828)

@Composable
fun PortfolioHistoryScreen(
    onBack: () -> Unit,
    viewModel: PortfolioHistoryViewModel = koinViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    when (val uiState = viewModel.flow.collectAsState().value) {
        PortfolioHistoryUiState.Loading -> LoadingView()
        PortfolioHistoryUiState.Error -> ErrorView()
        PortfolioHistoryUiState.Empty -> EmptyHistoryView(onBack)
        is PortfolioHistoryUiState.Loaded -> LoadedView(uiState.model, onBack)
    }
}

@Composable
private fun LoadedView(
    model: PortfolioHistoryUiModel,
    onBack: () -> Unit
) {

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(top = WindowInsets.safeContent.asPaddingValues().calculateTopPadding())
    ) {

        BackRow(onBack)

        Text(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp),
            text = "Portfolio value",
            style = MaterialTheme.typography.titleMedium,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )

        Text(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            text = model.latestValue,
            fontSize = TextUnit(40f, TextUnitType.Sp),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            text = "${model.changeLabel}  ·  ${model.rangeLabel}",
            color = if (model.isGain) GainGreen else LossRed,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        if (model.isPlottable) {

            ValueLineChart(
                points = model.points,
                lineColor = if (model.isGain) GainGreen else LossRed,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(horizontal = 20.dp, vertical = 24.dp)
            )

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = model.points.first().dayLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )

                Text(
                    text = model.points.last().dayLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )

            }

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                ValueStat("Low", model.lowLabel)

                ValueStat("High", model.highLabel)

                ValueStat("Days", model.points.size.toString())

            }

        } else {

            SingleReadingNotice(model)

        }

    }
}

@Composable
private fun ValueStat(label: String, value: String) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.DarkGray
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

    }
}

@Composable
private fun SingleReadingNotice(model: PortfolioHistoryUiModel) {

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "One reading so far",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = "Recorded ${model.rangeLabel}. Refresh your portfolio on another day to start the graph.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )

    }
}

@Composable
private fun EmptyHistoryView(onBack: () -> Unit) {

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(top = WindowInsets.safeContent.asPaddingValues().calculateTopPadding())
    ) {

        BackRow(onBack)

        Spacer(modifier = Modifier.height(60.dp))

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "No history yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = "Refresh your portfolio to record its value. One value is kept per day.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                textAlign = TextAlign.Center
            )

        }

    }
}

@Composable
private fun BackRow(onBack: () -> Unit) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = onBack) {

            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(Res.drawable.back),
                tint = Color.DarkGray,
                contentDescription = "Back"
            )

        }

    }
}
