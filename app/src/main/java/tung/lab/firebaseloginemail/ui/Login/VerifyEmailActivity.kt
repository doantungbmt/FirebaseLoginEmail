package tung.lab.firebaseloginemail.ui.Login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.databinding.ActivityVerifyEmailBinding
import tung.lab.firebaseloginemail.db.DBHelper

class VerifyEmailActivity : BaseActivity() {
    lateinit var binding : ActivityVerifyEmailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyEmailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.apply {
            btnSendVerify.setOnClickListener {
                user!!.sendEmailVerification()
            }
            btnUseAnotherAccount.setOnClickListener{
                signOut()
                intentToActivity(this@VerifyEmailActivity, SignIn::class.java)
                finish()
            }
        }
    }

    private fun signOut() {
        Firebase.auth.signOut()
        ///sign out -> delete
        val sqlDB = DBHelper(this, null)
        sqlDB.deleteUser()
    }
}