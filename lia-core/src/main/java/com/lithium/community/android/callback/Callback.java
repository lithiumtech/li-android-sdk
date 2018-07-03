package com.lithium.community.android.callback;

/**
 * A generic callback interface for SDK asynchronous interactions and interfaces
 * @param <Result> - An expected object, example could be Reponses
 * @param <Exception> - As it states what caused the failure, in the form of an Exception
 * @param <Reason> - An exception again, why was the call cancelled or aborted.
 */
public interface Callback<Result, Exception, Reason> {

    /**
     * Called when the operation succeeds
     * @param r - An expected object, example could be Reponses
     */
    void success(Result r);

    /**
     * Called when the operation fails due to some server issue or network issue
     * @param t - As it states what caused the failure, in the form of an Exception
     */
    void failure(Exception t);

    /**
     * Called when the operation is explicitly halted even before taking place, any parameter or local state issues.
     * @param t - As it states what caused the failure, in the form of an Exception
     */
    void abort(Reason t);
}
