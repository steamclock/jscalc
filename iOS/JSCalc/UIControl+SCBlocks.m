//
//  UIControl+SCBlocks.m
//  JSCalc
//
//  Created by Chani Armitage on 2013-09-05.
//  Copyright (c) 2013 Nigel Brooke. All rights reserved.
//

#import "UIControl+SCBlocks.h"

@implementation UIControl (SCBlocks)


- (void) addEventHandler:(void(^)(void))handler forControlEvents:(UIControlEvents)controlEvents {
    //TODO
}

- (void) addClickHandler:(void(^)(void))handler {
    [self addEventHandler:handler forControlEvents:UIControlEventTouchUpInside];
}

@end
