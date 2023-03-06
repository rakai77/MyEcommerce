package com.example.myecommerce.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.myecommerce.R
import com.example.myecommerce.databinding.FragmentProfileBinding
import com.example.myecommerce.ui.login.LoginActivity
import com.example.myecommerce.utils.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.util.*
import com.example.myecommerce.data.Result
import com.example.myecommerce.data.response.ErrorResponse
import com.example.myecommerce.utils.SpinnerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var currentPhotoPath: String? = null
    private var file: File? = null

    private var userID: Int? = null

    private val viewModel by viewModels<ProfileViewModel>()

    private val arrLanguage = arrayOf("EN", "ID")
    private val arrFlag = intArrayOf(R.drawable.flag_us, R.drawable.flag_id)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentProfileBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        getData()
        setupLanguage()

        binding.fabProfileChange.setOnClickListener {
            selectImageProfile()
        }

        binding.tflLogout.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            Toast.makeText(
                requireContext(),
                "Logout Successfully",
                Toast.LENGTH_LONG
            ).show()
            activity?.finish()
        }

        binding.tflChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_changePasswordActivity)
        }
    }

    private fun getData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.getUserID.collect {
                        userID = it
                        Log.d("check id", "$it")
                    }
                }
                launch {
                    viewModel.getNameUser.collect {
                        binding.profileName.text = it
                        Log.d("check name", it)
                    }
                }

                launch {
                    viewModel.getEmailUser.collect {
                        binding.profileEmail.text = it
                        Log.d("check email", it)
                    }
                }
                launch {
                    viewModel.getImagePath.collect {
                        Glide.with(requireActivity()).load(it).into(binding.ivProfile)
                        Log.d("check image", it)
                    }
                }
                launch {
                    viewModel.getLanguage.collect {
                        when (it) {
                            0 -> {
                                binding.spinnerLanguage.setSelection(0)
                            }
                            1 -> {
                                binding.spinnerLanguage.setSelection(1)
                            }
                        }
                    }
                }
            }
        }

    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = Utils.uriToFile(selectedImg, requireContext())
            file = myFile
            changeImageProfile()
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val myFile = currentPhotoPath?.let { it1 -> File(it1) }
        file = myFile
        val result = BitmapFactory.decodeFile(myFile?.path)
        binding.ivProfile.setImageBitmap(result)
        changeImageProfile()

    }

    private fun selectImageProfile() {
        val items = arrayOf(getString(R.string.btn_camera), getString(R.string.btn_gallery))
        MaterialAlertDialogBuilder(requireContext())
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
        Glide.with(requireContext()).load(currentPhotoPath).into(binding.ivProfile)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireActivity().packageManager)
        Utils.createTempFile(requireContext()).also {
            val photoUri: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.myecommerce.ui.register",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun changeImageProfile() {
        if (file != null) {
            val media = Utils.reduceFileImage(file as File)
            val requestImageFile = media.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "image",
                media.name,
                requestImageFile
            )
            viewModel.changeImage(userID!!, imageMultipart)
            viewModel.state.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Result.Loading -> {
                        binding.progressbar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressbar.visibility = View.GONE

                        viewModel.saveImagePath(response.data.success.path)
                        Glide.with(requireActivity()).load(response).into(binding.ivProfile)
                        Toast.makeText(
                            requireContext(),
                            "Change image success ${response.data.success.status}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Result.Error -> {
                        binding.progressbar.visibility = View.GONE
                        try {
                            val errors =
                                response.errorBody?.string()?.let { JSONObject(it).toString() }
                            val gson = Gson()
                            val jsonObject = gson.fromJson(errors, JsonObject::class.java)
                            val errorResponse = gson.fromJson(jsonObject, ErrorResponse::class.java)

                            Toast.makeText(
                                requireContext(),
                                "${errorResponse.error?.message} ${errorResponse.error?.status}",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                "Token Has Expired",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupLanguage() {
        val spinnerAdapter = SpinnerAdapter(requireContext(), arrFlag, arrLanguage)
        var isSpinnerTouched = false

        binding.apply {

            spinnerLanguage.adapter = spinnerAdapter
            spinnerLanguage.setOnTouchListener { _, _ ->
                isSpinnerTouched = true
                false
            }

            spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (isSpinnerTouched) {
                        when (position) {
                            0 -> {
                                setLocate("EN")
                                viewModel.saveLanguage(position)
                                activity!!.recreate()
                                Log.d("Check Config Lang", "$position")
                            }
                            1 -> {
                                setLocate("ID")
                                viewModel.saveLanguage(position)
                                activity!!.recreate()
                                Log.d("Check Config Lang", "$position")

                            }
                        }
                    } else {
                        isSpinnerTouched = false
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun setLocate(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        requireActivity().resources.updateConfiguration(config, resources.displayMetrics)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}