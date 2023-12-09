package com.gp.posts.presentation.createpost

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.listener.ColorListener
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.gp.material.utils.FileUtils.getEnumMimeTypeFromUri
import com.gp.material.utils.FileUtils.getFileName
import com.gp.posts.R
import com.gp.posts.databinding.DialogAddTagBinding
import com.gp.posts.databinding.FragmentCreatePostBinding
import com.gp.posts.listeners.OnFilePreviewClicked
import com.gp.socialapp.database.model.MimeType
import com.gp.socialapp.database.model.PostFile
import com.gp.socialapp.model.Tag
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreatePostFragment : Fragment(), OnFilePreviewClicked {
    private val viewModel: CreatePostViewModel by viewModels()
    lateinit var binding: FragmentCreatePostBinding
    lateinit var addTagBinding: DialogAddTagBinding
    private val args: CreatePostFragmentArgs by navArgs()
    private val openFileResultLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents())
    { list ->
        list.forEach {uri ->
            if(viewModel.uiState.value.files.any { it.uri == uri }){
                makeSnackbar("This File is already added!", Snackbar.LENGTH_LONG)
            } else {
                viewModel.addFile(
                    PostFile(
                        uri = uri,
                        name = getFileName(uri, requireContext()),
                        type = getEnumMimeTypeFromUri(uri, requireContext())
                    )
                )
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_create_post, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.fragment = this
        binding.context = requireContext()
        binding.onFilePreviewsClicked = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setType(args.type)
        addCancelPressedCollector()
        addPostFilesChangeCollector()
    }

    private fun addPostFilesChangeCollector() {
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle).distinctUntilChanged { old, new ->
                old.files == new.files
            }.collect{

            }
        }
    }

    fun onPostClick() {
        val tags = getAllTags()
        viewModel.insertNewTags(tags)
        viewModel.uiState.value.tags = tags
        addCreatedStateCollector()
        viewModel.onCreatePost()
    }

    private fun addCreatedStateCollector() {
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
                        hideProgressBar()
                    }

                    is State.Error -> {
                        hideProgressBar()
                        makeSnackbar(
                            (uiState.createdState as State.Error).message,
                            Snackbar.LENGTH_LONG
                        )
                        //TODO("Provide an informative error message to the user")
                    }
                    is State.Loading ->{
                        showProgressBar()
                    }
                    else -> {}
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
        val labelMenu = addTagBinding.labelTextField.editText as? AutoCompleteTextView
        val colorEditText = addTagBinding.colorField
        val addButton = addTagBinding.addButton
        val cancelButton = addTagBinding.cancelButton
        val tags = viewModel.tags.value
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Tag")
            .setView(addTagBinding.root)
            .create()

        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, tags.map { it.label })
        (labelMenu)?.setAdapter(adapter)
        labelMenu?.setOnItemClickListener { parent, view, position, id ->
            val tag = tags[position]
            labelEditText.editText?.setText(tag.label)
            colorEditText.boxBackgroundColor = Color.parseColor(tag.hexColor)
            colorEditText.editText?.setTextColor(Color.BLACK)
            colorEditText.editText?.setText(tag.hexColor)
        }

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

            val label = labelMenu?.text.toString()
            if (label.isBlank()) {
                labelEditText?.error = "Label is Required!"
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

                    override fun afterTextChanged(s: Editable?) {

                    }
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

    private fun addCancelPressedCollector() {
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle).distinctUntilChanged { old, new ->
                old.cancelPressed == new.cancelPressed
            }.collect { uiState ->
                if (uiState.cancelPressed) {
                    MaterialAlertDialogBuilder(requireContext()).setTitle("Discard post draft?")
                        .setPositiveButton("Discard") { dialog, which ->
                            findNavController().navigate(R.id.action_createPostFragment_to_feedFragment)
                        }.setNegativeButton("Cancel") { _, _ ->
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
                val chipColor = String.format(
                    "#%06X",
                    (0xFFFFFF and chipView.chipBackgroundColor!!.defaultColor)
                )
                tags.add(Tag(chipText, chipColor))
            }
        }
        return tags
    }
    fun onOpenImageClick(){
        openFileResultLauncher.launch(MimeType.IMAGE.value)
    }
    fun onOpenVideoClick(){
        openFileResultLauncher.launch(MimeType.VIDEO.value)
    }
    fun onOpenFileClick(){
        openFileResultLauncher.launch(MimeType.ALL_FILES.value)
    }

    private fun makeSnackbar(message: String, duration: Int) {
        Snackbar.make(
            requireContext(),
            binding.root,
            message,
            duration
        ).show()
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun onFilePreviewClicked(file: PostFile) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(file.uri, file.type.value)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
           requireContext().startActivity(intent)
        } else {
            makeSnackbar("No app available to open this file format!", Snackbar.LENGTH_SHORT)
        }
    }

    override fun onFileRemoveClicked(file: PostFile) {
        viewModel.removeFile(file)
    }
    private fun showProgressBar(){
        binding.progressIndicator.visibility = View.VISIBLE
    }
    private fun hideProgressBar(){
        binding.progressIndicator.visibility = View.INVISIBLE
    }
}