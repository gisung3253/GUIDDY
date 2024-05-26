package com.flius.guiddy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.flius.guiddy.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var mAuth: FirebaseAuth
    private lateinit var rg_UserType: RadioGroup
    private lateinit var bt_RadioLocal: RadioButton
    private lateinit var bt_RadioForeign: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth
        rg_UserType = findViewById(R.id.rg_userType)
        bt_RadioLocal = findViewById(R.id.bt_radioLocal)
        bt_RadioForeign = findViewById(R.id.bt_radioForeign)

        binding.btLogin.setOnClickListener {
            val email = binding.emailEdit.text.toString()
            val password = binding.passwordEdit.text.toString()

            login(email, password)
        }

        binding.btGoToSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login(email: String, password: String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                val selectedUserType = rg_UserType.checkedRadioButtonId

                if(task.isSuccessful) {
                    when (selectedUserType) {
                        R.id.bt_radioLocal -> {
                            val intent = Intent(this, KoMainActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                            finish()
                        }

                        R.id.bt_radioForeign -> {
                            val intent = Intent(this, FoMainActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }else{
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                    Log.d("Login", "Error: ${task.exception}")
                }


            }
    }
}
