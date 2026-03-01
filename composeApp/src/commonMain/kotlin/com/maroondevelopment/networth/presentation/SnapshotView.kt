package com.maroondevelopment.networth.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maroondevelopment.networth.di.Factory
import com.maroondevelopment.networth.domain.entity.Portfolio

@Composable
@Preview
fun SnapshotView(viewModel: SnapshotViewModel = Factory.snapshotViewModel()) {

    val stateUpdate = viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetch()
    }

    when (val state = stateUpdate.value) {
        is SnapshotUiState.Loading -> LoadingView()
        is SnapshotUiState.Error -> ErrorView()
        is SnapshotUiState.Success -> PortfolioView(state.portfolio)
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

        Text("An error occurred")

    }

}

@Composable
private fun PortfolioView(portfolio: Portfolio) {

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(top = 55.dp, start = 25.dp, end = 25.dp)
    ) {

        Text(
            modifier =
                Modifier
                    .fillMaxWidth(),
            text = "$${portfolio.totalValue}",
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold
        )

    }

}