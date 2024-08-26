package com.ecommerce.app.ui.fragments

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecommerce.app.R
import com.ecommerce.app.R.color.black
import com.ecommerce.app.R.color.red
import com.ecommerce.app.constants.RequestApiType
import com.ecommerce.app.constants.ScreenName
import com.ecommerce.app.data.address.AddressItem
import com.ecommerce.app.data.cart.CartData
import com.ecommerce.app.data.cart.CartItem
import com.ecommerce.app.databinding.FragmentCartBinding
import com.ecommerce.app.databinding.FragmentCheckoutBinding
import com.ecommerce.app.ui.activities.HomeActivity
import com.ecommerce.app.ui.adapters.CommonRVAdapter
import com.ecommerce.app.ui.viewmodels.AddressViewModel
import com.ecommerce.app.ui.viewmodels.CartViewModel
import com.ecommerce.app.utils.AlertDialogListener
import com.ecommerce.app.utils.CommonSelectItemRVListerner
import com.ecommerce.app.utils.CommonUtility
import com.ecommerce.app.utils.DebugHandler
import com.ecommerce.app.utils.ResourceViewState
import com.ecommerce.app.utils.UICommon
import com.ecommerce.app.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CheckoutFragment : Fragment() {
    private var binding: FragmentCheckoutBinding by autoCleared()
    private val viewModel: CartViewModel by viewModels()
    private val addressViewModel: AddressViewModel by viewModels()
    private lateinit var adapter: CommonRVAdapter
    private var cartItemList = ArrayList<CartItem>()
    private var addressItemList = ArrayList<AddressItem>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as HomeActivity).setupToolbar(getString(R.string.fragment_checkout))
        setupViews()
        setupObservers()
        viewModel.getCartItems(RequestApiType.REQUEST_GET_CART_ITEMS.value, "")
        addressViewModel.getAddress(ScreenName.FRAGMENT_ADDRESS.value)
    }

    private fun setupViews() {
        binding.topProgressLyt.addressDotView.setBackgroundTintList(
            ColorStateList.valueOf(requireContext().getColor(
                red
            )))
        binding.topProgressLyt.addressLineView.setBackgroundColor(requireContext().getColor(red))
        binding.topProgressLyt.addressTV.setTextColor(requireContext().getColor(black))

        binding.topProgressLyt.checkoutDotView.setBackgroundTintList(
            ColorStateList.valueOf(requireContext().getColor(
                red
            )))
        binding.topProgressLyt.checkoutLineView.setBackgroundColor(requireContext().getColor(red))
        binding.topProgressLyt.checkoutTV.setTextColor(requireContext().getColor(black))


    }

    private fun setProgressBar(b: Boolean) {
        binding.progressBar.visibility = if (!b) View.GONE else View.VISIBLE
    }

    private fun setCartDataOnViews(cartData: CartData) {
        binding.priceHeadingTV.setText(getString(R.string.price_details) + " ( ${cartData.totalProducts} Items)")
        binding.mrpValTV.setText(
            getString(
                R.string.input_rs_symbol,
                CommonUtility.roundOffToTwoDecimalPlaces(cartData.totalPrice)
            )
        )
        binding.discountValTV.setText(
            getString(
                R.string.input_rs_symbol,
                CommonUtility.roundOffToTwoDecimalPlaces(cartData.totalDiscountPrice).toString()
            )

        )
        binding.totalValTV.setText(
            getString(
                R.string.input_rs_symbol,
                CommonUtility.roundOffToTwoDecimalPlaces(cartData.totalPrice-cartData.totalDiscountPrice).toString()
            )
        )

        ((requireActivity() as? HomeActivity)?.showBottomNavigationBar(false))
    }

    private fun setAddressDataOnViews(item: AddressItem) {
        binding.nameTV.text = item.name
        binding.address1TV.text=item.address1
        binding.address2TV.text=item.address2+", "+item.locality
        binding.landmark.text= requireContext().getString(R.string.landmark_str,item.landmark)
        binding.cityPincodeTV.text = item.city+"-"+item.pincode
        binding.state.text= item.state
        binding.mobileTV.text= requireContext().getString(R.string.mobile_str,item.mobile)

        /*if(item.isDefault==1)
        {
            binding.defaultIV.setColorFilter(context.getColor(R.color.red))
            binding.defaultTV.setTextColor(context.getColor(R.color.red))
        }*/

        binding.changeAddressBTN.setOnClickListener {
            findNavController().navigate(R.id.action_checkoutFragment_to_addressFragment)
        }

        ((requireActivity() as? HomeActivity)?.showBottomNavigationBar(false))
    }

    private fun setupObservers() {

        viewModel.response.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                ResourceViewState.Status.SUCCESS -> {
                    setProgressBar(false)
                    if (it.data != null && it.data.status == 1)
                        setCartDataOnViews(it.data.data)
                     else
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
                        activity?.let { it1 -> CommonUtility.logoutAppSession(it1) };

                    } else
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                }

                ResourceViewState.Status.LOADING -> {
                    setProgressBar(true)
                }
            }
        })

        addressViewModel.response.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                ResourceViewState.Status.SUCCESS -> {
                    setProgressBar(false)
                    if (it.data != null && it.data.status == 1) {
                        if (it.data.data.isNotEmpty()) {
                            addressItemList = it.data.data as ArrayList<AddressItem>
                           // adapter.setItems(addressItemList)
                            var addressItem=addressItemList.find { addressItem ->addressItem.isDefault==1  }
                            addressItem?.let { it1 -> setAddressDataOnViews(it1) }

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




}