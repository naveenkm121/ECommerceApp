package com.ecommerce.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecommerce.app.R
import com.ecommerce.app.constants.IntentConstants
import com.ecommerce.app.data.filter.Filter
import com.ecommerce.app.data.filter.FilterItem
import com.ecommerce.app.data.filter.FilterRes
import com.ecommerce.app.databinding.FragmentProductFilterBinding
import com.ecommerce.app.ui.activities.HomeActivity
import com.ecommerce.app.ui.adapters.FilterItemsAdapter
import com.ecommerce.app.ui.adapters.FilterTypeAdapter
import com.ecommerce.app.utils.CommonUtility
import com.ecommerce.app.utils.DebugHandler
import com.ecommerce.app.utils.GsonHelper
import com.ecommerce.app.utils.autoCleared


class ProductFilterFragment : Fragment(), FilterTypeAdapter.SelectFilterTypeListener,
    FilterItemsAdapter.SelectFilterItemListener {

    private var binding: FragmentProductFilterBinding by autoCleared()
    private lateinit var filterTypeAdapter: FilterTypeAdapter
    private lateinit var filterItemsAdapter: FilterItemsAdapter
    private lateinit var filterRes: FilterRes
    private var selectedFilterTypeIndex = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductFilterBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as HomeActivity).setupToolbar(getString(R.string.fragment_filter))
        //setupToolbar()
        setupFilterData()
        setupRecyclerView()

    }

    private fun setupToolbar() {
        val toolbar: Toolbar = requireActivity().findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setTitle(getString(R.string.filter))

    }


    private fun setupRecyclerView() {
        filterTypeAdapter = FilterTypeAdapter(this)
        filterTypeAdapter.setItem(filterRes.filters)
        binding.filterTypeRV.layoutManager = LinearLayoutManager(requireContext())
        binding.filterTypeRV.adapter = filterTypeAdapter


        filterItemsAdapter = FilterItemsAdapter(this)
        filterItemsAdapter.setItem(filterRes.filters.get(0).filterItems)
        binding.filterItemsRV.layoutManager = LinearLayoutManager(requireContext())
        binding.filterItemsRV.adapter = filterItemsAdapter

    }

    private fun setupFilterData() {

        filterRes=
            GsonHelper.fromJson(arguments?.getString(IntentConstants.PRODUCT_FILTER_DATA)!!,FilterRes::class.java)!!
        if(filterRes==null) {
            val filterJsonString =
                CommonUtility.getJsonDataFromAsset(requireContext(), "filter.json")
            filterRes = GsonHelper.fromJson(filterJsonString, FilterRes::class.java)!!
        }
        DebugHandler.log("FilterData ==" + filterRes)
        filterRes.filters.get(0).isSelected = true


    }

    private fun setOnClickListener() {
        binding.applyBTN.setOnClickListener {
            findNavController().navigate(
                R.id.action_productFilterFragment_to_productListFragment,
                bundleOf(
                    IntentConstants.PRODUCT_FILTER_DATA to GsonHelper.toJson(filterRes)
                )
            )
        }

        binding.cancelBTN.setOnClickListener {

        }
    }

    override fun onSelectFilterType(selectedFilter: Filter) {

        selectedFilterTypeIndex = filterRes.filters.indexOf(selectedFilter)
        if (selectedFilterTypeIndex != -1) {
            filterRes.filters.forEach { filter ->
                filter.isSelected = false
            }

            filterRes.filters.get(selectedFilterTypeIndex).isSelected = true
            filterTypeAdapter.notifyDataSetChanged()

            filterItemsAdapter.setItem(filterRes.filters.get(selectedFilterTypeIndex).filterItems)
            filterTypeAdapter.notifyDataSetChanged()
        }
    }

    override fun onSelectFilterType(selectedFilterItem: FilterItem) {

        var selectedFilterItemIndex =
            filterRes.filters.get(selectedFilterTypeIndex).filterItems.indexOf(selectedFilterItem)
        filterRes.filters.get(selectedFilterTypeIndex).filterItems.get(selectedFilterItemIndex).isSelected =
            true

        filterItemsAdapter.setItem(filterRes.filters.get(selectedFilterTypeIndex).filterItems)
        filterTypeAdapter.notifyDataSetChanged()
    }


}