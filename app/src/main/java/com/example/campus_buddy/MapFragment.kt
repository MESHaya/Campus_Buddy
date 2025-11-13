package com.example.campus_buddy

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var radioGroupCampus: RadioGroup
    private lateinit var btnBack: ImageButton

    // Test location - Johannesburg City Center (very obvious location)
    private val testLocation = LatLng(-26.2041, 28.0473)
    private val msaLocation = LatLng(-26.0833, 27.8765)
    private val varsityLocation = LatLng(-26.09017666692695, 28.052551253580052)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_map)

        Log.d("MapFragment", "onCreate called")
        Toast.makeText(this, "Loading map...", Toast.LENGTH_SHORT).show()

        // Initialize views
        radioGroupCampus = findViewById(R.id.radioGroupCampus)
        btnBack = findViewById(R.id.btnBack)

        // Set up the map fragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as? SupportMapFragment

        if (mapFragment == null) {
            Log.e("MapFragment", "Map fragment is NULL!")
            Toast.makeText(this, "Error: Map fragment not found", Toast.LENGTH_LONG).show()
        } else {
            Log.d("MapFragment", "Map fragment found, calling getMapAsync")
            mapFragment.getMapAsync(this)
        }

        // Back button functionality
        btnBack.setOnClickListener {
            finish()
        }

        // Radio button listener
        radioGroupCampus.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioMSA -> {
                    if (::googleMap.isInitialized) {
                        Log.d("MapFragment", "Showing MSA location")
                        showLocation(msaLocation, "IIE MSA")
                    } else {
                        Log.e("MapFragment", "Map not initialized yet")
                    }
                }
                R.id.radioVarsity -> {
                    if (::googleMap.isInitialized) {
                        Log.d("MapFragment", "Showing Varsity location")
                        showLocation(varsityLocation, "IIE Varsity College")
                    } else {
                        Log.e("MapFragment", "Map not initialized yet")
                    }
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        Log.d("MapFragment", "onMapReady called - MAP IS READY!")
        Toast.makeText(this, "Map loaded successfully!", Toast.LENGTH_SHORT).show()

        googleMap = map

        // Configure map settings
        googleMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMyLocationButtonEnabled = false
        }

        // Set map type to normal
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        // Show test location first
        Log.d("MapFragment", "Showing test location: Johannesburg")
        showLocation(testLocation, "Test Location - Johannesburg")
    }

    private fun showLocation(location: LatLng, title: String) {
        try {
            Log.d("MapFragment", "showLocation called for: $title at $location")

            // Clear existing markers
            googleMap.clear()

            // Add marker for the campus
            googleMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(title)
            )

            // Move camera to the location with animation
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(location, 15f),
                1000,
                null
            )

            Log.d("MapFragment", "Camera moved successfully")
        } catch (e: Exception) {
            Log.e("MapFragment", "Error in showLocation: ${e.message}")
            Toast.makeText(this, "Error showing location: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}