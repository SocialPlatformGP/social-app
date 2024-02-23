import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.gp.posts.presentation.editPostContent.EditPostEvents
import com.gp.posts.presentation.editPostContent.EditPostViewModel
import com.gp.socialapp.database.model.MimeType
import com.gp.socialapp.database.model.PostAttachment
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Tag
import com.gp.socialapp.theme.logoColor
import kotlinx.coroutines.launch


@Composable
fun EditPostScreen(
    viewModel: EditPostViewModel,
    back: () -> Unit,
    onAddImageClick: () -> Unit,
    onAddVideoClick: () -> Unit,
    onAddFileClick: () -> Unit,
    onPreviewFile: (PostAttachment) -> Unit = {},
) {
    val post by viewModel.post.collectAsState()
    val channelTags by viewModel.tags.collectAsState()
    EditPostScreen(
        post = post,
        tags = channelTags.toSet(),
        editPostEvents = {
            when (it) {
                is EditPostEvents.NavigateBack -> back()
                is EditPostEvents.OnAddImageClicked -> onAddImageClick()
                is EditPostEvents.OnAddVideoClicked -> onAddVideoClick()
                is EditPostEvents.OnAddFileClicked -> onAddFileClick()
                is EditPostEvents.OnPreviewClicked -> onPreviewFile(it.file)
                else -> {
                    viewModel.handleEvent(it)
                }
            }

        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostScreen(
    post: Post,
    tags: Set<Tag>,
    editPostEvents: (EditPostEvents) -> Unit
) {
    Scaffold(
        topBar = {
            EditPostTopBar(
                editPostEvents = editPostEvents
            )
        },
    ) { paddindValues ->
        CreatePostContent(
            paddingValues = paddindValues,
            titleValue = post.title,
            contentValue = post.body,
            tags = post.tags,
            tagsChannel = tags,
            selectedFiles = post.attachments,
            editPostEvents = editPostEvents
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostTopBar(
    editPostEvents: (EditPostEvents) -> Unit
) {
    androidx.compose.material.TopAppBar(
        title = { Text(text = "Edit Post", color = Color.White, fontSize = 20.sp) },
        navigationIcon = {
            IconButton(
                onClick = { editPostEvents(EditPostEvents.NavigateBack) },
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew, contentDescription = null, tint = Color.White
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    editPostEvents(EditPostEvents.OnApplyEditClicked)
                    editPostEvents(EditPostEvents.NavigateBack)
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Check, contentDescription = null, tint = Color.White
                )
            }
        },
        backgroundColor = logoColor,
    )
}

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalLayoutApi::class,
)
@Composable
fun CreatePostContent(
    paddingValues: PaddingValues,
    titleValue: String = "",
    contentValue: String = "",
    tags: List<Tag>,
    tagsChannel: Set<Tag>,
    selectedFiles: List<PostAttachment>,
    emptyError: Boolean = false,
    editPostEvents: (EditPostEvents) -> Unit

) {
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var existingTagsDialogState by remember { mutableStateOf(false) }
    var newTagDialogState by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.run {
            fillMaxSize()
                .padding(paddingValues)
                .background(Color.LightGray)
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val height = LocalConfiguration.current.screenHeightDp.dp
        EditPostTextField(
            label = "Title",
            value = titleValue,
            onValueChange = { editPostEvents(EditPostEvents.OnTitleChanged(it)) },
            icon = Icons.Filled.Title,
            modifier = Modifier.height(height * 0.08f),
            errorState = emptyError

        )
        EditPostTextField(
            label = "Content",
            value = contentValue,
            onValueChange = { editPostEvents(EditPostEvents.OnContentChanged(it)) },
            icon = Icons.Filled.TextFormat,
            modifier = Modifier.weight(1f),
            errorState = emptyError
        )
        FlowTags(tags.toSet(), editPostEvents)
        LazyRow(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(selectedFiles) { file ->
                PreviewFileItem(
                    file = file,
                    editPostEvents
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height * 0.08f)
                .background(MaterialTheme.colors.surface),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            EditPostOutlinedButton(label = "Add Tag",
                onClick = { scope.launch { bottomSheetState.show() } })
            EditPostAction(
                icon = Icons.Filled.Image,
                onClick = { editPostEvents(EditPostEvents.OnAddImageClicked) })
            EditPostAction(
                icon = Icons.Filled.VideoFile,
                onClick = { editPostEvents(EditPostEvents.OnAddVideoClicked) })
            EditPostAction(
                icon = Icons.Filled.AttachFile,
                onClick = { editPostEvents(EditPostEvents.OnAddFileClicked) })

        }


    }
    ButtonSheetOptions(
        options = listOf(TagType.New.label, TagType.Existing.label),
        state = bottomSheetState, onOptionSelected = { option ->
            scope.launch { bottomSheetState.hide() }
            when (option) {
                TagType.New.label -> {
                    newTagDialogState = true
                }

                TagType.Existing.label -> {
                    existingTagsDialogState = true
                }
            }
        })
    if (existingTagsDialogState) {
        var tempTags by remember { mutableStateOf(emptySet<Tag>()) }
        AlertDialog(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
            onDismissRequest = { existingTagsDialogState = false },
            title = { Text(text = "Select Tag") },
            text = {
                FlowRow {
                    tagsChannel.forEach { tag ->
                        Chip(onClick = {
                            tempTags = if (tempTags.contains(tag)) {
                                tempTags - tag
                            } else {
                                tempTags + tag
                            }
                        }, colors = ChipDefaults.chipColors(
                            backgroundColor = Color(android.graphics.Color.parseColor(tag.hexColor)),
                            contentColor = Color.White
                        ), leadingIcon = {
                            if (tempTags.contains(tag)) {
                                Icon(
                                    imageVector = Icons.Filled.Check, contentDescription = null
                                )
                            }
                        }) {
                            Text(text = tag.label)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    editPostEvents(EditPostEvents.OnTagAdded(tempTags.toSet()))
                    existingTagsDialogState = false
                }) {
                    Text(text = "Add")
                }
            },
            dismissButton = {
                Button(onClick = { existingTagsDialogState = false }) {
                    Text(text = "Cancel")
                }
            })
    }
    if (newTagDialogState) {
        var tempTag by remember { mutableStateOf(Tag("", "#000000")) }
        val keyboardController = LocalSoftwareKeyboardController.current
        AlertDialog(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
            onDismissRequest = { newTagDialogState = false },
            title = { Text(text = "Add new Tag") },
            text = {
                Column {
                    OutlinedTextField(value = tempTag.label,
                        onValueChange = {
                            tempTag = tempTag.copy(label = it)
                        },
                        label = { Text(text = "Tag") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            keyboardController?.hide()
                        }),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Label,
                                contentDescription = null,
                                tint = Color(android.graphics.Color.parseColor(tempTag.hexColor))
                            )
                        })
                    Spacer(modifier = Modifier.height(8.dp))
                    ColorPickerDialog(colors = listOf(
                        Color(android.graphics.Color.parseColor("#FF0000")),
                        Color(android.graphics.Color.parseColor("#00FF00")),
                        Color(android.graphics.Color.parseColor("#0000FF")),
                        Color(android.graphics.Color.parseColor("#FFFF00")),
                        Color(android.graphics.Color.parseColor("#00FFFF")),
                        Color(android.graphics.Color.parseColor("#FF00FF"))
                    ), onColorSelected = {
                        tempTag = tempTag.copy(hexColor = "#" + Integer.toHexString(it.toArgb()))
                    })

                }

            },
            confirmButton = {
                Button(onClick = {
                    editPostEvents(EditPostEvents.OnTagAdded(setOf(tempTag)))
                    newTagDialogState = false
                }) {
                    Text(text = "Add")
                }
            },
            dismissButton = {
                Button(onClick = { newTagDialogState = false }) {
                    Text(text = "Cancel")
                }
            })
    }


}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun FlowTags(
    selectedTags: Set<Tag>,
    editPostEvents: (EditPostEvents) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.Start
    ) {
        items(selectedTags.toList()) { tag ->
            Chip(onClick = {
                editPostEvents(EditPostEvents.OnTagRemoved(tag))
            }, leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Cancel, contentDescription = null
                )
            }, colors = ChipDefaults.chipColors(
                backgroundColor = Color(android.graphics.Color.parseColor(tag.hexColor)),
                contentColor = Color.White
            )
            ) {
                Text(text = tag.label)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}


@Composable
private fun EditPostOutlinedButton(
    onClick: () -> Unit = {},
    label: String,
) {
    OutlinedButton(
        onClick = onClick, shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(
                top = 3.dp, bottom = 3.dp, start = 4.dp, end = 4.dp
            ),
            style = MaterialTheme.typography.button,
            color = logoColor
        )
    }
}

@Composable
private fun EditPostAction(
    icon: ImageVector, onClick: () -> Unit = {}
) {
    IconButton(
        onClick = onClick,
    ) {
        Icon(
            imageVector = icon, contentDescription = null, tint = logoColor
        )
    }
}

@Composable
fun EditPostTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    modifier: Modifier,
    errorState: Boolean
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
                    .fillMaxHeight()
                    .wrapContentHeight(Alignment.Top)
                    .padding(16.dp),
                tint = logoColor
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface),
        isError = errorState
    )
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ButtonSheetOptions(
    options: List<String>, onOptionSelected: (String) -> Unit, state: ModalBottomSheetState
) {
    ModalBottomSheetLayout(
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                options.forEach { option ->
                    Button(
                        onClick = {
                            onOptionSelected(option)
                        },
                        shape = RoundedCornerShape(32.dp),
                    ) {
                        Text(text = option, fontSize = 16.sp)
                    }
                }
            }

        },
        sheetState = state,
        sheetShape = RoundedCornerShape(16.dp),
        sheetElevation = 8.dp,
        sheetBackgroundColor = MaterialTheme.colors.surface,
        sheetContentColor = MaterialTheme.colors.onSurface,
        sheetGesturesEnabled = true,
    ) {

    }

}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun EditPostPreview() {
    EditPostScreen(
        post = Post(),
        tags = emptySet(),
        editPostEvents = {}
    )
}

enum class TagType(val label: String) {
    New("Add New Tag"),
    Existing("Select From Existing Tags")
}

@Composable
fun ColorPickerDialog(
    colors: List<Color>,
    onColorSelected: (Color) -> Unit,
) {

    Surface(
        shape = MaterialTheme.shapes.medium,

        ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Choose a Color",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), modifier = Modifier.fillMaxWidth()
            ) {
                items(colors) { color ->
                    ColorItem(color = color, onColorSelected = onColorSelected)
                }
            }
        }
    }
}

@Composable
fun ColorItem(color: Color, onColorSelected: (Color) -> Unit) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .padding(4.dp)
            .clip(CircleShape)
            .background(color)
            .clickable { onColorSelected(color) }, contentAlignment = Alignment.Center
    ) {}
}

@Composable
fun PreviewFileItem(
    file: PostAttachment,
    editPostEvents: (EditPostEvents) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .width(70.dp)
            .height(105.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colors.surface)
            .clickable { editPostEvents(EditPostEvents.OnPreviewClicked(file)) }
            .border(1.dp, MaterialTheme.colors.onSurface, MaterialTheme.shapes.medium)

    ) {
        var icon by remember { mutableStateOf(Icons.Filled.Image) }
        when (file.type) {
            in listOf(
                MimeType.VIDEO.readableType,
                MimeType.MKV.readableType,
                MimeType.MP4.readableType,
                MimeType.MOV.readableType,
                MimeType.AVI.readableType,
            ) -> {
                icon = Icons.Filled.VideoLibrary
            }

            in listOf(
                MimeType.PDF.readableType,
                MimeType.DOCX.readableType,
                MimeType.XLSX.readableType,
                MimeType.PPTX.readableType,
            ) -> {
                icon = Icons.Filled.InsertDriveFile
            }

            in listOf(
                MimeType.JPEG.readableType,
                MimeType.PNG.readableType,
                MimeType.GIF.readableType,
                MimeType.BMP.readableType,
                MimeType.WEBP.readableType,
            ) -> {
                SubcomposeAsyncImage(
                    model = file.url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop,

                    ) {
                    val state = painter.state
                    if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                        CircularProgressIndicator()
                    } else {
                        SubcomposeAsyncImageContent()
                    }
                }
            }

            in listOf(
                MimeType.AUDIO.readableType,
                MimeType.MP3.readableType,
                MimeType.AAC.readableType,
                MimeType.WAV.readableType,
                MimeType.OGG.readableType,
                MimeType.FLAC.readableType,
            ) -> {
                icon = Icons.Filled.MusicNote
            }

            else -> {
                icon = Icons.Filled.InsertDriveFile

            }

        }

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = if (icon == Icons.Filled.Image) {
                Modifier
                    .padding(1.dp)
                    .align(Alignment.TopEnd)
            } else {
                Modifier
                    .padding(1.dp)
                    .align(Alignment.TopEnd)
                    .fillMaxSize()
            },
            tint = MaterialTheme.colors.onSurface
        )
        IconButton(
            onClick = { editPostEvents(EditPostEvents.OnFileRemoved(file)) },
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(Color.Transparent)
                .size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Cancel,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(20.dp)
            )

        }
        Text(
            text = file.name,
            style = MaterialTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(MaterialTheme.colors.surface)
                .padding(3.dp)
        )
    }
}






