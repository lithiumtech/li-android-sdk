package com.lithium.community.android.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.ui.components.activities.LiSupportHomeActivity;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LiSDKManager.isInitialized()) {
            startActivity(new Intent(this, LiSupportHomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, R.string.error_credentials_invalid, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
