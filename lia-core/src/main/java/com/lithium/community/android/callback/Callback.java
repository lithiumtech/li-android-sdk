package com.lithium.community.android.callback;

public interface Callback<Result, Exception, Reason> {

    void success(Result r);

    void failure(Exception t);

    void abort(Reason t);
}
