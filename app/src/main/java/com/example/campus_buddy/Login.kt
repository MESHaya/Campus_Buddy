package com.example.campus_buddy

import android.content.Intent
import android.os.Bundle
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

class Login : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var googleSignInManager: GoogleSignInManager

    // Activity Result Launcher for Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Handle the sign-in result
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
        setContentView(R.layout.activity_login)

        // Initialize
        dbHelper = DatabaseHelper(this)
        googleSignInManager = GoogleSignInManager(this)

        // Check if already signed in with Google
        val existingAccount = googleSignInManager.getLastSignedInAccount()
        if (existingAccount != null) {
            // User already signed in, auto-login
            handleGoogleSignInSuccess(existingAccount)
            return
        }

        // UI elements
        val usernameInput = findViewById<EditText>(R.id.etUsername)
        val passwordInput = findViewById<EditText>(R.id.etPassword)
        val loginBTN = findViewById<Button>(R.id.btnLogin)
        val googleBTN = findViewById<Button>(R.id.btnGoogleLogin)

        // Traditional login
        loginBTN.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val hashedPassword = hashPassword(password)

            if (checkUser(username, hashedPassword)) {
                Toast.makeText(this, "Login successful! ✅", Toast.LENGTH_SHORT).show()
                navigateToMain(username)
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }

        // Google Sign-In
        googleBTN.setOnClickListener {
            startGoogleSignIn()
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

        // Check if user exists in database
        if (dbHelper.emailExists(userData.email)) {
            // Existing user - just login
            Toast.makeText(
                this,
                "Welcome back, ${userData.displayName}! 👋",
                Toast.LENGTH_SHORT
            ).show()
            navigateToMain(userData.email)
        } else {
            // New user - create account
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
                navigateToMain(userData.email)
            } else {
                Toast.makeText(
                    this,
                    "Failed to create account. Please try again.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Navigate to MainActivity
     */
    private fun navigateToMain(identifier: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("USER_IDENTIFIER", identifier)
        startActivity(intent)
        finish()
    }

    /**
     * Check traditional login credentials
     */
    private fun checkUser(username: String, password: String): Boolean {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM User WHERE username = ? AND password = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    /**
     * Hash password for traditional login
     */
    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
}