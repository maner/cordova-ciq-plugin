function ConnectIQ() {}

ConnectIQ.prototype.initialize = function(successCallback, failureCallback) {
    cordova.exec(successCallback, failureCallback, "ConnectIQ", "initialize", []);
};

ConnectIQ.prototype.getKnownDevices = function(successCallback, failureCallback) {
    cordova.exec(successCallback, failureCallback, "ConnectIQ", "knownDevices", []);
};