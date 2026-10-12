package com.maroondevelopment.networth.presentation.portfolio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.maroondevelopment.networth.presentation.account.ErrorView
import com.maroondevelopment.networth.presentation.account.LoadingView
import com.maroondevelopment.networth.presentation.components.BottomSheet
import com.maroondevelopment.networth.presentation.model.PortfolioUiModel
import networth.composeapp.generated.resources.Res
import networth.composeapp.generated.resources.add
import networth.composeapp.generated.resources.refresh
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PortfolioScreen(
    onAccountSelected: (Long) -> Unit,
    viewModel: PortfolioViewModel = koinViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    when (val uiStateUpdate = viewModel.flow.collectAsState().value) {
        PortfolioUiState.Error -> ErrorView()
        PortfolioUiState.Loading -> LoadingView()
        is PortfolioUiState.Success -> LoadedView(uiStateUpdate.model, onAccountSelected, viewModel)
    }

}

@Composable
private fun LoadedView(
    portfolio: PortfolioUiModel,
    onAccountSelected: (Long) -> Unit,
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
            state = state
        ) {

            item(key = "total") {

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                ) {

                    Row(
                        modifier =
                            Modifier
                                .align(Alignment.TopEnd)
                    ) {

                        Image(
                            modifier =
                                Modifier
                                    .size(55.dp)
                                    .padding(8.dp)
                                    .clickable(onClick = { showModal = true }),
                            painter = painterResource(Res.drawable.add),
                            colorFilter = ColorFilter.tint(Color.DarkGray),
                            contentDescription = ""
                        )

                        Image(
                            modifier =
                                Modifier
                                    .size(55.dp)
                                    .padding(10.dp)
                                    .clickable(onClick = {}),
                            painter = painterResource(Res.drawable.refresh),
                            colorFilter = ColorFilter.tint(Color.DarkGray),
                            contentDescription = ""
                        )
                    }

                    Text(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 25.dp)
                                .padding(top = 85.dp),
                        text = "$1,022,450.56",
                        fontSize = TextUnit(45f, TextUnitType.Sp),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                }

            }


            items(portfolio.accounts, key = { it.id }) { item ->

                Column(
                    modifier =
                        Modifier
                            .padding(horizontal = 25.dp)
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .clickable(onClick = { onAccountSelected(item.id) })
                            .clip(RoundedCornerShape(20.dp))
                            .padding(25.dp)
                ) {

                    Text("Title: ${item.title}")

                    Text("Total Value: ${item.totalValue}")

                    Text("Total Change: ${item.valueChange}")

                }

            }

        }

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