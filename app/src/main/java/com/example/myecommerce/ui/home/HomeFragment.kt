package com.example.myecommerce.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myecommerce.R
import com.example.myecommerce.data.Result
import com.example.myecommerce.databinding.FragmentHomeBinding
import com.example.myecommerce.ui.adapter.ProductListAdapter
import com.example.myecommerce.ui.detail.DetailProductActivity
import com.example.myecommerce.ui.detail.DetailProductActivity.Companion.PRODUCT_ID
import com.example.myecommerce.utils.SortedBy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProductListAdapter
    private val viewModel by viewModels<HomeViewModel>()

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        initRecyclerView()
        setupView()
        setupData(SortedBy.DefaultSort)
    }

    private fun initRecyclerView() {
        binding.apply {
            adapter = ProductListAdapter(
                onClickItem = {
                    val intent = Intent(requireContext(), DetailProductActivity::class.java)
                    intent.putExtra(PRODUCT_ID, it)
                    startActivity(intent)
                }
            )
            rvProduct.adapter = adapter
            rvProduct.setHasFixedSize(true)
        }
    }

    private fun setupView() {
        binding.apply {
            searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    job?.cancel()
                    job = coroutineScope.launch {
                        newText?.let {
                            delay(2000)
                            if (newText.isEmpty() || newText.toString() == "") {
                                setupViewModel(null)
                            } else {
                                setupViewModel(newText)
                            }
                        }
                    }
                    return false
                }
            })
            fabSort.setOnClickListener {
                showDialogSorted()
            }

            swipeToRefresh.setOnRefreshListener {
                shimmerProduct.root.startShimmer()
                shimmerProduct.root.visibility = View.VISIBLE
                rvProduct.visibility = View.GONE
                searchBar.setQuery(null, false)
                searchBar.clearFocus()
                swipeToRefresh.isRefreshing = false
            }
        }
    }

    private fun setupViewModel(query: String?) {
        viewModel.getListProduct(query)
    }

    private fun setupData(sortedBy: SortedBy) {
        viewModel.state.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.apply {
                        shimmerProduct.root.startShimmer()
                        shimmerProduct.root.visibility = View.VISIBLE
                        rvProduct.visibility = View.GONE
                        emptyState.root.visibility = View.GONE
                        fabSort.visibility = View.GONE
                    }
                }
                is Result.Success -> {
                    if (result.data.success.data.isNotEmpty()) {
                        binding.apply {
                            shimmerProduct.root.stopShimmer()
                            shimmerProduct.root.visibility = View.GONE
                            rvProduct.visibility = View.VISIBLE
                            emptyState.root.visibility = View.GONE
                            fabSort.visibility = View.VISIBLE
                        }

                        when (sortedBy) {
                            SortedBy.SortAtoZ -> {
                                result.data.success.data.let {
                                    adapter.submitList(result.data.success.data.sortedBy { it.nameProduct })
                                }

                            }
                            SortedBy.SortZtoA -> {
                                result.data.success.data.let {
                                    adapter.submitList(result.data.success.data.sortedByDescending { it.nameProduct })
                                }
                            }
                            SortedBy.DefaultSort -> {
                                result.data.success.data.let {
                                    adapter.submitList(result.data.success.data)
                                }
                            }
                        }
                    } else {
                        binding.apply {
                            shimmerProduct.root.stopShimmer()
                            shimmerProduct.root.visibility = View.GONE
                            rvProduct.visibility = View.GONE
                            emptyState.root.visibility = View.VISIBLE
                            fabSort.visibility = View.GONE
                        }
                    }
                }
                is Result.Error -> {
                    binding.apply {
                        shimmerProduct.root.stopShimmer()
                        shimmerProduct.root.visibility = View.GONE
                        rvProduct.visibility = View.GONE
                        emptyState.root.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun showDialogSorted() {
        val items = arrayOf("From A to Z", "From Z to A")
        var selectedItem = ""
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Sort By")
            .setSingleChoiceItems(items, -1) { _, position ->
                selectedItem = items[position]
            }
            .setPositiveButton("OK") { _, _ ->
                when (selectedItem) {
                    "From A to Z" -> setupData(SortedBy.SortAtoZ)
                    "From Z to A" -> setupData(SortedBy.SortZtoA)
                }
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}