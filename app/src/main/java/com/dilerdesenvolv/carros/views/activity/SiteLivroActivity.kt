package com.dilerdesenvolv.carros.views.activity

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.dilerdesenvolv.carros.R
import com.dilerdesenvolv.carros.extensions.setupToolbar
import com.dilerdesenvolv.carros.utils.AndroidUtils
import com.dilerdesenvolv.carros.views.activity.dialogs.AboutDialog
import kotlinx.android.synthetic.main.activity_site_livro.*

class SiteLivroActivity : BaseActivity() {

//    private val URL_SOBRE = "http://ec2-18-231-67-27.sa-east-1.compute.amazonaws.com/carros_blog/sobreWebViewAndroid"
//    private val URL_SOBRE = "http://carros-env.twpdxsvkdy.us-east-2.elasticbeanstalk.com/carros_blog_aws/sobreWebViewAndroid"
    private val URL_SOBRE = "http://carros-env.twpdxsvkdy.us-east-2.elasticbeanstalk.com/divulgue_blog/sobreWebViewAndroid"
//    lateinit var webview: WebView //
//      lateinit = pode ser null ja recebe findViewById<WebView>(R.id.webview) do kotlinx.android.synthetic.main.activity_site_livro.*
    private var mBoolLoad = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site_livro)
        // Toolbar
        setupToolbar(R.id.toolbar, getString(R.string.site_sobre_nos))
                .setDisplayHomeAsUpEnabled(true)
        setWebViewClient(webview)

        loadOrReloadURL()

        swipeToRefresh.setOnRefreshListener { loadOrReloadURL(true) }
        swipeToRefresh.setColorSchemeResources(R.color.refresh_progress_1, R.color.refresh_progress_2, R.color.refresh_progress_3)
    }

    private fun setWebViewClient(webview: WebView?) {
        webview?.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progress.visibility = if (swipeToRefresh.isRefreshing) View.INVISIBLE else View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progress.visibility = View.INVISIBLE
                swipeToRefresh.isRefreshing = false
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                if (url.endsWith("sobre.htm")) {
                    // mostra dialog
                    AboutDialog.showAbout(supportFragmentManager)

                    // Short Dialog
//                    alert(R.string.app_name, R.string.app_name) {
//                        positiveButton(R.string.ok) {  }
//                    }.show()
                }
                return super.shouldOverrideUrlLoading(view, request)
            }

            @SuppressWarnings("deprecation")
            @Override
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url.toString().endsWith("sobre.htm")) {
                    // mostra dialog
                    AboutDialog.showAbout(supportFragmentManager)
                }
                return super.shouldOverrideUrlLoading(view, url)
            }
        }
    }

    private fun loadOrReloadURL(reload: Boolean = false) {
        if (!AndroidUtils.isNetworkAvailable(this)) {
            AndroidUtils.showNoNetwork(findViewById(R.id.linearLayout))
            progress.visibility = View.INVISIBLE
            swipeToRefresh.isRefreshing = false
            return
        }
        if (!reload) {
            webview.loadUrl(URL_SOBRE)
            mBoolLoad = true
        } else {
            if (mBoolLoad) {
                webview.reload()
            } else {
                webview.loadUrl(URL_SOBRE)
            }
        }
    }

}
