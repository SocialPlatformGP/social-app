package com.gp.material.presentation

import android.app.Activity
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gp.material.R
import com.gp.material.adapter.MaterialAdapter
import com.gp.material.databinding.FragmentMaterialBinding
import com.gp.material.listener.ClickOnFileClicKListener
import com.gp.material.model.FileType
import com.gp.material.model.MaterialItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class materialfragment : Fragment(), ClickOnFileClicKListener {

    private lateinit var binding: FragmentMaterialBinding
    private val PICK_FILE_REQUEST = 1
    private val viewModel: MaterialViewModel by viewModels()
    private lateinit var adapter: MaterialAdapter
    private lateinit var dialog: BottomSheetDialog
    private val actionUpload= registerForActivityResult(ActivityResultContracts.GetContent()){
        it?.let {
            viewModel.uploadFile(it,requireContext())
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {


        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMaterialBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchDataFromFirebaseStorage()
        adapter = MaterialAdapter(this)
        dialog = BottomSheetDialog(requireContext())
        binding.materialListRecyclerView.adapter = adapter
        val view = layoutInflater.inflate(R.layout.material_bottom_sheet, null)
        val btnClose = view.findViewById<Button>(R.id.close)
        btnClose.setOnClickListener {
            dialog.dismiss()
        }


        binding.addMaterial.setOnClickListener {
            val uploadImage = view.findViewById<Button>(R.id.uploadImage)
            uploadImage.setOnClickListener {
                actionUpload.launch("*/*")
                dialog.dismiss()
            }


            dialog.setCancelable(false)
            dialog.setContentView(view)
            dialog.show()
        }

        val uploadFolderButton = view.findViewById<Button>(R.id.createNewFolder)
        uploadFolderButton.setOnClickListener {
            openNameDialog()
        }



        lifecycleScope.launch {
            viewModel.folderItems.combine(viewModel.fileItems) { folderItems, fileItems ->
                folderItems + fileItems
            }.flowWithLifecycle(lifecycle).collect { it ->
                Log.d("waleed32", it.toString())
                adapter.submitList(it.sortedBy { it.creationTime })
            }
        }



        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(  viewModel.goBack()) {
                    viewModel.fetchDataFromFirebaseStorage()
                }

                else{

                    findNavController().popBackStack()
                }


            }
        })

    }



    override fun deleteFile(item: MaterialItem) {
        viewModel.deleteFile(item.path)
        viewModel.fetchDataFromFirebaseStorage()
    }



    override fun openFile(item: MaterialItem) {

            val openFileIntent = Intent(Intent.ACTION_VIEW)
            openFileIntent.setDataAndType(Uri.parse(item.fileUrl), item.fileType.getMimeType())
            openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            context?.startActivity(openFileIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No app found to open the file", Toast.LENGTH_SHORT).show()
        }
    }



    private fun FileType.getMimeType(): String {
        return when (this) {
            FileType.IMAGE -> "image/*"
            FileType.VIDEO -> "video/*"
            FileType.PDF -> "application/pdf"
            FileType.AUDIO -> "audio/*"
            else -> "*/*"
        }
    }



    override fun downloadFile(item: MaterialItem) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.fileUrl))
        context?.startActivity(intent)
    }




    override fun openFolder(path: String) {
        viewModel.openFolder(path)
        viewModel.clearData()
        viewModel.fetchDataFromFirebaseStorage()
    }



    override fun deleteFolder(folderPath: String) {
        viewModel.deleteFolder(folderPath)
    }



    override fun shareLink(item: MaterialItem) {
        val intent=Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, item.fileUrl)
            type = "*/*"
        }
        startActivity(intent)
    }



    private fun openNameDialog(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enter Your Name")

        val input = EditText(requireContext())
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            val name = input.text.toString()
            viewModel.uploadFolder(viewModel.getCurrentPath(),name)
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }





}


