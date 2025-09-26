package com.example.campus_buddy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.campus_buddy.databse.DatabaseHelper

class Welcome : AppCompatActivity() {

    private lateinit var regBTN : Button
    private lateinit var loginBTN: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)

        //inistialise UI elements
        regBTN = findViewById<Button>(R.id.regBTN)
        loginBTN = findViewById<Button>(R.id.loginBTN)

        //button functionality
        regBTN.setOnClickListener() {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        loginBTN.setOnClickListener() {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
        }


    }

