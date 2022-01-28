package com.sister.myapplication


import android.Manifest
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.webkit.WebViewCompat
import java.io.File
import android.webkit.WebView

import android.view.ViewGroup
import android.webkit.WebView.WebViewTransport

import android.widget.FrameLayout
import android.net.http.SslError

import android.webkit.SslErrorHandler
import androidx.core.content.ContextCompat.startActivity


class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var webViewPop: WebView
    private val PERMISSION_REQUEST_CODE = 200
    private var mUploadMessage: ValueCallback<Array<Uri>>? = null
    private var mCapturedImageURI: Uri? = null
    private lateinit var userAgent: String
    private val mContainer: FrameLayout? = null


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
        val url = "https://sisterskominda.eagleye.id/"
        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.settings.domStorageEnabled = true
//        webView.settings.userAgentString = getString(R.string.app_name)
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
        val webSettings = webView.settings
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setSaveFormData(true);
        webSettings.setEnableSmoothTransition(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT


// Set User Agent
        userAgent = "";
//        webView.settings.setUserAgentString(userAgent + "com.sister.myapplication");
        Log.d("hosts", webSettings.userAgentString)
        webSettings.userAgentString = "Mozilla/5.0 (Linux; Android 11; SM-A025F Build/RP1A.200720.012; wv)"

        // Enable Cookies
        CookieManager.getInstance().setAcceptCookie(true);
        if (android.os.Build.VERSION.SDK_INT >= 21)
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        webView.addJavascriptInterface(this, "sisterskominda.eagleye.id")


        val webViewPackageInfo = WebViewCompat.getCurrentWebViewPackage(this@MainActivity)
        Log.d("MY_APP_TAG", "WebView version: ${webViewPackageInfo?.versionName}")


        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//                view?.loadUrl(url.toString())
                for (i in list.indices) {
                    checkPermission(list[i], i)
                }
                val host = Uri.parse(url).host
                Log.d("hosts", host.toString())
                if (host!!.contains("m.facebook.com") || host.contains("facebook.co")
                    || host.contains("google.co")
                    || host.contains("www.facebook.com")
                    || host.contains(".google.com")
                    || host.contains(".google")
                    || host.contains("accounts.google.com/signin/oauth/consent")
                    || host.contains("accounts.youtube.com")
                    || host.contains("accounts.google.com")
                    || host.contains("accounts.google.co.in")
                    || host.contains("www.accounts.google.com")
                    || host.contains("oauth.googleusercontent.com")
                    || host.contains("content.googleapis.com")
                    || host.contains("ssl.gstatic.com")
                ) {
                    return false
                }
                view?.loadUrl(url.toString())
                // Otherwise, the link is not for a page on my site, so launch
                // another Activity that handles URLs
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                startActivity(intent)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d(
                    "url override",
                    url.toString()
                )
                super.onPageFinished(view, url)
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                // Initialize a new instance of ManagePermissions class

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.resources)
                }
            }

            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {

                webViewPop = WebView(this@MainActivity)
                webViewPop.setVerticalScrollBarEnabled(false)
                webViewPop.setHorizontalScrollBarEnabled(false)
                webViewPop.setWebViewClient(webView.webViewClient)
                webViewPop.getSettings().setJavaScriptEnabled(true)
                webViewPop.getSettings().setSavePassword(false)
                webViewPop.setLayoutParams(
                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
                mContainer?.addView(webViewPop)
                val transport = resultMsg!!.obj as WebViewTransport
                transport.webView = webViewPop
                resultMsg!!.sendToTarget()

                return true
            }
      

            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {

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

