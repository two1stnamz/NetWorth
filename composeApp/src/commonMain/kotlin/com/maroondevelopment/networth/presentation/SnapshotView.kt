package com.maroondevelopment.networth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maroondevelopment.networth.di.SnapshotViewModelFactory
import com.maroondevelopment.networth.domain.entity.Portfolio
import com.maroondevelopment.networth.presentation.components.AssetListItemView
import networth.composeapp.generated.resources.Res
import networth.composeapp.generated.resources.refresh
import org.jetbrains.compose.resources.painterResource

@Composable
@Preview
fun SnapshotView(viewModel: SnapshotViewModel = viewModel(factory = SnapshotViewModelFactory())) {

    val stateUpdate = viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetch()
    }

    when (val state = stateUpdate.value) {
        is SnapshotUiState.Loading -> LoadingView()
        is SnapshotUiState.Error -> ErrorView()
        is SnapshotUiState.Success -> PortfolioView(state.portfolio, onRefresh = { viewModel.refresh() })
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
private fun PortfolioView(
    portfolio: Portfolio,
    onRefresh: () -> Unit) {

    val state = rememberLazyListState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(top = 55.dp, start = 25.dp, end = 25.dp)
    ) {

        Row(
            modifier =
                Modifier
                    .padding(vertical = 15.dp)
                    .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                modifier =
                    Modifier
                        .weight(1f),
                text = "$${portfolio.totalValue}",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )

            Image(
                modifier =
                    Modifier
                        .size(45.dp)
                        .clickable(onClick = onRefresh, role = Role.Button),
                painter = painterResource(Res.drawable.refresh),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inversePrimary),
                contentDescription = "Refresh",
            )

        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = state,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {

            items(portfolio.assets) { item ->

                AssetListItemView(asset = item)

            }

            // Footer
            item {

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 35.dp),
                    )

            }
        }
    }

}