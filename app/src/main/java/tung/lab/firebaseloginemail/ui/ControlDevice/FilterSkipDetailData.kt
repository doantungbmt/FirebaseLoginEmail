package tung.lab.firebaseloginemail.ui.ControlDevice

import android.app.DatePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import tung.lab.firebaseloginemail.R
import tung.lab.firebaseloginemail.Utils.extensions.toCalendar
import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.databinding.ActivityFilterSkipDetailDataBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.log

class FilterSkipDetailData : BaseActivity() {
    lateinit var binding : ActivityFilterSkipDetailDataBinding
    var address = ""
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterSkipDetailDataBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        address = intent.getStringExtra("address").toString()
        getDateTime()
        getSkipDetailFrFireStore()
        click()
    }

    fun click(){
        binding.txtSelectDate.setOnClickListener{
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                binding.txtSelectDate.setText("" + dayOfMonth + "-" + monthOfYear + "-" + year)

            }, year, month, day)

            dpd.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getSkipDetailFrFireStore() {
        modelGetDetailSkip("FreeMode")
        modelGetDetailSkip("SkippingCountdownMode")
        modelGetDetailSkip("TimeCountdownMode")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun modelGetDetailSkip(mode: String) {
        if (uid != null) {
            db.collection("users").document(uid).collection("devices").document(address)
                .collection(mode)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val firstApiFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                        val date = LocalDate.parse(document.getString("date") , firstApiFormat)

                        Log.d("were1", date.toString())
                        binding.txtLog.append(
                            "\n" + "$mode: ${document.getString("date").toString()}, " +
                                    "Skip count: ${document.getString("skipCount")}, duration time: ${
                                        document.getString(
                                            "durationTime"
                                        )
                                    }"
                        )
                        Log.d(TAG, "$mode:  ${document.id} => ${document.data}")

                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }
        }
    }



}