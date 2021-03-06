/********* SatispaySDK Plugin.m Cordova Plugin Implementation *******/

#import "SatispaySDK.h"
#import <Cordova/CDV.h>

@implementation SatispaySDK

- (void)isSatispayAvailable:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    BOOL useSandbox = [command.arguments objectAtIndex:0];
    NSString* appUrl = useSandbox ? "satispay-stag://" : "satispay://";

    if (appUrl != nil && [appUrl length] > 0) {
        NSURL *url = [NSURL URLWithString: appUrl];
        BOOL result = [[UIApplication sharedApplication] canOpenURL: url];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:result];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end
