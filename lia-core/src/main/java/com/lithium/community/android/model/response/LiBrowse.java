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

package com.lithium.community.android.model.response;

import com.lithium.community.android.model.LiBaseModelImpl;

/**
 * Data Model for GET call for displaying contents to browse.
 * Created by shoureya.kant on 11/15/16.
 */

public class LiBrowse extends LiBaseModelImpl {

    private String id;
    private String title;
    private LiBrowse parent;

    private int depth;

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LiBrowse getParent() {
        return parent;
    }

    public void setParent(LiBrowse parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || (getClass() != o.getClass())) {
            return false;
        }
        LiBrowse that = (LiBrowse) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "LiBrowse{" + "id='" + id + '\'' + ", title='" + title + '\'' + ", parent=" + parent + '}';
    }
}
