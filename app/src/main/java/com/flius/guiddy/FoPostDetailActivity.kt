package com.flius.guiddy


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FoPostDetailActivity : AppCompatActivity() {

    private lateinit var nickname: String
    private lateinit var free: String
    private lateinit var imageUrl: String
    private lateinit var curruid: String
    private lateinit var language: String
    private lateinit var sex: String
    private lateinit var country: String
    private lateinit var money: String
    private lateinit var mDbRef: DatabaseReference
    private lateinit var btnPay: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fo_post_detail)

        //넘어온 데이터 변수에 담기
        nickname = intent.getStringExtra("nickname").toString()
        free = intent.getStringExtra("free").toString()
        imageUrl = intent.getStringExtra("image").toString()
        curruid = intent.getStringExtra("uid").toString()

        findViewById<TextView>(R.id.tv_profilename).text = nickname
        findViewById<TextView>(R.id.tv_profiledetail5).text = free
        Glide.with(this).load(imageUrl).into(findViewById(R.id.profileImage))
        mDbRef = FirebaseDatabase.getInstance().reference

        fetchProfileData(curruid)
        fetchPayData(curruid)

        btnPay = findViewById(R.id.bt_pay1)

        btnPay.setOnClickListener {
            val intent: Intent = Intent(this@FoPostDetailActivity, PayImageActivity::class.java)
            intent.putExtra("Ouid", curruid)
            startActivity(intent)
        }
    }

    private fun fetchProfileData(uid: String) {
        mDbRef.child("Profile").child(uid).get().addOnSuccessListener { dataSnapshot ->
            language = dataSnapshot.child("language").getValue(String::class.java).toString()
            sex = dataSnapshot.child("sex").getValue(String::class.java).toString()
            country = dataSnapshot.child("country").getValue(String::class.java).toString()

            // 가져온 데이터로 UI 업데이트
            findViewById<TextView>(R.id.tv_profiledetail1).text = language
            findViewById<TextView>(R.id.tv_profiledetail2).text = sex
            findViewById<TextView>(R.id.tv_profiledetail3).text = country
        }.addOnFailureListener {
            // 에러 처리
        }
    }

    private fun fetchPayData(uid: String) {
        mDbRef.child("Pay").child(uid).get().addOnSuccessListener { dataSnapshot ->
            money = dataSnapshot.child("money").getValue(String::class.java).toString()

            // 가져온 데이터로 UI 업데이트
            findViewById<TextView>(R.id.tv_pay).text = money
        }.addOnFailureListener {
            // 에러 처리
        }
    }


}