/*
 * LiAuthReceiverActivity.java
 * Created on Dec 27, 2016
 *
 * Copyright 2016 Lithium Technologies, Inc.
 * San Francisco, California, U.S.A.  All Rights Reserved.
 *
 * This software is the  confidential and proprietary information
 * of  Lithium  Technologies,  Inc.  ("Confidential Information")
 * You shall not disclose such Confidential Information and shall
 * use  it  only in  accordance  with  the terms of  the  license
 * agreement you entered into with Lithium.
 */

package lithium.community.android.sdk.auth;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import lithium.community.android.sdk.R;

/**
 * Activity that receives the redirect Uri sent by the OpenID endpoint. This activity gets launched
 * when the user approves the app for use and it starts the {@link PendingIntent} given in
 * <p>
 * <p>App developers using this library <em>must</em> to register this activity in the manifest
 * with one intent filter for each redirect URI they are intending to use.
 * <p>
 * <pre>
 * {@code
 * < intent-filter>
 *   < action android:name="android.intent.action.VIEW"/>
 *   < category android:name="android.intent.category.DEFAULT"/>
 *   < category android:name="android.intent.category.BROWSABLE"/>
 *   < data android:scheme="REDIRECT_URI_SCHEME"/>
 * < /intent-filter>
 * }
 * </pre>
 */
@SuppressLint("Registered")
public class LiAuthReceiverActivity extends LiBaseAuthActivity {
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.li_activity_auth_receiver);
        Intent intent = getIntent();
        Uri data = intent.getData();
        extractAuthCode(data);
    }
}
