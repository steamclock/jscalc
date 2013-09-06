//
//  UIControl+SCBlocks.h
//  JSCalc
//
//  Created by Chani Armitage on 2013-09-05.
//  Copyright (c) 2013 Nigel Brooke. All rights reserved.
//

/*
 This category adds support for using a JSValue instead of a selector for UIControlEvents.
 Only one binding per UIControl is supported.
 FIXME: this class needs a rename.
 */


#import <UIKit/UIKit.h>
#import "JavaScriptCore/JSValue.h"

@interface UIControl (SCBlocks)

- (void) addEventHandler:(JSValue*)handler forControlEvents:(UIControlEvents)controlEvents;
//convenience func for UIControlEventTouchUpInside
- (void) addClickHandler:(JSValue*)handler;

@end
