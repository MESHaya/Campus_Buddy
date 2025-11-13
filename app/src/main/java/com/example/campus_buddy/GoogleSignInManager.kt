package com.example.campus_buddy.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

/**
 * Manages Google Sign-In functionality
 * Handles authentication and user data retrieval
 */
class GoogleSignInManager(private val context: Context) {

    private var googleSignInClient: GoogleSignInClient

    companion object {
        private const val TAG = "GoogleSignInManager"



        private const val WEB_CLIENT_ID = "639197056183-5ri2nqe36mifb4i9c3mps36qte9tqenj.apps.googleusercontent.com"
    }

    init {
        // Configure Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)  // Request ID token for backend authentication
            .requestEmail()                  // Request user's email
            .requestProfile()                // Request user's name and profile picture
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    /**
     * Get the sign-in intent to launch
     */
    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    /**
     * Handle the sign-in result from the intent
     */
    fun handleSignInResult(data: Intent?, onSuccess: (GoogleSignInAccount) -> Unit, onFailure: (Exception) -> Unit) {
        try {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)

            if (account != null) {
                Log.d(TAG, "Sign-in successful: ${account.email}")
                onSuccess(account)
            } else {
                Log.e(TAG, "Sign-in failed: Account is null")
                onFailure(Exception("Account is null"))
            }
        } catch (e: ApiException) {
            Log.e(TAG, "Sign-in failed with code: ${e.statusCode}", e)
            onFailure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during sign-in", e)
            onFailure(e)
        }
    }

    /**
     * Check if user is already signed in
     */
    fun getLastSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    /**
     * Sign out the current user
     */
    fun signOut(onComplete: () -> Unit) {
        googleSignInClient.signOut().addOnCompleteListener {
            Log.d(TAG, "User signed out")
            onComplete()
        }
    }

    /**
     * Revoke access (complete disconnect)
     */
    fun revokeAccess(onComplete: () -> Unit) {
        googleSignInClient.revokeAccess().addOnCompleteListener {
            Log.d(TAG, "Access revoked")
            onComplete()
        }
    }

    /**
     * Extract user data from Google account
     */
    data class UserData(
        val id: String,
        val email: String,
        val displayName: String?,
        val givenName: String?,
        val familyName: String?,
        val photoUrl: String?
    )

    fun extractUserData(account: GoogleSignInAccount): UserData {
        return UserData(
            id = account.id ?: "",
            email = account.email ?: "",
            displayName = account.displayName,
            givenName = account.givenName,
            familyName = account.familyName,
            photoUrl = account.photoUrl?.toString()
        )
    }
}