package tung.lab.firebaseloginemail.ui.Login

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import tung.lab.firebaseloginemail.ScanDevice

import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.databinding.ActivityMainBinding
import tung.lab.firebaseloginemail.db.DBHelper

class SignIn : BaseActivity() {


    private lateinit var binding: ActivityMainBinding

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var spEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        askPermission()
        rememberMe()
        binding.btnSignUp.setOnClickListener {
            intentToActivity(this@SignIn, SignUp::class.java)
        }

        binding.btnSignIn.setOnClickListener {
            if (binding.edtEmail.text.trim().toString()
                    .isEmpty() || binding.edtPassWord.text.trim().toString().isEmpty()
            ) {
                Toast.makeText(this@SignIn, "Nhap du thong tin", Toast.LENGTH_SHORT).show()
            } else {
                signIn(
                    binding.edtEmail.text.trim().toString(),
                    binding.edtPassWord.text.trim().toString()
                )
            }
        }

    }

    override fun onStart() {
        super.onStart()

    }

    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
//                    val user = auth.currentUser
                    val uidLogin = Firebase.auth.currentUser?.uid
                    getUserInfo(uidLogin)
                    val emailVerified = Firebase.auth.currentUser?.isEmailVerified
                    if (emailVerified == false) {
                        intentToActivity(this@SignIn, VerifyEmailActivity::class.java)
                    } else {
                        Toast.makeText(this@SignIn, "Sign In successful", Toast.LENGTH_SHORT)
                            .show()
                        intent = Intent(this@SignIn, ScanDevice::class.java)
                        startActivity(intent)
                        Toast.makeText(this@SignIn, "Say Hi!", Toast.LENGTH_SHORT).show()
                        finish()
                    }

//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
//                    updateUI(null)
                }
            }
        // [END sign_in_with_email]
    }

    private fun updateUI(user: FirebaseUser?) {

    }

    private fun getUserProfile() {
        // [START get_user_profile]
        user?.let {
            // Check if user's email is verified
            val emailVerified = user.isEmailVerified

        }
        // [END get_user_profile]
    }

    private fun rememberMe() {
        sharedPreferences = getSharedPreferences("my_sf", MODE_PRIVATE)
        spEditor = sharedPreferences.edit()

        if (sharedPreferences.contains("checked") && sharedPreferences.getBoolean(
                "checked",
                false
            ) == true
        ) {
            binding.cbRememberMe.setChecked(true);
        } else {
            binding.cbRememberMe.setChecked(false);

        }
        binding.cbRememberMe.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (binding.cbRememberMe.isChecked()) {
                spEditor.putBoolean("checked", true)
                spEditor.apply()
            } else {
                spEditor.putBoolean("checked", false)
                spEditor.apply()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        if (binding.cbRememberMe.isChecked) {
            spEditor.apply {
                putString("email", binding.edtEmail.text.trim().toString())
                putString("password", binding.edtPassWord.text.trim().toString())
                commit()
            }
        } else {
            spEditor.apply {
                putString("email", "")
                putString("password", "")
                commit()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.edtEmail.setText(sharedPreferences.getString("email", null))
        binding.edtPassWord.setText(sharedPreferences.getString("password", null))
    }


    private fun askPermission() {
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Toast.makeText(this@SignIn, "Permission Granted", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(
                    this@SignIn,
                    "Permission Denied\n$deniedPermissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_ADVERTISE
                )
                .check();
        } else {
            TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .check();
        }

    }

    private fun getUserInfo(uidLogin: String?) {
        val docRef = uidLogin?.let { db.collection("users").document(it) }
        if (docRef != null) {
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        binding.apply {
                            val sqlDB = DBHelper(this@SignIn, null)
                            sqlDB.addUser(
                                document.getString("name"),
                                document.getLong("gender")?.toInt(),
                                document.getLong("height")?.toInt(),
                                document.getLong("weight")?.toInt(),
                                document.getString("birthday")
                            )
                        }

                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }
    }
}