package com.example.red_v1.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.red_v1.databinding.FragmentMapBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.resume
import android.content.Context
import com.example.red_v1.R
import android.location.Location
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import com.example.red_v1.MainViewModel

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var geocoder: Geocoder
    private var locationPermissionGranted = false
    private lateinit var binding: FragmentMapBinding
    private val points = mutableListOf<LatLng>()
    private var startLatLng: LatLng? = null
    private lateinit var ViewModel: MainViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun getAddresses(query: String): List<Address> = suspendCoroutine { cont ->
        geocoder.getFromLocationName(query, 1) {
            cont.resume(it)
        }
    }

    private suspend fun processAddresses(addresses: List<Address>) {
        if (addresses.isNotEmpty()) {
            val address = addresses[0]
            val latLng = LatLng(address.latitude, address.longitude)
            map.addMarker(MarkerOptions().position(latLng).title("${"%.3f".format(latLng.latitude)} ${"%.3f".format(latLng.longitude)}"))
            withContext(Dispatchers.Main) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMapBinding.inflate(inflater, container, false)
        this.binding = binding
        ViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        checkGooglePlayServices()
        requestPermission()

        // Initialize Geocoder
        geocoder = Geocoder(requireContext(), Locale.getDefault())

        // Set up the map fragment asynchronously

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapFrag) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Set up the "Go" button to search for location
        binding.goBut.setOnClickListener {
            val locationName = binding.mapET.text.toString()
            Log.d("Geocoding", locationName)

            MainScope().launch {
                val addresses = getAddresses(locationName)
                processAddresses(addresses)
                Log.d("Geocoding1", "Geocoding result received: $addresses")
                if (addresses.isNotEmpty()) {
                    val address = addresses[0].getAddressLine(0)
                    ViewModel.setAddress(address)
                    Log.d("ProfileActivity2", "Extracted address: $address")
                }
            }
        }

        // Handle enter key or done button press on keyboard
        binding.mapET.setOnEditorActionListener { _, actionId, event ->
            if ((event != null && event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                binding.goBut.callOnClick()
            }
            false
        }

        // Set up the "Clear" button to clear the map
        binding.clearBut.setOnClickListener {
            map.clear()
        }

        return binding.root
    }

    // Callback when the map is ready to be used
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.setOnMapClickListener { latLng ->
            if (startLatLng == null) {
                startLatLng = latLng
                map.addMarker(MarkerOptions().position(latLng).title("Start"))
            } else {
                val distance = calculateDistance(startLatLng!!, latLng)
                Toast.makeText(requireContext(), "Distance: $distance meters", Toast.LENGTH_LONG).show()

                drawRoute(startLatLng!!, latLng)

                startLatLng = null
            }
        }
        map.setOnMapClickListener { latLng ->
            points.add(latLng)

            drawRoutePath(points)

            map.addMarker(MarkerOptions().position(latLng).title("Point"))

            if (startLatLng == null) {
                startLatLng = latLng
            } else {
                val distance = calculateDistance(startLatLng!!, latLng)
                Toast.makeText(requireContext(), "Distance: $distance meters", Toast.LENGTH_LONG).show()

                startLatLng = null
            }
        }
        if (locationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
        }

        map.setOnMapClickListener { latLng ->
            val markerOptions = MarkerOptions()
                .position(latLng)
                .title("${"%.3f".format(latLng.latitude)} ${"%.3f".format(latLng.longitude)}")
            map.addMarker(markerOptions)
        }

        map.setOnMapLongClickListener {
            map.clear()
        }

        // Start the map at a default location (Harry Ransom Center, for example)
        val harryRansomCenter = LatLng(30.28444, -97.74111)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(harryRansomCenter, 15.0f))

    }

    // Hide the soft keyboard
    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    // Check if Google Play Services are available
    private fun checkGooglePlayServices() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(requireContext())
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(requireActivity(), resultCode, 257)?.show()
            } else {
                Log.i(javaClass.simpleName, "This device must install Google Play Services.")
                requireActivity().finish()
            }
        }
    }

    // Request location permissions
    private fun requestPermission() {
        val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    locationPermissionGranted = true
                }
                else -> {
                    Toast.makeText(requireContext(), "Unable to show location - permission required", Toast.LENGTH_LONG).show()
                }
            }
        }
        locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
    }

    private fun calculateDistance(startLatLng: LatLng, endLatLng: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(startLatLng.latitude, startLatLng.longitude, endLatLng.latitude, endLatLng.longitude, results)
        return results[0] // Distance in meters
    }

    private fun drawRoute(startLatLng: LatLng, endLatLng: LatLng) {
        // Example using static data, integrate Google Directions API for dynamic routing
        val options = PolylineOptions().add(startLatLng, endLatLng)
        val polyline: Polyline = map.addPolyline(options)
    }

    private fun drawRoutePath(points: List<LatLng>) {
        val options = PolylineOptions().addAll(points).width(5f).color(Color.BLUE)
        map.addPolyline(options)
    }
}