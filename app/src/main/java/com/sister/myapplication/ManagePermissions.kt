package com.sister.myapplication

import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ManagePermissions(val activity: Activity, val list: List<String>, val code:Int) {

	// Check permissions at runtime
	fun checkPermissions() {
		if (isPermissionsGranted() != PackageManager.PERMISSION_GRANTED) {
			requestPermissions()
		} else {
			Log.d("Permission","Permission Alredy Granted")
		}
	}

	// Check permissions status
	private fun isPermissionsGranted(): Int {
		var counter = 0;
		for (permission in list) {
			counter += ContextCompat.checkSelfPermission(activity, permission)
		}
		return counter
	}

	// Find the first denied permission
	private fun deniedPermission(): String {
		for (permission in list) {
			if (ContextCompat.checkSelfPermission(activity, permission)
				== PackageManager.PERMISSION_DENIED) return permission
		}
		return ""
	}

	// Request the permissions at run time
	private fun requestPermissions() {
		val permission = deniedPermission()
		if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
			// Show an explanation asynchronously
			Toast.makeText(activity, "Should show an explanation.", Toast.LENGTH_SHORT).show()
		} else {
			ActivityCompat.requestPermissions(activity, list.toTypedArray(), code)
		}
	}
}