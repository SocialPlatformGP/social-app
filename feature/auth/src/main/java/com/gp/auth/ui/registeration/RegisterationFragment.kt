package com.gp.auth.ui.registeration

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gp.auth.R

class RegisterationFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterationFragment()
    }

    private lateinit var viewModel: RegisterationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registeration, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterationViewModel::class.java)
        // TODO: Use the ViewModel
    }

}