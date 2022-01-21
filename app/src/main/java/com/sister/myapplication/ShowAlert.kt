package com.sister.myapplication

import android.app.Activity
import android.app.AlertDialog

class ShowAlert(val activity: Activity,) {
	private fun showAlert() {
		val builder = AlertDialog.Builder(activity)
		builder.setTitle("Need permission(s)")
		builder.setMessage("Some permissions are required to do the task.")
		builder.setPositiveButton("OK", { dialog, which ->  })
		builder.setNeutralButton("Cancel", null)
		val dialog = builder.create()
		dialog.show()
	}
}