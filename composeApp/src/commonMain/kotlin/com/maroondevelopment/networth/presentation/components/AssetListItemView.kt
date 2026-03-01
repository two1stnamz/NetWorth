package com.maroondevelopment.networth.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maroondevelopment.networth.domain.entity.Asset

@Composable
fun AssetListItemView(asset: Asset) {

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape((8.dp)))
                .border(width = 1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .padding(15.dp)
    ) {

        Text(
            text = asset.holding.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "$${asset.value}",
            color = MaterialTheme.colorScheme.secondary
        )
    }

}