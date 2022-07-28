package tung.lab.firebaseloginemail.ui.UserProfile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.SetOptions
import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.databinding.ActivityWeightBinding
import tung.lab.firebaseloginemail.db.DBHelper

class WeightActivity : BaseActivity() {
    lateinit var binding : ActivityWeightBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeightBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initPicker()
        onClick()
    }

    fun onClick(){
        binding.apply {
            tvConfirm.setOnClickListener {
                addWeightToFS(npWeight.value)
                updateSQLWeight(npWeight.value)
                val intent = Intent(this@WeightActivity, UserProfile::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            imgBack.setOnClickListener{
                finish()
            }
        }
    }

    fun addWeightToFS(weight: Int) {
        val weight = hashMapOf(
            "weight" to weight
        )
        if (uid != null) {
            db.collection("users").document(uid)
                .set(weight, SetOptions.merge())
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }
    }

    fun updateSQLWeight(weight: Int) {
        val sqlDB = DBHelper(this@WeightActivity, null)
        val user = sqlDB.getUserDetails()
        sqlDB.updateWeight(user?.get("id").toString(),weight)
    }


    private fun initPicker() {
        binding?.npWeight?.apply {
            maxValue = 250
            minValue = 30
            wrapSelectorWheel = false
            value = 47
        }
    }
}