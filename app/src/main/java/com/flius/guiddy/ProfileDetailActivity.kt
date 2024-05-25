package com.flius.guiddy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.flius.guiddy.databinding.ActivityPostDetailBinding
import com.flius.guiddy.databinding.ActivityProfileDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ProfileDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileDetailBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mStorageRef: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference
        mStorageRef = FirebaseStorage.getInstance()

        val userId = mAuth.currentUser?.uid ?: return

        //DB접근, 해당되는 내용 찾아오기(Snapshot)
        mDbRef.child("Profile").child(userId).get().addOnSuccessListener { snapshot ->
            val nickname = snapshot.child("nickname").getValue(String::class.java)
            val region = snapshot.child("country").getValue(String::class.java)
            val gender = snapshot.child("sex").getValue(String::class.java)
            val languages = snapshot.child("language").getValue(String::class.java)
            val imageUrl = snapshot.child("profileImageUrl").getValue(String::class.java)

            binding.nicknameEdit.text = nickname
            binding.regionEdit.text = region
            binding.sexEdit.text = gender
            binding.languageEdit.text = languages


            if (imageUrl != null) {
                mStorageRef.getReferenceFromUrl(imageUrl).downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this)
                        .load(uri)
                        .into(binding.imageView)
                }.addOnFailureListener {

                }
            }
        }.addOnFailureListener {

        }

        binding.modifyBtn.setOnClickListener {
            val intent = Intent(this, ProfileModifyActivity::class.java)
            startActivity(intent)
        }
    }
}