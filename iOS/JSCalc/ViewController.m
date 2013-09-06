//
//  ViewController.m
//  JSCalc
//
//  Created by Nigel Brooke on 2013-08-08.
//  Copyright (c) 2013 Nigel Brooke. All rights reserved.
//

#import "ViewController.h"
#import "JavaScriptCore/JSContext.h"
#import "JavaScriptCore/JSExport.h"
#import "JavaScriptCore/JSValue.h"
#import "objc/runtime.h"

@protocol ButtonExport <JSExport>

- (void) setOnClickListener:(JSValue*)handler;

@end

@protocol LabelExport <JSExport>

-(void)setText:(NSString*)text;
-(void)setTextAlignment:(UITextAlignment)alignment;

@end

@protocol ConsoleExport <JSExport>
-(void)log:(NSString*)string;
@end

@interface Console : NSObject <ConsoleExport>
@end

@implementation Console

-(void)log:(NSString*)string {
    NSLog(@"js: %@", string);
}
@end

@interface ViewController ()

@property (nonatomic) JSContext* context;
@property (nonatomic) JSValue* calculator;

@property (nonatomic) IBOutlet UILabel* display;
@property (nonatomic) IBOutlet UIButton* clearButton;

@property (nonatomic) NSDate* lastLoad;
@end

@implementation ViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    Protocol* newProtocol = @protocol(ButtonExport);
    class_addProtocol([UIButton class], newProtocol);
    
    newProtocol = @protocol(LabelExport);
    class_addProtocol([UILabel class], newProtocol);
    
    self.context = [[JSContext alloc] init];
    
    self.context[@"console"] = [[Console alloc] init];
    self.context[@"display"] = self.display;
    self.context[@"clearButton"] = self.clearButton;

    [self setup:[[NSBundle mainBundle] pathForResource:@"calc" ofType:@"js"]];
    
    // Poll for changes in the JavaScript
    [NSTimer scheduledTimerWithTimeInterval:1.0f target:self selector:@selector(checkReload) userInfo:nil repeats:YES];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

-(void)checkException {
    if (self.context.exception) {
        NSLog(@"js excaption: %@", self.context.exception);
    }

    self.context.exception = nil;
}

-(NSDate*)modificationDateForFile:(NSString*)path {
    NSFileManager* fm = [NSFileManager defaultManager];
    NSDictionary* attrs = [fm attributesOfItemAtPath:path error:nil];
    
    if (attrs != nil) {
        NSDate *date = (NSDate*)[attrs objectForKey: NSFileModificationDate];
        return date;
    }
    else {
        return [NSDate distantPast];
    }
}

-(void)setup:(NSString*)source {
    NSString* js = [[NSString alloc] initWithData:[NSData dataWithContentsOfFile:source] encoding:NSUTF8StringEncoding];
    
    [self.context evaluateScript:js];
    [self checkException];

    self.calculator = self.context[@"calculator"];
    
    self.lastLoad = [NSDate new];
}

-(void) checkReload {
    NSString* originalFile = @"/Users/nigel/Development/SteamClock/JSCalc/JSCalc/JSCalc/calc.js";
    
    if ([[self modificationDateForFile:originalFile] compare:self.lastLoad] == NSOrderedDescending) {
        NSLog(@"Reloading calc.js");
        [self setup:originalFile];
    }    
}

-(IBAction)buttonPress:(id)sender {
    UIButton *button = (UIButton*)sender;
    NSAssert(button, @"buttonPress without a button?");

    NSString* operation = [button currentTitle];

    [self.calculator invokeMethod:@"buttonPress" withArguments:@[operation]];
    [self checkException];
}

@end
