package com.ryanamaral.arykey

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ryanamaral.arykey.databinding.ActivityMainBinding
import com.ryanamaral.arykey.module.root.RootBottomSheetFragment
import com.ryanamaral.arykey.module.usb.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    @Inject lateinit var usbRepository: UsbRepository
    private var usbService: UsbService? = null
    private var mBound: Boolean = false

    private val usbConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as UsbBinder
            usbService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            usbService = null
            mBound = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listenToLiveData()
        if (savedInstanceState == null) {
            RootBottomSheetFragment().show(supportFragmentManager, "tag")
        }
    }

    override fun onNewIntent(intent: Intent?) {
        if (intent?.action == "android.hardware.usb.action.USB_DEVICE_ATTACHED") {
            //TODO: Navigate to right fragment when hardware device is plugged in
        }
        super.onNewIntent(intent)
    }

    fun finishApp() {
        stopService()
        finishAndRemoveTask()
    }

    private fun listenToLiveData() {
        usbRepository.state.observe(this, {
            if (it == UsbState.Disconnected)  {
                stopConnection()
            }
        })
        usbRepository.permission.observe(this, {
            when (it) {
                UsbPermission.Granted -> {
                    //showToast(getString(R.string.usb_permission_granted))
                    startConnection()
                }
                UsbPermission.NotGranted -> {
                    //showToast(getString(R.string.usb_permission_not_granted))
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        startService()
    }

    override fun onStop() {
        super.onStop()
        stopService()
    }

    private fun stopService() {
        if (usbRepository.isConnect.value == true) {
            stopConnection()
        }
        if (mBound) {
            unbindService(usbConnection)
            usbService = null
            mBound = false
        }
    }

    private fun startService() {
        startService(Intent(this, UsbService::class.java))
        bindService(
            Intent(this, UsbService::class.java),
            usbConnection,
            BIND_AUTO_CREATE
        )
    }

    private fun startConnection() {
        usbRepository.changeConnection(true)
    }

    private fun stopConnection() {
        usbRepository.changeConnection(false)
    }
}

fun Fragment.withMainActivity(action: MainActivity.() -> Unit) {
    (activity as? MainActivity)?.run(action)
}
