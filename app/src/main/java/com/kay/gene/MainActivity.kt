package com.kay.gene

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kay.gene.databinding.ActivityLoginBinding
import com.kay.gene.databinding.ActivityMainBinding
import com.kay.gene.util.UiUtil

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.bottomNavBar.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.bottom_menu_home-> {
                    UiUtil.showToast(this,"Home")//if tap home shows prompt saying home
                }
                R.id.bottom_menu_post_video->{
                    startActivity(Intent(this,VideoUploadActivity::class.java))

                }
                R.id.bottom_menu_profile->{
                    UiUtil.showToast(this,"Profile")
                    //Goto ProfileActivity

                }
            }
            false
        }
    }
}