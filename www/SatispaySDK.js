var exec = require('cordova/exec');

exports.isSatispayAvailable = function (useSandbox, success, error) {
    exec(success, error, 'SatispaySDK', 'isSatispayAvailable', [useSandbox]);
};

exports.payChargeId = function (useSandbox, chargeId, success, error) {
    exec(success, error, 'SatispaySDK', 'payChargeId', [useSandbox, chargeId]);
};

exports.satispayPreAuthorizedPayment = function (useSandbox, token, success, error) {
    exec(success, error, 'SatispaySDK', 'satispayPreAuthorizedPayment', [useSandbox, token]);
};
