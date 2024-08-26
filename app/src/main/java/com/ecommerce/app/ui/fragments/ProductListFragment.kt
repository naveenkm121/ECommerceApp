package com.ecommerce.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ecommerce.app.R
import com.ecommerce.app.constants.AppConstants
import com.ecommerce.app.constants.Constants
import com.ecommerce.app.constants.IntentConstants
import com.ecommerce.app.data.filter.FilterRes
import com.ecommerce.app.data.product.ProductItem
import com.ecommerce.app.data.product.ProductReqParam
import com.ecommerce.app.databinding.BottomSheetSortDialogBinding
import com.ecommerce.app.databinding.FragmentProductlistBinding
import com.ecommerce.app.ui.activities.HomeActivity
import com.ecommerce.app.ui.adapters.ProductPageAdapter
import com.ecommerce.app.ui.viewmodels.ProductListViewModel
import com.ecommerce.app.utils.DebugHandler
import com.ecommerce.app.utils.GsonHelper
import com.ecommerce.app.utils.autoCleared
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductListFragment : Fragment(), ProductPageAdapter.CardItemListener {

    private val viewModel: ProductListViewModel by viewModels()
    private var binding: FragmentProductlistBinding by autoCleared()
    private var bottomSheetSortDialogBinding: BottomSheetSortDialogBinding by autoCleared()
    private lateinit var bottomSheetSortDialog: BottomSheetDialog
    private lateinit var adapter: ProductPageAdapter
    private lateinit var productReqParam: ProductReqParam



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProductlistBinding.inflate(inflater, container, false)
        //val toolbar: Toolbar = root.findViewById(R.id.toolbar)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // filterRes= GsonHelper.fromJson(arguments?.getString(IntentConstants.PRODUCT_FILTER_DATA)!!,FilterRes::class.java)!!
        (requireActivity() as HomeActivity).setupToolbar(getString(R.string.fragment_products))
        setOnClickListener()
        setupRecyclerView()
        setupObservers()
        //  sortDialogUIHandling()

        productReqParam =
            ProductReqParam(0, Constants.DEFAULT_SORT_BY, Constants.DEFAULT_SORT_DIRECTION)
        viewModel.getProducts(productReqParam)
        // productListViewModel.getProducts(null)
    }

    private fun setupRecyclerView() {
        adapter = ProductPageAdapter(requireContext(), this)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
    }

    private fun setOnClickListener() {
        binding.sorLL.setOnClickListener {
            showSortDialog()
        }

        binding.filterLL.setOnClickListener {
            findNavController().navigate(
                R.id.action_productListFragment_to_productFilterFragment
            )
           /* findNavController().navigate(
                R.id.action_productListFragment_to_productFilterFragment,
                bundleOf(IntentConstants.PRODUCT_FILTER_DATA to GsonHelper.toJson(filterRes))
            )*/
        }
    }

    private fun showSortDialog() {
        bottomSheetSortDialog = BottomSheetDialog(requireActivity())
        //  val view = layoutInflater.inflate(R.layout.bottom_sheet_sort_dialog, null)
        bottomSheetSortDialogBinding = BottomSheetSortDialogBinding.inflate(layoutInflater)
        bottomSheetSortDialog.setContentView(bottomSheetSortDialogBinding.root)
        bottomSheetSortDialog.setCancelable(true)
        bottomSheetSortDialog.show()


        when (productReqParam.sortBy) {
            AppConstants.SORT_BY_DISCOUNT -> {
                // bottomSheetSortDialogBinding.discountTV.textColors=requireContext().getColor(R.color.red)
                bottomSheetSortDialogBinding.discountIcon.setColorFilter(requireContext().getColor(R.color.select_color))
                bottomSheetSortDialogBinding.discountTV.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.select_color
                    )
                )
            }

            AppConstants.SORT_BY_PRICE -> {
                // bottomSheetSortDialogBinding.discountTV.textColors=requireContext().getColor(R.color.red)
                when (productReqParam.sortDir) {
                    AppConstants.SORT_DESCENDING -> {
                        bottomSheetSortDialogBinding.priceHighToLowIcon.setColorFilter(
                            requireContext().getColor(R.color.select_color)
                        )
                        bottomSheetSortDialogBinding.priceHighToLowTV.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.select_color
                            )
                        )
                    }

                    else -> {
                        bottomSheetSortDialogBinding.priceLowToHighIcon.setColorFilter(
                            requireContext().getColor(R.color.select_color)
                        )
                        bottomSheetSortDialogBinding.priceLowToHighTV.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.select_color
                            )
                        )
                    }
                }

            }
        }

        bottomSheetSortDialogBinding.popularityLL.setOnClickListener {
            DebugHandler.log("Hello popularityLL")
        }

        bottomSheetSortDialogBinding.discountLL.setOnClickListener {
            productReqParam.pageNo = 0
            productReqParam.sortBy = AppConstants.SORT_BY_DISCOUNT
            productReqParam.sortDir = AppConstants.SORT_DESCENDING
            viewModel.getProducts(productReqParam)
            bottomSheetSortDialog.dismiss()
        }
        bottomSheetSortDialogBinding.priceHighToLowLL.setOnClickListener {
            productReqParam.pageNo = 0
            productReqParam.sortBy = AppConstants.SORT_BY_PRICE
            productReqParam.sortDir = AppConstants.SORT_DESCENDING
            viewModel.getProducts(productReqParam)
            bottomSheetSortDialog.dismiss()


        }
        bottomSheetSortDialogBinding.priceLowToHighLL.setOnClickListener {
            productReqParam.pageNo = 0
            productReqParam.sortBy = AppConstants.SORT_BY_PRICE
            productReqParam.sortDir = AppConstants.SORT_ASCENDING
            viewModel.getProducts(productReqParam)
            bottomSheetSortDialog.dismiss()
        }
        bottomSheetSortDialogBinding.ratingLL.setOnClickListener {
            DebugHandler.log("Hello ratingLL")
        }
    }


    private fun setupObservers() {
        setProgressBar(true)

        viewModel.response.observe(viewLifecycleOwner, Observer {

            setProgressBar(false)
            adapter.submitData(lifecycle, it)
        })

    }


    override fun onClickedCard(selectedProduct: ProductItem) {

        launchProductDetailScreen(selectedProduct)
    }

    private fun launchProductDetailScreen(selectedItem: ProductItem){
        findNavController().navigate(
            R.id.action_productListFragment_to_productDetailFragment,
            bundleOf(
                IntentConstants.PRODUCT_ID to selectedItem.id,
                IntentConstants.PRODUCT_BRAND to selectedItem.brand
            )
        )
    }
    private fun setProgressBar(b: Boolean) {
        if (!b) {
            binding.progressBar.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //binding = null
    }
}