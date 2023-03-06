package com.example.myecommerce.ui.register

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.myecommerce.R
import com.example.myecommerce.data.Result
import com.example.myecommerce.data.response.ErrorResponse
import com.example.myecommerce.databinding.ActivityRegisterBinding
import com.example.myecommerce.utils.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var currentPhotoPath: String
    private val viewModel by viewModels<RegisterViewModel>()
    private var file: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSION
            )
        }

        setupViewModel()
        setupRegister()
    }

    private fun setupRegister() {
        binding.apply {
            btnSignup.setOnClickListener {
                if (setupForm() || file != null) {
                    val media = Utils.reduceFileImage(file as File)
                    val requestImageFile = media.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "image",
                        media.name,
                        requestImageFile
                    )
                    viewModel.register(
                        imageMultipart,
                        email = binding.edtEmail.text.toString().toRequestBody("text/plain".toMediaType()),
                        password = binding.edtPassword.text.toString().toRequestBody("text/plain".toMediaType()),
                        name = binding.edtName.text.toString().toRequestBody("text/plain".toMediaType()),
                        phone = binding.edtPhone.text.toString().toRequestBody("text/plain".toMediaType()),
                        gender = if (binding.radGenderMale.isChecked) 0 else 1
                    )
                }
            }
            bindingPhoto()
        }
    }

    private fun bindingPhoto() {
        binding.fabProfileChange.setOnClickListener {
            selectImageFrom()
        }
    }

    private fun setupForm(): Boolean {
        var form = false

        val name = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()
        val confirmPassword = binding.edtConfirmPassword.text.toString()
        val phone = binding.edtPhone.text.toString()
        val male = binding.radGenderMale
        val female = binding.radGenderFemale
        val genderList = listOf(male, female).firstOrNull { it.isChecked }

        when {
            name.isEmpty() -> {
                binding.tflName.error = "Please enter your name."
            }
            email.isEmpty() -> {
                binding.tflEmail.error = "Please fill your email address."
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.tflEmail.error = "Please enter a valid email address."
            }
            password.isEmpty() -> {
                binding.tflPassword.error = "Please enter your password."
            }
            password.length < 6 -> {
                binding.tflPassword.error = "Password length must be 6 character."
            }
            phone.isEmpty() -> {
                binding.tflPhone.error = "Please enter your phone number."
            }
            confirmPassword != password -> {
                binding.tflPhone.error = "Password must be same New Password ."
            }
            genderList == null -> {
                Toast.makeText(
                    this@RegisterActivity,
                    "Please select your gender.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> form = true
        }
        return form
    }

    private fun selectImageFrom() {
        val items = arrayOf(getString(R.string.btn_camera), getString(R.string.btn_gallery))
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.select_image))
            .setItems(items) { _, which ->
                when (which) {
                    0 -> startTakePhoto()
                    1 -> startGallery()
                }
            }.show()
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, resources.getString(R.string.open_gallery))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = Utils.uriToFile(selectedImg, this@RegisterActivity)
            file = myFile
            binding.ivUserRegister.setImageURI(selectedImg)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            file = myFile
            val result = BitmapFactory.decodeFile(myFile.path)
            binding.ivUserRegister.setImageBitmap(result)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        Utils.createTempFile(application).also {
            val photoUri: Uri = FileProvider.getUriForFile(
                this@RegisterActivity,
                "com.example.myecommerce.ui.register",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intent)
        }
    }


    private fun setupViewModel() {
        viewModel.state.observe(this@RegisterActivity) { response ->
            when (response) {
                is Result.Loading -> {
                    binding.stateLoading.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.stateLoading.visibility = View.GONE

                    Toast.makeText(
                        this@RegisterActivity,
                        "Register Successfully ${response.data.success.status}",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                is Result.Error -> {
                    binding.stateLoading.visibility = View.GONE
                    val errors = response.errorBody?.string()?.let { JSONObject(it).toString() }
                    val gson = Gson()
                    val jsonObject = gson.fromJson(errors, JsonObject::class.java)
                    val errorResponse = gson.fromJson(jsonObject, ErrorResponse::class.java)

                    Toast.makeText(
                        this@RegisterActivity,
                        "${errorResponse.error?.message} ${errorResponse.error?.status}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionGranted()) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.permission_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
        const val CAMERA_X_RESULT = 200
    }
}

