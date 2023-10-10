package com.gp.posts.presentation.postsfeed

import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.Post

val feedDataList = mutableListOf(
    PostEntity(
        id = "1",
        title = "Amazing Sunset",
        authorID = 201589,
        publishedAt = "2023-10-01",
        body = "Witnessed an incredible sunset today at the beach.",
        upvotes = 50,
        downvotes = 5,
        editStatus = true,
        moderationStatus = "Approved"
    ),
    PostEntity(
        id = "2",
        title = "Delicious Dinner",
        authorID = 5486543,
        publishedAt = "2023-09-28",
        body = "Had a fantastic dinner at a local restaurant.",
        upvotes = 30,
        downvotes = 2,
        editStatus = true,
        moderationStatus = "Approved"
    ),
    PostEntity(
        id = "3",
        title = "Hiking Adventure",
        authorID = 6453653,
        editStatus = true,
        publishedAt = "2023-09-25",
        body = "Went on an exciting hiking trip over the weekend.",
        upvotes = 75,
        downvotes = 8,
        moderationStatus = "Approved"
    ),
    PostEntity(
        id = "4",
        title = "Movie Night",
        editStatus = true,
        authorID = 6435645,
        publishedAt = "2023-09-20",
        body = "Enjoyed a great movie night with friends.",
        upvotes = 42,
        downvotes = 3,
        moderationStatus = "Approved"
    ),
    PostEntity(
        id = "5",
        title = "New Recipe",
        authorID = 78634568,
        editStatus = true,
        publishedAt = "2023-09-18",
        body = "Tried a new recipe for dinner tonight, and it was a hit!",
        upvotes = 68,
        downvotes = 7,
        moderationStatus = "Approved"
    ),
    PostEntity(
        id = "6",
        title = "Sunny Day",
        editStatus = true,
        authorID = 7486346,
        publishedAt = "2023-09-15",
        body = "Enjoyed the beautiful weather at the park today.",
        upvotes = 55,
        downvotes = 4,
        moderationStatus = "Approved"
    ),
    PostEntity(
        id = "7",
        title = "Art Exhibition",
        editStatus = true,
        authorID = 874637,
        publishedAt = "2023-09-12",
        body = "Visited an amazing art exhibition over the weekend.",
        upvotes = 62,
        downvotes = 6,
        moderationStatus = "Approved"
    ),
    PostEntity(
        id = "8",
        title = "Travel Adventure",
        editStatus = true,
        authorID = 485636,
        publishedAt = "2023-09-10",
        body = "Embarked on a thrilling travel adventure to a new country.",
        upvotes = 89,
        downvotes = 9,
        moderationStatus = "Approved"
    ),
    PostEntity(
        id = "9",
        title = "Gardening Success",
        editStatus = true,
        authorID = 78687686,
        publishedAt = "2023-09-07",
        body = "My garden is flourishing, and I couldn't be happier.",
        upvotes = 37,
        downvotes = 2,
        moderationStatus = "Approved"
    ),
    PostEntity(
        id = "10",
        title = "Tech Review",
        authorID = 8756786,
        editStatus = true,
        publishedAt = "2023-09-05",
        body = "Reviewed the latest gadgets and tech trends.",
        upvotes = 50,
        downvotes = 3,
        moderationStatus = "Approved"
    )
)


