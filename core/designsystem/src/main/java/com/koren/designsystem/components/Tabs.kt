@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun Tabs(
    modifier: Modifier = Modifier,
    tabIndex: MutableState<Int>,
    onTabChanged: (Int) -> Unit = {},
    items: List<TabItem>
) {

    SecondaryTabRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        selectedTabIndex = tabIndex.value,
        containerColor = BottomSheetDefaults.ContainerColor
    ) {
        items.forEachIndexed { index, item ->
            Tab(
                modifier = Modifier.padding(vertical = 8.dp),
                selected = tabIndex.value == index,
                onClick = {
                    tabIndex.value = index
                    onTabChanged(index)
                }
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = item.icon,
                    contentDescription = item.title
                )
                Text(
                    text = item.title
                )
            }
        }
    }
}

data class TabItem(
    val title: String,
    val icon: ImageVector
)