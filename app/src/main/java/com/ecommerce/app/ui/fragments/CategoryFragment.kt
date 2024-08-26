package com.ecommerce.app.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.ecommerce.app.R
import com.ecommerce.app.constants.IntentConstants
import com.ecommerce.app.constants.ScreenName
import com.ecommerce.app.data.category.Category
import com.ecommerce.app.databinding.FragmentCategoryBinding
import com.ecommerce.app.ui.activities.HomeActivity
import com.ecommerce.app.ui.adapters.CommonRVAdapter
import com.ecommerce.app.ui.viewmodels.CategoryViewModel
import com.ecommerce.app.utils.CommonSelectItemRVListerner
import com.ecommerce.app.utils.GsonHelper
import com.ecommerce.app.utils.ResourceViewState
import com.ecommerce.app.utils.SaveSharedPreference
import com.ecommerce.app.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryFragment : Fragment(),CommonSelectItemRVListerner {
    private var binding: FragmentCategoryBinding by autoCleared()
    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var adapter: CommonRVAdapter
    private var categoryList = ArrayList<Category>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as HomeActivity).setupToolbar(getString(R.string.fragment_category))
        //setHasOptionsMenu(true)
        setupRecyclerView()
        setupObservers()
        viewModel.getCategories(ScreenName.FRAGMENT_CATEGORY.value)


    }


    private fun launchSubCategoryUI(category: Category) {

        val bundle = Bundle().apply {
            putString(IntentConstants.CATEGORY_ITEM, GsonHelper.toJson(category))
        }
        val subCategoryFragment=SubCategoryFragment()
        subCategoryFragment.arguments=bundle

        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_subcategory, subCategoryFragment)
            .commit()

    }

    private fun setupRecyclerView() {
        adapter = CommonRVAdapter(ScreenName.FRAGMENT_CATEGORY.value,this)
        binding.recyclerView.layoutManager =  GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
    }


    private fun setupObservers() {
        viewModel.response.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                ResourceViewState.Status.SUCCESS -> {
                    setProgressBar(false)
                    if (it.data != null && it.data.status == 1) {
                        if (it.data.data.isNotEmpty()) {
                            SaveSharedPreference.setCategoryValue(requireContext(),it.data)
                            categoryList = it.data.data as ArrayList<Category>
                            adapter.setItems(categoryList)
                        } else {
                            //binding.noResultIV.visibility = View.VISIBLE
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_LONG).show()
                        }
                    } else
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                }
                ResourceViewState.Status.ERROR -> {
                    setProgressBar(false)
                    if (it.message?.contains("401") == true) {
                        //  Toast.makeText(requireContext(), R.string.session_expired, Toast.LENGTH_SHORT).show()
                        //  activity?.let { it1 -> CommonUtility.logoutAppSession(it1) };

                    } else
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                }
                ResourceViewState.Status.LOADING ->{
                    setProgressBar(true)
                }
            }
        })

    }



    private fun setProgressBar(b: Boolean) {
        if (!b) {
            // binding.progressBarShim.shimmerLayout.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            // binding.progressBarShim.shimmerLayout.showShimmer(false)
        } else {
            // binding.progressBarShim.shimmerLayout.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE
            // binding.progressBarShim.shimmerLayout.showShimmer(true)
        }
    }


    override fun onSelectItemRVType(selectedItem: Any, selectedAction: String) {
        selectedItem as Category
        launchSubCategoryUI(selectedItem)
    }


}