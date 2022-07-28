package tung.lab.firebaseloginemail.ui.UserProfile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.SetOptions
import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.databinding.ActivityHeightBinding
import tung.lab.firebaseloginemail.db.DBHelper
import tung.lab.firebaseloginemail.model.FSUser

class HeightActivity : BaseActivity() {
    lateinit var binding : ActivityHeightBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHeightBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initPicker()
        onClick()
    }

    fun onClick(){
        binding.apply {
            tvConfirm.setOnClickListener {
                addHeightToFS(npHeight.value)
                updateSQLHeight(npHeight.value)
                val intent = Intent(this@HeightActivity, UserProfile::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            imgBack.setOnClickListener{
                finish()
            }
        }
    }

    fun addHeightToFS(height: Int) {
        val height = hashMapOf(
            "height" to height
        )

        if (uid != null) {
            db.collection("users").document(uid)
                .set(height, SetOptions.merge())
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }
    }

    fun updateSQLHeight(height: Int) {
        val sqlDB = DBHelper(this@HeightActivity, null)
        val user = sqlDB.getUserDetails()
        sqlDB.updateHeight(user?.get("id").toString(),height)
    }

    private fun initPicker() {
        binding.npHeight?.apply {
            maxValue = 250
            minValue = 100
            wrapSelectorWheel = false
            value = 150
        }
    }
}