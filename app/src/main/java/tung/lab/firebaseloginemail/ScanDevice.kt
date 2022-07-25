package tung.lab.firebaseloginemail

import android.Manifest
import android.R
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import tung.lab.firebaseloginemail.base.BaseActivity
import tung.lab.firebaseloginemail.databinding.ActivityScanDeviceBinding
import tung.lab.firebaseloginemail.ui.Login.SignIn
import java.util.HashMap
@RequiresApi(Build.VERSION_CODES.O)
private const val BLUETOOTH_SCAN_PERMISSION = 1
private const val BLUETOOTH_CONNECT_PERMISSION = 2
private const val REQUEST_PERMISSION_LOCATION = 3
private const val REQUEST_ENABLE_BT = 8

class ScanDevice : BaseActivity() {
    companion object {
        private val TAG = ScanDevice::class.java.simpleName
    }
    private lateinit var mBluetoothAdapter: BluetoothAdapter

    lateinit var binding: ActivityScanDeviceBinding



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanDeviceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnSignOutDevice.setOnClickListener {
            signOut()
            Toast.makeText(baseContext, "Sign out successful", Toast.LENGTH_SHORT).show()
            intent = Intent(this@ScanDevice, SignIn::class.java)
            startActivity(intent)
            finish()
        }

        initView()
        initBlueAdapter()
        registerReceivers()

//        if(checkDevice()){
//            getDevices()
//        }


    }

    private fun signOut() {
        Firebase.auth.signOut()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver)
    }

    private fun registerReceivers() {
        Log.d(TAG, "registerReceivers: ")
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(mBroadcastReceiver, filter)
    }

    private val mBroadcastReceiver = object : BroadcastReceiver() {

        private val deviceSet: MutableSet<BluetoothDevice> = mutableSetOf()

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    Log.d(TAG, "onReceive: ACTION_FOUND: $device.name, $device.address ")
                    if (deviceSet.add(device)) setListView()
                }

                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val perState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                    val nowState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                    Log.d(TAG, "ACTION_STATE_CHANGED: $perState")

                    when (nowState) {
                        BluetoothAdapter.STATE_TURNING_ON ->
                            Log.d(TAG, "ACTION_STATE_CHANGED: STATE_TURNING_ON")
                        BluetoothAdapter.STATE_ON ->
                            Log.d(TAG, "ACTION_STATE_CHANGED: STATE_ON")
                        BluetoothAdapter.STATE_TURNING_OFF ->
                            Log.d(TAG, "ACTION_STATE_CHANGED: STATE_TURNING_OFF")
                        BluetoothAdapter.STATE_OFF ->
                            Log.d(TAG, "ACTION_STATE_CHANGED: STATE_OFF")
                    }
                }

                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    runOnUiThread {
                        binding.btnScan.text = "STOP"
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }

                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    runOnUiThread {
                        binding.btnScan.text = "START"
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }

        private fun setListView() {
            val devices = mutableListOf<Map<String, String>>()
            for (d in deviceSet) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    checkPermission(Manifest.permission.BLUETOOTH_CONNECT, BLUETOOTH_CONNECT_PERMISSION)
                }
                val map = mapOf<String, String>("name" to d.name, "address" to d.address)
                devices.add(map)
            }
            val adapter = SimpleAdapter(
                this@ScanDevice,
                devices,
                R.layout.simple_list_item_2,
                listOf("name", "address").toTypedArray(),
                listOf(R.id.text1, R.id.text2).toIntArray()
            )
            runOnUiThread {
                binding.lvDevices.adapter = adapter
            }
        }
    }

    private fun initBlueAdapter() {
        Log.d(TAG, "initBlueAdapter: ")
        mBluetoothAdapter =
            (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        checkedBluetoothEnable()
    }

    private fun checkedBluetoothEnable() {
        Log.d(TAG, "checkedBluetoothEnable: ")
        if (!mBluetoothAdapter.isEnabled) {
            Log.d(TAG, "Bluetooth isn't Enable")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                checkPermission(Manifest.permission.BLUETOOTH_CONNECT, BLUETOOTH_CONNECT_PERMISSION)
            }
            startActivityForResult(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                REQUEST_ENABLE_BT
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initView() {
        binding.btnScan.setOnClickListener {
            discoveryBtDevices()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            checkPermission(Manifest.permission.BLUETOOTH_SCAN, BLUETOOTH_SCAN_PERMISSION)
        }

        binding.lvDevices.setOnItemClickListener { parent, _, position, _ ->
            if (mBluetoothAdapter.isDiscovering) mBluetoothAdapter.cancelDiscovery()
            val o: HashMap<*, *> = parent?.getItemAtPosition(position) as HashMap<*, *>
            val deviceName: String? = o["name"] as String?
            val deviceAddress: String? = o["address"] as String?

            var intent = Intent(this@ScanDevice, ControlDeviceActivity::class.java)
            intent.putExtra("macAddress", deviceAddress)
            startActivity(intent)
        }
    }

    private fun discoveryBtDevices() {
        Log.d(TAG, "discoveryBtDevices: ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            checkPermission(Manifest.permission.BLUETOOTH_SCAN, BLUETOOTH_SCAN_PERMISSION)
        }
        if (!mBluetoothAdapter.isDiscovering)
            mBluetoothAdapter.startDiscovery()
        else mBluetoothAdapter.cancelDiscovery()
    }


    // Function to check and request permission.
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@ScanDevice, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@ScanDevice, arrayOf(permission), requestCode)
        } else {
            Log.d(TAG, "Permission already granted")
//            Toast.makeText(this@ScanDevice, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == BLUETOOTH_SCAN_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@ScanDevice, "Bluetooth scan Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@ScanDevice, "Bluetooth scan Permission Denied", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == BLUETOOTH_CONNECT_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@ScanDevice, "Bluetooth connect Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@ScanDevice, "Bluetooth connect Permission Denied", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@ScanDevice, "Location Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@ScanDevice, "Location Permission Denied", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: $requestCode, $resultCode")
        if (requestCode == REQUEST_ENABLE_BT) {
            when (resultCode) {
                Activity.RESULT_OK -> Log.d(TAG, "REQUEST_ENABLE_BT: OK")
                Activity.RESULT_CANCELED -> Log.d(TAG, "REQUEST_ENABLE_BT: CANCELED")
            }
        }
    }

//    fun checkDevice() : Boolean {
//        val docRef = uid?.let { db.collection("users").document(it).collection("devices").document()}
//        if (docRef != null) {
//            return true
//        }
//        return false
//    }
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun getDevices(){
//        if (uid != null) {
//            db.collection("users").document(uid).collection("devices")
//                .get()
//                .addOnSuccessListener { result ->
//                    for (document in result) {
//                        var intent = Intent(this@ScanDevice, ControlDeviceActivity::class.java)
//                        intent.putExtra("macAddress", document.id)
//                        startActivity(intent)
//                        finish()
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    Log.d(TAG, "Error getting documents: ", exception)
//                }
//        }
//    }
}
