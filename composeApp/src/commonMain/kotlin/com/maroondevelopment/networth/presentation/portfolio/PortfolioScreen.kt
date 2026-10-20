package com.maroondevelopment.networth.presentation.portfolio

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.maroondevelopment.networth.presentation.account.ErrorView
import com.maroondevelopment.networth.presentation.account.LoadingView
import com.maroondevelopment.networth.presentation.components.BottomSheet
import com.maroondevelopment.networth.presentation.components.FloatingToolbar
import com.maroondevelopment.networth.presentation.components.MESSAGE_DURATION_MS
import com.maroondevelopment.networth.presentation.components.MessageBanner
import com.maroondevelopment.networth.presentation.components.SummaryCard
import com.maroondevelopment.networth.presentation.components.TOOLBAR_CONTENT_PADDING
import com.maroondevelopment.networth.presentation.components.ToolbarAction
import com.maroondevelopment.networth.presentation.model.InstitutionSummaryUiModel
import com.maroondevelopment.networth.presentation.theme.appColors
import kotlinx.coroutines.delay
import networth.composeapp.generated.resources.Res
import networth.composeapp.generated.resources.add
import networth.composeapp.generated.resources.refresh
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PortfolioScreen(
    onInstitutionSelected: (Long) -> Unit,
    onHistorySelected: () -> Unit,
    viewModel: PortfolioViewModel = koinViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    when (val uiStateUpdate = viewModel.flow.collectAsState().value) {
        PortfolioUiState.Error -> ErrorView()
        PortfolioUiState.Loading -> LoadingView()
        is PortfolioUiState.Success ->
            LoadedView(uiStateUpdate, onInstitutionSelected, onHistorySelected, viewModel)
    }

}

@Composable
private fun LoadedView(
    state: PortfolioUiState.Success,
    onInstitutionSelected: (Long) -> Unit,
    onHistorySelected: () -> Unit,
    viewModel: PortfolioViewModel
) {

    val portfolio = state.model
    val listState = rememberLazyListState()
    var showModal by remember { mutableStateOf(false) }
    var renameTarget by remember { mutableStateOf<InstitutionSummaryUiModel?>(null) }
    var deleteTarget by remember { mutableStateOf<InstitutionSummaryUiModel?>(null) }

    LaunchedEffect(state.message) {
        if (state.message != null) {
            delay(MESSAGE_DURATION_MS)
            viewModel.messageShown()
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
            contentPadding = PaddingValues(bottom = TOOLBAR_CONTENT_PADDING),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item(key = "total") {

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                ) {

                    Text(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp, bottom = 12.dp)
                                .clickable(onClick = onHistorySelected)
                                .padding(horizontal = 25.dp),
                        text = portfolio.totalValue,
                        fontSize = TextUnit(45f, TextUnitType.Sp),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                }

            }

            state.message?.let { message ->

                item(key = "message") {
                    MessageBanner(message)
                }

            }

            items(portfolio.institutions, key = { it.id }) { item ->

                SummaryCard(
                    title = item.title,
                    totalValue = item.totalValue,
                    valueChange = item.valueChange,
                    isGain = item.isGain,
                    onClick = { onInstitutionSelected(item.id) },
                    onRename = { renameTarget = item },
                    onDelete = { deleteTarget = item }
                )

            }

        }

        FloatingToolbar(
            modifier = Modifier.align(Alignment.BottomCenter),
            actions = listOf(
                ToolbarAction(
                    icon = Res.drawable.add,
                    contentDescription = "Add an institution",
                    onClick = { showModal = true }
                ),
                ToolbarAction(
                    icon = Res.drawable.refresh,
                    contentDescription = "Refresh values",
                    onClick = viewModel::refresh
                )
            )
        )

        if (showModal) {

            val nameState = rememberTextFieldState()

            BottomSheet {

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(25.dp)
                ) {

                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        state = nameState,
                        label = { Text("Enter institution name") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )

                    Button(
                        onClick = {
                            showModal = false
                            viewModel.createNewInstitution(nameState.text.toString())
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Create")
                    }
                }

            }

        }

        renameTarget?.let { institution ->

            RenameInstitutionSheet(
                institution = institution,
                onCancel = { renameTarget = null },
                onConfirm = { name ->
                    renameTarget = null
                    viewModel.renameInstitution(institution.id, name)
                }
            )

        }

        deleteTarget?.let { institution ->

            DeleteInstitutionSheet(
                institution = institution,
                onCancel = { deleteTarget = null },
                onConfirm = {
                    deleteTarget = null
                    viewModel.deleteInstitution(institution.id, institution.title)
                }
            )

        }

    }
}

@Composable
private fun RenameInstitutionSheet(
    institution: InstitutionSummaryUiModel,
    onCancel: () -> Unit,
    onConfirm: (name: String) -> Unit
) {

    val nameState = rememberTextFieldState(institution.title)

    BottomSheet {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(25.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Rename ${institution.title}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                state = nameState,
                label = { Text("Institution name") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                TextButton(onClick = onCancel) {
                    Text("Cancel")
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(onClick = { onConfirm(nameState.text.toString()) }) {
                    Text("Save")
                }

            }

        }

    }
}

@Composable
private fun DeleteInstitutionSheet(
    institution: InstitutionSummaryUiModel,
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
                text = "Delete ${institution.title}?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = institution.deleteWarning(),
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

/** Spells out what else a delete takes with the institution. */
private fun InstitutionSummaryUiModel.deleteWarning(): String = when (accountCount) {
    0 -> "This institution holds no accounts. It will be removed."
    1 -> "1 account and its positions will be removed, worth $totalValue in total."
    else -> "$accountCount accounts and their positions will be removed, worth $totalValue in total."
}
