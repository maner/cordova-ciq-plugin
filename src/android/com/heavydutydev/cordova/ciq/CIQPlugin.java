package com.heavydutydev.cordova.ciq;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import android.util.Log;
import com.garmin.android.connectiq.exception.InvalidStateException;
import com.garmin.android.connectiq.exception.ServiceUnavailableException;
import java.util.concurrent.Callable;
import org.json.JSONArray;
import org.json.JSONException;

public class CIQPlugin extends CordovaPlugin {

    public static final String TAG = "CIQPlugin";

    public CIQPlugin() {
    }

    private CIQContext ciqContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        ciqContext = new CIQContext(cordova.getActivity());
    }

    @Override
    public boolean execute(final String action, final JSONArray args,
            final CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "CIQPlugin received:" + action + " with params: " + args);

        Callable<Void> callable = null;

        if ("initialize".equals(action)) {
            callable = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    ciqContext.initialize(cordova.getActivity(), callbackContext);

                    return null;
                }
            };
        } else if ("knownDevices".equals(action)) {
            callable = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    ciqContext.getKnownDevices(callbackContext);
                    
                    return null;
                }
            };
        } else if ("subscribeDeviceEvents".equals(action)) {
        }

        if (callable != null) {
            run(new CIQExceptionHandlingRunnable(callbackContext, action, callable));
        } else {
            Log.e(TAG, "Unknown action: " + action);
            callbackContext.error("UNKNOWN_ACTION");
        }

        return true;
    }

    protected void runNonBlocking(Runnable runnable) {
        cordova.getThreadPool().execute(runnable);
    }
    
    protected void run(Runnable runnable) {
        runnable.run();
    }

    protected static final class CIQExceptionHandlingRunnable implements Runnable {

        private final CallbackContext callbackContext;
        private final Callable<Void> callable;
        private final String action;

        public CIQExceptionHandlingRunnable(CallbackContext callbackContext,
                String action, Callable<Void> callable) {
            this.callbackContext = callbackContext;
            this.callable = callable;
            this.action = action;
        }

        @Override
        public void run() {
            try {
                callable.call();
            } catch (InvalidStateException e) {
                Log.e(TAG, "Request: " + action + " with invalid state", e);
                callbackContext.error("INVALID_STATE");
            } catch (ServiceUnavailableException e) {
                Log.e(TAG, "Request: " + action + ", but Garmin unavailable", e);
                callbackContext.error("SERVICE_UNAVAILABLE");
            } catch (Exception e) {
                Log.e(TAG, "Request: " + action + " with general exception", e);
                callbackContext.error("GENERAL_ERROR");
            }
        }
    }
}
