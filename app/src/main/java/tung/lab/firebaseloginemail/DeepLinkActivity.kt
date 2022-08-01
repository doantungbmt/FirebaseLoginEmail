package tung.lab.firebaseloginemail

import android.content.Intent
import android.os.Bundle
import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.ui.SplashScreen

class DeepLinkActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this,SplashScreen::class.java).apply {
            data = intent?.data
            intent?.extras?.also {
                putExtras(it)
            }
        }
        startActivity(intent)
        finish()
    }
}