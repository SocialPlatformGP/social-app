package com.gp.posts.presentation.postDetails


import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gp.posts.R
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.posts.presentation.postsfeed.DropDownMenu
import com.gp.posts.presentation.postsfeed.FileMaterial
import com.gp.posts.presentation.postsfeed.IMAGE_TYPES
import com.gp.posts.presentation.postsfeed.ImagePager
import com.gp.socialapp.model.Post
import com.gp.socialapp.util.DateUtils

@Composable
fun DetailsScreen(viewModel: PostDetailsViewModel, post:Post,edit:(Post)->Unit){
    val statePost by viewModel.currentPost.collectAsState()
    val stateReply by viewModel.currentReplies.collectAsState()
    Log.d("vip", "DetailsScreen:${statePost.id.toString() +stateReply.toString()} ")
    Column {
        CommentListScreen(replies = stateReply,post=post)
    }
    }