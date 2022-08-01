package tung.lab.firebaseloginemail.ui.Login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.databinding.ActivitySignInBinding
import tung.lab.firebaseloginemail.db.DBHelper

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
                    val user = Firebase.auth.currentUser
                    val actionCodeSettings = actionCodeSettings {
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        url = "https://learn-firebase-4368c.firebaseapp.com"
                        // This must be true
                        handleCodeInApp = true
                        dynamicLinkDomain = "firebaseloginemail.page.link"
                        setAndroidPackageName(
                            "tung.lab.firebaseloginemail",
                            true, /* installIfNotAvailable */
                            "23" /* minimumVersion */
                        )
                    }
                    user!!.sendEmailVerification(actionCodeSettings)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                showToast("send")
                            }
                        }
                    //updateUI(user)
                    val sqlDB = DBHelper(this@SignUp, null)
                    sqlDB.addUser(null,null,null,null,null)
                    Toast.makeText(baseContext, "Sign up successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SignUp, VerifyEmailActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
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