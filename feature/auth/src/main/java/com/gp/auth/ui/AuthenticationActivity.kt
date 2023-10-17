package com.gp.auth.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gp.auth.R
import com.gp.auth.ui.login.LoginFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthenticationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
    }
}