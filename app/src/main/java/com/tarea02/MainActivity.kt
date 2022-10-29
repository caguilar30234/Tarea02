package com.tarea02

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.tarea02.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase



class MainActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        //Google Sign In configuration

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("740044271366-qqo99fnqrsime4j7m304o5kkcg3qnbp4.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.googleSignIn.setOnClickListener {
            signIn()
        }

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


    //Result launched from launching the intent from GoogleSignInApi.getSignINIntent(....);
    if (requestCode == RC_SIGN_IN)
    {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            //Google sign in success then auth with Firebase
            val account = task.getResult(ApiException::class.java)!!
            Log.d(ContentValues.TAG, "firebaseAuthWithGoogle" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            //Google sign in failed, update UI
            Log.w(ContentValues.TAG, "Inicio de sesión con Google fallido", e)
        }
    }
}

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(applicationContext, GoogleSignInActivity::class.java)
            intent.putExtra(EXTRA_NAME, user.displayName)
            startActivity(intent)
        }
    }

    companion object{
        const val RC_SIGN_IN = 1001
        const val EXTRA_NAME = "EXTRA NAME"
    }
}

