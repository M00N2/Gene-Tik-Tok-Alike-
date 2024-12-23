package com.kay.gene

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.kay.gene.databinding.ActivitySignupBinding
import com.kay.gene.model.UserModel
import com.kay.gene.util.UiUtil
import com.google.firebase.firestore.ktx.firestore
import android.widget.Toast

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.submitBtn.setOnClickListener() {
            signup()
        }

        binding.goToLoginBtn.setOnClickListener(){
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }//everything after this//maybe needs to go last? //

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

    fun signup() {
        val email = binding.emailInput.text.toString()//getting email from email input
        val password = binding.passwordInput.text.toString()
        val confirmPassword = binding.confirmPasswordInput.text.toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())//if doesn't match email template
        {
            binding.emailInput.setError("Email not valid")
            return;
        }
        if (password.length < 6) {
            binding.passwordInput.setError("Need at least 6 characters")
            return
        }
        if (password != confirmPassword) {
            binding.confirmPasswordInput.setError("Passwords don't match")
            return
        }//if it reaches here all conditions have been met
        signupWithFireBase(email, password)

    }

    fun signupWithFireBase(email: String, password: String) {
        setInProgress(true)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            email, password
        ).addOnSuccessListener {
            it.user?.let {user->
                val userModel = UserModel(user.uid, email, email.substringBefore("@"))
                Firebase.firestore.collection("users")//creating users collection
                    .document(user.uid)
                    .set(userModel).addOnSuccessListener {
                        UiUtil.showToast(applicationContext, "Account created successfully")
                        setInProgress(false)
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
            }
        }.addOnFailureListener {
            UiUtil.showToast(applicationContext, it.localizedMessage?: "Something went wrong")
            setInProgress(false)
        }
    }
}