package com.flius.guiddy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.flius.guiddy.databinding.ActivityPostUpBinding
import com.flius.guiddy.databinding.ActivitySignUp2Binding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class PostUpActivity : AppCompatActivity() {
    private lateinit var postbtn: Button
    lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    lateinit var binding: ActivityPostUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = Firebase.auth
        mDbRef = Firebase.database.reference
        postbtn= findViewById(R.id.btn_submit)

        postbtn.setOnClickListener {
            val free = binding.free.text.toString()
            val money = binding.money.text.toString().trim()
            inputdb(money, free, mAuth.currentUser?.uid!!)
        }
    }
    private fun inputdb(money: String, free: String, uId:String){
        mDbRef.child("Intro").child(uId).setValue(Intro(free))
        mDbRef.child("Pay").child(uId).setValue(Pay(money))
        Toast.makeText(this, "게시물 등록 성공", Toast.LENGTH_SHORT).show()
        val intent: Intent = Intent(this@PostUpActivity, KoMainActivity::class.java)
        startActivity(intent)
    }
}
