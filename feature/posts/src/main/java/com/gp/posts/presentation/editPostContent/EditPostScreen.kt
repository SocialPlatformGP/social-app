import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.fragment.app.viewModels

import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.gp.posts.presentation.editPostContent.EditPostViewModel

@Composable
fun EditPostContent(
    state: EditPostViewModel.EditPostUIState,
    updateTitle: (String) -> Unit,
    updateBody: (String) -> Unit,
    updatePost: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = state.title,
            onValueChange = { updateTitle(it) },
            label = { Text(text = "Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            )
        )

        OutlinedTextField(
            value = state.body,
            onValueChange = { updateBody(it) },
            label = { Text("Content") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
        )

        Button(
            onClick = {
                updatePost()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp).background(Color.Gray)
        ) {
            Text("Apply Edits")
        }
    }
}

@Composable
fun EditPostScreen(viewModel: EditPostViewModel) {
    val state by viewModel.uiState.collectAsState()
    EditPostContent(
        state = state,
        updateTitle = { newTitle ->viewModel.updateTitle(newTitle)},
        updateBody = { newBody -> viewModel.updateBody(newBody)},
        updatePost = { viewModel.updatePost()}
    )
}

@Preview(showBackground = true, showSystemUi = true)

@Composable
fun preview() {
    EditPostContent(state = EditPostViewModel.EditPostUIState(), updateTitle ={} , updateBody ={} ) {

    }
}

fun EditPostViewModel.updateBody(body: String) {
    uiState.value = uiState.value.copy(body = body)
}
fun EditPostViewModel.updateTitle(title: String) {
    uiState.value = uiState.value.copy(body = title)
}
