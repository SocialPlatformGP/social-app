package com.gp.posts.presentation.createpost

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen() {
    Scaffold(
        topBar = { CreatePostTopBar() }
    ) { paddindValues ->
        CreatePostContent(
            paddindValues = paddindValues
        )
    }
}

@Composable
fun CreatePostTopBar() {
    Row(
        Modifier.fillMaxWidth()
    ) {

    }
}

@Composable
fun CreatePostContent(paddindValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddindValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier =Modifier.size(8.dp))
        CreatePostTextField(
            label = "Title",
            value = "",
            onValueChange = {},
            icon = Icons.Filled.Title,
            height = 0.1f
        )
        Spacer(modifier =Modifier.size(4.dp))
        CreatePostTextField(
            label = "Body",
            value = "",
            onValueChange = {},
            icon = Icons.Filled.TextFormat,
            height = 0.5f
        )

    }

}

@Composable
fun CreatePostTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    height: Float = 1f
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight().
                    wrapContentHeight(Alignment.Top)
                    .padding(16.dp)
            ) },
        modifier = Modifier.fillMaxWidth().fillMaxHeight(height)
    )
}

@Composable
fun CreatePostAvatar() {
    Icon(
        modifier = Modifier
            .padding(8.dp)
            .size(40.dp),
        imageVector = Icons.Filled.Person,
        contentDescription = null
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun CreatePostPreview() {
    CreatePostScreen()
}
