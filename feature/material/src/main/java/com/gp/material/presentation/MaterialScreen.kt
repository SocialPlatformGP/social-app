package com.gp.material.presentation

import android.util.Log
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gp.material.R
import com.gp.material.model.FileType
import com.gp.material.model.MaterialItem

@Composable
fun MaterialScreen(
    modifier: Modifier = Modifier,
    isAdmin: Boolean,
    onOpenFile: (MaterialItem) -> Unit,
    onFolderClicked: (String) -> Unit,
    onDownloadFile: (MaterialItem) -> Unit,
    onShareLink: (MaterialItem) -> Unit,
    onBackPressed: () -> Unit,
    onNewFileClicked: () -> Unit,
    viewModel: MaterialViewModel,
) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val currentPath by viewModel.currentPath.collectAsStateWithLifecycle()
    var isCreateDialogOpen by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var isFileDetailsDialogOpen by remember { mutableStateOf(false) }
    var fileWithOpenDetails by remember { mutableStateOf(MaterialItem()) }
    val folderDropDownItems = listOf("Delete")
    val fileDropDownItems = listOf("Download", "Share", "Details", "Delete")
    val onDropDownItemClicked: (String, MaterialItem) -> Unit = { dropDownItem, item ->
        if (item.fileType == FileType.FOLDER) {
            if (dropDownItem == "Delete") {
                viewModel.deleteFolder(item.path)
            }
        } else {
            when (dropDownItem) {
                "Delete" -> {
                    viewModel.deleteFile(item.path)
                }

                "Download" -> {
                    onDownloadFile(item)
                }

                "Share" -> {
                    onShareLink(item)
                }

                "Details" -> {
                    fileWithOpenDetails = item
                    isFileDetailsDialogOpen = true
                }
            }
        }
    }
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if ((currentPath != "materials") && (currentPath != "/materials")) {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = null,
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(40.dp))
                }
                Text(
                    text = viewModel.getCurrentFolderName(currentPath),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        floatingActionButton = {
            if (isAdmin) {
                MultiFloatingActionButton(
                    fabIcon = Icons.Filled.Add,
                    items = arrayListOf(
                        FabItem(
                            icon = painterResource(id = R.drawable.baseline_create_new_folder_24),
                            label = "Create Folder",
                            backgroundColor = Color(0xff222f86),
                            onFabItemClicked = {
                                isCreateDialogOpen = true
                            }
                        ),
                        FabItem(
                            icon = painterResource(id = R.drawable.baseline_upload_file_24),
                            label = "Upload File",
                            backgroundColor = Color.DarkGray,
                            onFabItemClicked = {
                                onNewFileClicked()
                            }
                        )
                    ),
                    backgroundColor = Color.DarkGray
                )
            }
        },
    ) {
        Box(modifier = modifier.padding(it)) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items) { item ->
                    MaterialItem(
                        item = item,
                        onItemClick = {
                            if (item.fileType == FileType.FOLDER) {
                                onFolderClicked(item.path)
                            } else {
                                onOpenFile(item)
                            }
                        },
                        dropDownItems = if (item.fileType == FileType.FOLDER) folderDropDownItems else fileDropDownItems,
                        onDropDownItemClicked = onDropDownItemClicked
                    )
                }
            }
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = 48.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
            if (isCreateDialogOpen) {
                CreateFolderDialog(
                    onConfirmation = {
                        viewModel.uploadFolder(it)
                        isCreateDialogOpen = false
                    },
                    onDismissRequest = { isCreateDialogOpen = false })
            }
            if(isFileDetailsDialogOpen) {
                FileDetailsDialog(
                    onDismiss = { isFileDetailsDialogOpen = false },
                    onOpenFile = {
                        onOpenFile(fileWithOpenDetails)
                        isFileDetailsDialogOpen = false
                    },
                    file = fileWithOpenDetails
                )
            }
        }
    }

}

@Composable
fun MaterialItem(
    modifier: Modifier = Modifier,
    item: MaterialItem,
    onItemClick: () -> Unit,
    onDropDownItemClicked: (String, MaterialItem) -> Unit,
    dropDownItems: List<String>,
) {
    var isDropDownMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var pressOffset by remember {
        mutableStateOf(DpOffset(333.dp, 33.dp))
    }
    var itemHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current
    val interactionSource = remember {
        MutableInteractionSource()
    }
    Column {
        Row(
            modifier = modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .onSizeChanged {
                    itemHeight = with(density) { it.height.toDp() }
                }
                .indication(interactionSource = interactionSource, LocalIndication.current)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            isDropDownMenuVisible = true
                            pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                            Log.d("SEERDE", "MaterialItem: x:${it.x.toDp()} y:${it.y.toDp()}")
                        },
                        onTap = {
                            onItemClick()
                        },
                        onPress = {
                            val press = PressInteraction.Press(it)
                            interactionSource.emit(press)
                            tryAwaitRelease()
                            interactionSource.emit(PressInteraction.Release(press))
                        })
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = getItemPainterResource(item.fileType),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.size(16.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.widthIn(max = 260.dp)
            ) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.size,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = {
                        isDropDownMenuVisible = true
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = null
                    )
                }
            }
        }
        DropdownMenu(
            expanded = isDropDownMenuVisible,
            onDismissRequest = {
                isDropDownMenuVisible = false
            },
            offset = pressOffset.copy(
                y = pressOffset.y - itemHeight
            )
        ) {
            dropDownItems.forEach { dropDownItem ->
                DropdownMenuItem(
                    text = {
                        Text(text = dropDownItem)
                    },
                    onClick = {
                        isDropDownMenuVisible = false
                        onDropDownItemClicked(dropDownItem, item)
                    })
            }

        }
    }
}

@Composable
fun getItemPainterResource(type: FileType): Painter {
    return painterResource(
        id = when (type) {
            FileType.AUDIO -> R.drawable.ic_audio
            FileType.EXCEL -> R.drawable.ic_excel
            FileType.FOLDER -> R.drawable.ic_folder
            FileType.IMAGE -> R.drawable.ic_image
            FileType.PDF -> R.drawable.ic_pdf
            FileType.PPT -> R.drawable.ic_ppt
            FileType.TEXT -> R.drawable.ic_text
            FileType.VIDEO -> R.drawable.ic_video
            FileType.WORD -> R.drawable.ic_word
            FileType.ZIP -> R.drawable.ic_zip
            else -> R.drawable.ic_file
        }
    )
}

@Composable
fun CreateFolderDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    onConfirmation: (String) -> Unit,
) {
    var textValue by remember { mutableStateOf("") }
    Dialog(
        onDismissRequest = {
            onDismissRequest()
        }
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(230.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(text = "New Folder", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.size(8.dp))
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    placeholder = { Text(text = "Folder Name") })
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Cancel")
                    }
                    TextButton(
                        onClick = { onConfirmation(textValue) },
                        enabled = textValue.isNotBlank(),
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Composable
fun FileDetailsDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onOpenFile: (MaterialItem) -> Unit,
    file: MaterialItem,
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card (
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(4.dp),
        ){
            Column (
                modifier = Modifier.padding(16.dp)
            ){
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                ){
                    Icon(
                        painter = getItemPainterResource(file.fileType),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = file.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column (
                    modifier = Modifier.padding(start = 32.dp)
                ){
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailsDialogField(
                        title = "Location",
                        value = file.path.substringBeforeLast("/").replaceFirst("/materials" , "Home")
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    DetailsDialogField(
                        title = "Size",
                        value = file.size
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    DetailsDialogField(
                        title = "Created By",
                        value = file.createdBy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    DetailsDialogField(
                        title = "Created At",
                        value = file.creationTime
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                    ) {
                        Text("Cancel")
                    }
                    TextButton(
                        onClick = { onOpenFile(file) },
                    ) {
                        Text("Open File")
                    }
                }
            }

        }
    }

}
@Composable
fun DetailsDialogField(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
) {
    Column (modifier = modifier){
        Text(
            text = title,
            color = Color.Gray,
            fontSize = 12.sp
            )
        Text(
            text = value,
            fontSize = 18.sp,
        )
    }
}