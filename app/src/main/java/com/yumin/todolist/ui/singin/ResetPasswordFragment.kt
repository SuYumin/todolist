package com.yumin.todolist.ui.singin

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.yumin.todolist.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class ResetPasswordFragment: Fragment() {
    private lateinit var mFirebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root: View = inflater.inflate(R.layout.fragment_reset_password,container,false)

        val back: TextView = root.findViewById(R.id.reset_back)
        back.setOnClickListener { findNavController().navigateUp() }

        val resetEmail: EditText = root.findViewById(R.id.reset_email)

        val resetPasswordButton: Button = root.findViewById(R.id.reset_password)
        resetPasswordButton.setOnClickListener {
            val email = resetEmail.text.toString()

            if (email.isNullOrEmpty()) {
                Toast.makeText(activity,R.string.email_empty_warning,Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            activity?.apply {
                mFirebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(this) { task ->
                    resetPasswordButton.setText(R.string.sent_email)
                    lifecycleScope.launch{
                        delay(2000)
                        findNavController().navigate(R.id.nav_sign_in)
                    }
                }
            }
        }
        return root
    }
}

private fun Handler.postDelayed() {
    TODO("Not yet implemented")
}
