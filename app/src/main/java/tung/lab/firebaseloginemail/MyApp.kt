package tung.lab.firebaseloginemail

import android.app.Application
import tung.lab.firebaseloginemail.ble.BleManager

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        BleManager.init(this)
    }
}