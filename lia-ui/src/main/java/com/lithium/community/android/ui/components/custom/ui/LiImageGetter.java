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

package com.lithium.community.android.ui.components.custom.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.lithium.community.android.ui.components.adapters.LiConversationAdapter;
import com.lithium.community.android.ui.components.utils.LiSDKConstants;
import com.squareup.picasso.Picasso;

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