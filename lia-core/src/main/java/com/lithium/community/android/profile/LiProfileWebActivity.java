package com.lithium.community.android.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.lithium.community.android.R;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.utils.LiCoreSDKConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class LiProfileWebActivity extends AppCompatActivity {

    public static String CLOSE_ACCOUNT_KEY = "CLOSE_ACCOUNT_KEY";
    public static String HTACCESS_USERNAME = "HTACCESS_USERNAME";
    public static String HTACCESS_PASSWORD = "HTACCESS_PASSWORD";
    public static String COMMUNITY_COOKIES = "COMMUNITY_COOKIES";

    private ProgressBar progressBar;
    private WebView webView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.li_profile_web_activity);

        Toolbar toolbar = findViewById(R.id.li_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        LiSDKManager manager = LiSDKManager.getInstance();
        if (manager != null) {

            webView = findViewById(R.id.li_profile_web);
            progressBar = findViewById(R.id.li_profile_page_prog_bar);

            progressBar.setVisibility(View.VISIBLE);
            webView.setVisibility(View.VISIBLE);

            webView.clearHistory();
            webView.clearFormData();
            webView.clearCache(true);

            Uri communityUri = manager.getCredentials().getCommunityUri();
            if (communityUri != null) {
                String communityDomain = communityUri.toString();
                String url = communityDomain + "/t5/user/myprofilepage/tab/personal-profile";
                setTitle(url);

                //set the web client
                webView.setWebViewClient(new ProfileWebViewClient());
                webView.setWebChromeClient(new ProfileWebChromeClient());

                String communityCookies = LiSDKManager.getInstance().getFromSecuredPreferences(this, LiProfileWebActivity.COMMUNITY_COOKIES);
                CookieManager.getInstance().setCookie(communityDomain, communityCookies);

                //activates JavaScript (just in case)
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
                webSettings.setAppCacheEnabled(false);
                webSettings.setAllowContentAccess(true);
                webSettings.setAllowFileAccess(true);
                webSettings.setAllowFileAccessFromFileURLs(true);
                webSettings.setAllowUniversalAccessFromFileURLs(true);
                webSettings.setBuiltInZoomControls(true);
                webSettings.setDomStorageEnabled(true);
                webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                webSettings.setSupportZoom(true);
                webSettings.setUserAgentString(String.format("%s : %s", getString(R.string.app_name), System.getProperty("http.agent")));

                webView.addJavascriptInterface(new WebAppInterface(), "Android");

                //load the url of the oAuth login page
                webView.loadUrl(url);
            } else {
                Log.e("LiProfileWebActivity", "Profile page url is null.");
            }
        }
    }

    private class ProfileWebChromeClient extends WebChromeClient {
        public void onProgressChanged(WebView view, int progress) {
            progressBar.setProgress(progress);
        }
    }

    private class ProfileWebViewClient extends WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Log.e(LiCoreSDKConstants.LI_LOG_TAG, "SSL Error opening profile webpage");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
            progressBar.setProgress(100);
            String scriptjs = "javascript:(function f() {"
                    + "var open = XMLHttpRequest.prototype.open;"
                    + "XMLHttpRequest.prototype.open = function() {"
                    + "    this.addEventListener(\"load\", function() {"
                    + "        var message = {\"status\" : this.status, \"responseURL\" : this.responseURL, \"responseText\" : this.responseText};"
                    + "        window.Android.postMessage(JSON.stringify(message));"
                    + "    });"
                    + "    open.apply(this, arguments);"
                    + "};"
                    + "})();";
            webView.loadUrl(scriptjs);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            Uri communityUri = LiSDKManager.getInstance().getCredentials().getCommunityUri();
            if(communityUri != null && host.equalsIgnoreCase(communityUri.getHost())) {
                String htaccessUsername = LiSDKManager.getInstance().getFromSecuredPreferences(getApplicationContext(), LiProfileWebActivity.HTACCESS_USERNAME);
                String htaccessPassword = LiSDKManager.getInstance().getFromSecuredPreferences(getApplicationContext(), LiProfileWebActivity.HTACCESS_PASSWORD);
                handler.proceed(htaccessUsername, htaccessPassword);
            } else {
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
            }
        }

    }
    public class WebAppInterface {

        WebAppInterface() {
        }

        @JavascriptInterface
        public void postMessage(String message) throws JSONException {
            JSONObject obj = new JSONObject(message);
            String responseURL = obj.getString("responseURL");
            if (responseURL != null && responseURL.contains("closeuseraccountform")) {
                String responseText = obj.getString("responseText");
                JSONObject responseTextObj = new JSONObject(responseText);
                String responseState = responseTextObj.getJSONObject("response").getString("state");
                if ("success".equalsIgnoreCase(responseState)) {
                    Intent intent = new Intent();
                    intent.putExtra(CLOSE_ACCOUNT_KEY, "success");
                    setResult(Activity.RESULT_OK, intent);
                    LiSDKManager.getInstance().removeFromSecuredPreferences(getApplicationContext(), LiProfileWebActivity.COMMUNITY_COOKIES);
                    finish();
                }
            }
        }
    }
}
