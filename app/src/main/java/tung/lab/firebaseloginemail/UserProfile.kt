package tung.lab.firebaseloginemail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.databinding.ActivityUserProfileBinding
import tung.lab.firebaseloginemail.ui.Login.SignIn
import java.sql.Date
import java.sql.Timestamp

class UserProfile : BaseActivity() {

    lateinit var binding : ActivityUserProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        getUserProfile()

        binding.btnSignOut.setOnClickListener {
            signOut()
            Toast.makeText(baseContext, "Sign out successful", Toast.LENGTH_SHORT).show()
            intent = Intent(this@UserProfile, SignIn::class.java)
            startActivity(intent)
            finish()
        }
        dataTypes()

    }
    private fun signOut() {
        Firebase.auth.signOut()
    }

    private fun addDataWithUserId() {
        // [START add_ada_lovelace]
        // Create a new user with a first and last name
        val user = hashMapOf(
            "first" to "be",
            "last" to "ccc",
            "born" to 1999
        )
        // Add a new document with a generated ID
        if (uid != null) {
            db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }
        // [END add_ada_lovelace]
    }

    private fun dataTypes() {
        // [START data_types]

        val docData = hashMapOf(
            "listExample" to arrayListOf( 2, 3),
            "dm" to "12143213",
            "ccc" to 124,
            "tung" to "bmt"
        )

        val nestedData = hashMapOf(
            "a" to 5,
            "b" to true
        )
        val docData2 = hashMapOf(
            "listExample" to arrayListOf(1, 2, 3),
            "dm" to "12143213",
            "ccc" to 124,
            "tung" to "bmt"
        )

        docData2["ccccc"] = nestedData

        docData["dm"] = docData2

        db.collection("data").document("one")
            .set(docData)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        // [END data_types]
    }

    private fun getUserProfile() {
        // [START get_user_profile]
//        val user = Firebase.auth.currentUser
        user?.let {
            // Name, email address, and profile photo Url
//            val name = user.displayName
//            val email = user.email
//            val photoUrl = user.photoUrl

            // Check if user's email is verified
//            val emailVerified = user.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
//            val uid = user.uid
            showToast(uid)
        }
        // [END get_user_profile]
    }

}