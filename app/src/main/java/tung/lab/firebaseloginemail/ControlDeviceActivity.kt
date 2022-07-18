package tung.lab.firebaseloginemail

import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.common.collect.Maps
import com.jstyle.blesdk1963.Util.BleSDK
import com.jstyle.blesdk1963.constant.ParamKey
import com.jstyle.blesdk1963.constant.ReceiveConst
import com.jstyle.blesdk1963.model.MyDeviceTime
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import tung.lab.firebaseloginemail.Utils.BleData
import tung.lab.firebaseloginemail.Utils.RxBus
import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.ble.BleManager
import tung.lab.firebaseloginemail.ble.BleService
import tung.lab.firebaseloginemail.databinding.ActivityControlDeviceBinding
import java.util.*

class ControlDeviceActivity : BaseActivity() {

    lateinit var subscription: Disposable

    lateinit var binding: ActivityControlDeviceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControlDeviceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        subscription =
            RxBus.getInstance().toObservable(BleData::class.java).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe { bleData ->
                    val action = bleData.action
                    if (action == BleService.ACTION_GATT_onDescriptorWrite) {
                        progressDialog!!.dismiss()
                    } else if (action == BleService.ACTION_GATT_DISCONNECTED) {
                        progressDialog!!.dismiss()
                    }
                }
        connectDevice()
        chooseMode()
        clickButton()
    }

    fun clickButton(){
        binding.btnDisconnect.setOnClickListener {
            BleManager.getInstance().disconnectDevice()
            finish()
        }
    }

    fun chooseMode() {
        binding.rgSetMode.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbFreeMode -> {
                    binding.edtSkip.visibility = View.GONE
                    binding.edtSecond.visibility = View.GONE
                }
                R.id.rbTimeCountdown -> {
                    binding.edtSkip.visibility = View.GONE
                    binding.edtSecond.visibility = View.VISIBLE
                }
                R.id.rbSkipCountdown -> {
                    binding.edtSkip.visibility = View.VISIBLE
                    binding.edtSecond.visibility = View.GONE
                }
            }
        }
    }

    lateinit var address: String
    private fun connectDevice() {
        address = intent?.getStringExtra("macAddress")!!
        if (TextUtils.isEmpty(address)) {
            // Log.i(TAG, "onCreate: address null ");
            return
        }
        BleManager.getInstance().connectDevice(address)
        showConnectDialog()
    }

    var progressDialog: ProgressDialog? = null

    private fun showConnectDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage(getString(R.string.connectting) +" $address")
        if (!progressDialog!!.isShowing) progressDialog!!.show()
    }

}