package com.sister.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.webkit.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File


class MainActivity : AppCompatActivity() {
	private lateinit var webView: WebView
	private val PERMISSION_REQUEST_CODE = 200
	var filegambar: File? = null
//	var uploadMessage: ValueCallback<Array<Uri>>? = null
	private var mUploadMessage: ValueCallback<Array<Uri>>? = null
	var openCamera: ValueCallback<Array<Uri>>? = null
	private var mCapturedImageURI: Uri? = null

	@SuppressLint("SetJavaScriptEnabled", "InlinedApi", "JavascriptInterface")
	@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		// Initialize a list of required permissions to request runtime
		val list = listOf(
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.CAMERA,
			Manifest.permission.ACCESS_BACKGROUND_LOCATION,
			Manifest.permission.LOCATION_HARDWARE,
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.INSTALL_LOCATION_PROVIDER,
			Manifest.permission.INTERNET
		)


		webView = findViewById(R.id.webview)
		webView.settings.javaScriptEnabled = true
		webView.settings.allowFileAccess = true
		webView.settings.allowContentAccess = true
		webView.settings.domStorageEnabled = true
		webView.settings.userAgentString="Chrome/56.0.0.0 Mobile"
		webView.settings.allowFileAccess = true
		webView.settings.loadWithOverviewMode = true
		webView.settings.pluginState = WebSettings.PluginState.ON
		webView.settings.loadsImagesAutomatically = true
		webView.settings.mediaPlaybackRequiresUserGesture = true

		webView.addJavascriptInterface(this, "Android")
		val url = "https://sisterskominda.eagleye.id"

		webView.webViewClient = object : WebViewClient() {
			override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
				view?.loadUrl(url.toString())
				for (i in list.indices) {
					checkPermission(list[i], i)
				}
				return true
			}
		}
		webView.webChromeClient = object : WebChromeClient() {
			override fun onPermissionRequest(request: PermissionRequest) {
				// Initialize a new instance of ManagePermissions class

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					request.grant(request.resources)
				}
			}

			override fun onGeolocationPermissionsShowPrompt(
				origin: String?,
				callback: GeolocationPermissions.Callback?
			) {
				callback?.invoke(origin, true, false)
				super.onGeolocationPermissionsShowPrompt(origin, callback)
			}


			override fun onShowFileChooser(
				webView: WebView?,
				filePathCallback: ValueCallback<Array<Uri>>?,
				fileChooserParams: FileChooserParams?
			): Boolean {

				mUploadMessage = filePathCallback;
				try {

					// Create AndroidExampleFolder at sdcard
					val imageStorageDir = File(
						Environment.getExternalStoragePublicDirectory(
							Environment.DIRECTORY_PICTURES
						), "AndroidExampleFolder"
					)
					if (!imageStorageDir.exists()) {
						// Create AndroidExampleFolder at sdcard
						imageStorageDir.mkdirs()
					}

					// Create camera captured image file path and name
					var values = ContentValues()
					values.put(MediaStore.Images.Media.TITLE, "New Picture")
					values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
					mCapturedImageURI = contentResolver?.insert(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
					)
					val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
					intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI)

					val i = Intent(Intent.ACTION_GET_CONTENT)
					i.addCategory(Intent.CATEGORY_OPENABLE)
					i.type = "image/*"

					// Create file chooser intent
					val chooserIntent = Intent.createChooser(i, "Image Chooser")

					// Set camera intent to file chooser
					chooserIntent.putExtra(
						Intent.EXTRA_INITIAL_INTENTS, arrayOf<Parcelable>(intent)
					)

					// On select image call onActivityResult method of activity
					startActivityForResult(chooserIntent, PERMISSION_REQUEST_CODE)
				} catch (e: Exception) {
					Toast.makeText(
						baseContext, "Exception:$e",
						Toast.LENGTH_LONG
					).show()
				}
				//---------------------
//				if (uploadMessage != null) {
//					uploadMessage!!.onReceiveValue(null)
//					uploadMessage = null
//				}
//				uploadMessage = filePathCallback
//
//				val captureIntent = Intent(
//					MediaStore.ACTION_IMAGE_CAPTURE
//				);
//
//				captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, "Image url");
//
//				val i = Intent(Intent.ACTION_GET_CONTENT)
//				val choserIntent = Intent.createChooser(i, "File Chooser")
//				choserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
//					, object : Parcelable[]{captureIntent});
//				choserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, captureIntent)
//				Log.d("filechoss", intent.type.toString())
//				i.addCategory(Intent.CATEGORY_OPENABLE)
//				i.type = "image/*"
//				this@MainActivity.startActivityForResult(
//					Intent.createChooser(i,"File Chooser"),
//					PERMISSION_REQUEST_CODE
//				)


//				if (uploadMessage != null) {
//					uploadMessage!!.onReceiveValue(null)
//					uploadMessage = null
//				}
//
//				uploadMessage = filePathCallback
//
//				val intent = fileChooserParams!!.createIntent()
//				Log.d("filechoss",intent.toString())
//				try {
//					startActivityForResult(intent, PERMISSION_REQUEST_CODE)
//				} catch (e: ActivityNotFoundException) {
//					uploadMessage = null
//					Toast.makeText(this@MainActivity, "Cannot open file chooser", Toast.LENGTH_LONG).show()
//					return false
//				}

				return true
			}

		}
		webView.loadUrl(url)
	}

	@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
	@SuppressLint("MissingSuperCall")
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//		if (requestCode == PERMISSION_REQUEST_CODE) {
//			if (mUploadMessage == null) return;
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//				mUploadMessage!!.onReceiveValue(
//					WebChromeClient.FileChooserParams.parseResult(
//						resultCode,
//						data
//					)
//				)
//			};
//			mUploadMessage = null;
//		}
		if (requestCode == PERMISSION_REQUEST_CODE) {
			if (null == mUploadMessage) {
				return
			}
			var results: Array<Uri>? = null
			if (resultCode != RESULT_OK){
				results = null
			}else{
				if (intent == null)mCapturedImageURI else intent.data
			}
			mUploadMessage!!.onReceiveValue(results)
			mUploadMessage = null
		}
	}

	private fun checkPermission(permission: String, requestCode: Int) {
		if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
			// Requesting the permission
			ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
		} else {
//			Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
		}
	}

	override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
		if (event!!.action === KeyEvent.ACTION_DOWN) {
			when (keyCode) {
				KeyEvent.KEYCODE_BACK -> {
					if (webView.canGoBack()) {
						webView.goBack()
					} else {
						finish()
					}
					return true
				}
			}
		}
		return super.onKeyDown(keyCode, event)
	}

}

