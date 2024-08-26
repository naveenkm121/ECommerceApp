package com.ecommerce.app.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ecommerce.app.R
import com.ecommerce.app.constants.IntentConstants
import com.ecommerce.app.data.wishlist.WishlistItem
import com.ecommerce.app.databinding.FragmentWishlistBinding
import com.ecommerce.app.ui.activities.HomeActivity
import com.ecommerce.app.ui.adapters.WishlistAdapter
import com.ecommerce.app.ui.viewmodels.WishlistViewModel
import com.ecommerce.app.utils.CommonSelectItemRVListerner
import com.ecommerce.app.utils.DebugHandler
import com.ecommerce.app.utils.ResourceViewState
import com.ecommerce.app.utils.SaveSharedPreference
import com.ecommerce.app.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WishlistFragment : Fragment(),CommonSelectItemRVListerner {

    private val wishlistViewModel: WishlistViewModel by viewModels()
    private var binding: FragmentWishlistBinding by autoCleared()
    private lateinit var adapter: WishlistAdapter
    private var productListItem = ArrayList<WishlistItem>()


        override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentWishlistBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as HomeActivity).setupToolbar(getString(R.string.fragment_wishlist))
        setupRecyclerView()
        setupObservers()
        wishlistViewModel.getWishlist(null)
    }

    private fun setupRecyclerView() {
        adapter = WishlistAdapter(this)
        binding.recyclerView.layoutManager =  GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        wishlistViewModel.response.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                ResourceViewState.Status.SUCCESS -> {
                    setProgressBar(false)
                   if (it.data != null && it.data.status == 1) {
                        if (it.data.data.isNotEmpty()) {

                            productListItem = it.data.data as ArrayList<WishlistItem>
                            adapter.setItem(productListItem)
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
                       Toast.makeText(requireContext(), R.string.session_expired, Toast.LENGTH_SHORT).show()
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
        binding.progressBar.visibility = if (!b) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }


    override fun onSelectItemRVType(selectedItem: Any, selectedAction: String) {
        selectedItem as WishlistItem
        findNavController().navigate(R.id.action_wishlistFragment_to_productDetailFragment,
            bundleOf(
                IntentConstants.PRODUCT_ID to selectedItem.prodId,
                IntentConstants.PRODUCT_BRAND to selectedItem.brand
            )
        )
    }
}