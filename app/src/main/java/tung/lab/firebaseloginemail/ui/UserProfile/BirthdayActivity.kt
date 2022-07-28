package tung.lab.firebaseloginemail.ui.UserProfile

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.SetOptions
import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.databinding.ActivityBirthdayBinding
import tung.lab.firebaseloginemail.db.DBHelper
import java.text.DecimalFormat
import java.util.*

class BirthdayActivity : BaseActivity() {
    lateinit var binding : ActivityBirthdayBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBirthdayBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initPicker()
        onClick()
    }

    fun onClick(){
        binding?.apply {
            imgBack.setOnClickListener{
                finish()
            }
            tvConfirm.setOnClickListener {
                var birthday = "${npDay.displayedValues[npDay.value-1]}/${npMonth.displayedValues[npMonth.value-1]}/${npYear.displayedValues[npYear.value-1]}"
                addBirthdayToFS(birthday)
                updateBirthdaySQLite(birthday)
                val intent = Intent(this@BirthdayActivity, UserProfile::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
        }
    }

    fun addBirthdayToFS(birthday : String){
        val birthday = hashMapOf(
            "birthday" to birthday
        )
        if (uid != null) {
            db.collection("users").document(uid)
                .set(birthday, SetOptions.merge())
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }
    }

    fun updateBirthdaySQLite(birthday : String){
        val sqlDB = DBHelper(this@BirthdayActivity, null)
        val user = sqlDB.getUserDetails()
        sqlDB.updateBirthday(user?.get("id").toString(),birthday)
    }


    fun initPicker() {
        binding?.run {
            val format = DecimalFormat("00")
            val date = Calendar.getInstance()
            val dataYear = MutableList(101) {
                "${date.get(Calendar.YEAR) - 100 + it}"
            }
            val dataMonth = MutableList(12) {
                format.format(it + 1)
            }
            val dataDays = MutableList(31) {
                format.format(it + 1)
            }
            //Number Picker Year
            npYear.run year@{
                maxValue = dataYear.size
                minValue = 1
                displayedValues = dataYear.toTypedArray()
                value = maxValue
                setOnValueChangedListener { picker, _, _ ->
                    npMonth.run {
                        maxValue =
                            if (picker.value == picker.maxValue) {
                                date.get(Calendar.MONTH) + 1
                            } else {
                                12
                            }
                        value = if (value < maxValue) value else maxValue
                    }
                    npDay.run {
                        maxValue =
                            if (picker.value == picker.maxValue) date.get(Calendar.DAY_OF_MONTH) else 31
                        value = if (value < maxValue) value else maxValue
                    }
                }
            }
            // Number Picker Month
            npMonth.run {
                maxValue =
                    if (npYear.value == npYear.maxValue) date.get(Calendar.MONTH) + 1 else dataMonth.size
                minValue = 1
                displayedValues = dataMonth.toTypedArray()
                value = maxValue
                setOnValueChangedListener { picker, _, _ ->
                    val daysInMonth: Int =
                        if (picker.value == picker.maxValue && picker.maxValue == date.get(Calendar.MONTH) + 1) {
                            date.get(Calendar.DAY_OF_MONTH)
                        } else {
                            val mycal = GregorianCalendar(
                                npYear.displayedValues[npYear.value - 1].toInt(),
                                picker.value - 1,
                                1
                            )
                            mycal.getActualMaximum(Calendar.DAY_OF_MONTH)
                        }
                    npDay.maxValue = daysInMonth
                }
            }
            //Number Picker Day
            npDay.run {
                maxValue =
                    if (npYear.value == npYear.maxValue) date.get(Calendar.DAY_OF_MONTH) else dataDays.size
                minValue = 1
                displayedValues = dataDays.toTypedArray()
                value = maxValue
            }
        }
    }
}