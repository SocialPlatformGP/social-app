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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.gp.posts.presentation.editPostContent.EditPostViewModel
import com.gp.socialapp.model.Post


@Composable
fun EditPostScreen(viewModel: EditPostViewModel,post: Post) {
    val state by viewModel.uiState.collectAsState()
    EditPostContent(
        post = post,
        state = state,
        updateTitle = { newTitle ->viewModel.updateTitle(newTitle)},
        updateBody = { newBody -> viewModel.updateBody(newBody)},
        updatePost = { viewModel.updatePost()}
    )
}
@Composable
fun EditPostContent(
    post: Post,
    state: EditPostViewModel.EditPostUIState,
    updateTitle: (String) -> Unit,
    updateBody: (String) -> Unit,
    updatePost: () -> Unit
) {
    var title by remember { mutableStateOf(post.title) }
    var body by remember { mutableStateOf(post.body) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = title,
            onValueChange = { title = it
                            updateTitle(it)},
            label = { Text(text = "Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            )
        )

        OutlinedTextField(
            value = body,
            onValueChange = { body = it
                            updateBody(it)},
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








