var exec = require('cordova/exec');

exports.isSatispayAvailable = function (appUrl, success, error) {
    exec(success, error, 'SatispaySDK', 'isSatispayAvailable', [appUrl]);
};

exports.openApp = function (useSandbox, success, error) {
    exec(success, error, 'SatispaySDK', 'openApp', [useSandbox]);
};

exports.payChargeId = function (useSandbox, chargeId, success, error) {
    exec(success, error, 'SatispaySDK', 'payChargeId', [useSandbox, chargeId]);
};
