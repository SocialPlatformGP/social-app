package com.gp.material.presentation

import MaterialViewModel
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gp.material.R
import com.gp.material.adapter.MaterialAdapter
import com.gp.material.databinding.FragmentMaterialBinding
import com.gp.material.listener.ClickOnFileClicKListener
import com.gp.material.model.MaterialItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
@AndroidEntryPoint
class materialfragment : Fragment() , ClickOnFileClicKListener{
    private lateinit var binding:FragmentMaterialBinding
    private val PICK_FILE_REQUEST=1
    val viewModel:MaterialViewModel  by viewModels()
    lateinit var  adapter:MaterialAdapter

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
        adapter= MaterialAdapter(this)
        binding.addMaterial.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())


            val view = layoutInflater.inflate(R.layout.material_bottom_sheet, null)
            val btnClose = view.findViewById<Button>(R.id.close)
            btnClose.setOnClickListener {
                dialog.dismiss()
            }
            val uploadImage=view.findViewById<Button>(R.id.uploadImage)
            uploadImage.setOnClickListener{
                val intentImage=Intent(Intent.ACTION_PICK)
                intentImage.type="*/*"
                startActivityForResult(intentImage,PICK_FILE_REQUEST)
            }
            lifecycleScope.launch {
                viewModel.fileItems.flowWithLifecycle(lifecycle).collect{
                    adapter.submitList(it)
                }
            }

            dialog.setCancelable(false)
            dialog.setContentView(view)
            dialog.show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            val selectedFileUri: Uri? = data?.data
            if (selectedFileUri != null) {
                runBlocking {
                    viewModel.uploadFile("",selectedFileUri,requireContext())
                }
            }
        }
    }

    override fun deleteFile(item: MaterialItem) {

    }

    override fun openFile(item: MaterialItem) {
        TODO("Not yet implemented")
    }

    override fun downloadFile(item: MaterialItem) {
        TODO("Not yet implemented")
    }

    override fun showDetails(item: MaterialItem) {
        TODO("Not yet implemented")
    }
}