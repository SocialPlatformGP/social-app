package com.gp.auth.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.auth.R
import com.gp.auth.ui.login.LoginFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthenticationActivity : AppCompatActivity() {
    private val viewModel: AuthenticationViewModel by viewModels()
    private val PREFS_FILE_NAME = "shit_fix"
    private val KEY_BOOLEAN_VALUE = "isUserComplete"
    override fun onStart() {
        super.onStart()
        val currentUser = viewModel.getSignedInUser()
        if (currentUser != null && getBooleanFromSharedPreferences()) {
           val intent = Intent()
           intent.setClassName("com.gp.socialapp", "com.gp.socialapp.MainActivity")
           startActivity(intent)
           finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
    }
    private fun getBooleanFromSharedPreferences(): Boolean {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_BOOLEAN_VALUE, true)
    }
}
