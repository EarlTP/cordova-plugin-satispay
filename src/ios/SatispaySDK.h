/********* SatispaySDK Plugin.h Cordova Plugin Interface *******/

#import <Cordova/CDV.h>

@interface SatispaySDK : CDVPlugin {
  // Member variables go here.
}

- (void)isSatispayAvailable:(CDVInvokedUrlCommand*)command;
@end
