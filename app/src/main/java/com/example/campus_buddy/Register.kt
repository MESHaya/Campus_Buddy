package com.example.campus_buddy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.Context
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.campus_buddy.auth.GoogleSignInManager
import com.example.campus_buddy.databse.DatabaseHelper
import java.security.MessageDigest

class Register : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }


    private lateinit var dbHelper: DatabaseHelper
    private lateinit var googleSignInManager: GoogleSignInManager

    // UI elements
    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var studentIdEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var googleButton: Button

    // Activity Result Launcher for Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        googleSignInManager.handleSignInResult(
            result.data,
            onSuccess = { account ->
                handleGoogleSignInSuccess(account)
            },
            onFailure = { exception ->
                Toast.makeText(
                    this,
                    "Google Sign-In failed: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Initialize
        dbHelper = DatabaseHelper(this)
        googleSignInManager = GoogleSignInManager(this)

        // Initialize UI elements
        nameEditText = findViewById(R.id.etName)
        surnameEditText = findViewById(R.id.etSurname)
        usernameEditText = findViewById(R.id.etUsername)
        emailEditText = findViewById(R.id.etEmail)
        studentIdEditText = findViewById(R.id.etStudentId)
        passwordEditText = findViewById(R.id.etPassword)
        signUpButton = findViewById(R.id.btnSignUp)
        googleButton = findViewById(R.id.btnGoogle)

        // Traditional sign up
        signUpButton.setOnClickListener {
            handleTraditionalSignUp()
        }

        // Google Sign-Up
        googleButton.setOnClickListener {
            startGoogleSignIn()
        }
    }

    /**
     * Handle traditional registration
     */
    private fun handleTraditionalSignUp() {
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
            return
        }

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if email already exists
        if (dbHelper.emailExists(emailText)) {
            Toast.makeText(this, "Email already registered", Toast.LENGTH_LONG).show()
            emailEditText.error = "Email already exists"
            return
        }

        // Check if username already exists
        if (dbHelper.usernameExists(usernameText)) {
            Toast.makeText(this, "Username already taken", Toast.LENGTH_LONG).show()
            usernameEditText.error = "Username already exists"
            return
        }

        // Hash password before saving
        val hashedPassword = hashPassword(passwordText)

        try {
            val result = dbHelper.insertUser(
                nameText,
                surnameText,
                usernameText,
                emailText,
                studentIdText,
                hashedPassword
            )

            if (result != -1L) {
                Toast.makeText(this, "Registration successful! 🎉", Toast.LENGTH_SHORT).show()
                clearFields()
                finish()
            } else {
                Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("REGISTER_ERROR", "Error during registration", e)
            Toast.makeText(this, "Registration error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Start Google Sign-In flow
     */
    private fun startGoogleSignIn() {
        val signInIntent = googleSignInManager.getSignInIntent()
        googleSignInLauncher.launch(signInIntent)
    }

    /**
     * Handle successful Google Sign-In
     */
    private fun handleGoogleSignInSuccess(account: com.google.android.gms.auth.api.signin.GoogleSignInAccount) {
        val userData = googleSignInManager.extractUserData(account)

        // Check if user already exists
        if (dbHelper.emailExists(userData.email)) {
            Toast.makeText(
                this,
                "Account already exists. Redirecting to login...",
                Toast.LENGTH_SHORT
            ).show()

            // Navigate to Login
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Create new account
        val result = dbHelper.insertGoogleUser(
            googleId = userData.id,
            email = userData.email,
            displayName = userData.displayName,
            givenName = userData.givenName,
            familyName = userData.familyName
        )

        if (result != -1L) {
            Toast.makeText(
                this,
                "Account created! Welcome ${userData.displayName}! 🎉",
                Toast.LENGTH_SHORT
            ).show()

            // Navigate to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("USER_IDENTIFIER", userData.email)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(
                this,
                "Failed to create account. Please try again.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Hash password
     */
    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    /**
     * Clear all input fields
     */
    private fun clearFields() {
        nameEditText.text.clear()
        surnameEditText.text.clear()
        usernameEditText.text.clear()
        emailEditText.text.clear()
        studentIdEditText.text.clear()
        passwordEditText.text.clear()
    }
}