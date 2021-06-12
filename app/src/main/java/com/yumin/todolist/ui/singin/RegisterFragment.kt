package com.yumin.todolist.ui.singin

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.yumin.todolist.LogUtils
import com.yumin.todolist.R

class RegisterFragment: Fragment() {
    private lateinit var mFirebaseAuth: FirebaseAuth

    companion object{
        val TAG: String = RegisterFragment::class.qualifiedName.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root: View = inflater.inflate(R.layout.fragment_register,container,false)
        val back: TextView = root.findViewById(R.id.reset_back)

        back.setOnClickListener{
            findNavController().navigateUp()
        }

        val registerEmail: EditText = root.findViewById(R.id.register_email)
        val registerPassword: EditText = root.findViewById(R.id.register_password)
        val createAccount: Button = root.findViewById(R.id.create_account)

        createAccount.setOnClickListener {
            val email = registerEmail.text.toString()
            val password = registerPassword.text.toString()

            if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
                Toast.makeText(activity,R.string.email_password_empty_warning, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val progressBarDialog = progressbarDialog();
            progressBarDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            progressBarDialog.create()
            it.isEnabled = false

            activity?.apply {
                mFirebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this){ task ->
                    if (task.isSuccessful) {
                        LogUtils.logD(TAG,"[create user] success, user = "+mFirebaseAuth.currentUser.email)
                        findNavController().navigate(R.id.nav_home)
                    } else {
                        try {
                            throw task.exception!!
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            LogUtils.logD(TAG,"The email address is malformed. "+e.stackTrace)
                            Toast.makeText(activity, R.string.email_malformed_exception, Toast.LENGTH_SHORT).show()
                        } catch (e: FirebaseAuthUserCollisionException) {
                            LogUtils.logD(TAG,"The email address is already exists. "+e.stackTrace)
                            Toast.makeText(activity, R.string.email_exist_exception, Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.nav_sign_in)
                        }
                    }
                    it.isEnabled = true
                }
            }
        }
        return root
    }

    private fun progressbarDialog(): AlertDialog {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val view: View = LayoutInflater.from(activity).inflate(R.layout.dialog_progressbar, null)
        dialogBuilder.setView(view)
        return dialogBuilder.create()
    }
}