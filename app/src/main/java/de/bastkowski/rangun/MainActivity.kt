package de.bastkowski.rangun

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : Activity() {

    private lateinit var web: WebView
    private lateinit var swipe: SwipeRefreshLayout

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        web = WebView(this)
        val s: WebSettings = web.settings
        s.javaScriptEnabled = true
        s.domStorageEnabled = true
        s.loadWithOverviewMode = true
        s.useWideViewPort = true
        web.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                swipe.setRefreshing(false)
            }
        }

        // WebView ignores its own padding, so host it in a padded container.
        // Android 15+ (targetSdk 35) draws edge-to-edge: keep the page below the
        // system bars so the fixed header buttons stay reachable.
        val container = FrameLayout(this)
        container.addView(web, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT))

        // Pull-to-refresh: swipe down at the top of the page reloads it.
        swipe = SwipeRefreshLayout(this)
        swipe.addView(container, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT))
        swipe.setOnRefreshListener { web.reload() }
        // Only trigger when the page itself is scrolled to the top (the direct
        // child of SwipeRefreshLayout is the non-scrollable container).
        swipe.setOnChildScrollUpCallback { _, _ -> web.canScrollVertically(-1) }
        setContentView(swipe)

        window.decorView.setOnApplyWindowInsetsListener { _, insets ->
            val top: Int
            val bottom: Int
            if (Build.VERSION.SDK_INT >= 30) {
                top = insets.getInsets(WindowInsets.Type.statusBars()).top
                bottom = insets.getInsets(WindowInsets.Type.navigationBars()).bottom
            } else {
                top = insets.systemWindowInsetTop
                bottom = insets.systemWindowInsetBottom
            }
            container.setPadding(0, top, 0, bottom)
            insets
        }

        web.loadUrl(URL)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (web.canGoBack()) {
            web.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        web.destroy()
        super.onDestroy()
    }

    companion object {
        private const val URL = "http://news.digitalstep.de/"
    }
}
