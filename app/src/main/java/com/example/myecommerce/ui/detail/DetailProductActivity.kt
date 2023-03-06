package com.example.myecommerce.ui.detail

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myecommerce.R
import com.example.myecommerce.data.Result
import com.example.myecommerce.data.local.room.ProductEntity
import com.example.myecommerce.data.response.DataItemProduct
import com.example.myecommerce.databinding.ActivityDetailProductBinding
import com.example.myecommerce.ui.detail.bottomsheet.BottomSheetDetailFragment
import com.example.myecommerce.ui.profile.ProfileViewModel
import com.example.myecommerce.utils.ImageViewPagerAdapter
import com.example.myecommerce.utils.Utils.currency
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProductBinding

    private val viewModel by viewModels<DetailProductViewModel>()
    private val pref by viewModels<ProfileViewModel>()

    private var productID: Int = 0
    private var userID: Int = 0

    private lateinit var dataProduct: DataItemProduct
    private lateinit var productImgUrl: String

    private lateinit var imageSliderAdapter: ImageViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idProduct = intent.getIntExtra(PRODUCT_ID, 0)
        productID = idProduct
        if (productID == 0) {
            val data: Uri? = intent?.data
            val id = data?.getQueryParameter("id")
            if (id != null) {
                productID = id.toInt()
            }
        }

        setSupportActionBar(binding.customToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupData()
        setupWhenRefresh()
        setupToolbarMenu()
    }

    private fun setupWhenRefresh() {
        binding.swipeToRefresh.setOnRefreshListener {
            binding.swipeToRefresh.isRefreshing = false
            viewModel.onRefresh(productID, userID)
        }
    }

    private fun setupData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    val id = pref.getUserID.first()
                    userID = id
                }
                launch {
                    setupViewModel()
                }
            }
        }
    }

    private fun setupViewModel() {
        viewModel.getDetailProduct(productID, userID)
        viewModel.detail.observe(this@DetailProductActivity) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.apply {
                        shimmerDetailProduct.root.startShimmer()
                        shimmerDetailProduct.root.visibility = View.VISIBLE
                        scrollView.visibility = View.GONE
                        bottomAppBarDetail.visibility = View.GONE
                    }
                }
                is Result.Success -> {
                    binding.apply {
                        swipeToRefresh.isRefreshing = false
                        shimmerDetailProduct.root.stopShimmer()
                        shimmerDetailProduct.root.visibility = View.GONE
                        scrollView.visibility = View.VISIBLE
                        bottomAppBarDetail.visibility = View.VISIBLE
                        result.data.success.data.let { dataProduct = it }
                        result.data.success.data.let { setupView(it) }
                        result.data.success.data.let { setupAction(it) }
                    }
                }
                is Result.Error -> {
                    binding.apply {
                        swipeToRefresh.isRefreshing = false
                        shimmerDetailProduct.root.stopShimmer()
                        shimmerDetailProduct.root.visibility = View.GONE
                        scrollView.visibility = View.GONE
                        bottomAppBarDetail.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupView(data: DataItemProduct) {
        with(binding) {
            customToolbar.title = data.nameProduct
            imageSliderAdapter = ImageViewPagerAdapter(data.imageProduct)
            ivpSliderProduct.adapter = imageSliderAdapter
            dotsIndicator.attachTo(ivpSliderProduct)
            tvProductName.isSelected = true
            tvProductName.text = data.nameProduct
            tvProductPrize.text = data.harga.currency()
            tvProductRating.rating = data.rate.toString().toFloat()
            tvProductSize.text = data.size
            tvProductWeight.text = data.weight
            tvProductType.text = data.type
            tvProductDesc.text = data.desc
            productImgUrl = data.image

            if (data.stock <= 1) {
                tvProductStock.text = resources.getString(R.string.out_of_stock)
                btnBuy.isEnabled = false
                btnTrolley.isEnabled = false
            } else {
                tvProductStock.text = data.stock.toString()
            }

            if (data.isFavorite) btnToggleFavorite.setImageResource(R.drawable.ic_favorite)
            else btnToggleFavorite.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    private fun setupAction(data: DataItemProduct) {
        binding.btnBuy.setOnClickListener {
            val bottomSheet = BottomSheetDetailFragment(data)
            bottomSheet.show(supportFragmentManager, DetailProductActivity::class.java.simpleName)
        }

        binding.btnToggleFavorite.setOnClickListener {
            if (data.isFavorite) {
                viewModel.removeFavoriteProduct(productID, userID)
                removeFavorite()
            } else {
                viewModel.addFavoriteProduct(productID, userID)
                addFavorite()
            }
        }
        binding.btnTrolley.setOnClickListener {
            if (data.stock > 1) {
                viewModel.insertTrolley(
                    ProductEntity(
                        id = data.id,
                        nameProduct = data.nameProduct,
                        image = data.image,
                        price = data.harga,
                        quantity = 1,
                        stock = data.stock,
                        totalPrice = data.harga.toInt(),
                        isChecked = false
                    )
                )
                finish()
                Toast.makeText(this@DetailProductActivity, "Add to trolley.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@DetailProductActivity, "Failed add to trolley.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addFavorite() {
        viewModel.addFavorite.observe(this@DetailProductActivity) { result ->
            when(result) {
                is Result.Loading -> {}
                is Result.Success -> {
                    binding.btnToggleFavorite.setImageResource(R.drawable.ic_favorite)
                    Toast.makeText(this, "Product has successfully add to favorite.", Toast.LENGTH_SHORT).show()
                }
                is Result.Error -> {
                    Toast.makeText(this@DetailProductActivity, result.errorBody.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun removeFavorite() {
        viewModel.removeFavorite.observe(this@DetailProductActivity) { result ->
            when(result) {
                is Result.Loading -> {}
                is Result.Success -> {
                    binding.btnToggleFavorite.setImageResource(R.drawable.ic_favorite_border)
                    Toast.makeText(this, "Product has been remove.", Toast.LENGTH_SHORT).show()
                }
                is Result.Error -> {
                    Toast.makeText(this@DetailProductActivity, result.errorBody.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun setupToolbarMenu() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.detail_share_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        finish()
                    }
                    R.id.menu_share -> {
                        shareDeepLink()
                    }
                }
                return true
            }
        })
    }

    private fun shareDeepLink(){
        Picasso.get().load(productImgUrl).into(object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/*"
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Name : ${dataProduct.nameProduct}\nStock : ${dataProduct.stock}\nWeight : ${dataProduct.weight}\nSize : ${dataProduct.size}\nLink : https://myecommerce.com/deeplink?id=${productID}"
                )

                val path = MediaStore.Images.Media.insertImage(
                    contentResolver,
                    bitmap,
                    "image desc",
                    null
                )

                val uri = Uri.parse(path)

                intent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(Intent.createChooser(intent, "Share To"))
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Log.v("IMG Downloader", "Bitmap Failed...");
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                Log.v("IMG Downloader", "Bitmap Preparing Load...");
            }
        })
    }

    companion object {
        const val PRODUCT_ID = "product_id"
    }
}