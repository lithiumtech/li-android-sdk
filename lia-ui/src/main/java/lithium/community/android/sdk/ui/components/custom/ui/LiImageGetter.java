/*
 * LiImageGetter.java
 * Created on Dec 21, 2016
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

package lithium.community.android.sdk.ui.components.custom.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import lithium.community.android.sdk.ui.components.adapters.LiConversationAdapter;
import lithium.community.android.sdk.ui.components.utils.LiSDKConstants;

/**
 * This class is responsible for pulling the image bitmap from the server and setting it in the HTML content displayed on the Message row
 * in {@link LiConversationAdapter}
 */
public class LiImageGetter implements Html.ImageGetter {
    private final Resources resources;
    private final TextView targetTextView;
    private final Picasso picasso;

    public LiImageGetter(final TextView targetTextView, Resources resources, final Picasso picasso) {
        this.targetTextView = targetTextView;
        this.resources = resources;
        this.picasso = picasso;
    }

    @Override
    public Drawable getDrawable(final String source) {
        final BitmapDrawablePlaceHolder result = new BitmapDrawablePlaceHolder();
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    return picasso.load(source).get();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                try {
                    final BitmapDrawable drawable = new BitmapDrawable(resources, bitmap);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    result.setDrawable(drawable);
                    result.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    targetTextView.setText(targetTextView.getText());
                } catch (Exception e) {
                    Log.e(LiSDKConstants.GENERIC_LOG_TAG, e.getMessage());
                }
            }
        }.execute();
        return result;
    }

    private class BitmapDrawablePlaceHolder extends BitmapDrawable {
        protected Drawable drawable;

        @Override
        public void draw(final Canvas canvas) {
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }
    }
}