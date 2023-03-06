package com.example.myecommerce.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myecommerce.R
import com.example.myecommerce.data.Result
import com.example.myecommerce.databinding.FragmentFavoriteBinding
import com.example.myecommerce.ui.adapter.FavoriteListAdapter
import com.example.myecommerce.ui.detail.DetailProductActivity
import com.example.myecommerce.ui.profile.ProfileViewModel
import com.example.myecommerce.utils.SortedBy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<FavoriteViewModel>()
    private val pref by viewModels<ProfileViewModel>()

    private lateinit var adapter: FavoriteListAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoriteBinding.bind(view)

        initRecyclerView()
        setupView()
        setupDataStore()
        setupData(SortedBy.DefaultSort)

    }

    private fun setupView() {
        binding.apply {
            searchBarFavorite.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.onSearch(newText)
                    return false
                }
            })
            fabSortFavorite.setOnClickListener {
                showDialogSorted()
            }

            swipeToRefresh.setOnRefreshListener {
                shimmerProductFavorite.root.startShimmer()
                shimmerProductFavorite.root.visibility = View.VISIBLE
                rvProductFavorite.visibility = View.GONE
                searchBarFavorite.setQuery(null, false)
                searchBarFavorite.clearFocus()
                swipeToRefresh.isRefreshing = false
            }
        }
    }

    private fun initRecyclerView() {
        binding.apply {
            adapter = FavoriteListAdapter(
                onClickItem = {
                    val intent = Intent(requireContext(), DetailProductActivity::class.java)
                    intent.putExtra(DetailProductActivity.PRODUCT_ID, it)
                    startActivity(intent)
                }
            )
            rvProductFavorite.adapter = adapter
            rvProductFavorite.setHasFixedSize(true)
        }
    }

    private fun setupDataStore() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    val id = pref.getUserID.first()
                    viewModel.getFavoriteList("", id)
                }
            }
        }
    }

    private fun setupData(sortedBy: SortedBy) {
        viewModel.state.observe(viewLifecycleOwner) { result ->
            when(result) {
                is Result.Loading -> {
                    binding.apply {
                        shimmerProductFavorite.root.startShimmer()
                        shimmerProductFavorite.root.visibility = View.VISIBLE
                        rvProductFavorite.visibility = View.GONE
                        emptyStateFavorite.root.visibility = View.GONE
                        fabSortFavorite.visibility = View.GONE
                    }
                }
                is Result.Success -> {
                    if (result.data.success.data.isNotEmpty()) {
                        binding.apply {
                            shimmerProductFavorite.root.visibility = View.GONE
                            rvProductFavorite.visibility = View.VISIBLE
                            emptyStateFavorite.root.visibility = View.GONE
                            fabSortFavorite.visibility = View.VISIBLE
                        }

                        when(sortedBy) {
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
                            shimmerProductFavorite.root.stopShimmer()
                            shimmerProductFavorite.root.visibility = View.GONE
                            rvProductFavorite.visibility = View.GONE
                            emptyStateFavorite.root.visibility = View.VISIBLE
                            fabSortFavorite.visibility = View.GONE
                        }
                    }
                }
                is Result.Error -> {
                    binding.apply {
                        shimmerProductFavorite.root.stopShimmer()
                        shimmerProductFavorite.root.visibility = View.GONE
                        rvProductFavorite.visibility = View.GONE
                        emptyStateFavorite.root.visibility = View.GONE
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