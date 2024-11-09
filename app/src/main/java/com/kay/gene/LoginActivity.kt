package com.kay.gene

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.kay.gene.databinding.ActivityLoginBinding
import com.kay.gene.util.UiUtil

class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        FirebaseAuth.getInstance().currentUser?.let {
            //user is logged in
            startActivity(Intent(this, MainActivity::class.java))//takes user to main activity
            finish()
        }//checking

        binding.submitBtn.setOnClickListener() {
            login()
        }

        binding.goToSignupBtn.setOnClickListener(){
            startActivity(Intent(this,SignupActivity::class.java))
            finish()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.progressBar.visibility = View.VISIBLE//if prog bar visible
            binding.submitBtn.visibility = View.GONE//can see this
        } else//the opposite if button vis then prog bar not vis
        {
            binding.progressBar.visibility = View.GONE
            binding.submitBtn.visibility = View.VISIBLE
        }
    }

    fun login(){
        val email = binding.emailInput.text.toString()//getting email from email input
        val password = binding.passwordInput.text.toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())//if doesn't match email template
        {
            binding.emailInput.setError("Email not valid")
            return
        }
        if (password.length < 6) {
            binding.passwordInput.setError("Need at least 6 characters")
            return
        }
        loginWithFirebase(email, password)

    }

    fun loginWithFirebase(email : String, password : String){
        setInProgress(true)
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            email,
            password
        ).addOnSuccessListener {
            UiUtil.showToast(this, "Login sucessful")
            setInProgress(false)
            startActivity(Intent(this, MainActivity::class.java))//takes user to main activity
            finish()
        }.addOnFailureListener(){
            UiUtil.showToast(applicationContext, it.localizedMessage?: "Something went wrong")
            setInProgress(false)
        }
    }
}