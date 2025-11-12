package com.example.campus_buddy

import android.os.Bundle
import android.widget.ImageButton
import android.widget.RadioGroup
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

    // Campus locations - Update these coordinates with actual locations
    private val msaLocation = LatLng(-26.1076, 28.0567) // IIE MSA coordinates
    private val varsityLocation = LatLng(-26.1100, 28.0600) // IIE Varsity coordinates

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_map)

        // Initialize views
        radioGroupCampus = findViewById(R.id.radioGroupCampus)
        btnBack = findViewById(R.id.btnBack)

        // Set up the map fragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Back button functionality
        btnBack.setOnClickListener {
            finish()
        }

        // Radio button listener
        radioGroupCampus.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioMSA -> {
                    if (::googleMap.isInitialized) {
                        showLocation(msaLocation, "IIE MSA")
                    }
                }
                R.id.radioVarsity -> {
                    if (::googleMap.isInitialized) {
                        showLocation(varsityLocation, "IIE Varsity College")
                    }
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Configure map settings
        googleMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMyLocationButtonEnabled = false
        }

        // Show default location (MSA since it's checked by default)
        showLocation(msaLocation, "IIE MSA")
    }

    private fun showLocation(location: LatLng, title: String) {
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
    }
}