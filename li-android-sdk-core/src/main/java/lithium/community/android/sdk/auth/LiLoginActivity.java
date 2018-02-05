/*
 * Copyright 2018 Lithium Technologies Pvt Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lithium.community.android.sdk.auth;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import lithium.community.android.sdk.R;
import lithium.community.android.sdk.utils.LiCoreSDKConstants;
import lithium.community.android.sdk.utils.LiCoreSDKUtils;

/**
 * Activity that receives the redirect Uri sent by the OpenID endpoint. This activity gets launched
 * when the user approves the app for use and it starts the {@link PendingIntent} given in
 * {@link LiAuthService#performAuthorizationRequest}.
 * <p>App developers using this library <em>must</em> to register this activity in the manifest
 * with one intent filter for each redirect URI they are intending to use.
 * </p>
 */
@SuppressLint("Registered")
public class LiLoginActivity extends LiBaseAuthActivity {
    boolean isAccessTokenSaved = false;
    private ProgressBar progBar;
    private WebView webViewOauth;
    private TextView loginInProgTxt;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);

        setContentView(R.layout.li_login_activity);
        Intent intent = getIntent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.li_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        service = new LiAuthServiceImpl(this);

        webViewOauth = (WebView) findViewById(R.id.li_web_oauth);
        progBar = (ProgressBar) findViewById(R.id.li_login_page_prog_bar);
        loginInProgTxt = (TextView) findViewById(R.id.li_login_in_prog_txt);
        String authUrl = intent.getData().toString();
        progBar.setVisibility(View.VISIBLE);
        webViewOauth.setVisibility(View.INVISIBLE);
        loginInProgTxt.setText(getString(R.string.li_openingLoginPage));
        loginInProgTxt.setVisibility(View.VISIBLE);
        isAccessTokenSaved = false;
        webViewOauth.clearHistory();
        webViewOauth.clearFormData();
        webViewOauth.clearCache(true);
        setTitle(authUrl);
        //set the web client
        webViewOauth.setWebViewClient(new LoginWebViewClient());
        webViewOauth.setWebChromeClient(new LoginWebChromeClient());
        //activates JavaScript (just in case)
        WebSettings webSettings = webViewOauth.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);
        //load the url of the oAuth login page
        webViewOauth.loadUrl(authUrl);
    }

    private class LoginWebChromeClient extends WebChromeClient {
        public void onProgressChanged(WebView view, int progress) {
            progBar.setProgress(progress);
        }
    }

    private class LoginWebViewClient extends WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            LiCoreSDKUtils.sendLoginBroadcast(view.getContext(),
                    false,
                    LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR);
            Log.e(LiCoreSDKConstants.LI_LOG_TAG, "SSL Error opening login webpage");
            finish();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (!isAccessTokenSaved) {
                progBar.setVisibility(View.GONE);
                loginInProgTxt.setVisibility(View.GONE);
                webViewOauth.setVisibility(View.VISIBLE);
            }
            progBar.setVisibility(View.GONE);
            progBar.setProgress(100);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //check if the login was successful and the access token returned
            //this test depend of your API
            if (url.contains("code=")) {
                //save your token
                extractAuthCode(Uri.parse(url));
                isAccessTokenSaved = true;
                return true;
            }

            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progBar.setVisibility(View.VISIBLE);
            progBar.setProgress(0);
        }
    }
}