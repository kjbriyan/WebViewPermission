package com.sister.myapplication

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.concurrent.schedule

class SplashActivity : AppCompatActivity() {


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_splash)
		val iv_splash = findViewById<ImageView>(R.id.iv_splash)

		iv_splash.alpha = 0f
		iv_splash.animate()
			.alpha(1f)
			.setDuration(2000)

		val intent = Intent(this, MainActivity::class.java)
		Timer("", false).schedule(2000) {
			startActivity(intent)
			finish()
		}
//		Handler().postDelayed({
//
//		}, 2000)
	}
}