import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gp.posts.presentation.editPostContent.EditPostUIState
import com.gp.posts.presentation.editPostContent.EditPostViewModel

@Composable
fun EditPostScreen(
    viewModel: EditPostViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    EditPostContent(
        state = state,
        updateTitle = { newTitle -> viewModel.onTitleChange(newTitle) },
        updateBody = { newBody -> viewModel.onBodyChange(newBody) },
        updatePost = {
            viewModel.updatePost()
            onNavigateBack()
        }
    )
}

@Composable
fun EditPostContent(
    state: EditPostUIState= EditPostUIState(),
    updateTitle: (String) -> Unit= {},
    updateBody: (String) -> Unit= {},
    updatePost: () -> Unit= {}
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
                .padding(top = 16.dp)
        ) {
            Text("Apply Edits")
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)

@Composable
fun Preview() {
    EditPostContent(
        state = EditPostUIState(
            title = "Title",
            body = "Body"
        ),
        updateTitle = {},
        updateBody = {},
        updatePost = {}
    )
}


