package com.heavydutydev.cordova.ciq;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import android.util.Log;
import com.garmin.android.connectiq.exception.InvalidStateException;
import com.garmin.android.connectiq.exception.ServiceUnavailableException;
import org.json.JSONArray;
import org.json.JSONException;

public class CIQPlugin extends CordovaPlugin {
    public static final String TAG = "CIQPlugin";

    public CIQPlugin() {}

    private CIQContext ciqContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        ciqContext = new CIQContext(cordova.getActivity());
    }

    @Override
    public boolean execute(final String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "CIQPlugin received:" + action + " with params: " + args);

        try {
            if ("initialize".equals(action)) {
                ciqContext.initialize(cordova.getActivity(), callbackContext);
            } else if ("knownDevices".equals(action)) {
                ciqContext.getKnownDevices(callbackContext);
            } else if ("subscribeDeviceEvents".equals(action)) {
            }
        } catch (InvalidStateException e) {
            Log.e(TAG, "Request: " + action + " with invalid state", e);
            callbackContext.error("INVALID_STATE");
        } catch (ServiceUnavailableException e) {
            Log.e(TAG, "Request: " + action + ", but Garmin unavailable", e);
            callbackContext.error("SERVICE_UNAVAILABLE");
        }

        return true;
    }
}