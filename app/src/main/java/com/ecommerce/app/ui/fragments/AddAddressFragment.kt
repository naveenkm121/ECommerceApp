package com.ecommerce.app.ui.fragments

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.ecommerce.app.R
import com.ecommerce.app.constants.AppConstants
import com.ecommerce.app.constants.ScreenName
import com.ecommerce.app.databinding.FragmentAddAddressBinding
import com.ecommerce.app.databinding.FragmentAddressBinding
import com.ecommerce.app.ui.viewmodels.AddAddressViewModel
import com.ecommerce.app.ui.viewmodels.LoginViewModel
import com.ecommerce.app.utils.DebugHandler
import com.ecommerce.app.utils.ResourceViewState
import com.ecommerce.app.utils.SaveSharedPreference
import com.ecommerce.app.utils.autoCleared
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ecommerce.app.data.address.AddressReq
import com.ecommerce.app.data.address.PincodeRes
import com.ecommerce.app.data.login.SignupReq
import com.ecommerce.app.utils.CommonUtility
import com.ecommerce.app.utils.GsonHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddAddressFragment : Fragment() {
    private var binding: FragmentAddAddressBinding by autoCleared()
    private val viewModel: AddAddressViewModel by viewModels()
    private var addressReq: AddressReq = AddressReq()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
        setupObservers()

    }


    private fun validateInput(): Boolean {
        val username = binding.nameET.text.toString()
        val mobile = binding.mobileET.text.toString()
        val address1 = binding.address1ET.text.toString()
        val address2 = binding.address2ET.text.toString()
        val pincode = binding.pincodeET.text.toString()

        // Validate input fields and update UI accordingly
        if (username.isEmpty()) {
            binding.nameET.error = getString(R.string.err_username_empty)
            return false
        } else {
            binding.nameET.error = null
        }

        if (!CommonUtility.isValidPhoneNumber(mobile)) {
            binding.mobileTIL.error = getString(R.string.err_mobile)
            return false
        } else {
            binding.mobileTIL.error = null
        }

        if (address1.isEmpty()) {
            binding.address1TIL.error = getString(R.string.err_address1_empty)
            return false
        } else {
            binding.address1TIL.error = null
        }

        if (pincode.isEmpty()) {
            binding.pincodeTIL.error = getString(R.string.err_pincode_empty)
            return false
        } else {
            binding.pincodeTIL.error = null
        }

        return true
    }

    private fun setOnClickListener() {

        binding.pincodeET.setOnEditorActionListener { _, actionId, event ->
            if ((actionId == EditorInfo.IME_ACTION_DONE || (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) && (binding.pincodeET.text.toString().length == 6)) {
                var pincode: String = binding.pincodeET.text.toString();
                viewModel.getPincodeDetails(pincode)
                true // Consume the event
            } else {
                false // Let the system handle other key events
            }
        }


        binding.defaultAddCB.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                addressReq.isDefault=1
           else
                addressReq.isDefault=0

        }

        binding.proceedBTN.setOnClickListener {
            if (validateInput()) {
                addressReq.name = binding.nameET.text.toString()
                addressReq.mobile = binding.mobileET.text.toString()
                addressReq.address1 = binding.address1ET.text.toString()
                addressReq.address2 = binding.address2ET.text.toString()
                addressReq.landmark = binding.landmarkET.text.toString()
                addressReq.pincode = binding.pincodeET.text.toString()
                addressReq.city = binding.cityET.text.toString()
                addressReq.state = binding.stateET.text.toString()

                viewModel.addAddress(addressReq)

                DebugHandler.log("Address Req == "+ GsonHelper.toJson(addressReq))

            }
        }


    }

    private fun setPincodeDetails(pincodeRes: PincodeRes) {
        binding.pincodeTIL.error = ""
        binding.cityET.setText(pincodeRes.data.district)
        binding.stateET.setText(pincodeRes.data.state)
        setupSpinner(binding.localitySpinner, pincodeRes.data.locality)
        binding.localitySpinner.visibility = View.VISIBLE

    }

    private fun setupSpinner(spinner: Spinner, items: List<String>) {
        // Create an ArrayAdapter using the list of items
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set the adapter to the Spinner
        spinner.adapter = adapter

        // Set a listener to handle item selection
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Display a toast message with the selected item
                addressReq.locality = parent.getItemAtPosition(position).toString()
                Toast.makeText(
                    requireContext(),
                    "Selected: ${parent.getItemAtPosition(position)}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun setupObservers() {

        viewModel.response.observe(viewLifecycleOwner, Observer {
            when (it.status) {

                ResourceViewState.Status.LOADING -> {
                    // setProgressBar(true)
                }

                ResourceViewState.Status.SUCCESS -> {
                    setProgressBar(false)
                    if (it.data?.message?.startsWith(AppConstants.SUCCESS, true) == true) {
                        setPincodeDetails(it.data)

                    } else {
                        binding.pincodeTIL.error = it.data?.message
                        Toast.makeText(requireContext(), it.data?.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                }

                ResourceViewState.Status.ERROR -> {
                  //  setProgressBar(true)
                    Toast.makeText(requireContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                }

            }

        })

        viewModel.responseAddAddress.observe(viewLifecycleOwner, Observer {
            when (it.status) {

                ResourceViewState.Status.LOADING -> {
                    // setProgressBar(true)
                }

                ResourceViewState.Status.SUCCESS -> {
                    setProgressBar(false)
                    if (it.data?.message?.startsWith(AppConstants.SUCCESS, true) == true) {
                        launchAddressListScreen()
                    } else {
                        Toast.makeText(requireContext(), it.data?.message, Toast.LENGTH_SHORT).show()
                    }

                }

                ResourceViewState.Status.ERROR -> {
                    //  setProgressBar(true)
                    Toast.makeText(requireContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                }

            }

        })


    }

    private fun launchAddressListScreen(){
        findNavController().navigate(R.id.action_addAddressFragment_to_addressFragment)
    }

    private fun setProgressBar(b: Boolean) {
        if (!b) {
            // binding.progressBar.visibility = View.GONE
        } else {
            //   binding.progressBar.visibility = View.VISIBLE
        }
    }
}