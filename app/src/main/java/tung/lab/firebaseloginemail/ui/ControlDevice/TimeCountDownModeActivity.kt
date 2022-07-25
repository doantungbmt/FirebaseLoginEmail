package tung.lab.firebaseloginemail.ui.ControlDevice

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.jstyle.blesdk1963.Util.BleSDK
import com.jstyle.blesdk1963.constant.ParamKey
import com.jstyle.blesdk1963.constant.ReceiveConst
import tung.lab.firebaseloginemail.Utils.extensions.toCalendar
import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.databinding.ActivityCountDownModeBinding


class TimeCountDownModeActivity : BaseActivity() {
    lateinit var binding : ActivityCountDownModeBinding
    var timeCountdownInt = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountDownModeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        timeCountdownInt = intent.getStringExtra("time")?.toInt()!!
        binding.txtTimeCountDown.text = timeCountdownInt.toString()

        binding.btnStopSkip.setOnClickListener {
            sendValue(BleSDK.StartSkip(0x99, 0, 0))
        }

    }

    var durationTime = ""
    var skipCount = ""
    var todayDurationTime = ""
    var todaySkipCount = ""
    var mode = ""
    var strMode = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun dataCallback(maps: MutableMap<String, Any>) {
        super.dataCallback(maps)
        var dataType = getDataType(maps)
        var data: Map<*, *>? = getData(maps)
        if (dataType == ReceiveConst.StartSkip) {
            if (maps.containsKey(ParamKey.End)) {
                binding.txtLog.append("\n End")
                getDateTime()
                var dateTimestamp = formattedDateTime.toCalendar("dd-MM-yyy HH:mm:ss")?.time?.let { d ->
                    Timestamp(d)
                }
                if (dateTimestamp != null) {
                    addDataToFireStore(strMode ,dateTimestamp.seconds.toString(), durationTime, timeCountdownInt)
                }
                finish()
            } else {
                durationTime = maps.get(ParamKey.SkipDurationTime).toString()
                var durationTimeInt = durationTime.toInt()
                skipCount = maps.get(ParamKey.SkipCount).toString()
                todayDurationTime = maps.get(ParamKey.TodaySkipDurationTime).toString()
                todaySkipCount = maps.get(ParamKey.TodaySkipCount).toString()
                mode = maps.get(ParamKey.Mode).toString()
                strMode = ""
                when (mode) {
                    "1" -> strMode = "FreeMode"
                    "2" -> strMode = "TimeCountdownMode"
                    "3" -> strMode = "SkippingCountdownMode"
                }
                if (mode.toInt() > 0x30) {
                    Log.d(TAG, "dataCallback: ???? $mode")
                } else {
                    binding.txtLog.text =
                        "\n Mode: $strMode \n durationTime: $durationTime \n" +
                                " skipCount: $skipCount \n todayDurationTime: $todayDurationTime \n" +
                                " todaySkipCount: $todaySkipCount"
                    binding.txtTimeCountDown.text = (timeCountdownInt - durationTimeInt).toString()
                }
            }
        }
    }

    fun addDataToFireStore(typeData : String, dateTimestamp: String?, durationTime: String, targetTime : Int) {
        val dataTotalSkip = hashMapOf(
            "durationTime" to durationTime,
            "skipCount" to skipCount,
            "date" to FieldValue.serverTimestamp(),
            "targetTime" to targetTime
        )

//        val dataTotalSkip = HashMap<String, Any>()

        if (uid != null && dateTimestamp != null) {
            db.collection("users").document(uid)
                .collection("devices").document(intent.getStringExtra("address").toString())
                .collection(typeData).document(dateTimestamp)
                .set(dataTotalSkip)
        }
    }


}