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
import androidx.recyclerview.widget.RecyclerView;

import com.lithium.community.android.model.LiBaseModel;
import com.lithium.community.android.ui.components.fragments.LiOnMessageRowClickListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that is common to sdk fragments
 * This adapter is used where ever we want to show a list of items
 */
public abstract class LiBaseRecyclerAdapter extends RecyclerView.Adapter<LiViewHolder> {

    protected final Activity activity;
    protected final LiOnMessageRowClickListener mListener;
    protected List<LiBaseModel> mValues;

    public LiBaseRecyclerAdapter(List<LiBaseModel> items, Activity activity, LiOnMessageRowClickListener listener) {
        mValues = items;
        this.activity = activity;
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * This message returns the list of items that is currently displayed on the screen
     *
     * @return List of {@link LiBaseModel}
     */
    public List<LiBaseModel> getItems() {
        return mValues;
    }


    /**
     * This message sets the new list of items that will be displayed on the screen.
     */
    public void setItems(List<LiBaseModel> mValues) {
        this.mValues = mValues;
    }

}
