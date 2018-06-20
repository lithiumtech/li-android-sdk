package com.lithium.community.android.callback;

public interface Callback<R, T> {

    void success(R r);

    void failure(T t);

    void abort(T t);
}
