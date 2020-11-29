Cordova Plugin for Satispay SDK
=======

This plugin is usefull to implement the Satispay payment flow in a cordova based app

More info about Satispay [here](https://developers.satispay.com/docs/welcome)

### Android

For Android this plugin use the [official Satispay SDK](https://github.com/satispay/satispayintent-android-sdk)

Available methods:
- isSatispayAvailable: checks if the Satispay App is available
- payChargeId: checks if the Satispay App is available and then open it for the payment
- satispayPreAuthorizedPayment: checks if the Satispay App is available and then open it for the payment

### iOS

For iOS this plugin implements only the Satispay App check because, as the documentation says, the app has to open an url_scheme

Available methods:
- isSatispayAvailable: checks if the Satispay App is available

### Examples

Some examples of implementation in Angular

```javascript
    isSatispayAvailable() {
        const useSandbox = true;
        cordova.plugins.SatispaySDK.isSatispayAvailable(
            useSandbox,
            (response) => {
                console.log('SatispaySDK isSatispayAvailable success:', response);
            },
            (error) => {
                console.error('SatispaySDK isSatispayAvailable error: ', error);
            },
        );
    }
```

This is a possible implementation of the payment flow for each platform.
As fallback the app can open the payment url as web page

```javascript
    payChargeId() {
        const useSandbox = true;
        const chargeId = 'ABC12345'; // From server
        const paymentUrl = ''; // From server
        if (this.platform.is('android')) {
            cordova.plugins.SatispaySDK.payChargeId(
                useSandbox, chargeId,
                (response) => {
                    console.log('SatispaySDK payChargeId success:', response);
                },
                (error) => {
                    console.log('SatispaySDK payChargeId error:', error);
                    switch (error) {
                        case 'payment aborted':
                            // Handle payment aborted
                            break;
                        default:
                            // Handle payment error
                            this.openUrl(paymentUrl, '_blank');
                            break;
                    }
                },
            );
        } else if (this.platform.is('ios')) {
            cordova.plugins.SatispaySDK.isSatispayAvailable(
                useSandbox,
                (response) => {
                    console.log('SatispaySDK isSatispayAvailable success:', response);
                    if (response) {
                        const app = useSandbox ? 'satispay-stag://' : 'satispay://';
                        let url = app + 'external/generic/charge?token=' + chargeId;
                        url += '&callback_url=' + encodeURI('/satispay/ok'); // Custom callback url
                        this.openUrl(url, '_system');
                    } else {
                        this.openUrl(paymentUrl, '_blank');
                    }
                },
                (error) => {
                    console.error('SatispaySDK isSatispayAvailable error: ', error);
                    this.openUrl(paymentUrl, '_blank');
                },
            );
        } else {
            this.openUrl(paymentUrl, '_blank');
        }
    }
```
