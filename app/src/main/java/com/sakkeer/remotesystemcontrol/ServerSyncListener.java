package com.sakkeer.remotesystemcontrol;

/**
 * Created by sakkeer on 19/9/15.
 */
public interface ServerSyncListener {
    void gotSuccessResponse(String response);
    void gotFailureResponse(String response);
}
