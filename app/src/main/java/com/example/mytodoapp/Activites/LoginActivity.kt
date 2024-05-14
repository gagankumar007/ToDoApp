package com.example.mytodoapp.Activites

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mytodoapp.R
import com.example.mytodoapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initializer()
    }
    private fun initializer() {
        binding.btnNext.setOnClickListener(this)
        binding.txtSignUp.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.txtSignUp -> {
                startActivity(Intent(this, SignupActivity::class.java))
            }
            R.id.btnNext -> {
                login()
            }

        }
    }
    private fun login() {

        if (TextUtils.isEmpty(binding.edtEmail.text.toString())) {
            binding.edtEmail.error = "Email is Required"
            return
        }
        if (TextUtils.isEmpty(binding.edtPassword.text.toString())) {
            binding.edtPassword.error = "password is Required"
            return
        }
        if (binding.edtPassword.text.toString().length < 6) {
            binding.edtPassword.error = "password must be 6 or more Characters long"
            return
        }
        mAuth.signInWithEmailAndPassword(
            binding.edtEmail.text.toString(),
            binding.edtPassword.text.toString()
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()


                } else {
                    Log.d("DATA--->", "logInUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }

    }
}