package com.hijricalendar;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.FrameLayout;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private HijriInterface hijriInterface;
    private UpdateReceiver updateReceiver;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        getSupportActionBar().hide();

        setupRootView();
        setupWebView();
        setupJavaScriptInterface();
        setupBroadcastReceiver();
        loadContent();

        // Schedule daily update
        if (hijriInterface != null) {
            hijriInterface.scheduleUpdate(60);
        }
    }

    private void setupRootView() {
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.parseColor("#0b1120"));

        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressBar.setMinimumHeight(3);
        FrameLayout.LayoutParams pbarLayout = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, 6
        );
        progressBar.setLayoutParams(pbarLayout);
        progressBar.setBackgroundColor(Color.parseColor("#060e1a"));
        progressBar.setProgressDrawable(
                ContextCompat.getDrawable(this, android.R.drawable.progress_horizontal)
        );
        root.addView(progressBar);

        webView = new WebView(this);
        FrameLayout.LayoutParams wvLayout = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        webView.setLayoutParams(wvLayout);
        root.addView(webView);

        setContentView(root);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setAllowContentAccess(true);
        settings.setSaveFormData(false);
        settings.setSavePassword(false);

        // Cache for offline
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(getCacheDir().getAbsolutePath());
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(getFilesDir().getAbsolutePath());

        // Performance
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        }
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setBackgroundColor(Color.parseColor("#0b1120"));
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                progressBar.setVisibility(newProgress >= 100 ? View.GONE : View.VISIBLE);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                injectNotificationSupport();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
    }

    private void setupJavaScriptInterface() {
        hijriInterface = new HijriInterface(this);
        webView.addJavascriptInterface(hijriInterface, "AndroidInterface");
    }

    private void setupBroadcastReceiver() {
        updateReceiver = new UpdateReceiver();
        registerReceiver(updateReceiver, new IntentFilter("com.hijricalendar.REFRESH"));
    }

    private void loadContent() {
        webView.loadUrl("file:///android_asset/التقويم_الهجري.html");
    }

    private void injectNotificationSupport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(
                "if(typeof window.AndroidInterface !== 'undefined' && !window._androidReady){" +
                "  window._androidReady=true;" +
                "  window.notifyEvent=function(n,d,t){AndroidInterface.notifyEvent(n,d,t)};" +
                "  window.showToast=function(m){AndroidInterface.showToast(m)};" +
                "  window.savePref=function(k,v){AndroidInterface.savePreference(k,v)};" +
                "  window.getPref=function(k,d){return AndroidInterface.getPreference(k,d)};" +
                "  window.scheduleUpdate=function(m){AndroidInterface.scheduleUpdate(m)};" +
                "}", null
            );
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updateReceiver != null) {
            unregisterReceiver(updateReceiver);
        }
        if (webView != null) {
            webView.destroy();
        }
    }

    // BroadcastReceiver for scheduled updates
    public static class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null &&
                intent.getAction().equals("com.hijricalendar.REFRESH")) {
                // The WebView will be refreshed by the JavaScriptInterface
            }
        }
    }
}
