package com.flius.guiddy

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.flius.guiddy.databinding.ActivityChatListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatListActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatListBinding
    lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var userList: ArrayList<User>
    private lateinit var uidsList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth
        mDbRef = Firebase.database.reference
        userList = ArrayList()
        uidsList = ArrayList()
        adapter = UserAdapter(this, userList)

        binding.userRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.userRecyclerview.adapter = adapter

        // Uids에서 Ouid와 Cuid를 가져오기
        mDbRef.child("Uids").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                uidsList.clear()
                for (postSnapshot in snapshot.children) {
                    try {
                        val uids = postSnapshot.getValue(Uids::class.java)
                        if (uids != null) {
                            Log.d("ChatListActivity", "Ouid: ${uids.Ouid}, Cuid: ${uids.Cuid}")
                            uidsList.add(uids.Ouid)
                            uidsList.add(uids.Cuid)
                        } else {
                            Log.e("ChatListActivity", "Uids is null for snapshot: ${postSnapshot.key}")
                        }
                    } catch (e: DatabaseException) {
                        Log.e("ChatListActivity", "Error converting snapshot to Uids: ${e.message}")
                    }
                }
                // 중복 UID 제거
                uidsList = ArrayList(uidsList.distinct())
                Log.d("ChatListActivity", "UIDs List: $uidsList")
                fetchUsers()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatListActivity", "Error fetching Uids: ${error.message}")
            }
        })
    }

    private fun fetchUsers() {
        // 사용자 정보 가져오기
        mDbRef.child("user").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    // 나를 제외하고 Uids에 있는 사용자들만 추가
                    if (currentUser != null) {
                        Log.d("ChatListActivity", "Current User: ${currentUser.uId}")
                        if (currentUser.uId != mAuth.currentUser?.uid && uidsList.contains(currentUser.uId)) {
                            userList.add(currentUser)
                            Log.d("ChatListActivity", "User added: ${currentUser.uId}")
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatListActivity", "Error fetching users: ${error.message}")
            }
        })
    }
}
