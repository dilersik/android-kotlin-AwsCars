package com.dilerdesenvolv.carros.views.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.dilerdesenvolv.carros.R
import com.dilerdesenvolv.carros.domain.service.FirebaseGoogleAuthService
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginActivity : BaseActivity(), View.OnClickListener {

    private val TAG = "LoginActivity"
    //Request codes
    private val GOOGLE_LOG_IN_RC = 1
    private val FACEBOOK_LOG_IN_RC = 2
    // Google API Client object.
    private var mGoogleApiClient: GoogleApiClient? = null
    //Facebook Callback manager
    var mCallbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_google_sign_in.setOnClickListener(this)

//        // Configure Google Sign In
//        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.GOOGLE_WEB_CLIENT_ID))
//                .requestEmail()
//                .build()
//
//        // Creating and Configuring Google Api Client.
//        mGoogleApiClient = GoogleApiClient.Builder(this)
//                .enableAutoManage(this  /* OnConnectionFailedListener */) { }
//                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
//                .build()

        mGoogleApiClient = FirebaseGoogleAuthService.googleApiClient(this)

        // Facebook Login --- NEED THE KEY HASH FROM THE DEVICE
        facebookLogin()
    }

    override fun onStart() {
        super.onStart()
        this.verifyLogged()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Facebook Sign in
        mCallbackManager!!.onActivityResult(requestCode, resultCode, data)

        Log.i(TAG, "Got Result code ${requestCode}.")
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_LOG_IN_RC) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            Log.i(TAG, "With Google LogIn, is result a success? ${result.isSuccess}.")
            if (result.isSuccess) {
                // Google Sign In was successful, authenticate with Firebase
                firebaseAuthWithGoogle(result.signInAccount!!)
            } else {
                toast("Some error occurred.")
            }
        }
    }

    override fun onClick(view: View?) {
        this.showProgress()

        when (view?.id) {
            R.id.btn_google_sign_in -> {
                Log.i(TAG, "Trying Google LogIn.")
                googleLogin()
            }
        }
    }

    private fun googleLogin() {
        Log.i(TAG, "Starting Google LogIn Flow.")
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, GOOGLE_LOG_IN_RC)
    }

    private fun facebookLogin() {
        mCallbackManager = CallbackManager.Factory.create();
        btn_facebook_sign_in.setReadPermissions("email")
        // Callback registration
        btn_facebook_sign_in.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // App code
                handleFacebookAccessToken(loginResult.accessToken);
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        })
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.i(TAG, "Authenticating user with firebase.")

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            Log.i(TAG, "Firebase Authentication, is result a success? ${task.isSuccessful}.")
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                startActivity<MainActivity>()
            } else {
                longToast(getString(R.string.auth_firebase_google_failed))
            }

            this.hideProgress()
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:" + token)
        this.showProgress()

        val credential = FacebookAuthProvider.getCredential(token.token)
        mFirebaseAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        startActivity<MainActivity>()

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException())
                        longToast(getString(R.string.auth_firebase_google_failed))
                    }
                    this.hideProgress()
                }
    }

    override fun verifyLogged() {
        if (mFirebaseAuth.currentUser != null) {
            this.callMainActivity()
        }
    }

    private fun callMainActivity() {
        startActivity<MainActivity>()
        finish()
    }

    private fun showProgress() {
        progress?.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        progress?.visibility = View.INVISIBLE
    }

}
