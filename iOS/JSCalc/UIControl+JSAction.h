//
//  UIControl+SCBlocks.h
//  JSCalc
//
//  Created by Chani Armitage on 2013-09-05.
//  Copyright (c) 2013 Steamclock Software. All rights reserved.
//

/*
 This category adds support for using a JSValue instead of a selector for UIControlEvents.
 Only one binding per UIControl is supported.
 */


#import <UIKit/UIKit.h>
#import "JavaScriptCore/JSValue.h"

@interface UIControl (JSAction)

- (void) addEventHandler:(JSValue*)handler forControlEvents:(UIControlEvents)controlEvents;

//convenience func for UIControlEventTouchUpInside
//uses the Android method name so that we don't need additional wrappers.
- (void) setOnClickListener:(JSValue*)handler;

@end
