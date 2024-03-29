package tung.lab.firebaseloginemail.base

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jstyle.blesdk1963.Util.BleSDK
import com.jstyle.blesdk1963.callback.DataListener2023
import com.jstyle.blesdk1963.constant.ParamKey
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import tung.lab.firebaseloginemail.R
import tung.lab.firebaseloginemail.Utils.BleData
import tung.lab.firebaseloginemail.Utils.RxBus
import tung.lab.firebaseloginemail.ble.BleManager
import tung.lab.firebaseloginemail.ble.BleService
import tung.lab.firebaseloginemail.db.DBHelper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


abstract class BaseActivity : AppCompatActivity(), DataListener2023 {
    var db = Firebase.firestore
    val user = Firebase.auth.currentUser
    val uid = user?.uid
    lateinit var auth: FirebaseAuth
    lateinit var progressBar: ProgressBar;
    val TAG = "were"

    lateinit var formattedDate : String
    lateinit var formattedTime : String
    lateinit var formattedDateTime : String

    private lateinit var subscription : Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        subscribe()
    }

    protected open fun subscribe() {
        subscription =
            RxBus.getInstance().toObservable(BleData::class.java).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe { bleData ->
                    val action = bleData.action
                    if (action == BleService.ACTION_DATA_AVAILABLE) {
                        val value = bleData.value
                        BleSDK.DataParsingWithData(value, this@BaseActivity)
                    }
                }
    }

    protected open fun unSubscribe(disposable: Disposable?) {
        if (disposable != null && !disposable.isDisposed) {
            disposable.dispose()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unSubscribe(subscription)
    }

    protected open fun sendValue(value: ByteArray?) {
        if (!BleManager.getInstance().isConnected()) {
            showToast(getString(R.string.pair_device))
            return
        }
        if (value == null) return
        BleManager.getInstance().writeValue(value)
    }

    var alertDialog : AlertDialog? = null
    protected open fun showDialogInfo(message: String?) {
        if (null == alertDialog) {
            alertDialog = AlertDialog.Builder(this)
                .setMessage(message).setPositiveButton("Ok", null).create()
            alertDialog!!.show()
        } else {
            alertDialog!!.dismiss()
            alertDialog = null
            alertDialog = AlertDialog.Builder(this)
                .setMessage(message).setPositiveButton("Ok", null).create()
            alertDialog!!.show()
        }
    }

    protected open fun getDataType(maps: Map<String, Any>): String {
        return maps[ParamKey.DataType] as String
    }

    protected open fun getData(maps: Map<String, Any>): Map<*, *>? {
        return maps[ParamKey.Data] as Map<*, *>?
    }



    protected open fun showToast(text: String?) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    fun intentToActivity (context: Context, cl : Class<*>){
        startActivity(Intent(context, cl))
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDateTime(){
        val currentDate = LocalDateTime.now()
        val formatterDate = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss")
        val formatterDateTime = DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm:ss")
        formattedDateTime = currentDate.format(formatterDateTime)
        formattedDate = currentDate.format(formatterDate)
        formattedTime = currentDate.format(formatterTime)
    }

    override fun dataCallback(maps: MutableMap<String, Any>) {

    }

    override fun dataCallback(value: ByteArray) {

    }
}