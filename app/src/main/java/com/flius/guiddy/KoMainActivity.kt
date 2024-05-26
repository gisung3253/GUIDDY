package com.flius.guiddy

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flius.guiddy.databinding.ActivityChatListBinding
import com.flius.guiddy.databinding.ActivityKoMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class KoMainActivity : AppCompatActivity(){
    lateinit var binding: ActivityKoMainBinding
    lateinit var adapter: PostListAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var postList: ArrayList<Pair<Profile, Intro>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKoMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth
        mDbRef = Firebase.database.reference
        postList = ArrayList()
        adapter = PostListAdapter(this, postList)

        // BottomNavigationView 변수 선언 및 초기화
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // BottomNavigationView의 아이템 선택 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bt_message -> {
                    // 메시지 버튼 클릭 처리
                    val intent = Intent(this, ChatListActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.bt_p -> {
                    // 프로필 버튼 클릭 처리
                    val intent = Intent(this, ProfileDetailActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.bt_logout -> {
                    // 로그아웃 버튼 클릭 처리
                    showLogoutConfirmationDialog()
                    true
                }
                else -> false
            }
        }

        // ImageButton 변수 선언 및 초기화
        val buttonIcon: ImageButton = findViewById(R.id.baseline_add)
        buttonIcon.setOnClickListener {
            val intent = Intent(this@KoMainActivity, PostUpActivity::class.java)
            startActivity(intent)
        }

        // RecyclerView 설정
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val payRef = mDbRef.child("Pay")
        payRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(paySnapshot: DataSnapshot) {
                postList.clear()
                for (payUser in paySnapshot.children) {
                    val userId = payUser.key ?: continue

                    // 프로필 정보 가져오기
                    mDbRef.child("Profile").child(userId).get().addOnSuccessListener { profileSnapshot ->
                        val profile = profileSnapshot.getValue(Profile::class.java) ?: return@addOnSuccessListener

                        // 인트로 정보 가져오기
                        mDbRef.child("Intro").child(userId).get().addOnSuccessListener { introSnapshot ->
                            val intro = introSnapshot.getValue(Intro::class.java) ?: return@addOnSuccessListener

                            postList.add(Pair(profile, intro))
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }



    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("로그아웃 확인")
            setMessage("정말로 로그아웃 하시겠습니까?")
            setPositiveButton("예") { _, _ ->
                // 로그인 화면으로 이동
                mAuth.signOut()
                val intent = Intent(this@KoMainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            setNegativeButton("아니오") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }
}
