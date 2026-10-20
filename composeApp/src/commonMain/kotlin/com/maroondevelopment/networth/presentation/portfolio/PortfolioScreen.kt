package com.maroondevelopment.networth.presentation.portfolio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.maroondevelopment.networth.presentation.account.ErrorView
import com.maroondevelopment.networth.presentation.account.LoadingView
import com.maroondevelopment.networth.presentation.components.BottomSheet
import com.maroondevelopment.networth.presentation.components.FloatingToolbar
import com.maroondevelopment.networth.presentation.components.TOOLBAR_CONTENT_PADDING
import com.maroondevelopment.networth.presentation.components.ToolbarAction
import com.maroondevelopment.networth.presentation.model.AccountSummaryUiModel
import com.maroondevelopment.networth.presentation.model.PortfolioUiModel
import com.maroondevelopment.networth.presentation.theme.appColors
import networth.composeapp.generated.resources.Res
import networth.composeapp.generated.resources.add
import networth.composeapp.generated.resources.refresh
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PortfolioScreen(
    onAccountSelected: (Long) -> Unit,
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
            LoadedView(uiStateUpdate.model, onAccountSelected, onHistorySelected, viewModel)
    }

}

@Composable
private fun LoadedView(
    portfolio: PortfolioUiModel,
    onAccountSelected: (Long) -> Unit,
    onHistorySelected: () -> Unit,
    viewModel: PortfolioViewModel
) {

    val state = rememberLazyListState()
    var showModal by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = WindowInsets.safeContent.asPaddingValues().calculateTopPadding()),
            state = state,
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


            items(portfolio.accounts, key = { it.id }) { item ->

                AccountCard(
                    account = item,
                    onClick = { onAccountSelected(item.id) }
                )

            }

        }

        FloatingToolbar(
            modifier = Modifier.align(Alignment.BottomCenter),
            actions = listOf(
                ToolbarAction(
                    icon = Res.drawable.add,
                    contentDescription = "Add an account",
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

            val state = rememberTextFieldState()

            BottomSheet {

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(25.dp)
                ) {

                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        state = state,
                        label = { Text("Enter account name") }
                    )

                    Button(
                        onClick = {
                            showModal = false
                            viewModel.createNewAccount(state.text.toString())
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Create")
                    }
                }

            }

        }

    }
}

@Composable
private fun AccountCard(
    account: AccountSummaryUiModel,
    onClick: () -> Unit
) {

    Card(
        onClick = onClick,
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
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                modifier = Modifier.weight(1f),
                text = account.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Column(
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = account.totalValue,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = account.valueChange,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (account.isGain) appColors.gain else appColors.loss
                )

            }

        }

    }
}
