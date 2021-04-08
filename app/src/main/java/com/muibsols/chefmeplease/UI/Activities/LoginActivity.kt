package com.muibsols.chefmeplease.UI.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.muibsols.chefmeplease.Models.Order
import com.muibsols.chefmeplease.Models.User
import com.muibsols.chefmeplease.R

class LoginActivity : AppCompatActivity() {
    private val TAG = "SignInActivity"

    private val RC_SIGN_IN: Int = 1
    lateinit var signInBT: Button
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth
    val database = Firebase.database
    val dbRef = database.getReference("Users")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initViews()
        configureGoogleSignIn()
        firebaseAuth = FirebaseAuth.getInstance()

        signInBT.setOnClickListener {
            signIn()
        }

    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun initViews() {
        signInBT = findViewById(R.id.signInBT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {
                Log.i(TAG, "onActivityResult: "+e.message)
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val fbUser = firebaseAuth.currentUser
                var orders:List<Order> = ArrayList<Order>()
                val user = User(fbUser?.displayName?:"Null",fbUser?.email?:"Null",fbUser?.uid?:"Null",fbUser?.photoUrl.toString().replace("s96-c","s400-c"),fbUser?.phoneNumber?:"Null")
                addUserToDB(user)

            } else {
                Toast.makeText(this, it.result.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val intent = Intent(this, BaseActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun addUserToDB(user:User) {
        dbRef.child(user.id).setValue(user).addOnSuccessListener {
            val intent = Intent(this, BaseActivity::class.java)
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this,it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }
}