package com.yumin.todolist.ui.singin

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yumin.todolist.LogUtils
import com.yumin.todolist.R
import com.yumin.todolist.TodoListApplication
import com.yumin.todolist.ViewModelFactory


class SignInFragment : Fragment() {
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mCallbackManager: CallbackManager
    private lateinit var mSignInEmail: EditText
    private lateinit var mSignInPassword: EditText
    private lateinit var mEmailSignIn: Button
    private val mSignInViewModel: SignInViewModel by viewModels {
        ViewModelFactory(
            (activity?.application as TodoListApplication).roomRepository,
            (activity?.application as TodoListApplication).firebaseRepository
        )
    }
    private var mSyncData = false
    private var mSyncDataDialog: AlertDialog? = null

    companion object {
        const val RC_SIGN_IN: Int = 10000
        val TAG: String = SignInFragment::class.java.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_sign_in, container, false)
        val signInButton: SignInButton = root.findViewById(R.id.google_sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)
        signInButton.setOnClickListener {
            startGoogleSignIn()
        }

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create()
        val fbSignInButton: LoginButton = root.findViewById(R.id.fb_sign_in_button)
        fbSignInButton.fragment = this
        fbSignInButton.setReadPermissions("email", "public_profile")
        fbSignInButton.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {

            override fun onSuccess(loginResult: LoginResult) {
                LogUtils.logD(TAG, "facebook:onSuccess:$loginResult")
                handleSignInWithFacebook(loginResult.accessToken)
            }

            override fun onCancel() {
                LogUtils.logD(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                LogUtils.logE(TAG, "facebook:onError" + error)
            }
        })

        mEmailSignIn = root.findViewById(R.id.sign_in)
        mEmailSignIn.setOnClickListener {
            handleSignInWithEmail()
        }

        val register: TextView = root.findViewById(R.id.register)
        register.setOnClickListener {
            findNavController().navigate(R.id.nav_register)
        }

        val forgetPassword: TextView = root.findViewById(R.id.forgetPassword)
        forgetPassword.setOnClickListener {
            findNavController().navigate(R.id.nav_reset_password)
        }

        mSignInEmail = root.findViewById(R.id.sign_in_email)
        mSignInPassword = root.findViewById(R.id.sign_in_password)

        mSignInViewModel.fetchData()
        mSignInViewModel.missionResult.observe(viewLifecycleOwner,{
            if (it.isComplete()) {
                if (it.firebaseTodoItems?.size!! > 0 && !mSyncData) {
                    // sync firebase to room
                    mSyncDataDialog = progressbarDialog()
                    mSyncDataDialog?.show()
                    mSignInViewModel.syncFirebaseToRoom()
                    mSyncData = true
                } else {
                    findNavController().navigate(R.id.nav_home)
                }
            }
        })

        mSignInViewModel.syncResult.observe(viewLifecycleOwner,{
            if (it) {
                mSyncDataDialog?.let {
                    it.dismiss()
                }
                findNavController().navigate(R.id.nav_home)
            }
        })

        return root
    }

    private fun signInSuccess(){
        var firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseUser?.let {
            mSignInViewModel.firebaseUserExist.postValue(true)
        }
    }

    private fun progressbarDialog(): AlertDialog {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val view: View = LayoutInflater.from(activity).inflate(R.layout.dialog_progressbar, null)
        dialogBuilder.setView(view)
        var alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(false)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return alertDialog
    }

    private fun handleSignInWithEmail() {
        // use email & password
        val email = mSignInEmail.text.toString()
        val password = mSignInPassword.text.toString()

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Toast.makeText(activity, R.string.email_password_empty_warning, Toast.LENGTH_SHORT)
                .show()
            return
        }

        // add progressbar dialog
        var progressBarDialog = progressbarDialog()
        progressBarDialog.show()
        mEmailSignIn.isEnabled = false

        activity?.apply {
            mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progressBarDialog.dismiss()
                    if (task.isSuccessful) {
                        LogUtils.logD(
                            TAG,
                            "[SignInWithEmail] success, user = " + mFirebaseAuth.currentUser.email
                        )
                        signInSuccess()
//                        findNavController().navigate(R.id.nav_home)
                    } else {
                        try {
                            throw task.exception!!
                        } catch (invalidUser: FirebaseAuthInvalidUserException) {
                            LogUtils.logE(
                                TAG,
                                "[SignInWithEmail] failed : " + invalidUser.stackTrace
                            )
                            Toast.makeText(
                                activity,
                                R.string.sign_in_email_exception,
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (invalidCredentials: FirebaseAuthInvalidCredentialsException) {
                            LogUtils.logE(
                                TAG,
                                "[SignInWithEmail] failed : " + invalidCredentials.stackTrace
                            )
                            Toast.makeText(
                                activity,
                                R.string.sign_in_password_exception,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    mEmailSignIn.isEnabled = true
                }
        }
    }

    private fun handleSignInWithFacebook(accessToken: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        // add progressbar dialog
        var progressBarDialog = progressbarDialog()
        progressBarDialog.show()

        activity?.apply {
            mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    progressBarDialog.dismiss()
                    if (task.isSuccessful) {
                        LogUtils.logD(
                            TAG,
                            "[SignInWithFacebook] success , user = " + mFirebaseAuth.currentUser.email
                        )
                        signInSuccess()
//                        findNavController().navigate(R.id.nav_home)
                    } else {
                        try {
                            throw task.exception!!
                        } catch (e: FirebaseAuthInvalidUserException) {
                            LogUtils.logE(TAG, "[SignInWithFacebook] failed : " + e.stackTrace)
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            LogUtils.logE(TAG, "[SignInWithFacebook] failed : " + e.stackTrace)
                        } catch (e: FirebaseAuthUserCollisionException) {
                            LogUtils.logE(TAG, "[SignInWithFacebook] failed : " + e.stackTrace)
                        }
                    }
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseAuth = Firebase.auth

        val googleSignInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        mGoogleSignInClient = activity?.let { GoogleSignIn.getClient(it, googleSignInOptions) }


    }

    private fun startGoogleSignIn() {
        mGoogleSignInClient?.let {
            val signInIntent: Intent = it.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun handleSignInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        // add progressbar dialog
        var progressBarDialog = progressbarDialog()
        progressBarDialog.show()

        activity?.apply {
            mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    progressBarDialog.dismiss()
                    if (task.isSuccessful) {
                        Log.w(
                            TAG,
                            "[SignInWithGoogle] success , user = " + mFirebaseAuth.currentUser.email
                        )
                        signInSuccess()
                        // navigate up for update UI
//                        findNavController().navigate(R.id.nav_home)
                    } else {
                        try {
                            throw task.exception!!
                        } catch (e: FirebaseAuthInvalidUserException) {
                            LogUtils.logE(TAG, "[SignInWithGoogle] failed : " + e.stackTrace)
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            LogUtils.logE(TAG, "[SignInWithGoogle] failed : " + e.stackTrace)
                        } catch (e: FirebaseAuthUserCollisionException) {
                            LogUtils.logE(TAG, "[SignInWithGoogle] failed : " + e.stackTrace)
                        }
                    }
                }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_SIGN_IN -> {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    LogUtils.logD(TAG, "firebaseAuthWithGoogle:" + account.idToken)
                    handleSignInWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    LogUtils.logE(TAG, "Google sign in failed" + e)
                }

            }
        }
    }
}