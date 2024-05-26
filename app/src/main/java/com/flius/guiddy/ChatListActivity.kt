package com.flius.guiddy

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.flius.guiddy.databinding.ActivityChatListBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class ChatListActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatListBinding
    lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var userList: ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth
        mDbRef = Firebase.database.reference
        userList = ArrayList()
        adapter = UserAdapter(this, userList)

        binding.userRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.userRecyclerview.adapter = adapter

        //사용자 정보 가져오기
        mDbRef.child("user").addValueEventListener(object: ValueEventListener {
            // 데이터 변경때 마다 실행
            override fun onDataChange(snapshot: DataSnapshot) {
                for(postSnapshot in snapshot.children){

                    //유저 정보
                    val currentUser = postSnapshot.getValue(User::class.java)

                    // 나를 제외한 나머지 정보
                    // mAuth가 내 정보
                    if(mAuth.currentUser?.uid != currentUser?.uId){
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            // 오류 발생시 실행
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }//onCreate

}