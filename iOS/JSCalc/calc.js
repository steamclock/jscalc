// default state of calculator object
var initialState = {
    currentExpression: "0",
    fragile: true
};

if(typeof calculator != 'undefined') {
    initialState = calculator;
}

// define the calculator object
calculator = {
    currentExpression: initialState.currentExpression,
    
    fragile: initialState.fragile,
    
    buttonPress: function (button) {
        var operation = button.currentTitle();
        var isNumber = !isNaN(operation);
        
        if(operation === "=") {
            this.currentExpression = eval(this.currentExpression).toString();
            this.fragile = true;
        }
        else if(!isNumber) {
            this.currentExpression = this.currentExpression + " " + operation + " ";
            this.fragile = false;
        }
        else {
            if(this.fragile) {
                this.currentExpression = "";
                this.fragile = false;
            }
            
            this.currentExpression = this.currentExpression + operation;
        }
        
        display.setText(this.currentExpression);
        
    }
}

display.setText(calculator.currentExpression);
display.setTextAlignment(2);

console.log("Loaded calc.js");