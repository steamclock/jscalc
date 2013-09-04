// default state of calculator object
var initialState = {
    currentExpression: "0",
    endsInSymbol: false,
    inDecimal: false,
    fragile: true //fragile means it was just eval'd, and we may want to reuse it or wipe it depending on the next keypress.
};

//keep the old state if we're reloading this file.
if(typeof calculator != 'undefined') {
    initialState = calculator;
}

// define the calculator object
calculator = {
    currentExpression: initialState.currentExpression,
    endsInSymbol: initialState.endsInSymbol,
    inDecimal: initialState.inDecimal,
    fragile: initialState.fragile,
    
    buttonPress: function (operation) {
        var isNumber = !isNaN(operation);
        
        if(operation === "=") {
            if (this.endsInSymbol) return;

            this.currentExpression = eval(this.currentExpression).toString();
            this.fragile = true;
        } else if (operation === '.') {
            if(this.fragile) {
                this.clear();
            }

            if (this.inDecimal) return;

            this.inDecimal = true;
            this.currentExpression += operation;
            this.endsInSymbol = true;
        } else if(!isNumber) {
            if (this.endsInSymbol && (operation === '*' || this.inDecimal)) return;

            this.currentExpression = this.currentExpression + " " + operation + " ";
            this.fragile = false;
            this.endsInSymbol = true;
            this.inDecimal = false;
        } else { //number
            if(this.fragile) {
                this.clear();
            }
            
            this.currentExpression = this.currentExpression + operation;
            this.endsInSymbol = false;
        }
        
        display.setText(this.currentExpression);
        
    },
    clear: function(){
        this.currentExpression = "";
        this.fragile = false;
        this.inDecimal = false;
        this.endsInSymbol = false;
    }
};

display.setText(calculator.currentExpression);

console.log("Loaded calc.js");
