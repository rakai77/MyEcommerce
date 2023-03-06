package com.example.myecommerce.ui.trolley

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myecommerce.data.model.DataStockItem
import com.example.myecommerce.databinding.ActivityTrolleyBinding
import com.example.myecommerce.ui.adapter.TrolleyListAdapter
import com.example.myecommerce.utils.Utils.currency
import com.example.myecommerce.data.Result
import com.example.myecommerce.ui.buy.BuyActivity
import com.example.myecommerce.ui.buy.BuyActivity.Companion.LIST_PRODUCT_ID
import com.example.myecommerce.ui.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TrolleyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrolleyBinding

    private val viewModel by viewModels<TrolleyViewModel>()

    private var trolleyAdapter: TrolleyListAdapter? = null

    private val pref by viewModels<ProfileViewModel>()

    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrolleyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarTrolley)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupViewModel()
        setupRecyclerView()
        setupView()
    }

    private fun setupViewModel() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getAllProduct().collect { result ->
                    var totalPrice = 0
                    val filterResult = result.filter { it.isChecked }

                    for (i in filterResult.indices) {
                        totalPrice = totalPrice.plus(filterResult[i.toString().toInt()].totalPrice!!)
                    }
                    binding.tvProductPriceTrolley.text = totalPrice.toString().currency()
                    binding.cbTrolley.isChecked = result.size == filterResult.size

                    if (result.isNotEmpty()) {
                        binding.cbTrolley.visibility = View.VISIBLE
                        binding.rvTrolley.visibility = View.VISIBLE
                        trolleyAdapter?.submitList(result)
                    } else {
                        binding.rvTrolley.visibility = View.GONE
                        binding.cbTrolley.visibility = View.GONE
                        binding.bottomAppBarTrolley.visibility = View.GONE
                        binding.emptyState.root.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun initData() {
        val dataStockItems = arrayListOf<DataStockItem>()
        val listOfProductId = arrayListOf<String>()
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                getUserID()
                viewModel.getAllProductIsChecked().collect { result ->
                    for (i in result.indices) {
                        dataStockItems.add(DataStockItem(result[i].id.toString(), result[i].quantity!!))
                        listOfProductId.add(result[i].id.toString())
                        initBuyProduct(userID, dataStockItems, listOfProductId)
                    }
                }
            }
        }
    }

    private fun setupView() {
        binding.btnBuyTrolley.setOnClickListener {
            initData()
        }

        binding.cbTrolley.setOnClickListener {
            if (binding.cbTrolley.isChecked) {
                viewModel.updateProductIsCheckedAll(true)
            } else {
                viewModel.updateProductIsCheckedAll(false)
            }
        }
    }

    private fun initBuyProduct(userID: String, data: List<DataStockItem>, productId: ArrayList<String>) {
        viewModel.updateStock(userID, data)
        viewModel.updateStockState.observe(this@TrolleyActivity) { result ->
            when(result) {
                is Result.Loading -> {}
                is Result.Success -> {
                    val intent = Intent(this@TrolleyActivity, BuyActivity::class.java)
                    intent.putExtra(LIST_PRODUCT_ID, productId)
                    startActivity(intent)
                    Toast.makeText(this@TrolleyActivity, result.data.success.message, Toast.LENGTH_SHORT).show()
                    finishAffinity()
                }
                is Result.Error -> {}
            }
        }
    }

    private fun getUserID() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userID = pref.getUserID.first().toString()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.apply {
            trolleyAdapter = TrolleyListAdapter(
                onAddItem = {
                    val quantity = it.quantity?.plus(1)
                    val price =  quantity?.times(it.price?.toInt()!!)
                    val productId = it.id
                    viewModel.updateProductData(
                        quantity,
                        price,
                        productId
                    )
                },
                onDeleteItem = { viewModel.deleteProductByIdFromTrolley(it.id) },
                onMinItem = {
                    val quantity = it.quantity?.minus(1)
                    val price =  quantity?.times(it.price?.toInt()!!)
                    val productId = it.id
                    viewModel.updateProductData(
                        quantity,
                        price,
                        productId
                    )
                },
                onCheckedItem = {
                    val isChecked = !it.isChecked
                    val productId = it.id
                    viewModel.updateProductIsCheckedById(isChecked, productId)
                }
            )
            rvTrolley.adapter = trolleyAdapter
            rvTrolley.layoutManager = LinearLayoutManager(this@TrolleyActivity)
            rvTrolley.setHasFixedSize(true)
        }
    }
}