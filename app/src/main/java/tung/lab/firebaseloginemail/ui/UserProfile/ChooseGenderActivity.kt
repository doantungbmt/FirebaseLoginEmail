package tung.lab.firebaseloginemail.ui.UserProfile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.databinding.ActivityChooseGenderBinding

class ChooseGenderActivity : BaseActivity() {
    lateinit var binding : ActivityChooseGenderBinding
    var gender = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseGenderBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        onClick()
    }

    fun onClick(){
        binding?.apply {
            ctrMale.setOnClickListener{
                imgMale.isSelected = true
                tvMale.isSelected = true
                imgFemale.isSelected = false
                tvFemale.isSelected = false
                gender = 1
            }
            ctrFemale.setOnClickListener {
                imgMale.isSelected = false
                tvMale.isSelected = false
                imgFemale.isSelected = true
                tvFemale.isSelected = true
                gender = 0
            }
            tvConfirm.setOnClickListener {
                addGenderToFS(gender)
                val intent = Intent(this@ChooseGenderActivity, UserProfile::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            imgBack.setOnClickListener {
                finish()
            }
        }
    }

    fun addGenderToFS(gender : Int){
        val gender = hashMapOf(
            "gender" to gender
        )

        if (uid != null) {
            db.collection("users").document(uid)
                .update(gender as Map<String, Any>)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }
    }
}