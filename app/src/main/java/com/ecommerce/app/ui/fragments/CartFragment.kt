package com.ecommerce.app.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecommerce.app.R
import com.ecommerce.app.constants.ScreenName
import com.ecommerce.app.data.address.AddressItem
import com.ecommerce.app.data.cart.CartData
import com.ecommerce.app.data.cart.CartItem
import com.ecommerce.app.data.wishlist.WishlistItem
import com.ecommerce.app.databinding.FragmentCartBinding
import com.ecommerce.app.ui.adapters.CommonRVAdapter
import com.ecommerce.app.ui.viewmodels.CartViewModel
import com.ecommerce.app.utils.AlertDialogListener
import com.ecommerce.app.utils.CommonSelectItemRVListerner
import com.ecommerce.app.utils.DebugHandler
import com.ecommerce.app.utils.ResourceViewState
import com.ecommerce.app.utils.SaveSharedPreference
import com.ecommerce.app.utils.UICommon
import com.ecommerce.app.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment(), CommonSelectItemRVListerner {
    private var binding: FragmentCartBinding by autoCleared()
    private val viewModel: CartViewModel by viewModels()
    private lateinit var adapter: CommonRVAdapter
    private var cartItemList = ArrayList<CartItem>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupRecyclerView()
        viewModel.getCartItems(ScreenName.REQUEST_CART_LIST.value, "")
    }

    private fun setDataOnViews(cartData: CartData) {
        binding.priceHeadingTV.setText(getString(R.string.price_details) + " ( ${cartData.totalProducts} Items)")
        binding.mrpValTV.setText(
            getString(
                R.string.input_rs_symbol,
                cartData.totalPrice.toString()
            )
        )
        binding.discountValTV.setText(
            getString(
                R.string.input_rs_symbol,
                cartData.totalDiscountPrice.toString()
            )
        )
        binding.totalValTV.setText(
            getString(
                R.string.input_rs_symbol,
                cartData.totalPrice.toString()
            )
        )
    }

    private fun setupRecyclerView() {
        adapter = CommonRVAdapter(ScreenName.FRAGMENT_CART.value, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.response.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                ResourceViewState.Status.SUCCESS -> {
                    setProgressBar(false)
                    if (it.data != null && it.data.status == 1) {
                        setDataOnViews(it.data.data)
                        if (!it.data.data.cartItems.isNullOrEmpty()) {

                            cartItemList = it.data.data.cartItems as ArrayList<CartItem>
                            adapter.setItems(cartItemList)
                        } else {
                            //binding.noResultIV.visibility = View.VISIBLE
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    } else
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                }

                ResourceViewState.Status.ERROR -> {
                    setProgressBar(false)
                    if (it.message?.contains("401") == true) {
                        Toast.makeText(
                            requireContext(),
                            R.string.session_expired,
                            Toast.LENGTH_SHORT
                        ).show()
                        //  activity?.let { it1 -> CommonUtility.logoutAppSession(it1) };

                    } else
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                }

                ResourceViewState.Status.LOADING -> {
                    setProgressBar(true)
                }
            }
        })

    }

    private fun setProgressBar(b: Boolean) {
        binding.progressBar.visibility = if (!b) View.GONE else View.VISIBLE
    }

    override fun onSelectItemRVType(selectedItem: Any, selectedAction: String) {
        selectedItem as CartItem

        when (selectedAction) {
            ScreenName.ACTION_DELETE_CART.value -> {
                // viewModel.deleteAddress(selectedItem.id)
                UICommon.showAlertDialog(
                    requireContext(), // Pass the context of the fragment
                    false, // Cancellable
                    getString(R.string.alert), // Title of the dialog
                    getString(R.string.delete_address_message), // Message of the dialog
                    getString(R.string.yes), // Text for positive button
                    getString(R.string.no), // Text for negative button
                    "", // Text for neutral button
                    object : AlertDialogListener { // Listener for button clicks

                        override fun onPositiveButton(dialog: DialogInterface?) {
                            viewModel.deleteCartItem(
                                ScreenName.REQUEST_DELETE_CART_ITEM.value,
                                selectedItem.id
                            )
                        }
                    }
                )
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }

    }


}