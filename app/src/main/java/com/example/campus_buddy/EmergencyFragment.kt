package com.example.campus_buddy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.campus_buddy.R

class EmergencyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_emergency, container, false)

        // dial Ambulance
        val ambulanceCall: ImageView = view.findViewById(R.id.callAmbulance)
        ambulanceCall.setOnClickListener {
            dialPhoneNumber("10177")
        }

        // dial Police
        val policeCall: ImageView = view.findViewById(R.id.callPolice)
        policeCall.setOnClickListener {
            dialPhoneNumber("10111")
        }

        // dial Traffic
        val trafficCall: ImageView = view.findViewById(R.id.callTraffic)
        trafficCall.setOnClickListener {
            dialPhoneNumber("0861400800")
        }

        // dial Campus Security
        val campusCall: ImageView = view.findViewById(R.id.callCampus)
        campusCall.setOnClickListener {
            dialPhoneNumber("0119504099")
        }

        // dial Zoe test caller
        val zoeCall: ImageView = view.findViewById(R.id.callZoe)
        zoeCall.setOnClickListener {
            dialPhoneNumber("0832226432")
        }

        return view
    }

    private fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)
    }
}
