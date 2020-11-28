package com.satispay.sdk.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.satispay.satispayintent.SatispayIntent;

/**
 * This class echoes a string called from JavaScript.
 */
public class SatispaySDK extends CordovaPlugin {
    private static final String LOG_TAG = "SatispaySDK";
    private static final int REQUEST_PAY_CHARGE_ID = 3636;
    private CallbackContext callbackContext = null;
    private boolean useSandbox = false;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        Log.d(LOG_TAG, "SatispaySDK execute: " + action);

        this.callbackContext = callbackContext;

        try {
            switch (action) {
                case "openApp":
                    this.useSandbox = args.getBoolean(0);
                    this.openApp(callbackContext);
                    return true;
                case "payChargeId":
                    this.useSandbox = args.getBoolean(0);
                    String chargeId = args.getString(1);
                    this.payChargeId(chargeId, callbackContext);
                    return true;
                default:
                    this.callbackContext.error("Method not found");
                    return false;
            }
        } catch (Throwable e) {
            Log.d(LOG_TAG, "onPluginAction: " + action + ", ERROR: " + e.getMessage());
            this.callbackContext.error(e.getMessage());
            return false;
        }
    }

    public void openApp(CallbackContext callbackContext) {
        Context context = cordova.getActivity().getApplicationContext();
        String scheme = SatispayIntent.PRODUCTION_SCHEME;
        String appPackage = SatispayIntent.PRODUCTION_APP_PACKAGE;
        if (this.useSandbox) {
            scheme = SatispayIntent.SANDBOX_SCHEME;
            appPackage = SatispayIntent.SANDBOX_APP_PACKAGE;
        }

        boolean isSatispayAvailable = SatispayIntent.isSatispayAvailable(context, scheme);

        if (isSatispayAvailable) {
            Intent openAppIntent = SatispayIntent.openApp(scheme);
            this.cordova.getActivity().startActivity(openAppIntent);
        } else {
            Intent openPlayStoreIntent = SatispayIntent.openPlayStore(context, appPackage);
            this.cordova.getActivity().startActivity(openPlayStoreIntent);
        }
    }

    public void payChargeId(String chargeId, CallbackContext callbackContext) {
        Context context = cordova.getActivity().getApplicationContext();
        String scheme = SatispayIntent.PRODUCTION_SCHEME;
        String appPackage = SatispayIntent.PRODUCTION_APP_PACKAGE;
        if (this.useSandbox) {
            scheme = SatispayIntent.SANDBOX_SCHEME;
            appPackage = SatispayIntent.SANDBOX_APP_PACKAGE;
        }

        Uri uriToCheck = SatispayIntent.uriForPayChargeId(scheme, "generic", "TEST_API");
        SatispayIntent.ApiStatus apiStatus = SatispayIntent.getApiStatus(context, appPackage, uriToCheck);
        if (apiStatus.isValidRequest()) {
            String appId = "generic";
            Intent intent = SatispayIntent.payChargeId(scheme, appId, chargeId);
            if (SatispayIntent.isIntentSafe(context, intent)) {
                cordova.setActivityResultCallback(this);
                this.cordova.getActivity().startActivityForResult(intent, REQUEST_PAY_CHARGE_ID);
            } else {
                this.callbackContext.error("Cannot open this URI");
            }
        } else {
            this.callbackContext.error(getErrorHint(apiStatus.getCode()));
        }
    }

    private String getErrorHint(int errorCode) {
        String hint;
        switch (errorCode) {
            case SatispayIntent.RESULT_CANCEL_BAD_REQUEST:
                hint = "BAD REQUEST";
                break;
            case SatispayIntent.RESULT_CANCEL_FORBIDDEN:
                hint = "FORBIDDEN: User cannot proceed (User is logged?)";
                break;
            case SatispayIntent.RESULT_CANCEL_NOT_FOUND:
                hint = "NOT FOUND: Wrong URI or Satispay app cannot handle this URI yet";
                break;
            case SatispayIntent.RESULT_CANCEL_GONE:
                hint = "GONE: Indicates that the resource requested is no longer available and will not be available again";
                break;
            case SatispayIntent.RESULT_CANCEL_UPGRADE_REQUIRED:
                hint = "UPGRADE REQUIRED: Upgrade SatispayIntent SDK is REQUIRED!";
                break;
            case SatispayIntent.RESULT_CANCEL_TOO_MANY_REQUESTS:
                hint = "TOO MANY REQUESTS: Try again later";
                break;
            case SatispayIntent.RESULT_ERROR_UNKNOWN:
                hint = "UNKNOWN: Old Satispay app? Wrong appPackage? Other reason?";
                break;
            case SatispayIntent.RESULT_ERROR_SCHEME_NOT_FOUND:
                hint = "SCHEME NOT FOUND: Wrong scheme? Is Satispay installed? Restricted access?";
                break;
            default:
                hint = "NEW ERROR CODE: Try to update SatispayIntent SDK!";
                break;
        }
        return hint;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PAY_CHARGE_ID) {
            if (resultCode == Activity.RESULT_OK) {
                PluginResult result = new PluginResult(PluginResult.Status.OK, "payment ok");
                result.setKeepCallback(true);
                this.callbackContext.sendPluginResult(result);
            } else {
                PluginResult result = new PluginResult(PluginResult.Status.ERROR, "payment aborted");
                result.setKeepCallback(true);
                this.callbackContext.sendPluginResult(result);
            }
        }
    }
}
