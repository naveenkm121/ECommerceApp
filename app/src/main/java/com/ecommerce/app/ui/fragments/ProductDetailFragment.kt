package com.ecommerce.app.ui.fragments

import android.graphics.Paint
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ecommerce.app.R
import com.ecommerce.app.constants.IntentConstants
import com.ecommerce.app.constants.RequestApiType
import com.ecommerce.app.constants.ScreenName
import com.ecommerce.app.constants.SharedPrefs
import com.ecommerce.app.data.address.AddressReq
import com.ecommerce.app.data.cart.CartReq
import com.ecommerce.app.data.product.ProductImage
import com.ecommerce.app.data.product.ProductItem
import com.ecommerce.app.databinding.FragmentProductDetailBinding
import com.ecommerce.app.ui.activities.HomeActivity
import com.ecommerce.app.ui.adapters.ProductImageViewPagerAdapter
import com.ecommerce.app.ui.viewmodels.CartViewModel
import com.ecommerce.app.ui.viewmodels.ProductDetailViewModel
import com.ecommerce.app.utils.CommonUtility
import com.ecommerce.app.utils.DebugHandler
import com.ecommerce.app.utils.GsonHelper
import com.ecommerce.app.utils.ResourceViewState
import com.ecommerce.app.utils.SaveSharedPreference
import com.ecommerce.app.utils.autoCleared
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint

class ProductDetailFragment : Fragment() {
    private val viewModel: ProductDetailViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()
    private var binding:FragmentProductDetailBinding by autoCleared()
    private lateinit var adapter: ProductImageViewPagerAdapter
    private lateinit var productDetailPassObj: ProductItem
    private lateinit var toolbar: Toolbar
    private var prodId:Int=0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentProductDetailBinding.inflate(inflater,container,false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as HomeActivity).setupToolbar(getString(R.string.fragment_product_details))
         prodId= arguments?.getInt(IntentConstants.PRODUCT_ID)!!
        val prodBrand= arguments?.getString(IntentConstants.PRODUCT_BRAND).toString()
        viewModel.getProductDetailsById(prodId!!)
        setupToolbar(prodBrand)
        setupRecyclerView()
        setOnClickListener()
        setupObservers()

    }


    private fun setupRecyclerView() {
        adapter = ProductImageViewPagerAdapter()
        binding.imageViewPager.adapter = adapter
        TabLayoutMediator(binding.circularTabLayout, binding.imageViewPager) { tab, position ->
        }.attach()
    }

    private  fun setupDataToUI(productItem: ProductItem){
        binding.productNameTV.text=productItem.title
        binding.discountPriceTV.text=getString(R.string.input_rs_symbol,productItem.price.toString())
        binding.priceTV.text=getString(R.string.mrp_rs_input,productItem.price.toString())
        binding.discountTV.text="(${productItem.discountPercentage}% OFF)"
        binding.priceTV.setPaintFlags(binding.priceTV.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

        //binding.productDetailTV.text=productItem.description

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadDataWithBaseURL(null, productItem.description, "text/html", "UTF-8", null)
    }

    private fun setupToolbar(title:String) {
        toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(title)
        // toolbar.inflateMenu(R.menu.main)

        // Handle menu item clicks if needed


        toolbar.setOnMenuItemClickListener {
            DebugHandler.log("hello  Cart 11 "+it.itemId)
            when (it.itemId) {

                R.id.action_cart -> {
                    // Handle the action
                    DebugHandler.log("hello  Cart 1")
                    launchCartScreen()
                    true
                }
                else -> false
            }
        }

    }

    private fun setOnClickListener() {

        binding.addToCartBTN.setOnClickListener {
            //launchCartScreen()
            DebugHandler.log("Hello Add to Cart")
            var cartReq: CartReq = CartReq()
            cartReq.prodId= prodId.toLong()
            cartReq.quantity=1
            cartReq.userId= SaveSharedPreference.getUserDetails(requireContext())!!.id.toLong()
            cartViewModel.addToCartItems(RequestApiType.REQUEST_ADD_TO_CART.value,cartReq)

        }

        binding.buyBTN.setOnClickListener {
            launchCartScreen()
        }
    }
    private fun launchCartScreen() {
        findNavController().navigate(R.id.action_productDetailFragment_to_cartFragment)

    }


    private fun setupObservers(){

        viewModel.response.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                ResourceViewState.Status.SUCCESS -> {
                    //setProgressBar(false)
                    if (it.data != null && it.data.status == 1) {
                       // if (it.data.isNotEmpty()) {
                            productDetailPassObj=it.data.data
                        var productImageList = it.data.data.images as ArrayList<ProductImage>
                        if(productImageList.isEmpty())productImageList.add(ProductImage(1,1,productDetailPassObj.thumbnail))
                        adapter.setItem(productDetailPassObj.images)
                            setupDataToUI(productDetailPassObj)
//                        } else {
//                            //binding.noResultIV.visibility = View.VISIBLE
//                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_LONG).show()
//                        }
                    } else
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                }
                ResourceViewState.Status.ERROR -> {
                   // setProgressBar(false)
                    if (it.message?.contains("401") == true) {
                          Toast.makeText(requireContext(), R.string.session_expired, Toast.LENGTH_SHORT).show()
                         activity?.let { it1 -> CommonUtility.logoutAppSession(it1) };

                    } else
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                }
                ResourceViewState.Status.LOADING ->{
                  //  setProgressBar(true)
                }
            }
        })

        cartViewModel.responseAddToCart.observe(viewLifecycleOwner, Observer {
            when (it.status) {

                ResourceViewState.Status.SUCCESS -> {
                    if (it.data != null && it.data.status == 1) {
                        var itemType:Int=it.data.data.cartItems.size
                        ((requireActivity() as? HomeActivity)?.setupBadge(itemType))
                    }

                }

                ResourceViewState.Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                ResourceViewState.Status.LOADING -> {}
            }
        })

    }


}

