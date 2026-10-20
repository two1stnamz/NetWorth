package com.maroondevelopment.networth.presentation.institution

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ButtonDefaults
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
import com.maroondevelopment.networth.presentation.model.AccountSummaryUiModel
import com.maroondevelopment.networth.presentation.theme.appColors
import kotlinx.coroutines.delay
import networth.composeapp.generated.resources.Res
import networth.composeapp.generated.resources.add
import networth.composeapp.generated.resources.back
import networth.composeapp.generated.resources.refresh
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun InstitutionScreen(
    institutionId: Long,
    onAccountSelected: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: InstitutionViewModel = koinViewModel(parameters = { parametersOf(institutionId) })
) {

    LaunchedEffect(institutionId) {
        viewModel.load()
    }

    when (val uiState = viewModel.flow.collectAsState().value) {
        InstitutionUiState.Loading -> LoadingView()
        InstitutionUiState.Error -> ErrorView()
        is InstitutionUiState.Loaded -> LoadedView(
            state = uiState,
            onAccountSelected = onAccountSelected,
            onBack = onBack,
            onRefresh = viewModel::refresh,
            onCreateAccount = viewModel::createNewAccount,
            onRenameAccount = viewModel::renameAccount,
            onDeleteAccount = viewModel::deleteAccount,
            onMessageShown = viewModel::messageShown
        )
    }
}

@Composable
private fun LoadedView(
    state: InstitutionUiState.Loaded,
    onAccountSelected: (Long) -> Unit,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onCreateAccount: (String) -> Unit,
    onRenameAccount: (accountId: Long, name: String) -> Unit,
    onDeleteAccount: (accountId: Long, name: String) -> Unit,
    onMessageShown: () -> Unit
) {

    val listState = rememberLazyListState()
    var showAddSheet by remember { mutableStateOf(false) }
    var renameTarget by remember { mutableStateOf<AccountSummaryUiModel?>(null) }
    var deleteTarget by remember { mutableStateOf<AccountSummaryUiModel?>(null) }

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
            contentPadding = PaddingValues(bottom = TOOLBAR_CONTENT_PADDING),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item(key = "header") {
                InstitutionHeader(state, onBack)
            }

            state.message?.let { message ->

                item(key = "message") {
                    MessageBanner(message)
                }

            }

            if (state.accounts.isEmpty()) {

                item(key = "empty") {
                    EmptyAccounts()
                }

            } else {

                items(state.accounts, key = { it.id }) { account ->

                    SummaryCard(
                        title = account.title,
                        totalValue = account.totalValue,
                        valueChange = account.valueChange,
                        isGain = account.isGain,
                        onClick = { onAccountSelected(account.id) },
                        onRename = { renameTarget = account },
                        onDelete = { deleteTarget = account }
                    )

                }

            }

        }

        FloatingToolbar(
            modifier = Modifier.align(Alignment.BottomCenter),
            actions = listOf(
                ToolbarAction(
                    icon = Res.drawable.add,
                    contentDescription = "Add an account",
                    onClick = { showAddSheet = true }
                ),
                ToolbarAction(
                    icon = Res.drawable.refresh,
                    contentDescription = "Refresh values",
                    enabled = !state.isRefreshing,
                    onClick = onRefresh
                )
            )
        )

        if (state.isRefreshing) {

            LinearProgressIndicator(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
            )

        }

        if (showAddSheet) {

            AddAccountSheet(
                onCancel = { showAddSheet = false },
                onConfirm = { name ->
                    showAddSheet = false
                    onCreateAccount(name)
                }
            )

        }

        renameTarget?.let { account ->

            RenameAccountSheet(
                account = account,
                onCancel = { renameTarget = null },
                onConfirm = { name ->
                    renameTarget = null
                    onRenameAccount(account.id, name)
                }
            )

        }

        deleteTarget?.let { account ->

            DeleteAccountSheet(
                account = account,
                onCancel = { deleteTarget = null },
                onConfirm = {
                    deleteTarget = null
                    onDeleteAccount(account.id, account.title)
                }
            )

        }

    }
}

@Composable
private fun InstitutionHeader(
    state: InstitutionUiState.Loaded,
    onBack: () -> Unit
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

        }

        Text(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp),
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
private fun AddAccountSheet(
    onCancel: () -> Unit,
    onConfirm: (String) -> Unit
) {

    val nameState = rememberTextFieldState()

    BottomSheet {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(25.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Add an account",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                state = nameState,
                label = { Text("Account name") },
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
                    Text("Add")
                }

            }

        }

    }
}

@Composable
private fun RenameAccountSheet(
    account: AccountSummaryUiModel,
    onCancel: () -> Unit,
    onConfirm: (name: String) -> Unit
) {

    val nameState = rememberTextFieldState(account.title)

    BottomSheet {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(25.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Rename ${account.title}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                state = nameState,
                label = { Text("Account name") },
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
private fun DeleteAccountSheet(
    account: AccountSummaryUiModel,
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
                text = "Delete ${account.title}?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = account.deleteWarning(),
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

/** Spells out what else a delete takes with the account. */
private fun AccountSummaryUiModel.deleteWarning(): String = when (positionCount) {
    0 -> "This account holds no positions. It will be removed."
    1 -> "1 position worth $totalValue will be removed with it."
    else -> "$positionCount positions worth $totalValue in total will be removed with it."
}

@Composable
private fun EmptyAccounts() {

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "No accounts yet",
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
