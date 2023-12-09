package com.gp.chat.presentation

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.gp.chat.R
import com.gp.chat.databinding.FragmentCameraPreviewBinding
import com.gp.chat.presentation.groupchat.GroupChatViewModel
import com.gp.chat.presentation.privateChat.PrivateChatViewModel
import com.gp.material.utils.FileUtils.getFileName
import com.gp.material.utils.FileUtils.getMimeTypeFromUri
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class CameraPreviewFragment : DialogFragment() {
    lateinit var binding: FragmentCameraPreviewBinding
    private var imageCapture: ImageCapture? = null
    private val viewModel: PrivateChatViewModel by viewModels()
    private val groupViewModel: GroupChatViewModel by viewModels()
    private val args: CameraPreviewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initializeViewModel()

        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_camera_preview, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!hasPermissions(requireContext())) {
            activityResultLancer.launch(REQUIRED_PERMISSIONS)
        } else {
            lifecycleScope.launch {
                startCamera()
            }

        }
        binding.captureButton.setOnClickListener { takePhoto() }

    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                requireContext().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(requireContext().applicationContext, msg, Toast.LENGTH_SHORT)
                        .show()
                    Log.d(TAG, msg)
                    binding.imageView.visibility = View.VISIBLE
                    binding.viewFinder.visibility = View.GONE
                    binding.okButton.visibility = View.VISIBLE
                    binding.cancelButton.visibility = View.VISIBLE
                    Glide.with(requireContext())
                        .load(output.savedUri)
                        .into(binding.imageView)
                    binding.okButton.setOnClickListener {
                        val mimeType = getMimeTypeFromUri(output.savedUri!!, requireContext())
                        val fileName = getFileName(output.savedUri!!, requireContext())
                        Log.d("TAGRRR", "onViewCreated: $mimeType $fileName ${output.savedUri}")
                        if (args.isPrivateChat) {
                            viewModel.sendFile(output.savedUri!!, mimeType!!, fileName)
                        }
                        else {
                            groupViewModel.sendFile(output.savedUri!!, mimeType!!, fileName)
                        }
                        findNavController().popBackStack()
                    }
                    binding.cancelButton.setOnClickListener {
                        findNavController().popBackStack()
                    }

                }
            }
        )
    }


    private val activityResultLancer =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->

            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false) {
                    permissionGranted = false
                }
            }
            if (!permissionGranted) {
                Toast.makeText(requireContext(), "Permission not granted", Toast.LENGTH_SHORT)
                    .show()
            } else {
                startCamera()

            }
        }

    companion object {
        private const val TAG = "CameraPreviewFragment"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                android.Manifest.permission.CAMERA,
            ).apply {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()

        fun hasPermissions(context: Context) = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun initializeViewModel() {
        if (args.isPrivateChat) {
            viewModel.setData(
                args.chatId,
                args.senderName,
                args.receiverName,
                args.senderPic,
                args.receiverPic
            )
        } else {
            groupViewModel.setData(
                args.chatId,
                args.senderName,
                args.senderPic,

                )
        }
    }

}