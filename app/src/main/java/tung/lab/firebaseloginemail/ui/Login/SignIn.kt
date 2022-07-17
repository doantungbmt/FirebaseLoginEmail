package tung.lab.firebaseloginemail.ui.Login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import tung.lab.firebaseloginemail.UserProfile

import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.databinding.ActivityMainBinding

class SignIn : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        rememberMe()
        binding.btnSignUp.setOnClickListener {
            intentToActivity(this@SignIn, SignUp::class.java)
        }

        binding.btnSignIn.setOnClickListener {
            if (binding.edtEmail.text.trim().toString()
                    .isEmpty() || binding.edtPassWord.text.trim().toString().isEmpty()
            ) {
                Toast.makeText(this@SignIn, "Nhap du thong tin", Toast.LENGTH_SHORT).show()
            } else {
                signIn(binding.edtEmail.text.trim().toString(), binding.edtPassWord.text.trim().toString())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            intent = Intent(this@SignIn, UserProfile::class.java)
            startActivity(intent)
            Toast.makeText(this@SignIn, "Say Hi!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(this@SignIn, "Sign In successful", Toast.LENGTH_SHORT)
                        .show()
                    intent = Intent(this@SignIn, UserProfile::class.java)
                    startActivity(intent)
                    Toast.makeText(this@SignIn, "Say Hi!", Toast.LENGTH_SHORT).show()
                    finish()

//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    intent = Intent(this@SignIn, UserProfile::class.java)
                    startActivity(intent)
//                    updateUI(null)
                }
            }
        // [END sign_in_with_email]
    }

    private fun updateUI(user: FirebaseUser?) {

    }

    private fun rememberMe() {
        sharedPreferences = getSharedPreferences("my_sf", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        if(sharedPreferences.contains("checked") && sharedPreferences.getBoolean("checked",false) == true) {
            binding.cbRememberMe.setChecked(true);
        }else {
            binding.cbRememberMe.setChecked(false);

        }
        binding.cbRememberMe.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (binding.cbRememberMe.isChecked()) {
                editor.putBoolean("checked", true)
                editor.apply()
            } else {
                editor.putBoolean("checked", false)
                editor.apply()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        if (binding.cbRememberMe.isChecked){
            editor.apply {
                putString("email", binding.edtEmail.text.trim().toString())
                putString("password", binding.edtPassWord.text.trim().toString())
                commit()
            }
        } else {
            editor.apply {
                putString("email", "")
                putString("password", "")
                commit()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.edtEmail.setText(sharedPreferences.getString("email",null))
        binding.edtPassWord.setText(sharedPreferences.getString("password",null))
    }

}