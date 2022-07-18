package tung.lab.firebaseloginemail.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jstyle.blesdk1963.callback.DataListener2023



abstract class BaseActivity : AppCompatActivity(), DataListener2023 {

    val db = Firebase.firestore

    val user = Firebase.auth.currentUser
    val uid = user?.uid
    lateinit var auth: FirebaseAuth
    lateinit var progressBar: ProgressBar;
    val TAG = "were"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

    }

    protected open fun showToast(text: String?) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    fun intentToActivity (context: Context, cl : Class<*>){
        startActivity(Intent(context, cl))
    }

    override fun dataCallback(p0: MutableMap<String, Any>?) {
        TODO("Not yet implemented")
    }

    override fun dataCallback(p0: ByteArray?) {
        TODO("Not yet implemented")
    }

}