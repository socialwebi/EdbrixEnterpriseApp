package com.edbrix.enterprise.Interfaces;

/**
 * Created by Anvesh on 10-Dec-17.
 */

public interface Listeners {

    public static interface Listener {
        void onSessionConnectionDataReady(String apiKey, String sessionId, String token);

        void onWebServiceCoordinatorError(Exception error);
    }


}
