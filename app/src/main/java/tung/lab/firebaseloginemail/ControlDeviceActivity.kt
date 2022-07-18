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
        binding.btnGetTime.setOnClickListener {
            sendValue(BleSDK.GetDeviceTime())
        }
        binding.btnGetBattery.setOnClickListener {
            sendValue(BleSDK.GetDeviceBatteryLevel())
        }
        binding.btnSyncTime.setOnClickListener {
            sendValue(BleSDK.SetDeviceTime(MyDeviceTime(Calendar.getInstance())))
        }
        binding.btnGetVersion.setOnClickListener {
            sendValue(BleSDK.GetDeviceVersion())
        }
        binding.btnGetMac.setOnClickListener {
            sendValue(BleSDK.GetDeviceMacAddress())
        }

        binding.btnSkipTotalData.setOnClickListener {
            sendValue(BleSDK.GetDetailSkipData(0.toByte()))
            binding.txtLog.text = ""
        }
        binding.btnStartSkip.setOnClickListener {
            var mode: Int
            var second: Int
            var count: Int
            if (binding.rbFreeMode.isChecked) {
                mode = 0x1
                second = 0
                count = 0
                sendValue(BleSDK.StartSkip(mode, second, count))
            } else if (binding.rbTimeCountdown.isChecked) {
                mode = 0x02
                count = 0
                if (binding.edtSecond.text.trim().length == 0) {
                    Toast.makeText(this@ControlDeviceActivity, "Input time", Toast.LENGTH_SHORT).show()
                } else {
                    second = Integer.parseInt(binding.edtSecond.text.toString())
                    sendValue(BleSDK.StartSkip(mode, second, count))
                }
            } else if (binding.rbSkipCountdown.isChecked) {
                mode = 0x03
                second = 0
                if (binding.edtSkip.text.trim().length == 0) {
                    Toast.makeText(this@ControlDeviceActivity, "Input Skip", Toast.LENGTH_SHORT).show()
                } else {
                    count = (Integer.parseInt(binding.edtSkip.text.trim().toString()))/100
                    sendValue(BleSDK.StartSkip(mode, second, count))
                }
            }

        }
        binding.btnStopSkip.setOnClickListener {
            sendValue(BleSDK.StartSkip(0x99, 0, 0))
        }
        binding.btnDisconnect.setOnClickListener {
            BleManager.getInstance().disconnectDevice()
            finish()
        }
    }

    override fun dataCallback(maps: MutableMap<String, Any>) {
        super.dataCallback(maps)
        var dataType = getDataType(maps)
        var data: Map<*, *>? = getData(maps)
        when (dataType) {
            ReceiveConst.GetDeviceTime -> {
                showDialogInfo(maps.toString())
            }
            ReceiveConst.GetDeviceMacAddress -> {
                var mac = data?.get(ParamKey.MacAddress)
                showDialogInfo(mac as String?)
            }
            ReceiveConst.GetDeviceVersion -> {
                var version = data?.get(ParamKey.DeviceVersion)
                showDialogInfo(version as String?)
            }
            ReceiveConst.GetDeviceBatteryLevel -> {
                var batt = data?.get(ParamKey.BatteryLevel)
                showDialogInfo(batt as String?)
            }
            ReceiveConst.GetTotalSkipData -> {
                if (maps.containsKey(ParamKey.End)) {
                    binding.txtLog.append("\nEnd")
                } else {
                    var date = maps.get(ParamKey.Date).toString()
                    var durationTime = maps.get(ParamKey.SkipDurationTime).toString()
                    var skipCount = maps.get(ParamKey.SkipCount).toString()
                    binding.txtLog.append("\n $date === durationTime: $durationTime, skipCount: $skipCount")
                }
            }
            ReceiveConst.StartSkip -> {
                if (maps.containsKey(ParamKey.End)) {
                    binding.txtLog.append("\n End")
                } else {
                    var durationTime = maps.get(ParamKey.SkipDurationTime).toString()
                    var skipCount = maps.get(ParamKey.SkipCount).toString()
                    var todayDurationTime = maps.get(ParamKey.TodaySkipDurationTime).toString()
                    var todaySkipCount = maps.get(ParamKey.TodaySkipCount).toString()
                    var mode = maps.get(ParamKey.Mode).toString()
                    var strMode = ""
                    when (mode) {
                        "1" -> strMode = "Free mode"
                        "2" -> strMode = "Time countdown mode"
                        "3" -> strMode = "Skipping countdown mode"
                    }
                    if (mode.toInt() > 0x30) {

                    } else {
                        binding.txtLog.text = "\n Mode: $strMode \n durationTime: $durationTime \n" +
                                " skipCount: $skipCount \n todayDurationTime: $todayDurationTime \n" +
                                " todaySkipCount: $todaySkipCount"
                    }
                }
            }

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