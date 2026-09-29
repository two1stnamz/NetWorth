package com.maroondevelopment.networth.presentation.portfolio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maroondevelopment.networth.presentation.model.PortfolioUiModel
import networth.composeapp.generated.resources.Res
import networth.composeapp.generated.resources.add
import networth.composeapp.generated.resources.refresh
import org.jetbrains.compose.resources.painterResource

@Composable
fun PortfolioScreen(
    viewModel: PortfolioViewModel = viewModel()
) {

    when (val uiStateUpdate = viewModel.flow.collectAsState().value) {
        PortfolioUiState.Error -> ErrorView()
        PortfolioUiState.Loading -> LoadingView()
        is PortfolioUiState.Success -> LoadedView(uiStateUpdate.model)
    }

}

@Composable
private fun LoadingView() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {

        CircularProgressIndicator()

    }

}

@Composable
private fun ErrorView() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {

        Text("Something went wrong")

    }

}

@Composable
private fun LoadedView(
    portfolio: PortfolioUiModel
) {

    val state = rememberLazyListState()

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
                                .clickable(onClick = {}),
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


        items(portfolio.accounts) { item ->

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .clip(RoundedCornerShape(8.dp))
                        .padding(25.dp)
            ) {

                Text("Title: ${item.title}")

                Text("Total Value: ${item.totalValue}")

                Text("Total Change: ${item.valueChange}")

            }

        }

    }

}