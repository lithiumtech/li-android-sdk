/*
 * LiSupportHomeViewPagerAdapter.java
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

package lithium.community.android.sdk.ui.components.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import lithium.community.android.sdk.ui.components.R;
import lithium.community.android.sdk.ui.components.fragments.LiMessageListFragment;
import lithium.community.android.sdk.ui.components.fragments.LiUserActivityFragment;

/**
 * {@link FragmentStatePagerAdapter} that displays the two tabs on the {@link lithium.community.android.sdk.ui.components.activities.LiSupportHomeActivity}
 */
public class LiSupportHomeViewPagerAdapter extends FragmentStatePagerAdapter {
    private FragmentManager manager;
    private Bundle articleFragmentBundle;
    private Bundle questionFragmentBundle;
    private Activity activity;

    public LiSupportHomeViewPagerAdapter(Activity activity, FragmentManager manager,
            Bundle articleFragmentBundle,
            Bundle questionFragmentBundle) {
        super(manager);
        this.manager = manager;
        this.articleFragmentBundle = articleFragmentBundle;
        this.questionFragmentBundle = questionFragmentBundle;
        this.activity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = manager.findFragmentByTag(LiMessageListFragment.class.getName());
                if (fragment == null) {
                    fragment = LiMessageListFragment.newInstance(articleFragmentBundle);
                }
                break;
            case 1:
                fragment = manager.findFragmentByTag(LiUserActivityFragment.class.getName());
                if (fragment == null) {
                    fragment = LiUserActivityFragment.newInstance(questionFragmentBundle);
                }
                break;
            default:
                //do nothing
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return activity.getResources().getString(R.string.li_support_articles);
            case 1:
                return activity.getResources().getString(R.string.li_support_my_questions);
            default:
                return null;
        }
    }

}
