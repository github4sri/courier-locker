package name.lmj0011.courierlocker.fragments

import android.location.Address
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import name.lmj0011.courierlocker.MainActivity
import name.lmj0011.courierlocker.R
import name.lmj0011.courierlocker.adapters.AddressAutoSuggestAdapter
import name.lmj0011.courierlocker.database.CourierLockerDatabase
import name.lmj0011.courierlocker.databinding.FragmentCreateTripBinding
import name.lmj0011.courierlocker.factories.TripViewModelFactory
import name.lmj0011.courierlocker.helpers.LocationHelper
import name.lmj0011.courierlocker.viewmodels.TripViewModel
import name.lmj0011.courierlocker.helpers.todaysDate

/**
 * A simple [Fragment] subclass.
 *
 */
class CreateTripFragment : Fragment() {
    private lateinit var binding: FragmentCreateTripBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var tripViewModel: TripViewModel
    private lateinit var handler1: Handler
    private lateinit var handler2: Handler

    private var pickupAddressLatitude: Double = 0.toDouble()
    private var pickupAddressLongitude: Double = 0.toDouble()
    private var dropOffAddressLatitude: Double = 0.toDouble()
    private var dropOffAddressLongitude: Double = 0.toDouble()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_create_trip, container, false)

        mainActivity = activity as MainActivity

        val application = requireNotNull(this.activity).application
        val dataSource = CourierLockerDatabase.getInstance(application).tripDao
        val viewModelFactory = TripViewModelFactory(dataSource, application)
        this.tripViewModel = ViewModelProviders.of(this, viewModelFactory).get(TripViewModel::class.java)

        binding.tripViewModel = this.tripViewModel

        ArrayAdapter.createFromResource(
            mainActivity,
            R.array.gigs_array,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.gigSpinner.adapter = it
        }

        binding.saveButton.setOnClickListener(this::saveButtonOnClickListener)

        /// Auto Complete Text View Adapter setup

        // Initialize a new array adapter object
        val adapter1 = AddressAutoSuggestAdapter(
            mainActivity, // Context
            android.R.layout.simple_dropdown_item_1line
        )

        val adapter2 = AddressAutoSuggestAdapter(
            mainActivity, // Context
            android.R.layout.simple_dropdown_item_1line
        )

        handler1 = LocationHelper.getNewAddressAutoCompleteHandler(adapter1)

        handler2 = LocationHelper.getNewAddressAutoCompleteHandler(adapter2)

        // Set the AutoCompleteTextView adapter
        binding.pickupAddressAutoCompleteTextView.setAdapter(adapter1)
        binding.dropOffAddressAutoCompleteTextView.setAdapter(adapter2)

        // Auto complete threshold
        // The minimum number of characters to type to show the drop down
        binding.pickupAddressAutoCompleteTextView.threshold = 1
        binding.dropOffAddressAutoCompleteTextView.threshold = 1

        // Set an item click listener for auto complete text view
        binding.pickupAddressAutoCompleteTextView.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val address: Address? = adapter1.getItem(position)

            address?.let {
                binding.pickupAddressAutoCompleteTextView.setText(it.getAddressLine(0))
                this@CreateTripFragment.pickupAddressLatitude = it.latitude
                this@CreateTripFragment.pickupAddressLongitude = it.longitude
            }

        }

        binding.dropOffAddressAutoCompleteTextView.onItemClickListener = AdapterView.OnItemClickListener{
                parent,view,position,id->
            val address: Address? = adapter2.getItem(position)

            address?.let {
                binding.dropOffAddressAutoCompleteTextView.setText(it.getAddressLine(0))
                this@CreateTripFragment.dropOffAddressLatitude = it.latitude
                this@CreateTripFragment.dropOffAddressLongitude = it.longitude
            }

        }

        binding.pickupAddressAutoCompleteTextView.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adapter1.notifyDataSetChanged()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler1.removeMessages(MainActivity.TRIP_PICKUP_AUTO_COMPLETE)

                val bundle = Bundle()
                bundle.putString("address", binding.pickupAddressAutoCompleteTextView.text.toString())
                val msg = handler1.obtainMessage(MainActivity.TRIP_PICKUP_AUTO_COMPLETE)
                msg.data = bundle
                handler1.sendMessageDelayed(msg, MainActivity.AUTO_COMPLETE_DELAY)
            }
        })

        binding.dropOffAddressAutoCompleteTextView.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adapter2.notifyDataSetChanged()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler2.removeMessages(MainActivity.TRIP_DROP_OFF_AUTO_COMPLETE)

                val bundle = Bundle()
                bundle.putString("address", binding.dropOffAddressAutoCompleteTextView.text.toString())
                val msg = handler2.obtainMessage(MainActivity.TRIP_DROP_OFF_AUTO_COMPLETE)
                msg.data = bundle
                handler2.sendMessageDelayed(msg, MainActivity.AUTO_COMPLETE_DELAY)
            }
        })

        /// setting current location's address into the pickupAddress textview
        binding.insertMyLocationButtonForPickupAddress.setOnClickListener {
            val address = LocationHelper.getGeocoder().getFromLocation(LocationHelper.lastLatitude.value!!, LocationHelper.lastLongitude.value!!, 1)

            when{
                address.size > 0 -> {
                    binding.pickupAddressAutoCompleteTextView.setText(address[0].getAddressLine(0))
                    this@CreateTripFragment.pickupAddressLatitude = address[0].latitude
                    this@CreateTripFragment.pickupAddressLongitude = address[0].longitude
                }
                else -> {
                    Toast.makeText(mainActivity, "Unable to resolve an Address from current location", Toast.LENGTH_LONG)
                }
            }
        }

        binding.insertMyLocationButtonForDropOffAddress.setOnClickListener {
            val address = LocationHelper.getGeocoder().getFromLocation(LocationHelper.lastLatitude.value!!, LocationHelper.lastLongitude.value!!, 1)

            when{
                address.size > 0 -> {
                    binding.dropOffAddressAutoCompleteTextView.setText(address[0].getAddressLine(0))
                    this@CreateTripFragment.dropOffAddressLatitude = address[0].latitude
                    this@CreateTripFragment.dropOffAddressLongitude = address[0].longitude
                }
                else -> {
                    Toast.makeText(mainActivity, "Unable to resolve an Address from current location", Toast.LENGTH_LONG)
                }
            }
        }
        //////////////////

        mainActivity.hideFab()


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mainActivity.supportActionBar?.title = "Add new Trip"
        mainActivity.supportActionBar?.subtitle = null
        binding.tripDateTextView.text = todaysDate()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun saveButtonOnClickListener(v: View) {
        val date = binding.tripDateTextView.text.toString()
        val pickupAddress = binding.pickupAddressAutoCompleteTextView.text.toString()
        val dropOffAddress = binding.dropOffAddressAutoCompleteTextView.text.toString()
        val payAmount = binding.payAmountEditText.text.toString()
        val gig = binding.gigSpinner.selectedItem.toString()

        when{
            date.isNullOrBlank() -> {
                Toast.makeText(context, "Error: could not set the date", Toast.LENGTH_LONG).show()
                return
            }
            pickupAddress.isNullOrBlank() -> {
                Toast.makeText(context, "Must enter a pickup address", Toast.LENGTH_LONG).show()
                return
            }
            else -> {}
        }

        this.tripViewModel.insertTrip(
            date,
            pickupAddress,
            this@CreateTripFragment.pickupAddressLatitude,
            this@CreateTripFragment.pickupAddressLongitude,
            dropOffAddress,
            this@CreateTripFragment.dropOffAddressLatitude,
            this@CreateTripFragment.dropOffAddressLongitude,
            "-1".toDouble(),
            payAmount,
            gig
        )

        Toast.makeText(context, "New Trip added", Toast.LENGTH_SHORT).show()
        this.findNavController().navigate(R.id.tripsFragment)
    }
}