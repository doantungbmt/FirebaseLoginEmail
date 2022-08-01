package tung.lab.firebaseloginemail.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import tung.lab.firebaseloginemail.ScanDevice
import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.ui.Login.SignIn
import tung.lab.firebaseloginemail.ui.Login.VerifyEmailActivity

class SplashScreen : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentUser = auth.currentUser
        Log.d(TAG, "onStart: ${Firebase.auth.currentUser?.isEmailVerified}")
        if (currentUser != null) {
            Firebase.auth.currentUser?.reload()?.addOnCompleteListener {
                if(it.isSuccessful){
                    if(user?.isEmailVerified == true){
                        intent = Intent(this@SplashScreen, ScanDevice::class.java)
                        startActivity(intent)
                        Toast.makeText(this@SplashScreen, "Say Hi!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        intentToActivity(this@SplashScreen, VerifyEmailActivity::class.java)
                        finish()
                    }
                }
            }
        } else {
            intentToActivity(this@SplashScreen, SignIn::class.java)
            finish()
        }
    }
}