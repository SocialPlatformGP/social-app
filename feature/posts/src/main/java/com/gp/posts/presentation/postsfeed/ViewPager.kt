package com.gp.posts.presentation.postsfeed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ViewPager() {
    var currentPage by remember { mutableStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            ViewPagerIndicator(
                pageCount = 2,
                currentPage = currentPage,
                indicatorColor = MaterialTheme.colorScheme.primary
            )

            HorizontalPager(count = 2) {
                when (it) {
                    0 -> FirstComposable()
                    1 -> SecondComposable()
                }

                }
            }
        }
    }

@Composable
fun FirstComposable() {
    Text("First Page Content", modifier = Modifier.padding(16.dp))
}

@Composable
fun SecondComposable() {
    Text("Second Page Content", modifier = Modifier.padding(16.dp))
}


@Preview(showSystemUi = true, showBackground = true, backgroundColor = 0xFFFEFEFE)
@Composable
fun pagerPreview() {
    ViewPager()
}
