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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class KoMainActivity : AppCompatActivity(), PersonAdapter.OnItemClickListener {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ko_main)

        // RecyclerView 설정
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PersonAdapter(getPersonList(), this)
        mAuth = Firebase.auth

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
    }

    override fun onItemClick(person: Person) {
        val intent = Intent(this, PostDetailActivity::class.java).apply {
            putExtra(PostDetailActivity.EXTRA_NAME, person.name)
            putExtra(PostDetailActivity.EXTRA_DETAILS, person.details)
        }
        startActivity(intent)
    }

    private fun getPersonList(): List<Person> {
        return listOf(
            Person("좁밥준상", "Details about John", R.drawable.guiddy_main_logo),
            Person("멸치민재", "Details about Jane", R.drawable.guiddy_main_logo),
            Person("뿡뿡이기성", "Details about Sam", R.drawable.guiddy_main_logo),
            // 추가 인물 데이터
        )
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
