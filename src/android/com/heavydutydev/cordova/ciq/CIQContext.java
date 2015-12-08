package com.heavydutydev.cordova.ciq;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import android.content.Context;
import android.util.Log;
import com.garmin.android.connectiq.ConnectIQ;
import com.garmin.android.connectiq.IQDevice;
import com.garmin.android.connectiq.exception.InvalidStateException;
import com.garmin.android.connectiq.exception.ServiceUnavailableException;
import java.util.Collection;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CIQContext {
    public static final String TAG = "CIQContext";
    
    private final ConnectIQ ciqInstance;
    private Map<Long, IQDevice> deviceIdToDevice = new HashMap<Long, IQDevice>();

    public CIQContext(Context context) {
        ciqInstance = ConnectIQ.getInstance(context, ConnectIQ.IQConnectType.WIRELESS);
    }

    public void initialize(Context context, final CallbackContext callbackContext) {
        final CIQContext ciqContext = this;
        ciqInstance.initialize(context, true, new ConnectIQ.ConnectIQListener() {
            @Override
            public void onSdkReady() {
                deviceIdToDevice.clear();
                callbackContext.success();
            }

            @Override
            public void onInitializeError(ConnectIQ.IQSdkErrorStatus status) {
                deviceIdToDevice.clear();
                callbackContext.error(status.name());
            }

            @Override
            public void onSdkShutDown() {
                ciqContext.shutdown();
            }
        });
    }
    
    public void shutdown() {
        deviceIdToDevice.clear();
    }

    public void getKnownDevices(CallbackContext callbackContext) 
            throws InvalidStateException, ServiceUnavailableException {
            try {
                populateKnownDevices();
                callbackContext.success(devicesToJSON(deviceIdToDevice.values()));
            } catch (JSONException e) {
                Log.e(TAG, "Unable to serialize response", e);
                callbackContext.error("SERIALIZATION_ERROR");
            }
    }
    
    public void getDeviceStatus(String deviceId, CallbackContext callbackContext) 
            throws InvalidStateException, ServiceUnavailableException {
        try {
            Long longDeviceId = Long.parseLong(deviceId);
            IQDevice iqDevice = deviceIdToDevice.get(longDeviceId);
            if (iqDevice == null) {
                populateKnownDevices();
                iqDevice = deviceIdToDevice.get(longDeviceId);
                if (iqDevice == null) {
                    callbackContext.error("UNKNOWN_DEVICE_ID");
                }
            }
            callbackContext.success(ciqInstance.getDeviceStatus(iqDevice).name());
        } catch (NumberFormatException e) {
            callbackContext.error("INVALID_DEVICE_ID");
        }
    }
    
    protected void populateKnownDevices() throws InvalidStateException, 
            ServiceUnavailableException {
        List<IQDevice> iqDevices = ciqInstance.getKnownDevices();
        repopulateDeviceMap(iqDevices);
    }

    protected void repopulateDeviceMap(List<IQDevice> iqDevices) {
        deviceIdToDevice = new HashMap<Long, IQDevice>();

        for (IQDevice iqDevice : iqDevices) {
            deviceIdToDevice.put(iqDevice.getDeviceIdentifier(), iqDevice);
        }
    }

    protected JSONArray devicesToJSON(Collection<IQDevice> iqDevices) throws JSONException {
        JSONArray iqDevicesJson = new JSONArray();

        for (IQDevice iqDevice : iqDevices) {
            iqDevicesJson.put(deviceToJSON(iqDevice));
        }

        return iqDevicesJson;
    }

    protected JSONObject deviceToJSON(IQDevice iqDevice) throws JSONException {
        JSONObject iqDeviceJson = new JSONObject();

        iqDeviceJson.put("id", iqDevice.getDeviceIdentifier());
        iqDeviceJson.put("name", iqDevice.getFriendlyName());
        iqDeviceJson.put("status", iqDevice.getStatus().name());

        return iqDeviceJson;
    }
}