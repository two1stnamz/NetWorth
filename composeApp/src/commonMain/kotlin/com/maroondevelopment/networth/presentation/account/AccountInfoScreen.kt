package com.maroondevelopment.networth.presentation.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.maroondevelopment.networth.presentation.components.BottomSheet
import com.maroondevelopment.networth.presentation.model.PositionUiModel
import com.maroondevelopment.networth.presentation.theme.appColors
import kotlinx.coroutines.delay
import networth.composeapp.generated.resources.Res
import networth.composeapp.generated.resources.add
import networth.composeapp.generated.resources.back
import networth.composeapp.generated.resources.delete
import networth.composeapp.generated.resources.edit
import networth.composeapp.generated.resources.refresh
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val MESSAGE_DURATION_MS = 2_500L

@Composable
fun AccountInfoScreen(
    accountId: Long,
    onBack: () -> Unit,
    viewModel: AccountInfoViewModel = koinViewModel(parameters = { parametersOf(accountId) })
) {

    LaunchedEffect(accountId) {
        viewModel.load()
    }

    when (val uiState = viewModel.flow.collectAsState().value) {
        AccountInfoUiState.Loading -> LoadingView()
        AccountInfoUiState.Error -> ErrorView()
        is AccountInfoUiState.Loaded -> LoadedView(
            state = uiState,
            onBack = onBack,
            onRefresh = viewModel::refresh,
            onAddPosition = viewModel::addPosition,
            onUpdateUnits = viewModel::updateUnits,
            onDeletePosition = viewModel::deletePosition,
            onMessageShown = viewModel::messageShown
        )
    }
}

@Composable
private fun LoadedView(
    state: AccountInfoUiState.Loaded,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onAddPosition: (ticker: String, units: String) -> Unit,
    onUpdateUnits: (ticker: String, units: String) -> Unit,
    onDeletePosition: (ticker: String) -> Unit,
    onMessageShown: () -> Unit
) {

    val listState = rememberLazyListState()
    var showAddSheet by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<PositionUiModel?>(null) }
    var deleteTarget by remember { mutableStateOf<PositionUiModel?>(null) }

    LaunchedEffect(state.message) {
        if (state.message != null) {
            delay(MESSAGE_DURATION_MS)
            onMessageShown()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = WindowInsets.safeContent.asPaddingValues().calculateTopPadding()),
            state = listState,
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item(key = "header") {

                AccountHeader(
                    state = state,
                    onBack = onBack,
                    onRefresh = onRefresh,
                    onAdd = { showAddSheet = true }
                )

            }

            state.message?.let { message ->

                item(key = "message") {
                    MessageBanner(message)
                }

            }

            if (state.positions.isEmpty()) {

                item(key = "empty") {
                    EmptyPositions()
                }

            } else {

                items(state.positions, key = { it.ticker }) { position ->

                    PositionCard(
                        position = position,
                        onEdit = { editTarget = position },
                        onDelete = { deleteTarget = position }
                    )

                }

            }

        }

        if (state.isRefreshing) {

            LinearProgressIndicator(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
            )

        }

        if (showAddSheet) {

            AddPositionSheet(
                onCancel = { showAddSheet = false },
                onConfirm = { ticker, units ->
                    showAddSheet = false
                    onAddPosition(ticker, units)
                }
            )

        }

        editTarget?.let { position ->

            EditUnitsSheet(
                position = position,
                onCancel = { editTarget = null },
                onConfirm = { units ->
                    editTarget = null
                    onUpdateUnits(position.ticker, units)
                }
            )

        }

        deleteTarget?.let { position ->

            DeleteConfirmationSheet(
                position = position,
                onCancel = { deleteTarget = null },
                onConfirm = {
                    deleteTarget = null
                    onDeletePosition(position.ticker)
                }
            )

        }

    }
}

@Composable
private fun AccountHeader(
    state: AccountInfoUiState.Loaded,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onAdd: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = onBack) {

                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Res.drawable.back),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "Back"
                )

            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = onAdd) {

                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Res.drawable.add),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "Add a position"
                )

            }

            IconButton(
                onClick = onRefresh,
                enabled = !state.isRefreshing
            ) {

                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Res.drawable.refresh),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "Refresh values"
                )

            }

        }

        Text(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 24.dp),
            text = state.summary.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Text(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            text = state.summary.totalValue,
            fontSize = TextUnit(40f, TextUnitType.Sp),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 12.dp),
            text = state.summary.valueChange,
            color = if (state.summary.isGain) appColors.gain else appColors.loss,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

    }
}

@Composable
private fun PositionCard(
    position: PositionUiModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 12.dp, end = 4.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = position.ticker,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${position.unitsLabel} units",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            }

            Column(
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = position.currentValue,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = position.valueChange,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (position.isGain) appColors.gain else appColors.loss
                )

            }

            IconButton(onClick = onEdit) {

                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(Res.drawable.edit),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "Edit units for ${position.ticker}"
                )

            }

            IconButton(onClick = onDelete) {

                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(Res.drawable.delete),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "Delete ${position.ticker}"
                )

            }

        }

    }
}

@Composable
private fun AddPositionSheet(
    onCancel: () -> Unit,
    onConfirm: (ticker: String, units: String) -> Unit
) {

    val tickerState = rememberTextFieldState()
    val unitsState = rememberTextFieldState()

    BottomSheet {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(25.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Add a position",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                state = tickerState,
                label = { Text("Ticker") }
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                state = unitsState,
                label = { Text("Units") }
            )

            SheetActions(
                confirmLabel = "Add",
                onCancel = onCancel,
                onConfirm = {
                    onConfirm(tickerState.text.toString(), unitsState.text.toString())
                }
            )

        }

    }
}

@Composable
private fun EditUnitsSheet(
    position: PositionUiModel,
    onCancel: () -> Unit,
    onConfirm: (units: String) -> Unit
) {

    val unitsState = rememberTextFieldState(position.unitsLabel)

    BottomSheet {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(25.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Edit ${position.ticker}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                state = unitsState,
                label = { Text("Units") }
            )

            SheetActions(
                confirmLabel = "Save",
                onCancel = onCancel,
                onConfirm = { onConfirm(unitsState.text.toString()) }
            )

        }

    }
}

@Composable
private fun SheetActions(
    confirmLabel: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {

        TextButton(onClick = onCancel) {
            Text("Cancel")
        }

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedButton(onClick = onConfirm) {
            Text(confirmLabel)
        }

    }
}

@Composable
private fun DeleteConfirmationSheet(
    position: PositionUiModel,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {

    BottomSheet {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(25.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Delete ${position.ticker}?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${position.unitsLabel} units worth ${position.currentValue} will be removed from this account.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                TextButton(onClick = onCancel) {
                    Text("Cancel")
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = onConfirm,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = appColors.loss)
                ) {
                    Text("Delete")
                }

            }

        }

    }
}

@Composable
private fun MessageBanner(message: String) {

    Text(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun EmptyPositions() {

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "No positions yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Use + to add the first one.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

    }
}
