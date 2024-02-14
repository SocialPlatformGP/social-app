package com.gp.posts.presentation.postDetails

import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gp.socialapp.model.Tag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.gp.socialapp.model.Post



@Composable
fun TagChip(tag: Tag, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(
                    bounded = true,
                    radius = 250.dp,
                    color = Color.Green
                ),
                onClick = { onClick }
            )
            .padding(4.dp)
            .clip(CircleShape)
    ) {
        Box(
            modifier = Modifier
                .background(Color(android.graphics.Color.parseColor(tag.hexColor)))
        ) {
            Text(
                text = tag.label,
                modifier = Modifier
                    .padding(8.dp)
                    .wrapContentSize(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserPostTags(userPost: Post) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
            .padding(top = 8.dp),
        content = {
            for (tag in userPost.tags) {
                TagChip(tag = tag, onClick = { /* Handle tag click here */ })
            }
        }
    )
}

data class UserPost(
    val username: String,
    val tags: List<Tag>
)
val tag :Tag=Tag("test","#fff566")
@Preview(showSystemUi = true, showBackground = true, backgroundColor = 0xFF84FFFF)
@Composable
fun PreviewTags() {

}

