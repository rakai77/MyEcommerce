package com.example.myecommerce.ui.detail.bottomsheet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.myecommerce.R
import com.example.myecommerce.data.Result
import com.example.myecommerce.data.response.DataItemProduct
import com.example.myecommerce.data.response.ErrorResponse
import com.example.myecommerce.databinding.FragmentBottomSheetDetailBinding
import com.example.myecommerce.ui.buy.BuyActivity
import com.example.myecommerce.ui.buy.BuyActivity.Companion.PRODUCT_ID
import com.example.myecommerce.ui.profile.ProfileViewModel
import com.example.myecommerce.utils.Utils.currency
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
class BottomSheetDetailFragment(private val dataItemProduct: DataItemProduct) :
    BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<BottomSheetDetailViewModel>()
    private var quantity: Int = 0

    private val pref by viewModels<ProfileViewModel>()
    private lateinit var userID: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetDetailBinding.inflate(inflater, container, false)

        setupView()
        initData()
        countData()
        getUserID()

        return binding.root
    }

    private fun countData() {
        binding.btnIncreaseBottSheet.setOnClickListener {
            viewModel.increaseQuantity(dataItemProduct.stock)
            val sum = binding.tvQuantityBottSheet.text.toString()
            val total = (sum.toInt() * dataItemProduct.harga.toInt())
            (resources.getString(R.string.buy_now) + total.toString().currency()).also { binding.btnBuyNowBottSheet.text = it }
        }
        binding.btnDecreaseBottSheet.setOnClickListener {
            viewModel.decreaseQuantity()
            val sum = binding.tvQuantityBottSheet.text.toString()
            val total = (sum.toInt() * dataItemProduct.harga.toInt())
            (resources.getString(R.string.buy_now) + total.toString().currency()).also { binding.btnBuyNowBottSheet.text = it }
        }
    }

    private fun setupView() {
        with(binding) {
            Glide.with(requireContext())
                .load(dataItemProduct.image)
                .into(ivProductBottomSheet)
            tvProductPriceBottomSheet.text = dataItemProduct.harga.currency()
            tvStockProductBottSht.text = dataItemProduct.stock.toString()
        }
    }

    private fun initData() {
        viewModel.quantity.observe(viewLifecycleOwner) {
            binding.tvQuantityBottSheet.text = it.toString()
            quantity = it
            if (it < dataItemProduct.stock) {
                binding.btnIncreaseBottSheet.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_round_black)
            }
            if (it == dataItemProduct.stock) {
                binding.btnIncreaseBottSheet.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_btn_round_light)
                binding.btnDecreaseBottSheet.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_round_black)
            }
            if (it > 1) {
                binding.btnDecreaseBottSheet.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_round_black)
            }
            if (it == 1) {
                binding.btnIncreaseBottSheet.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_round_black)
                binding.btnDecreaseBottSheet.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_btn_round_light)
            }
        }

        viewModel.setPrice(dataItemProduct.harga.toInt())
        viewModel.price.observe(viewLifecycleOwner) {
            binding.tvProductPriceBottomSheet.text = it.toString().currency()
        }

        binding.btnBuyNowBottSheet.setOnClickListener {
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    updateStock(userID, dataItemProduct.id.toString(), quantity)
                }
            }
        }
    }

    private fun getUserID() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    userID = pref.getUserID.first().toString()
                }
            }
        }
    }

    private fun updateStock(userID: String, productID: String, stock: Int) {
        viewModel.updateStock(userID, productID, stock)
        viewModel.updateStockState.observe(viewLifecycleOwner) { result ->
            when(result) {
                is Result.Loading -> { }
                is Result.Success -> {
                    val intent = Intent(context, BuyActivity::class.java)
                    intent.putExtra(PRODUCT_ID, dataItemProduct.id)
                    startActivity(intent)
                }
                is Result.Error -> {
                    val errors = result.errorBody?.string()?.let { JSONObject(it).toString() }
                    val gson = Gson()
                    val jsonObject = gson.fromJson(errors, JsonObject::class.java)
                    val errorResponse = gson.fromJson(jsonObject, ErrorResponse::class.java)
                    Toast.makeText(requireContext(), "${errorResponse.error?.message} ${errorResponse.error?.status}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}