package com.gp.posts.presentation.utils

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object CurrentUser {
    fun getCurrentUser(): FirebaseUser? {
        return Firebase.auth.currentUser
    }
}