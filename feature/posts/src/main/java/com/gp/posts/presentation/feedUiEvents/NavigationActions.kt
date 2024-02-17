package com.gp.posts.presentation.feedUiEvents

sealed class NavigationActions{
    object NavigateToChat : NavigationActions()
    object NavigateToProfile : NavigationActions()
    object NavigateToSearch : NavigationActions()
    object NavigateToPost : NavigationActions()
    object NavigateToSuggestPost : NavigationActions()
    object NavigateToPostDetails : NavigationActions()
    object NavigateToMaterial : NavigationActions()

}