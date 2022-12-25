package com.aykuttasil.callrecorder

import android.Manifest
import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aykuttasil.callrecord.CallRecord
import com.aykuttasil.callrecord.helper.LogUtils
import com.aykuttasil.callrecord.helper.PrefsHelper
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
    private lateinit var tvPath:TextView
    private lateinit var callRecord: CallRecord

    private val PERMISSIONS_REQUEST_CODE = 123
    val permissionList = listOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.PROCESS_OUTGOING_CALLS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvPath = findViewById(R.id.tvPath)
        LogUtils.d(Environment.getExternalStorageDirectory().absolutePath)
        callRecord = CallRecord.Builder(this)
            .setLogEnable(true)
            .setRecordFileName("")
            .setRecordDirName("CallRecorderTest")
            .setRecordDirPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath)
            .setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
            .setShowSeed(true)
            .setShowPhoneNumber(true)
            .build()

        showSavePath()
//        callRecord.startCallRecordService()
//        callRecord.changeReceiver(MyCallRecordReceiver(callRecord));

    }

    override fun onResume() {
        super.onResume()
        isServiceRunning(MyAccessibilityService::class.java).apply {
            if (!this) {
                startActivity(
                    Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
            }
        }
        checkPermissions()
    }

    fun checkPermissions() {
        if (isPermissionsGranted() != PackageManager.PERMISSION_GRANTED) {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        val permission = deniedPermission()
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            // Show an explanation asynchronously
            Toast.makeText(this, "Should show an explanation.", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                permissionList.toTypedArray(),
                PERMISSIONS_REQUEST_CODE
            )
        }
    }

    // Find the first denied permission
    private fun deniedPermission(): String {
        for (permission in permissionList) {
            if (ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_DENIED
            ) return permission
        }
        return ""
    }

    private fun isPermissionsGranted(): Int {
        // PERMISSION_GRANTED : Constant Value: 0
        // PERMISSION_DENIED : Constant Value: -1
        var counter = 0
        for (permission in permissionList) {
            counter += ContextCompat.checkSelfPermission(this, permission)
        }
        return counter
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            var result = 0
            for (item in grantResults) {
                result += item
            }
            if (result == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "權限已請求", Toast.LENGTH_SHORT).show()
            } else {
                checkPermissions()
            }
        }

    }

    fun <T> isServiceRunning(service: Class<T>) =
        (getSystemService(ACTIVITY_SERVICE) as ActivityManager).getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == service.name }

    fun StartCallRecordClick(view: View) {
        LogUtils.i(TAG, "StartCallRecordClick")
        callRecord.startCallReceiver()

        //callRecord.enableSaveFile();
        //callRecord.changeRecordDirName("NewDirName");
    }

    fun StopCallRecordClick(view: View) {
        LogUtils.i(TAG, "StopCallRecordClick")
        callRecord.stopCallReceiver()

        //callRecord.disableSaveFile();
        //callRecord.changeRecordFileName("NewFileName");
    }

    fun showSavePath() {
        tvPath.text =
        PrefsHelper.readPrefString(this, CallRecord.PREF_DIR_PATH)
            .plus(File.separator)
            .plus(PrefsHelper.readPrefString(this, CallRecord.PREF_DIR_NAME))
    }
}
