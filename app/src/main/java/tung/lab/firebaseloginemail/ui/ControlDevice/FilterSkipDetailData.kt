package tung.lab.firebaseloginemail.ui.ControlDevice

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.datepicker.MaterialDatePicker
import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.databinding.ActivityFilterSkipDetailDataBinding
import tung.lab.firebaseloginemail.model.DetailDataModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class FilterSkipDetailData : BaseActivity() {
    lateinit var binding : ActivityFilterSkipDetailDataBinding
    var address = ""
    var listDetailDataModel : MutableList<DetailDataModel> = mutableListOf()
    var detailDataModel = DetailDataModel()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterSkipDetailDataBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        address = intent.getStringExtra("address").toString()
        getDateTime()

        click()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun click(){
        binding.txtSelectDate.setOnClickListener{
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(supportFragmentManager, "DatePicker")

            // Setting up the event for when ok is clicked
            datePicker.addOnPositiveButtonClickListener {
                // formatting date in dd-mm-yyyy format.
                val dateFormatter = SimpleDateFormat("dd-MM-yyyy")
                val date = dateFormatter.format(Date(it))
                binding.txtSelectDate.text = date
                binding.txtLog.text = ""
                getSkipDetailFrFireStore(date)

            }

            // Setting up the event for when cancelled is clicked
            datePicker.addOnNegativeButtonClickListener {
                Toast.makeText(this, "${datePicker.headerText} is cancelled", Toast.LENGTH_LONG).show()
            }

            // Setting up the event for when back button is pressed
            datePicker.addOnCancelListener {
                Toast.makeText(this, "Date Picker Cancelled", Toast.LENGTH_LONG).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getSkipDetailFrFireStore(date : String) {
        modelGetDetailSkip("FreeMode", date)
        modelGetDetailSkip("SkippingCountdownMode", date)
        modelGetDetailSkip("TimeCountdownMode", date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun modelGetDetailSkip(mode: String, date: String) {
        if (uid != null) {
            db.collection("users").document(uid).collection("devices").document(address)
                .collection(mode)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val dateGetFromFS = SimpleDateFormat("dd-MM-yyyy").parse(document.getString("date")) as Date
                        val dateForFilter = SimpleDateFormat("dd-MM-yyyy").format(dateGetFromFS)

                        var duarationTime = document.getString("durationTime")?.toInt()
                        var skipCount = document.getString("skipCount")?.toInt()
                        var stepPerMin = (60 * skipCount!!) / duarationTime!!

                        if(dateForFilter.compareTo(date) == 0){
                            binding.txtLog.append(
                                "\n" + "$mode: ${document.getString("date").toString()}, " +
                                        "Skip count: $skipCount, duration time: $duarationTime, Step per min: $stepPerMin"
                            )
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }
        }
    }



}

