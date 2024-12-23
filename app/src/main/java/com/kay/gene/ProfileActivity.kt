package com.kay.gene

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.kay.gene.adapter.ProfileVideoAdapter
import com.kay.gene.databinding.ActivityProfileBinding
import com.kay.gene.model.UserModel
import com.kay.gene.model.VideoModel

class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding
    lateinit var profileUserId: String
    lateinit var currentUserId: String
    lateinit var photoLauncher: ActivityResultLauncher<Intent>

    lateinit var adapter: ProfileVideoAdapter

    lateinit var profileUserModel: UserModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        profileUserId = intent.getStringExtra("profile_user_id")!!
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!

        photoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if(result.resultCode == RESULT_OK){
                uploadToFirestore(result.data?.data!!)//upload photo
            }
        }

        if (profileUserId == currentUserId) {
            //bring up current user profile
            binding.profileBtn.text = "Logout"
            binding.profileBtn.setOnClickListener(){
                logout()
            }
            binding.profilePic.setOnClickListener(){
                checkPermissionAndPickPhoto()
            }
        } else {
            binding.profileBtn.text = "Follow"//other profile
            binding.profileBtn.setOnClickListener()
            {
                followUnfollowUser()
            }
        }
        getProfileDatafromFirebase()
        setupRecyclerView()
    }

    fun followUnfollowUser()
    {
        Firebase.firestore.collection("users")
            .document(currentUserId)
            .get()
            .addOnSuccessListener {
                val currentUserModel = it.toObject(UserModel::class.java)!!
                if(currentUserModel.followerList.contains(currentUserId))
                {
                    profileUserModel.followerList.remove(currentUserId)//to unfollow user
                    currentUserModel.followingList.remove(profileUserId)//remoes logged in users id from persons list
                    binding.profileBtn.text = "Follow"//follow btn
                }else{
                    profileUserModel.followerList.add(currentUserId)//the opposite of above adds user to logged in users list(theres)
                    currentUserModel.followingList.add(profileUserId)//adds logged in user to id of persons(ours)
                    binding.profileBtn.text = "Unfollow"
                }

                updateUserData(profileUserModel)
                updateUserData(currentUserModel)

            }
    }


    fun updateUserData(model: UserModel)
    {
        Firebase.firestore.collection("users")
            .document(model.id)
            .set(model)
            .addOnSuccessListener {
                getProfileDatafromFirebase()
            }
    }

    fun checkPermissionAndPickPhoto()
    {
        var readExternalPhoto : String = ""
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            readExternalPhoto = android.Manifest.permission.READ_MEDIA_IMAGES
        }
        else{
            readExternalPhoto = android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if(ContextCompat.checkSelfPermission(this,readExternalPhoto) == PackageManager.PERMISSION_GRANTED){
            //we have permission
            openPhotoPicker()
        }
        else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(readExternalPhoto),
                100
            )
        }
    }

    private fun openPhotoPicker(){
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/"
        photoLauncher.launch(intent)
    }

    fun uploadToFirestore(photoUri : Uri){
        binding.progressBar.visibility = View.VISIBLE
        val photoRef = FirebaseStorage.getInstance("gs://gene-video-host.firebasestorage.app")//this line of code is what held me back for 15 hours you need the file path
            .reference
            .child("profilePic/*"+ currentUserId )
        photoRef.putFile(photoUri)
            .addOnSuccessListener {
                photoRef.downloadUrl.addOnSuccessListener { downloadUrl->
                    //video model store in firebase firestore
                    postToFirestore(downloadUrl.toString())

                }
            }
    }

    fun postToFirestore(url : String)
    {
        Firebase.firestore.collection("users")
            .document(currentUserId)
            .update("profilePic", url)
            .addOnSuccessListener {
                setUI()
            }
    }

    fun logout()
    {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this,LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun getProfileDatafromFirebase() {
        Firebase.firestore.collection("users")
            .document(profileUserId)
            .get()
            .addOnSuccessListener {
                profileUserModel = it.toObject(UserModel::class.java)!!
                setUI()
            }
    }

    fun setUI() {
        profileUserModel.apply {
            Glide.with(binding.profilePic).load(profilePic)
                .apply(RequestOptions().placeholder(R.drawable.icon_account_circle))
                .circleCrop()
                .into(binding.profilePic)
            binding.profileUsername.text ="@" + username
            if(profileUserModel.followerList.contains(currentUserId))//if statement checking if user already following and if already following then sets btn to unfollow
                binding.profileBtn.text = "Unfollow"
            binding.progressBar.visibility = View.INVISIBLE
            binding.followingCount.text = followingList.size.toString()
            binding.followerCount.text = followerList.size.toString()
            Firebase.firestore.collection("videos")
                .whereEqualTo("uploaderId", profileUserId)
                .get().addOnSuccessListener {
                    binding.postCount.text = it.size().toString()
                }

        }
    }

    fun setupRecyclerView() {
        val options = FirestoreRecyclerOptions.Builder<VideoModel>()
            .setQuery(
                Firebase.firestore.collection("videos")
                    .whereEqualTo("uploaderId", profileUserId)
                    .orderBy("createdTime", Query.Direction.DESCENDING),
                VideoModel::class.java
            ).build()
        adapter = ProfileVideoAdapter(options)
        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerView.adapter = adapter
    }
        override fun onStart() {
            super.onStart()
            adapter.startListening()
        }

     fun onDestoy() {
            super.onStop()
            adapter.stopListening()
        }


}