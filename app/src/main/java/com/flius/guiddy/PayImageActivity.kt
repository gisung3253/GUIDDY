package com.flius.guiddy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class PayImageActivity : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etCardNumber: EditText
    private lateinit var etExpiryDate: EditText
    private lateinit var etCvv: EditText
    private lateinit var btnPay: Button

    private lateinit var Ouid: String
    lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pay_image)

        Ouid = intent.getStringExtra("Ouid").toString()
        mAuth = Firebase.auth
        mDbRef = Firebase.database.reference

        etName = findViewById(R.id.et_name)
        etEmail = findViewById(R.id.et_email)
        etCardNumber = findViewById(R.id.et_card_number)
        etExpiryDate = findViewById(R.id.et_expiry_date)
        etCvv = findViewById(R.id.et_cvv)
        btnPay = findViewById(R.id.btn_pay)

        btnPay.setOnClickListener {
            processPayment()
        }
    }

    private fun processPayment() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val cardNumber = etCardNumber.text.toString().trim()
        val expiryDate = etExpiryDate.text.toString().trim()
        val cvv = etCvv.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || cardNumber.isEmpty() || expiryDate.isEmpty() || cvv.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        addUserToDatabase(Ouid, mAuth.currentUser?.uid!!)

        // 결제 처리 로직
        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, FoMainActivity::class.java))
    }

    private fun addUserToDatabase(Ouid: String, Cuid: String) {
        val uid = mDbRef.child("Uids").push().key
        if (uid != null) {
            val uids = Uids(Ouid, Cuid)
            mDbRef.child("Uids").child(uid).setValue(uids)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("ChatListActivity", "Uids added successfully")
                    } else {
                        Log.e("ChatListActivity", "Failed to add Uids: ${task.exception?.message}")
                    }
                }
        }
    }

}
