package com.lithium.community.android.ui.components.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.manager.LiClientManager;
import lithium.community.android.sdk.model.request.LiClientRequestParams;
import lithium.community.android.sdk.rest.LiAsyncRequestCallback;
import lithium.community.android.sdk.rest.LiBaseRestRequest;
import lithium.community.android.sdk.rest.LiPostClientResponse;
import lithium.community.android.sdk.ui.components.R;
import com.lithium.community.android.ui.components.utils.LiSDKConstants;
import lithium.community.android.sdk.utils.LiCoreSDKConstants;

/**
 * Created by Lithium Technologies Inc on 4/5/17.
 * This activity is used for reporting abuse for a message on a community.
 * Usage:
 * <pre>
 * Intent i = new Intent(activity, LiReportAbuseActivity.class);
 *          Bundle bundle = new Bundle();
 *          bundle.putString(LiSDKConstants.REPORT_ABUSE_MESSAGE_ID, item.getId()+"");
 *          bundle.putString(LiSDKConstants.REPORT_ABUSE_AUTHOR_ID, item.getAuthor().getId()+"");
 *          bundle.putString(LiSDKConstants.REPORT_ABUSE_MESSAGE_BODY, item.getBody());
 *          i.putExtras(bundle);
 *          activity.startActivity(i);
 * </pre>
 * {@link LiCoreSDKConstants}.REPORT_ABUSE_MESSAGE_ID ID of the message which is being reported as abuse
 * {@link LiCoreSDKConstants}.REPORT_ABUSE_AUTHOR_ID ID of the author of the message which is being reported as abuse
 * {@link LiCoreSDKConstants}.REPORT_ABUSE_MESSAGE_BODY body of the message which is being reported as abuse
 */

public class LiReportAbuseActivity extends AppCompatActivity {

    private String messageId;
    private String authorId;
    private EditText reportAbuseDescriptionTxt;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.li_activity_report_abuse);
        Toolbar toolbar = findViewById(R.id.li_toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        messageId = bundle.getString(LiSDKConstants.REPORT_ABUSE_MESSAGE_ID);
        authorId = bundle.getString(LiSDKConstants.REPORT_ABUSE_AUTHOR_ID);
        reportAbuseDescriptionTxt = findViewById(R.id.li_report_abuse_description);
        activity = this;
    }

    /**
     * Handles the back button click behavior.
     * If the user has entered some data which has not been posted yet,
     * then the user will see a confirmation box whether he wants to discard the form data and return back.
     */
    public void handleBackButton() {
        if (!TextUtils.isEmpty(reportAbuseDescriptionTxt.getText().toString())) {
            DialogInterface.OnClickListener dialogClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    finish();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.li_are_you_sure))
                    .setPositiveButton(getString(R.string.li_yes), dialogClickListener)
                    .setNegativeButton(R.string.li_no, dialogClickListener).show();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        handleBackButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.li_menu_report_abuse, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            handleBackButton();
            return true;
        } else if (i == R.id.li_action_report_abuse) {
            LiClientRequestParams.LiReportAbuseClientRequestParams params =
                    new LiClientRequestParams.LiReportAbuseClientRequestParams(
                            this, messageId, authorId, reportAbuseDescriptionTxt.getText().toString());
            try {
                LiClientManager.getReportAbuseClient(params).processAsync(new LiAsyncRequestCallback<LiPostClientResponse>() {
                    @Override
                    public void onSuccess(LiBaseRestRequest request, final LiPostClientResponse response) throws LiRestResponseException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (response.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                                    Toast.makeText(activity, R.string.li_moderator_notified, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(activity, R.string.li_report_abouse_error, Toast.LENGTH_LONG).show();
                                }

                            }
                        });
                    }

                    @Override
                    public void onError(Exception exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, R.string.li_report_abouse_error, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            } catch (LiRestResponseException e) {
                Log.d(LiSDKConstants.GENERIC_LOG_TAG, e.getMessage());
            }

            finish();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
