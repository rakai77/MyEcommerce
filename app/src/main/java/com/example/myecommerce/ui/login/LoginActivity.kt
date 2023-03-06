package com.example.myecommerce.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.myecommerce.MainActivity
import com.example.myecommerce.data.Result
import com.example.myecommerce.data.response.ErrorResponse
import com.example.myecommerce.databinding.ActivityLoginBinding
import com.example.myecommerce.ui.register.RegisterActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupViewModel()
        setupLogin()
    }

    private fun setupLogin() {
        binding.apply {
            btnLogin.setOnClickListener {
                if (setupForm()) {
                    viewModel.login(
                        email = edtEmailLogin.text.toString(),
                        password = edtPasswordLogin.text.toString(),
                        tokenFCM = "1342i409jdiofofjwodfdsoif" //blank token FCM
                    )
                }
            }
            binding.btnSignup.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }

    }

    private fun setupForm() : Boolean{
        var form = false

        val email = binding.edtEmailLogin.text.toString()
        val password = binding.edtPasswordLogin.text.toString()

        when {
            email.isEmpty() -> {
                binding.tflEmailLogin.error = "Please fill your email address."
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.tflEmailLogin.error = "Please enter a valid email address."
            }
            password.isEmpty() -> {
                binding.tflPassword.error = "Please enter your password."
            }
            password.length < 6 -> {
                binding.tflPassword.error = "Password length must be 6 character."
            }
            else -> form = true
        }
        return form
    }

    private fun setupViewModel() {
        viewModel.state.observe(this@LoginActivity) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.stateLoading.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.stateLoading.visibility = View.GONE
                    Toast.makeText(
                        this@LoginActivity,
                        "Login Successfully ${result.data.success.status}",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
                is Result.Error -> {
                    binding.stateLoading.visibility = View.GONE
                    val errors = result.errorBody?.string()?.let { JSONObject(it).toString() }
                    val gson = Gson()
                    val jsonObject = gson.fromJson(errors, JsonObject::class.java)
                    val errorResponse = gson.fromJson(jsonObject, ErrorResponse::class.java)

                    Toast.makeText(
                        this@LoginActivity,
                        "${errorResponse.error?.message} ${errorResponse.error?.status}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}