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

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Common ViewHolder class for SDK
 */

public class LiViewHolder extends RecyclerView.ViewHolder {

    private final View mView;

    LiViewHolder(View view) {
        super(view);
        this.mView = view;
    }

    public View getView() {
        return mView;
    }
}
