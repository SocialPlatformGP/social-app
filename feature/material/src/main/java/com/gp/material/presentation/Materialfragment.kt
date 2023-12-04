package com.gp.material.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gp.material.R
import com.gp.material.databinding.FragmentMaterialBinding
import com.gp.material.source.remote.MaterialRemoteDataSource
import com.gp.material.source.remote.MaterialStorageClient
import kotlinx.coroutines.runBlocking

class materialfragment : Fragment() {
    private lateinit var binding:FragmentMaterialBinding
    private val PICK_FILE_REQUEST=1
    val material= MaterialStorageClient()

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
                    material.uploadFile("",selectedFileUri,requireContext())
                }
            }
        }
    }
}