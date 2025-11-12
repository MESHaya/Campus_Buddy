package com.example.campus_buddy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.example.campus_buddy.data.Attendance
import com.example.campus_buddy.databse.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AttendanceFragment : Fragment() {

    private lateinit var txtResult: TextView
    private lateinit var dbHelper: DatabaseHelper // your DB helper/DAO

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            handleQRCodeResult(result.contents)
        } else {
            Toast.makeText(requireContext(), "Scan cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_attendance, container, false)
        val btnScanQR = view.findViewById<Button>(R.id.btnScanQR)
        txtResult = view.findViewById(R.id.tvResult)


        dbHelper = DatabaseHelper(requireContext()) // initialize your DB connection

        btnScanQR.setOnClickListener {
            startQRScanner()
        }

        return view
    }

    private fun startQRScanner() {
        val options = ScanOptions()
        options.setPrompt("Scan class QR code")
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        barcodeLauncher.launch(options)
    }

    private fun handleQRCodeResult(data: String) {
        txtResult.text = "Scanned: $data"

        // Example format: CLASS101|2025-11-12|SESSION2
        val parts = data.split("|")
        val classId = parts.getOrNull(0)
        val date = parts.getOrNull(1)
        val session = parts.getOrNull(2)

        if (classId != null && date != null) {
            if (session != null) {
                markAttendance(
                    "STU123", date, session,
                    session = TODO()
                )
            }
        } else {
            Toast.makeText(requireContext(), "Invalid QR format", Toast.LENGTH_SHORT).show()
        }
    }

    private fun markAttendance(userId: String, moduleId: String, method: String, session: String?) {
        // Make sure 'date' is defined somewhere, e.g., current date as string
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val attendance = Attendance(
            id = System.currentTimeMillis().toString(),
            userId = userId,
            moduleId = moduleId,
            sessionAt = date + " " + (session ?: ""),
            method = method, // use the method passed to the function
            valid = true
        )

        val success = dbHelper.insertAttendance(
            attendance.toString(),
            moduleId = TODO(),
            method = TODO(),
            valid = TODO()
        )
        if (true) {
            Toast.makeText(requireContext(), "Attendance marked!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "Already marked or DB error", Toast.LENGTH_SHORT).show()
        }
    }

}
