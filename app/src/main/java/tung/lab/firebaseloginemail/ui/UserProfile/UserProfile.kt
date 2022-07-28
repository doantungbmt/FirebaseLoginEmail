package tung.lab.firebaseloginemail.ui.UserProfile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import tung.lab.firebaseloginemail.R
import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.databinding.ActivityUserProfileBinding
import tung.lab.firebaseloginemail.db.DBHelper
import tung.lab.firebaseloginemail.ui.Login.SignIn
import java.text.SimpleDateFormat
import java.util.*

class UserProfile : BaseActivity() {
    lateinit var binding: ActivityUserProfileBinding
    var gender = "0"
    var weightKg = 0
    var weightLb = 0L
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        onClick()
        initView()
    }

    fun onClick() {
        binding.apply {
            llName.setOnClickListener {
                var intent = Intent(this@UserProfile, ChangeNameActivity::class.java)
                intent.putExtra("name", binding.txtName.text.toString())
                startActivity(intent)
            }
            llGender.setOnClickListener {
                var intent = Intent(this@UserProfile, ChooseGenderActivity::class.java)
                intent.putExtra("gender", gender)
                startActivity(intent)
            }

            llHeight.setOnClickListener {
                var intent = Intent(this@UserProfile, HeightActivity::class.java)
                intent.putExtra("height", binding.txtName.text.toString())
                startActivity(intent)
            }
            llWeight.setOnClickListener {
                var intent = Intent(this@UserProfile, WeightActivity::class.java)
                intent.putExtra("weight", binding.txtName.text.toString())
                startActivity(intent)
            }
            llBirthday.setOnClickListener {
                var intent = Intent(this@UserProfile, BirthdayActivity::class.java)
                intent.putExtra("birthday", binding.txtName.text.toString())
                startActivity(intent)
            }

            btnDone.setOnClickListener {
                finish()
            }
        }
    }

    fun initView() {
        getUserInfoSQLite()
    }

    fun changeWeight(){
        binding.apply {
            rgWeight.setOnCheckedChangeListener{ group, checkedId ->
                when(checkedId){
                    R.id.rbKg -> {

                    }
                    R.id.rbLb -> {

                    }
                }
            }
        }
    }

    //get UserInfo from firestore
//    private fun getUserInfo() {
//        val docRef = uid?.let { db.collection("users").document(it) }
//        if (docRef != null) {
//            docRef.get()
//                .addOnSuccessListener { document ->
//                    if (document != null) {
//                        binding.apply {
//                            txtName.text = document.getString("name")
//                            if (document.getLong("gender")?.toInt() == 1)
//                                txtGender.text = getString(R.string.male)
//                            else if (document.getLong("gender")?.toInt() == 0)
//                                txtGender.text = getString(R.string.female)
//                            else txtGender.text = ""
//                            txtWeight.text = document.getLong("weight")?.toString()
//                            txtHeight.text = document.getLong("height")?.toString()
//                            txtBirthday.text = document.getString("birthday")?.toString()
//                        }
//
//                    } else {
//                        Log.d(TAG, "No such document")
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    Log.d(TAG, "get failed with ", exception)
//                }
//        }
//    }

    //get user infor saved in sqlite
    private fun getUserInfoSQLite(){
        val sqlDB = DBHelper(this@UserProfile, null)
        val user = sqlDB.getUserDetails()
        binding.apply {
            txtName.text = user?.get("name")
            if(user?.get("gender")?.toInt() == 1){
                txtGender.text = getString(R.string.male)
                gender = "1"
            } else if(user?.get("gender")?.toInt() == 0){
                gender = "0"
                txtGender.text = getString(R.string.female)
            }
            txtHeight.text = user?.get("height")
            txtWeight.text = user?.get("weight")
            weightKg = user?.get("weight")?.toInt()!!
            weightLb = (user?.get("weight")?.toInt()!! * 2.2).toLong()
            txtBirthday.text = user?.get("birthday")
        }
    }

}