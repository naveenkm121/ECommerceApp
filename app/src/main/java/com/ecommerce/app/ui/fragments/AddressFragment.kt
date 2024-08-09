package com.ecommerce.app.ui.fragments

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecommerce.app.R
import com.ecommerce.app.R.color.black
import com.ecommerce.app.R.color.red
import com.ecommerce.app.constants.IntentConstants
import com.ecommerce.app.constants.RequestApiType
import com.ecommerce.app.constants.ScreenName
import com.ecommerce.app.data.address.AddressItem
import com.ecommerce.app.data.address.AddressReq
import com.ecommerce.app.databinding.FragmentAddressBinding
import com.ecommerce.app.ui.activities.HomeActivity
import com.ecommerce.app.ui.adapters.CommonRVAdapter
import com.ecommerce.app.ui.viewmodels.AddressViewModel
import com.ecommerce.app.utils.AlertDialogListener
import com.ecommerce.app.utils.CommonSelectItemRVListerner
import com.ecommerce.app.utils.DebugHandler
import com.ecommerce.app.utils.GsonHelper
import com.ecommerce.app.utils.ResourceViewState
import com.ecommerce.app.utils.UICommon
import com.ecommerce.app.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressFragment : Fragment(), CommonSelectItemRVListerner {
    private var binding: FragmentAddressBinding by autoCleared()
    private val viewModel: AddressViewModel by viewModels()
    private lateinit var adapter: CommonRVAdapter
    private var addressItemList = ArrayList<AddressItem>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setHasOptionsMenu(true)
        setupToolbar(getString(R.string.fragment_add_address))
        setupMenuOption()
        setupViews()
        setupRecyclerView()
        setupObservers()
        setOnClickListener()
        viewModel.getAddress(ScreenName.FRAGMENT_ADDRESS.value)
    }

    private fun setupRecyclerView() {
        adapter = CommonRVAdapter(ScreenName.FRAGMENT_ADDRESS.value, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupToolbar(title: String) {
        val toolbar: Toolbar = requireActivity().findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setTitle(title)
    }
    private fun setupViews() {
        binding.topProgressLyt.addressDotView.setBackgroundTintList(ColorStateList.valueOf(requireContext().getColor(red)))
        binding.topProgressLyt.addressLineView.setBackgroundColor(requireContext().getColor(red))
        binding.topProgressLyt.addressTV.setTextColor(requireContext().getColor(black))
    }

    private fun setupMenuOption() {


        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(com.ecommerce.app.R.menu.main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {

                    R.id.action_search -> {
                        DebugHandler.log("Hello Address Search")
                        true
                    }

                    R.id.action_wishlist -> {
                        DebugHandler.log("Hello Address Wishlist")
                        true
                    }

                    else -> false
                }
            }

        })
    }

    private fun setupObservers() {

        viewModel.response.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                ResourceViewState.Status.SUCCESS -> {
                    setProgressBar(false)
                    if (it.data != null && it.data.status == 1) {
                        if (it.data.data.isNotEmpty()) {
                            addressItemList = it.data.data as ArrayList<AddressItem>
                            adapter.setItems(addressItemList)
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

        viewModel.responseAddress.observe(viewLifecycleOwner, Observer {
            when (it.status) {

                ResourceViewState.Status.SUCCESS -> {
                    setProgressBar(false)

                    when (it.requestType) {
                        RequestApiType.REQUEST_DELETE_ADDRESS.value -> {
                            if (it.data != null && it.data.status == 1) {
                                addressItemList.removeAll { item -> item.id == it.data.data.id }
                                adapter.setItems(addressItemList)
                            } else {
                                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                        RequestApiType.REQUEST_UPDATE_ADDRESS.value -> {
                            if (it.data != null && it.data.status == 1) {
                                // Update isSelect to 1 and update the addressItemList
                                addressItemList = addressItemList.map { item ->
                                    if (item.id == it.data.data.id) {
                                        item.copy(isDefault = 1)
                                    } else {
                                        item.copy(isDefault = 0)
                                    }
                                } as ArrayList<AddressItem>

                                // Update the adapter with the new list
                                adapter.setItems(addressItemList)
                            } else {
                                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
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
                    DebugHandler.log("Loading ....")
                    setProgressBar(true)
                }
            }
        })

    }

    private fun setOnClickListener() {
        binding.fab.setOnClickListener({
            findNavController().navigate(R.id.action_addressFragment_to_addAddressFragment)
        })

        binding.proceedBTN.setOnClickListener({
            findNavController().navigate(R.id.action_addressFragment_to_checkoutFragment)
        })
    }

    override fun onSelectItemRVType(selectedItem: Any, selectedAction: String) {
        selectedItem as AddressItem
        when (selectedAction) {
            ScreenName.ACTION_DELETE_ADDRESS.value -> {
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
                            viewModel.deleteAddress(RequestApiType.REQUEST_DELETE_ADDRESS.value,selectedItem.id)
                            // adapter.notifyDataSetChanged()
                        }
                    }
                )
            }

            ScreenName.ACTION_EDIT_ADDRESS.value -> {
                findNavController().navigate(
                    R.id.action_addressFragment_to_addAddressFragment,
                    bundleOf(
                        IntentConstants.ADDRESS_DATA to GsonHelper.toJson(selectedItem)
                    )
                )
            }

            ScreenName.ACTION_DEFAULT_ADDRESS.value -> {
                var addressReq = AddressReq()
                addressReq.isDefault=1
                addressReq.name=selectedItem.name
                addressReq.address1=selectedItem.address1
                addressReq.address2=selectedItem.address2
                addressReq.locality=selectedItem.locality
                addressReq.landmark=selectedItem.landmark
                addressReq.pincode=selectedItem.pincode
                addressReq.city=selectedItem.city
                addressReq.state=selectedItem.state
                addressReq.mobile=selectedItem.mobile

                viewModel.updateAddress(RequestApiType.REQUEST_UPDATE_ADDRESS.value,addressReq,selectedItem.id)
            }
        }
    }

    private fun setProgressBar(b: Boolean) {
        binding.progressBar.visibility = if (!b) View.GONE else View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as? HomeActivity)?.showBottomNavigationBar(true)
    }
    /*    fun onBackPressFragment()
        {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Pop the current fragment from the back stack
                    requireActivity().supportFragmentManager.popBackStack()

                    // Optionally, you can add additional logic here
                    // For example, navigate to another fragment or activity
                    // Or perform some action specific to this fragment
                }
            })
        }*/

}