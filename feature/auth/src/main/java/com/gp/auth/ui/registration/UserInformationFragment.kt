package com.gp.auth.ui.registration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.gp.auth.R
import com.gp.auth.databinding.FragmentUserInformationBinding
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class UserInformationFragment : Fragment() {
    private val viewModel: UserInformationViewModel by viewModels()
    private lateinit var binding: FragmentUserInformationBinding
    private val args: UserInformationFragmentArgs by navArgs()
    private val galleryImageResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            binding.profilePictureImageview.setImageURI(it)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_information, container, false)
        binding.lifecycleOwner = this
        binding.fragment = this
        binding.viewmodel = viewModel
        return binding.root
    }

    fun onDateFieldClick() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Birthdate")
                .build()
        datePicker.addOnPositiveButtonClickListener { unixTime ->
            val selectedDate = Date(unixTime)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate)
            viewModel.uiState.value.birthDate = selectedDate
            binding.birthDateField.editText?.setText(formattedDate)
        }
        datePicker.show(requireActivity().supportFragmentManager, "tag")

    }

    fun onLoadPictureClick() {
        val items = arrayOf("Take Photo", "Choose Existing Photo")
        MaterialAlertDialogBuilder(requireContext())
            .setItems(items) { dialog, which ->
                when (which) {
                    0 -> {
                        onTakePhotoSelected()
                    }

                    1 -> {
                        onChoosePhotoSelected()
                    }
                }
            }
            .show()
    }

    private fun onTakePhotoSelected() {
        TODO("implement camera capturing")
    }

    private fun onChoosePhotoSelected() {
        galleryImageResultLauncher.launch("image/*")
    }


    fun onCompleteAccountClick() {
        if (validateInputs()) {
            createAccount()
        }

    }

    private fun validateInputs() =
        validateEmptyFields()
//                && validatePhoneNumber() && validateBirthDate()
//                && validateFirstName() && validateLastName()

    private fun validateEmptyFields(): Boolean {
        with(viewModel.uiState.value){
            if(firstName.isNullOrBlank()){
                binding.firstNameTextField.error = "First name is required"
                binding.firstNameTextField.editText?.addTextChangedListener {
                    binding.firstNameTextField.error = null
                }
                return false
            }
            else if(lastName.isNullOrBlank()){
                binding.lastNameTextField.error = "Last name is required"
                binding.lastNameTextField.editText?.addTextChangedListener {
                    binding.lastNameTextField.error = null
                }
                return false
            }
            else if(phoneNumber.isNullOrBlank()){
                binding.phonenumberTextField.error = "Phone number is required"
                binding.phonenumberTextField.editText?.addTextChangedListener {
                    binding.phonenumberTextField.error = null
                }
                return false
            }
            else if(binding.birthDateField.editText?.text.isNullOrBlank()){
                binding.birthDateField.error = "Birth date is required"
                binding.birthDateField.editText?.addTextChangedListener {
                    binding.birthDateField.error = null
                }
                return false
            }
            else if(bio.isNullOrBlank()){
                binding.aboutTextField.error = "Bio is required"
                binding.aboutTextField.editText?.addTextChangedListener {
                    binding.aboutTextField.error = null
                }
                return false
            }
            else{
                return true
            }
        }

    }

    private fun createAccount() {
        viewModel.uiState.value.pfp = binding.profilePictureImageview.drawable
        viewModel.onCompleteAccount(args.userEmail, args.userPassword)
        makeSnackbar("Account created successfully")
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .distinctUntilChanged { old, new ->
                    old.createdState == new.createdState
                }.collect {
                    when (it.createdState) {
                        is State.Success -> {
                            val intent = Intent()
                            intent.setClassName("com.gp.socialapp", "com.gp.socialapp.MainActivity")
                            startActivity(intent)
                            activity?.finish()
                        }

                        is State.Error -> {
                            makeSnackbar(it.createdState.message)
                        }

                        else -> {}
                    }
                }
        }
        Log.d("edrees", "state: ${viewModel.uiState.value}")
    }

    private fun makeSnackbar(text: String) =
        Snackbar.make(requireContext(), binding.root, text, Snackbar.LENGTH_SHORT).show()
}