package com.maroondevelopment.networth.presentation.summary

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

@Composable
fun SummaryScreen() {

    val state = rememberLazyListState()

    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(top = WindowInsets.safeContent.asPaddingValues().calculateTopPadding()),
        state = state
    ) {

        item(key = "total") {

            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                        .padding(top = 25.dp),
                text = "$1,022,450.56",
                fontSize = TextUnit(35f, TextUnitType.Sp),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

        }

    }

}