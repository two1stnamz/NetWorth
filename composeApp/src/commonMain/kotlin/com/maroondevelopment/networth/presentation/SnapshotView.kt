package com.maroondevelopment.networth.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maroondevelopment.networth.di.Factory

@Composable
@Preview
fun SnapshotView(viewModel: SnapshotViewModel = Factory.snapshotViewModel()) {

    viewModel.fetch()

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
            text = "$123,456.78",
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold
        )

    }

}