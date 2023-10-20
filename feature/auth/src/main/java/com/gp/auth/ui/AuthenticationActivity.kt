package com.gp.auth.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.auth.R
import com.gp.auth.ui.login.LoginFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            val intent = Intent()
            intent.setClassName("com.gp.socialapp", "com.gp.socialapp.MainActivity")
            startActivity(intent)
            finish()
        }
    }
}