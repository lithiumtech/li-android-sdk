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

package com.lithium.community.android.ui.components.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lithium.community.android.ui.R;
import com.lithium.community.android.ui.components.activities.LiSupportHomeActivity;
import com.lithium.community.android.ui.components.fragments.LiMessageListFragment;
import com.lithium.community.android.ui.components.fragments.LiUserActivityFragment;

/**
 * {@link FragmentStatePagerAdapter} that displays the two tabs on the {@link LiSupportHomeActivity}
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
