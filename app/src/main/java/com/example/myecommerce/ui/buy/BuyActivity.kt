package com.example.myecommerce.ui.buy

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myecommerce.MainActivity
import com.example.myecommerce.data.Result
import com.example.myecommerce.data.model.UpdateRate
import com.example.myecommerce.data.response.ErrorResponse
import com.example.myecommerce.databinding.ActivityBuyBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
class BuyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuyBinding

    private val viewModel by viewModels<BuyViewModel>()

    private var productID: Int = 0
    private var listProductId: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productID = intent.getIntExtra(PRODUCT_ID, 0)
        listProductId = intent.getStringArrayListExtra(LIST_PRODUCT_ID)

        setupView()
    }

    private fun setupView() {
        binding.apply {
            btnSubmit.setOnClickListener {
                setupViewModel()
            }
        }
    }

    private fun setupViewModel() {
        val rate = binding.ratingBuy.rating
        if (productID != 0) {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.updateRate(productID, UpdateRate(rate.toString()))
                    viewModel.state.observe(this@BuyActivity) { result ->
                        when (result) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                startActivity(Intent(this@BuyActivity, MainActivity::class.java))
                                finishAffinity()
                            }
                            is Result.Error -> {
                                val errors =
                                    result.errorBody?.string()?.let { JSONObject(it).toString() }
                                val gson = Gson()
                                val jsonObject = gson.fromJson(errors, JsonObject::class.java)
                                val errorResponse =
                                    gson.fromJson(jsonObject, ErrorResponse::class.java)

                                Toast.makeText(
                                    this@BuyActivity,
                                    "${errorResponse.error?.message} ${errorResponse.error?.status}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        } else {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    for (i in listProductId!!.indices) {
                        viewModel.updateRate(listProductId!![i].toInt(), UpdateRate(rate.toString()))
                        viewModel.deleteProductByIdFromTrolley(listProductId!![i].toInt())
                        viewModel.state.observe(this@BuyActivity) { result ->
                            when (result) {
                                is Result.Loading -> {}
                                is Result.Success -> {
                                    finishAffinity()
                                }
                                is Result.Error -> {
                                    val errors = result.errorBody?.string()?.let { JSONObject(it).toString() }
                                    val gson = Gson()
                                    val jsonObject = gson.fromJson(errors, JsonObject::class.java)
                                    val errorResponse =
                                        gson.fromJson(jsonObject, ErrorResponse::class.java)

                                    Toast.makeText(
                                        this@BuyActivity,
                                        "${errorResponse.error?.message} ${errorResponse.error?.status}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        }
        startActivity(Intent(this@BuyActivity, MainActivity::class.java))
        finishAffinity()
    }

    companion object {
        const val PRODUCT_ID = "product_id"
        const val LIST_PRODUCT_ID = "list_product_id"
    }
}