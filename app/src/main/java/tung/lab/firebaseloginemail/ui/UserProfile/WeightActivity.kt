package tung.lab.firebaseloginemail.ui.UserProfile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.databinding.ActivityWeightBinding

class WeightActivity : BaseActivity() {
    lateinit var binding : ActivityWeightBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeightBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}