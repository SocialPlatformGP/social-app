package com.gp.socialapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gp.socialapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Data Binding
        setContentView(R.layout.activity_main)



    }
}