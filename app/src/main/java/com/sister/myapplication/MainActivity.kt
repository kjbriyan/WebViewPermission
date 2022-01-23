package com.sister.myapplication

import android.Manifest
import android.R.attr
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.webkit.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.webkit.WebViewCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import java.io.File
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.R.attr.data
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.os.*

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope


class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private val PERMISSION_REQUEST_CODE = 200
    private var mUploadMessage: ValueCallback<Array<Uri>>? = null
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
        webView.settings.userAgentString = getString(R.string.app_name)
        webView.settings.allowFileAccess = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.pluginState = WebSettings.PluginState.ON
        webView.settings.loadsImagesAutomatically = true
        webView.settings.mediaPlaybackRequiresUserGesture = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.allowUniversalAccessFromFileURLs = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.saveFormData = true
        webView.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = true



        webView.addJavascriptInterface(this, "Android")
        val url = "https://sisterskominda.eagleye.id"

        val webViewPackageInfo = WebViewCompat.getCurrentWebViewPackage(this@MainActivity)
        Log.d("MY_APP_TAG", "WebView version: ${webViewPackageInfo?.versionName}")

        val serverClientId = "426452358858-v9g4g38et8jlfaaftb03brm6kphn2fqi.apps.googleusercontent.com"
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.DRIVE_APPFOLDER))
            .requestServerAuthCode(serverClientId)
            .requestEmail()
            .build()

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
//                callback?.invoke(origin, true, false)
                super.onGeolocationPermissionsShowPrompt(origin, callback)
                val permission = Manifest.permission.ACCESS_FINE_LOCATION;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                    ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // that is you already implement, but it works only
                    // we're on SDK < 23 OR user has ALREADY granted permission
                    callback?.invoke(origin, true, false);
                }

            }


            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {

                mUploadMessage = filePathCallback;
                /**updated, out of the IF  */
                try {
                    val imageStorageDir = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "DirectoryNameHere"
                    )

                    if (!imageStorageDir.exists()) {
                        imageStorageDir.mkdirs()
                    }

                    val file = File(
                        imageStorageDir.toString() + File.separator + "IMG_" + System.currentTimeMillis()
                            .toString() + ".jpg"
                    )

                    mCapturedImageURI = Uri.fromFile(file) // save to the private variable
                    val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI)
                    // captureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    val i = Intent(Intent.ACTION_GET_CONTENT)
                    i.addCategory(Intent.CATEGORY_OPENABLE)
                    i.type = "image/*"
                    val chooserIntent = Intent.createChooser(i, "Image Chooser")
                    chooserIntent.putExtra(
                        Intent.EXTRA_INITIAL_INTENTS,
                        arrayOf<Parcelable>(captureIntent)
                    )
                    startActivityForResult(chooserIntent, PERMISSION_REQUEST_CODE)
                } catch (e: Exception) {
                    Toast.makeText(baseContext, "Camera Exception:$e", Toast.LENGTH_LONG).show()
                }
                return true
            }

        }
        shouldOverrideUrlLoading(webView, url)
//        webView.loadUrl(url)
    }

    private class OnTokenAcquired : AccountManagerCallback<Bundle> {

        override fun run(result: AccountManagerFuture<Bundle>) {
            // Get the result of the operation from the AccountManagerFuture.
            val bundle: Bundle = result.getResult()

            // The token is a named value in the bundle. The name of the value
            // is stored in the constant AccountManager.KEY_AUTHTOKEN.
            val token: String = bundle.getString(AccountManager.KEY_AUTHTOKEN).toString()
        }
    }

    fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        // When user clicks a hyperlink, load in the existing WebView
        if (url.contains("geo:")) {
            val gmmIntentUri = Uri.parse(url)
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            }
            return true
        }
        view.loadUrl(url)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != PERMISSION_REQUEST_CODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data)
                return
            }
            var results: Array<Uri>? = null
            if (resultCode == RESULT_OK) {
                if (data == null || data.data == null) {
                    // if there is not data, then we may have taken a photo
                    if (mCapturedImageURI != null) {
                        results = arrayOf(Uri.parse(mCapturedImageURI.toString()))
                    }
                } else {
                    val dataString = data.dataString
                    if (dataString != null) {
                        results = arrayOf(Uri.parse(dataString))
                    }
                }
            }
            mUploadMessage!!.onReceiveValue(results)
            mCapturedImageURI = null
        }
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
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

