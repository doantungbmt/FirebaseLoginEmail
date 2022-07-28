package tung.lab.firebaseloginemail.ui.UserProfile

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.google.firebase.firestore.SetOptions
import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.databinding.ActivityChangeNameBinding
import tung.lab.firebaseloginemail.db.DBHelper
import tung.lab.firebaseloginemail.model.FSUser

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
        binding.apply {
            imgBack.setOnClickListener {
                finish()
            }
            btnConfirm.setOnClickListener {
                addNameToFS(edtInputName.text.toString())
                updateNameSQLite(edtInputName.text.toString())
                val intent = Intent(this@ChangeNameActivity, UserProfile::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
        }
    }

    fun addNameToFS(name: String) {
        val name = hashMapOf(
            "name" to name
        )

        if (uid != null) {
            db.collection("users").document(uid)
                .set(name, SetOptions.merge())
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }
    }

    fun updateNameSQLite(name : String){
        val sqlDB = DBHelper(this@ChangeNameActivity, null)
        val user = sqlDB.getUserDetails()
        sqlDB.updateName(user?.get("id").toString(),name)
    }
}