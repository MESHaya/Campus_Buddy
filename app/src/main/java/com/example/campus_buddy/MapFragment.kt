package com.example.campus_buddy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.ImageButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.bumptech.glide.Glide
import java.net.URLEncoder
import java.util.Locale

class MapFragment : AppCompatActivity() {

    private lateinit var staticMapImage: ImageView
    private lateinit var radioGroup: RadioGroup
    private lateinit var btnBack: ImageButton

    // Put your API key securely (see notes). For demo, we read from strings.xml
    private val apiKey: String by lazy { getString(R.string.google_maps_key) }

    // Campus coordinates map: id -> Pair(lat, lng)
    private val campusCoordinates = mapOf(
        R.id.radioMSA to Pair(-26.0833, 27.8765),                // IIE MSA (Ruimsig, Gauteng)
        R.id.radioVarsity to Pair(-26.11441731442983, 28.057343816972416), // Varsity College Sandton
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_map)

        staticMapImage = findViewById(R.id.staticMapImage)
        radioGroup = findViewById(R.id.radioGroupCampus)
        btnBack = findViewById(R.id.btnBack)

        // Load default (the checked radio)
        val initialId = radioGroup.checkedRadioButtonId.takeIf { it != -1 } ?: R.id.radioMSA
        val coords = campusCoordinates[initialId] ?: campusCoordinates.values.first()
        loadCampusMap(coords!!.first, coords.second)

        // Change when radio selection changes
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val p = campusCoordinates[checkedId]
            if (p != null) {
                loadCampusMap(p.first, p.second)
            } else {
                Toast.makeText(this, "Coordinates not found", Toast.LENGTH_SHORT).show()
            }
        }

        // Tap -> open Google Maps directions to current selection
        staticMapImage.setOnClickListener {
            val checkedId = radioGroup.checkedRadioButtonId.takeIf { it != -1 } ?: R.id.radioMSA
            val p = campusCoordinates[checkedId]
            if (p != null) openGoogleMapsDirections(p.first, p.second) else
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
        }

        btnBack.setOnClickListener { finish() }
    }

    private fun loadCampusMap(lat: Double, lng: Double) {
        // Build Static Maps URL
        // size=640x400 is max in free tier for some apps; you may adjust
        val size = "800x600" // adjust if you want different resolution
        val zoom = 15
        // marker label and color
        val marker = "color:blue|label:C|$lat,$lng"

        // URL encode marker string
        val markerEncoded = URLEncoder.encode(marker, "utf-8")
        // Center param
        val center = "$lat,$lng"

        val mapUrl = String.format(
            Locale.US,
            "https://maps.googleapis.com/maps/api/staticmap?center=%s&zoom=%d&size=%s&markers=%s&key=%s",
            center, zoom, size, markerEncoded, apiKey
        )

        // Load with Glide
        Glide.with(this)
            .load(mapUrl)
            .into(staticMapImage)
    }

    private fun openGoogleMapsDirections(lat: Double, lng: Double) {
        // Open Google Maps with directions to lat,lng
        val uri = Uri.parse("google.navigation:q=$lat,$lng&mode=d")
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps")

        // If Google Maps app not available, open in browser
        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            val browserUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng")
            startActivity(Intent(Intent.ACTION_VIEW, browserUri))
        }
    }
}
