package com.gp.posts.presentation.createpost

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.listener.ColorListener
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.gp.posts.R
import com.gp.posts.databinding.DialogAddTagBinding
import com.gp.posts.databinding.FragmentCreatePostBinding
import com.gp.socialapp.database.model.Tag
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
@AndroidEntryPoint
class CreatePostFragment : Fragment() {
    private val viewModel: CreatePostViewModel by viewModels()
    lateinit var binding: FragmentCreatePostBinding
    lateinit var addTagBinding: DialogAddTagBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_create_post, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.fragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addCancelPressedCollector()
        //TODO("implement visiblity option dropdown menu")
    }
    fun onPostClick(){
        viewModel.uiState.value.tags = getAllTags()
        addCreatedStateCollector()
        viewModel.onCreatePost()
    }
    private fun addCreatedStateCollector(){
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle).distinctUntilChanged { old, new ->
                old.createdState == new.createdState
            }.collect { uiState ->
                when (uiState.createdState) {
                    is State.Success -> {
                        findNavController().navigate(R.id.action_createPostFragment_to_feedFragment)
                        makeSnackbar(
                            getString(R.string.post_created_successfully),
                            Snackbar.LENGTH_LONG
                        )
                    }
                    is State.Error -> {
                        makeSnackbar(
                            (uiState.createdState as State.Error).message,
                            Snackbar.LENGTH_LONG
                        )
                        //TODO("Provide an informative error message to the user")
                    }
                    else ->{}
                }
            }
        }
    }
    fun onAddTagClick() {
        addTagBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.dialog_add_tag,
            binding.root as? ViewGroup,
            false
        )
        val labelEditText = addTagBinding.labelTextField
        val colorEditText = addTagBinding.colorField
        val addButton = addTagBinding.addButton
        val cancelButton = addTagBinding.cancelButton

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Tag")
            .setView(addTagBinding.root)
            .create()

        colorEditText.editText?.setOnClickListener {
            Log.d("edrees", "ColorEditText clicked")
            showColorPicker { color ->
                val hexColor = String.format("#%06X", 0xFFFFFF and color)
                colorEditText.boxBackgroundColor = color
                colorEditText.editText?.setTextColor(Color.BLACK)
                colorEditText.editText?.setText(hexColor)
            }
        }

        addButton.setOnClickListener {
            val label = labelEditText.editText?.text
            if (label.isNullOrBlank()) {
                labelEditText.error = "Label is Required!"
                labelEditText.editText!!.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }
                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        labelEditText.error = null
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })
            } else {
                val color = colorEditText.boxBackgroundColor
                val chip = Chip(ContextThemeWrapper(context, R.style.Chip_Entry))
                chip.text = label
                chip.setChipBackgroundColorResource(android.R.color.transparent)
                chip.chipBackgroundColor = ColorStateList.valueOf(color)
                chip.isCloseIconVisible = true
                chip.setOnCloseIconClickListener {
                    binding.tagsChipgroup.removeView(chip)
                    binding.addTagButton.isEnabled = true
                    binding.addTagButton.alpha = 1f
                }
                binding.tagsChipgroup.addView(chip)
                if (binding.tagsChipgroup.childCount == 8) {
                    binding.addTagButton.isEnabled = false
                    binding.addTagButton.alpha = 0.5f
                }
                dialog.dismiss()
            }
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        labelEditText.requestFocus()
    }

    private fun showColorPicker(onColorSelected: (color: Int) -> Unit) {
        Log.d("edrees", "Color Picker Created")
        MaterialColorPickerDialog
            .Builder(requireActivity())
            .setTitle("Pick Tag Color")
            .setColorShape(ColorShape.CIRCLE)
            .setColorSwatch(ColorSwatch.A400)
            .setDefaultColor(R.color.Gray)
            .setColorListener(object : ColorListener {
                override fun onColorSelected(color: Int, colorHex: String) {
                    onColorSelected(color)
                }
            })
            .show()
    }

    private fun addCancelPressedCollector(){
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle).distinctUntilChanged { old, new ->
                old.cancelPressed == new.cancelPressed
            }.collect { uiState ->
                if (uiState.cancelPressed) {
                    MaterialAlertDialogBuilder(requireContext()).setTitle("Discard post draft?")
                        .setPositiveButton("Discard") { dialog, which ->
                            findNavController().navigate(R.id.action_createPostFragment_to_feedFragment)
                        }.setNegativeButton("Cancel"){_, _ ->
                        }.show()
                    viewModel.resetCancelPressed()
                }
            }
        }
    }
    private fun getAllTags(): List<Tag> {
        val tags = mutableListOf<Tag>()
        for (i in 0 until binding.tagsChipgroup.childCount) {
            val chipView = binding.tagsChipgroup.getChildAt(i)
            if (chipView is Chip) {
                val chipText = chipView.text.toString()
                val chipColor = String.format("#%06X", (0xFFFFFF and chipView.chipBackgroundColor!!.defaultColor))
                tags.add(Tag(chipText, chipColor))
            }
        }
        return tags
    }
    private fun makeSnackbar(message: String, duration: Int) {
        Snackbar.make(
            requireContext(),
            binding.root,
            message,
            duration
        ).show()
    }
}