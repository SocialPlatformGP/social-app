package com.gp.posts.presentation.feedUiEvents

import com.gp.socialapp.model.Post

sealed class NavigationActions{
    object NavigateToChat : NavigationActions()
    object NavigateToProfile : NavigationActions()
    object NavigateToSearch : NavigationActions()
    object NavigateToPost : NavigationActions()
    object NavigateToSuggestPost : NavigationActions()
    data class NavigateToPostDetails(val post: Post) : NavigationActions()
    object NavigateToMaterial : NavigationActions()
    object NavigateToNotification : NavigationActions()

}