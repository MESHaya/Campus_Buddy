package com.example.campus_buddy

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.campus_buddy.databse.DatabaseHelper

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        //get reference to ui elements

        val usernameInput = findViewById<EditText>(R.id.etUsername)
        val passwordInput = findViewById<EditText>(R.id.etPassword)
        val loginBTN = findViewById<Button>(R.id.btnLogin)
        val googleBTN = findViewById<Button>(R.id.btnGoogleLogin)

        //implement functionality of the login button

        loginBTN.setOnClickListener(){
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            //ensure all fields are filled in
            if(username.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"Fill in all fields",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

        }
    }
}