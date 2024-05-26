package com.flius.guiddy

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.flius.guiddy.databinding.ActivitySignUp2Binding
import com.flius.guiddy.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class SignUpActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivitySignUp2Binding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var storageRef: StorageReference
    private lateinit var mDbRef: DatabaseReference
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUp2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        //초기화
        mAuth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().reference
        mDbRef = Firebase.database.reference

        binding.btAddpic.setOnClickListener {
            openFileChooser()
        }

        binding.btSignUpConfirm2.setOnClickListener {
            submitProfile()
        }
    }

    //VM 갤러리 열기
    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    //갤러리에서 이미지 선택 후 처리하는 부분 (그냥 사용해야 됨)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            binding.imageView.setImageURI(imageUri) //프로필 이미지 뷰에 띄우기
        }
    }

    private fun submitProfile() { //프로필 제출함수 내부에 DB에 저장하는 기능이 있음
        val nickname = binding.nicknameEdit.text.toString().trim()
        val country = binding.countryEdit.text.toString().trim()
        val sex = binding.sexEdit.text.toString().trim()
        val language = binding.languageEdit.text.toString().trim()

        if (nickname.isEmpty() || country.isEmpty() || language.isEmpty() || sex.isEmpty() ||imageUri == null) { //한 칸이라도 비워져 있다면
            Toast.makeText(this, "모든 칸을 채워주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = mAuth.currentUser?.uid
        if (uid != null) { //스토리지에 "Profile_image/" 만들고  UUID.randomUUID() 형식으로 집어넣기
            val fileReference = storageRef.child("Profile_image/" + UUID.randomUUID().toString())
            fileReference.putFile(imageUri!!)
                .addOnSuccessListener { //성공 시
                    fileReference.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        saveProfileToDatabase(uid, nickname, country, sex, language,  imageUrl) //DB에 정보 넘기는 파트
                        val intent: Intent = Intent(this, LoginActivity::class.java) // 화면 전환
                        startActivity(intent)
                    }
                }
                .addOnFailureListener { e -> //실패 시
                    Log.e("SignUp2", "이미지 저장 실패", e)
                    Toast.makeText(this, "이미지 저장 실패", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveProfileToDatabase(uid: String, nickname: String, country: String, sex: String, language: String, imageUrl: String) {
        val profile = Profile(nickname, country, sex ,language, imageUrl, uid)
        mDbRef.child("Profile").child(uid).setValue(profile)
            .addOnSuccessListener {
                Toast.makeText(this, "프로필 저장 성공", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("SignUp2", "프로필 저장 실패", e)
                Toast.makeText(this, "Failed to Save Profile", Toast.LENGTH_SHORT).show()
            }
    }
}