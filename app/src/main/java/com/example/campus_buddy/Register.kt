package com.example.campus_buddy

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.campus_buddy.databse.DatabaseHelper
import java.security.MessageDigest

class Register : AppCompatActivity() {

    // Database helper
    private lateinit var dbHelper: DatabaseHelper

    // UI elements
    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var studentIdEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)


        // Initialize database helper
        dbHelper = DatabaseHelper(this)

        // Initialize UI elements
        nameEditText = findViewById(R.id.etName)
        surnameEditText = findViewById(R.id.etSurname)
        usernameEditText = findViewById(R.id.etUsername)
        emailEditText = findViewById(R.id.etEmail)
        studentIdEditText = findViewById(R.id.etStudentId)
        passwordEditText = findViewById(R.id.etPassword)
        signUpButton = findViewById(R.id.btnSignUp)

        // Handle sign up button click
        signUpButton.setOnClickListener {
            val nameText = nameEditText.text.toString().trim()
            val surnameText = surnameEditText.text.toString().trim()
            val usernameText = usernameEditText.text.toString().trim()
            val emailText = emailEditText.text.toString().trim()
            val studentIdText = studentIdEditText.text.toString().trim()
            val passwordText = passwordEditText.text.toString().trim()

            // Validate input fields
            if (nameText.isEmpty() || surnameText.isEmpty() || usernameText.isEmpty() ||
                emailText.isEmpty() || studentIdText.isEmpty() || passwordText.isEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Hash password before saving
            val hashedPassword = hashPassword(passwordText)

            dbHelper.insertUser(
                nameText,
                surnameText,
                usernameText,
                emailText,
                studentIdText,
                hashedPassword
            )
            dbHelper.getAllUsersDebug()

            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
            clearFields()
            finish() // optional

        }
    }


    // Simple SHA-256 hashing for password
    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    // Clear all input fields
    private fun clearFields() {
        nameEditText.text.clear()
        surnameEditText.text.clear()
        usernameEditText.text.clear()
        emailEditText.text.clear()
        studentIdEditText.text.clear()
        passwordEditText.text.clear()
    }







}
