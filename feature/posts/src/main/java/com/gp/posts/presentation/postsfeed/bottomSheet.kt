package com.gp.posts.presentation.postsfeed
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun FeedOptionsBottomSheet(
    onSortByNewest: () -> Unit,
    onSortByPopularity: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedSortOption by remember { mutableStateOf(SortOption.POPULAR) }

    Surface(
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth().wrapContentWidth(Alignment.Start)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Sort Options", style = MaterialTheme.typography.bodyMedium)
                IconButton(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null
                    )
                }
            }

            LazyColumn {
                items(SortOption.values()) { option ->
                    SortOptionItem(
                        option = option,
                        isSelected = selectedSortOption == option,
                        onSortSelected = {
                            selectedSortOption = option
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        when (selectedSortOption) {
                            SortOption.NEWEST -> onSortByNewest()
                            SortOption.POPULAR -> onSortByPopularity()
                        }
                        onDismiss()
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Apply")
                }
            }
        }
    }
}

@Composable
fun SortOptionItem(option: SortOption, isSelected: Boolean, onSortSelected: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSortSelected() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = option.displayName)
    }
}

@Composable
fun FeedOptionsScreen(
    onSortByNewest: () -> Unit,
    onSortByPopularity: () -> Unit,
    onDismiss: () -> Unit) {
    var showBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.End
    ) {
        IconButton(
            onClick = { showBottomSheet = true },
            modifier = Modifier
                .padding(5.dp)
                .size(20.dp)
        ) {
            Icon(imageVector = Icons.Default.Sort, contentDescription = null)
        }

        if (showBottomSheet) {
            FeedOptionsBottomSheet(
                onSortByNewest = {
                    onSortByNewest()
                    showBottomSheet = false
                },
                onSortByPopularity = {
                    onSortByPopularity
                    showBottomSheet = false
                },
                onDismiss = {
                    showBottomSheet = false
                }
            )
        }
    }
}

enum class SortOption(val displayName: String) {
    NEWEST("Newest"),
    POPULAR("Popular")
}

@Preview(showBackground = true, apiLevel = 30, showSystemUi = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun FeedOptionsScreenPreview() {
}
