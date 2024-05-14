package com.example.mytodoapp.Activites

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mytodoapp.R
import com.example.mytodoapp.databinding.ActivitySignupBinding
import com.example.mytodoapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity(), View.OnClickListener {

    private val binding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }
    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val mDbRef by lazy {
        FirebaseDatabase.getInstance().getReference()
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
        binding.txtSignIn.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.txtSignIn -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

            R.id.btnNext -> {
                signUp()
            }
        }
    }

    private fun signUp() {

        if (TextUtils.isEmpty(binding.edtEmail.text.toString())) {
            binding.edtEmail.setError("Email is Required")
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
        if(binding.edtPassword.text.toString() != binding.edtVerifyPass.text.toString()){
            Toast.makeText(this, "password not match", Toast.LENGTH_SHORT).show()
            return
        }
        mAuth.createUserWithEmailAndPassword(
            binding.edtEmail.text.toString(),
            binding.edtPassword.text.toString()
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    addUserToDatabase()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("DATA--->", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        " ${task.exception}",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun addUserToDatabase() {

        mDbRef.child("user").child(mAuth.currentUser!!.uid).setValue(
            User(
                binding.edtEmail.text.toString(),
                binding.edtEmail.text.toString(),
                binding.edtPassword.text.toString(),
                mAuth.currentUser!!.uid
            )
        )

    }
}