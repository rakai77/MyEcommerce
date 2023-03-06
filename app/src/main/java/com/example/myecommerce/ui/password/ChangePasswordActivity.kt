package com.example.myecommerce.ui.password

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myecommerce.data.Result
import com.example.myecommerce.data.response.ErrorResponse
import com.example.myecommerce.databinding.ActivityChangePasswordBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private val viewModel by viewModels<ChangePasswordViewModel>()
    private var userId: Int? = null
    private var auth: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupChangePassword()
        getData()
    }

    private fun setupChangePassword() {
        binding.apply {
            btnSave.setOnClickListener {
                if (setupForm()) {
                    viewModel.changePassword(
                        id = userId!!,
                        password = binding.edtOldPassword.text.toString(),
                        newPassword = binding.edtNewPassword.text.toString(),
                        confirmPassword = binding.edtConfirmNewPassword.text.toString()
                    )
                }
            }
        }
    }

    private fun setupForm(): Boolean {
        var form = false

        val oldPassword = binding.edtOldPassword.text.toString()
        val newPassword = binding.edtNewPassword.text.toString()
        val confirmPassword = binding.edtConfirmNewPassword.text.toString()

        when {
            oldPassword.isEmpty() -> {
                binding.tflOldPassword.error = "Please enter your old password."
            }
            newPassword.isEmpty() -> {
                binding.tflNewPassword.error = "Please enter your new password."
            }
            confirmPassword != newPassword -> {
                binding.tflConfirmNewPassword.error = "Password not match."
            }
            else -> form = true
        }
        return form
    }

    private fun setupViewModel() {
        viewModel.state.observe(this@ChangePasswordActivity) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.stateLoading.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.stateLoading.visibility = View.GONE
                    Toast.makeText(
                        this@ChangePasswordActivity,
                        "Change password success ${result.data!!.success.status}",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                is Result.Error -> {
                    try {
                        binding.stateLoading.visibility = View.GONE
                        val errors = result.errorBody?.string()?.let { JSONObject(it).toString() }
                        val gson = Gson()
                        val jsonObject = gson.fromJson(errors, JsonObject::class.java)
                        val errorResponse = gson.fromJson(jsonObject, ErrorResponse::class.java)

                        Toast.makeText(
                            this@ChangePasswordActivity,
                            "${errorResponse.error?.message} ${errorResponse.error?.status}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ChangePasswordActivity,
                            "Token Has Expired",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        }
    }

    private fun getData() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userId = viewModel.getUserID.first()
                auth = viewModel.getAuth.first()
            }
        }
    }
}