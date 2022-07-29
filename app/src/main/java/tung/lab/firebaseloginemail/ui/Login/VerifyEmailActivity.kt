package tung.lab.firebaseloginemail.ui.Login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.databinding.ActivityVerifyEmailBinding

class VerifyEmailActivity : BaseActivity() {
    lateinit var binding : ActivityVerifyEmailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyEmailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnSendVerify.setOnClickListener {
            user!!.sendEmailVerification()
        }
    }
}