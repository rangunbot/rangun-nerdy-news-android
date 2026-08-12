package de.bastkowski.rangun;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends Activity {

    private static final String URL = "http://news.digitalstep.de/";

    private WebView web;
    private SwipeRefreshLayout swipe;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        web = new WebView(this);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (swipe != null) {
                    swipe.setRefreshing(false);
                }
            }
        });

        // WebView ignores its own padding, so host it in a padded container.
        // Android 15+ (targetSdk 35) draws edge-to-edge: keep the page below the
        // system bars so the fixed header buttons stay reachable.
        FrameLayout container = new FrameLayout(this);
        container.addView(web, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        // Pull-to-refresh: swipe down at the top of the page reloads it.
        swipe = new SwipeRefreshLayout(this);
        swipe.addView(container, new SwipeRefreshLayout.LayoutParams(
                SwipeRefreshLayout.LayoutParams.MATCH_PARENT,
                SwipeRefreshLayout.LayoutParams.MATCH_PARENT));
        swipe.setOnRefreshListener(() -> web.reload());
        // Only trigger when the page itself is scrolled to the top (the direct
        // child of SwipeRefreshLayout is the non-scrollable container).
        swipe.setOnChildScrollUpCallback((parent, child) -> web.canScrollVertically(-1));
        setContentView(swipe);

        getWindow().getDecorView().setOnApplyWindowInsetsListener((v, insets) -> {
            int top, bottom;
            if (Build.VERSION.SDK_INT >= 30) {
                top = insets.getInsets(WindowInsets.Type.statusBars()).top;
                bottom = insets.getInsets(WindowInsets.Type.navigationBars()).bottom;
            } else {
                top = insets.getSystemWindowInsetTop();
                bottom = insets.getSystemWindowInsetBottom();
            }
            container.setPadding(0, top, 0, bottom);
            return insets;
        });

        web.loadUrl(URL);
    }

    @Override
    public void onBackPressed() {
        if (web != null && web.canGoBack()) {
            web.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (web != null) {
            web.destroy();
        }
        super.onDestroy();
    }
}
