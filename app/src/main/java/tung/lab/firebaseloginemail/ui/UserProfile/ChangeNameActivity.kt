package tung.lab.firebaseloginemail.ui.UserProfile

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.databinding.ActivityChangeNameBinding

class ChangeNameActivity : BaseActivity() {
    lateinit var binding: ActivityChangeNameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeNameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.edtInputName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.btnConfirm.isEnabled = s.isNotEmpty()
            }
        })
        binding.edtInputName.setText(intent.getStringExtra("name"))

        onClick()
    }

    fun onClick() {
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.btnConfirm.setOnClickListener {
            addNameToFS(binding.edtInputName.text.toString())
            val intent = Intent(this@ChangeNameActivity, UserProfile::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    fun addNameToFS(name: String) {
        val name = hashMapOf(
            "name" to name
        )

        if (uid != null) {
            db.collection("users").document(uid)
                .update(name as Map<String, Any>)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }
    }
}