package tung.lab.firebaseloginemail.ui.UserProfile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.databinding.ActivityHeightBinding

class HeightActivity : BaseActivity() {
    lateinit var binding : ActivityHeightBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHeightBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}