/*
 * LiBrowse.java
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

package lithium.community.android.sdk.model.response;

import lithium.community.android.sdk.model.LiBaseModelImpl;

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
        return "LiBrowse{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", parent=" + parent +
                '}';
    }
}
