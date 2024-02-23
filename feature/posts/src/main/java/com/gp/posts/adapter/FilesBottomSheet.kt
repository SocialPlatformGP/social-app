@file:OptIn(ExperimentalMaterialApi::class)

package com.gp.posts.adapter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gp.posts.presentation.feedUiEvents.PostEvent
import com.gp.socialapp.database.model.MimeType
import com.gp.socialapp.database.model.PostAttachment

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun FilesBottomSheet(
    currentAttachments: List<PostAttachment>,
    bottomSheetState: ModalBottomSheetState,
    postEvent: (PostEvent) -> Unit
) {
    ModalBottomSheetLayout(
        sheetContent = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.Center,
                horizontalArrangement = Arrangement.Center,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp),
            ) {
                items(currentAttachments) {
                    Row(
                        modifier = Modifier
                            .wrapContentWidth(align = Alignment.Start)
                            .padding(4.dp)
                            .background(Color.White, shape = RoundedCornerShape(8.dp))

                    ) {
                        var icon by remember {
                            mutableStateOf(Icons.Filled.InsertDriveFile)
                        }

                        var color by remember {
                            mutableStateOf(Color.Black)
                        }

                        when (it.type) {
                            in listOf(
                                MimeType.MKV.readableType,
                                MimeType.MP4.readableType,
                                MimeType.MOV.readableType,
                                MimeType.AVI.readableType,
                            ) -> {
                                Image(
                                    imageVector = Icons.Filled.VideoFile,
                                    contentDescription = "video file",
                                    modifier = Modifier.size(75.dp)
                                        .clickable { postEvent(PostEvent.OnVideoClicked(it)) },
                                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                                        Color.Blue.copy(alpha = 0.7f)
                                    )
                                )

                            }

                            in listOf(
                                MimeType.JPEG.readableType,
                                MimeType.PNG.readableType,
                                MimeType.GIF.readableType,
                            ) -> {

                                Image(
                                    imageVector = Icons.Filled.Image,
                                    contentDescription = "image file",
                                    modifier = Modifier.size(75.dp)
                                        .clickable { postEvent(PostEvent.OnImageClicked(it)) },
                                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                                        Color.Green.copy(alpha = 0.7f)
                                    )
                                )
                            }

                            in listOf(
                                MimeType.DOCX.readableType,
                                MimeType.XLSX.readableType,
                                MimeType.PPTX.readableType,
                            ) -> {
                                Image(
                                    imageVector = Icons.Filled.InsertDriveFile,
                                    contentDescription = "document file",
                                    modifier = Modifier.size(75.dp)
                                        .clickable { postEvent(PostEvent.OnDocumentClicked(it)) },
                                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                                        Color.Blue.copy(alpha = 0.7f)
                                    )
                                )
                            }

                            MimeType.PDF.readableType -> {
                                Image(
                                    imageVector = Icons.Filled.PictureAsPdf,
                                    contentDescription = "pdf file",
                                    modifier = Modifier.size(75.dp)
                                        .clickable { postEvent(PostEvent.OnDocumentClicked(it)) },
                                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                                        Color.Red.copy(alpha = 0.7f)
                                    )
                                )
                            }

                            in listOf(
                                MimeType.ZIP.readableType,
                                MimeType.RAR.readableType,
                                MimeType.TAR.readableType,
                            ) -> {
                                Image(
                                    imageVector = Icons.Filled.FolderZip,
                                    contentDescription = "zip file",
                                    modifier = Modifier.size(75.dp)
                                        .clickable { postEvent(PostEvent.OnDocumentClicked(it)) },
                                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                                        Color.Yellow.copy(alpha = 0.7f)
                                    )
                                )
                            }

                            in listOf(
                                MimeType.MP3.readableType,
                                MimeType.WAV.readableType,
                                MimeType.FLAC.readableType,
                            ) -> {
                                Image(
                                    imageVector = Icons.Filled.AudioFile,
                                    contentDescription = "audio file",
                                    modifier = Modifier.size(75.dp)
                                        .clickable { postEvent(PostEvent.OnAudioClicked(it)) },
                                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                                        Color.Cyan.copy(alpha = 0.7f)
                                    )
                                )
                            }

                            else -> {
                                Image(
                                    imageVector = Icons.Filled.InsertDriveFile,
                                    contentDescription = "document file",
                                    modifier = Modifier.size(75.dp)
                                        .clickable { postEvent(PostEvent.OnDocumentClicked(it)) },
                                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                                        Color.Blue.copy(alpha = 0.7f)
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.size(8.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(75.dp)
                                .wrapContentHeight(align = Alignment.CenterVertically),
                            verticalArrangement = Arrangement.SpaceAround,
                        ) {
                            Text(
                                text = "Name: " + it.name,
                                maxLines = 1,
                            )
                            Text(
                                text = "Type: " + it.type,
                                maxLines = 1,
                            )
                        }
                    }

                }
            }
        },
        sheetState = bottomSheetState,
        sheetGesturesEnabled = true,
        sheetShape = RoundedCornerShape(18.dp),
        sheetBackgroundColor = Color.LightGray

    ) {}
}

@Preview
@Composable
fun ff() {
    FilesBottomSheet(
        currentAttachments = listOf(
            PostAttachment(
                name = "name",
                type = MimeType.MKV.readableType,
                url = "url"
            ),
            PostAttachment(
                name = "name",
                type = MimeType.JPEG.readableType,
                url = "url"
            ),
            PostAttachment(
                name = "name",
                type = MimeType.DOCX.readableType,

                url = "url"
            ),
            PostAttachment(
                name = "name",
                type = MimeType.PDF.readableType,
                url = "url"
            ),
            PostAttachment(
                name = "name",
                type = MimeType.ZIP.readableType,
                url = "url"
            ),
            PostAttachment(
                name = "name",
                type = MimeType.MP3.readableType,
                url = "url"
            ),
            PostAttachment(
                name = "name",
                type = "type",
                url = "url"
            ),
        ),
        bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Expanded),
        postEvent = { PostEvent.Initial }
    )

}