//
//  UIControl+SCBlocks.h
//  JSCalc
//
//  Created by Chani Armitage on 2013-09-05.
//  Copyright (c) 2013 Nigel Brooke. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIControl (SCBlocks)

- (void) addEventHandler:(void(^)(void))handler forControlEvents:(UIControlEvents)controlEvents;
//convenience func for UIControlEventTouchUpInside
- (void) addClickHandler:(void(^)(void))handler;

@end
