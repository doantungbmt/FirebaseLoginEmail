package tung.lab.firebaseloginemail.ui.Login

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.databinding.ActivitySignInBinding

class SignUp : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.btnSignUp.setOnClickListener {
            if (binding.edtEmailSignUp.text.toString().trim().isEmpty()
                || binding.edtPassWordSignUp.text.toString().trim().isEmpty()
                || binding.edtConfirmPassWord.text.toString().trim().isEmpty()) {
                Toast.makeText(this@SignUp, "Nhap du thong tin", Toast.LENGTH_SHORT).show()
            }else {
                createUser(
                    binding.edtEmailSignUp.text.trim().toString(),
                    binding.edtPassWordSignUp.text.trim().toString()
                )
            }

        }


    }

    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    //updateUI(user)
                    Toast.makeText(baseContext, "Sign up successful", Toast.LENGTH_SHORT).show()
                    intentToActivity(this@SignUp, SignIn::class.java)
                    finish()
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", it.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    //updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {

    }
}