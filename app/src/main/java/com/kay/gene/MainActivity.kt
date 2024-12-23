package com.kay.gene

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kay.gene.adapter.VideoListAdapter
import com.kay.gene.databinding.ActivityLoginBinding
import com.kay.gene.databinding.ActivityMainBinding
import com.kay.gene.model.VideoModel
import com.kay.gene.util.UiUtil

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var adapter : VideoListAdapter

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

        binding.bottomNavBar.setOnItemSelectedListener { menuItem->
            when(menuItem.itemId){
                R.id.bottom_menu_home-> {
                    UiUtil.showToast(this,"Home")//if tap home shows prompt saying home
                }
                R.id.bottom_menu_post_video->{
                    startActivity(Intent(this,VideoUploadActivity::class.java))

                }
                R.id.bottom_menu_profile->{
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("profile_user_id",FirebaseAuth.getInstance().currentUser?.uid)
                    startActivity(intent)

                }
            }
            false
        }
        setupViewPager()

    }

    private fun setupViewPager() {
        val options = FirestoreRecyclerOptions.Builder<VideoModel>()
            .setQuery(
                Firebase.firestore.collection("videos"),//be weary of the file path
                VideoModel::class.java
            ).build()
        adapter = VideoListAdapter(options)
        binding.viewPager.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.startListening()
    }
}