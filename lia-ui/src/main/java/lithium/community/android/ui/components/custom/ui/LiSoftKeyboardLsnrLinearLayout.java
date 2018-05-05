/*
 * SoftKeyboardLsnrRelativeLayout.java
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

package lithium.community.android.ui.components.custom.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * This class extends {@link LinearLayout} so that it can listen to the soft keyboard show and hide
 */

public class LiSoftKeyboardLsnrLinearLayout extends LinearLayout {
    private static final float DETECT_ON_SIZE_PERCENT = 0.8f;
    private boolean isKeyboardShown = false;
    private List<SoftKeyboardLsner> lsners = new ArrayList<>();
    private float layoutMaxH = 0f; // max measured height is considered layout normal size

    public LiSoftKeyboardLsnrLinearLayout(Context context) {
        super(context);
    }

    public LiSoftKeyboardLsnrLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LiSoftKeyboardLsnrLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    public LiSoftKeyboardLsnrLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int newH = MeasureSpec.getSize(heightMeasureSpec);
        if (newH > layoutMaxH) {
            layoutMaxH = newH;
        }
        if (layoutMaxH != 0f) {
            final float sizePercent = newH / layoutMaxH;
            if (!isKeyboardShown && sizePercent <= DETECT_ON_SIZE_PERCENT) {
                isKeyboardShown = true;
                for (final SoftKeyboardLsner lsner : lsners) {
                    lsner.onSoftKeyboardShow();
                }
            } else if (isKeyboardShown && sizePercent > DETECT_ON_SIZE_PERCENT) {
                isKeyboardShown = false;
                for (final SoftKeyboardLsner lsner : lsners) {
                    lsner.onSoftKeyboardHide();
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void addSoftKeyboardLsner(SoftKeyboardLsner lsner) {
        lsners.add(lsner);
    }

    public void removeSoftKeyboardLsner(SoftKeyboardLsner lsner) {
        lsners.remove(lsner);
    }

    // Callback
    public interface SoftKeyboardLsner {
        public void onSoftKeyboardShow();

        public void onSoftKeyboardHide();
    }
}
