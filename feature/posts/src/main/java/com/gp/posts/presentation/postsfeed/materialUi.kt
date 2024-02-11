package com.gp.posts.presentation.postsfeed

import android.graphics.pdf.PdfRenderer
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.F
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gp.posts.R
import com.gp.posts.presentation.postDetails.imageCaching
import com.gp.socialapp.database.model.PostAttachment
import kotlinx.coroutines.delay



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagePager(images: List<PostAttachment>) {
    Card(
        modifier = Modifier.padding(6.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        ManualSlidingCarousel(images = images, itemsCount = images.size) { index ->
            if (images.indices.contains(index)) {
                val image = images[index]
                imageCaching(imageUri = image.url, modifier = Modifier.height(300.dp).fillMaxWidth() )
//                AsyncImage(
//                    model = ImageRequest.Builder(LocalContext.current)
//                        .data(image.url)
//                        .crossfade(true)
//                        .build(),
//                    placeholder = painterResource(R.drawable.pngwing_com),
//                    contentDescription = "picture image",
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .height(300.dp)
//                        .fillMaxWidth()
//                )
            }
        }
    }
}

@Composable
fun IndicatorDot(
    modifier: Modifier = Modifier,
    size: Dp,
    color: Color
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun DotsIndicator(
    modifier: Modifier = Modifier,
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color = Color.White,
    unSelectedColor: Color = Color.Gray,
    dotSize: Dp
) {
    LazyRow(
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight()
    ) {
        items(totalDots) { index ->
            IndicatorDot(
                color = if (index == selectedIndex) selectedColor else unSelectedColor,
                size = dotSize
            )

            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ManualSlidingCarousel(
    images: List<PostAttachment>,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState { images.size },
    itemsCount: Int,
    itemContent: @Composable (index: Int) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        HorizontalPager(
            state = pagerState
        ) { page ->
            val image = images.getOrNull(page)
            if (image != null) {
                itemContent(page)
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray)
                )
            }
        }

        Surface(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.BottomCenter),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.5f)
        ) {
            DotsIndicator(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                totalDots = itemsCount,
                selectedIndex = pagerState.currentPage,
                dotSize = 8.dp
            )
        }
    }
}

@Composable
fun FileMaterial(fileList: List<PostAttachment>) {
    var expanded by remember { mutableStateOf(false) }

    val visibleItems = if (expanded) fileList else fileList.take(3)
    val hiddenItems = fileList.drop(3)

    Column {
        for (file in visibleItems) {
            FileItem(file)
        }
        if (hiddenItems.isNotEmpty()) {
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Show less" else "Show more",
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun FileItem(
    file: PostAttachment,
    size: Dp = 44.dp
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.surface)
            .clickable { },
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colors.secondary)
            ) {
                val imageResource = when (file.type) {
                    "PDF" -> R.drawable.img
                    else -> R.drawable.img
                }
                Image(
                    painter = painterResource(id = imageResource),
                    contentDescription = "File Icon",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.subtitle1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = file.type,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.secondaryVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Size: ${formatFileSize(file.size)}",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.secondaryVariant
                )
            }
        }
    }
}

private fun formatFileSize(size: Long): String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    var fileSize = size.toDouble()
    var unitIndex = 0
    while (fileSize > 1024 && unitIndex < units.size - 1) {
        fileSize /= 1024
        unitIndex++
    }
    return "%.1f %s".format(fileSize, units[unitIndex])
}


@Preview(showSystemUi = true, showBackground = true, backgroundColor = 0xFFFFFFFF, apiLevel = 29)

@Composable
fun PreviewFileItem() {
}



