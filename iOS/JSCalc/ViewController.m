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

@end

@interface ViewController ()

@property (nonatomic) JSContext* context;
@property (nonatomic) JSValue* calculator;

@property (nonatomic) IBOutlet UILabel* display;
@property (nonatomic) IBOutlet UIButton* clearButton;
@property (nonatomic) IBOutlet UIButton* memStoreButton;
@property (nonatomic) IBOutlet UIButton* memRecallButton;

@property (nonatomic) NSDate* lastLoad;
@end

@implementation ViewController

- (void)viewDidLoad
{
    [super viewDidLoad];

    [self setupProtocols];
    [self setupContext];
    [self setup:[[NSBundle mainBundle] pathForResource:@"calc" ofType:@"js"]];
    
    // Poll for changes in the JavaScript
    [NSTimer scheduledTimerWithTimeInterval:1.0f target:self selector:@selector(checkReload) userInfo:nil repeats:YES];
}

-(void) setupProtocols {
    //existing classes need a protocol added to expose API to javascript
    Protocol* newProtocol = @protocol(ButtonExport);
    class_addProtocol([UIButton class], newProtocol);

    newProtocol = @protocol(LabelExport);
    class_addProtocol([UILabel class], newProtocol);
}

-(void) setupContext {
    //expose our objects to javascript
    self.context = [[JSContext alloc] init];

    //we can't set console.log in the context directly, only top-level objects, so let's build a top-level dummy object for console
    JSValue* console = [JSValue valueWithNewObjectInContext:self.context];
    console[@"log"] = ^void(NSString* string){
        NSLog(@"js: %@", string);
    };
    self.context[@"console"] = console;

    self.context[@"display"] = self.display;
    self.context[@"clearButton"] = self.clearButton;
    self.context[@"memStoreButton"] = self.memStoreButton;
    self.context[@"memRecallButton"] = self.memRecallButton;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

-(void)checkException {
    if (self.context.exception) {
        NSLog(@"js exception: %@", self.context.exception);
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
    //load the cross-platform javascript
    NSString* js = [[NSString alloc] initWithData:[NSData dataWithContentsOfFile:source] encoding:NSUTF8StringEncoding];
    [self.context evaluateScript:js];
    [self checkException];

    //make its calculator object easily accessible to objective C
    self.calculator = self.context[@"calculator"];
    
    self.lastLoad = [NSDate new];
}

-(void) checkReload {
    // Poll your local file for changes, very convenient
    
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
