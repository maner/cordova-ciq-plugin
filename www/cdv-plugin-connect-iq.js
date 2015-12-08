function ConnectIQ() {}

ConnectIQ.prototype.initialize = function(successCallback, failureCallback) {
    cordova.exec(successCallback, failureCallback, 'ConnectIQ', 'initialize', []);
};

ConnectIQ.prototype.getKnownDevices = function(successCallback, failureCallback) {
    cordova.exec(successCallback, failureCallback, 'ConnectIQ', 'knownDevices', []);
};

ConnectIQ.prototype.getDeviceStatus = function(deviceId, successCallback, failureCallback) {
    if (!deviceId) {
        failureCallback('MISSING_DEVICE_ID');
    } else {
        cordova.exec(successCallback, failureCallback, 'ConnectIQ', 'deviceStatus', [deviceId]);
    }
};

module.exports = new ConnectIQ();