// default state of calculator object
var initialState = {
    currentExpression: "0",
    endsInSymbol: false,
    inDecimal: false,
    expressionIsAns: true
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
    expressionIsAns: initialState.expressionIsAns,
    
    buttonPress: function (operation) {
        var isSymbol = isNaN(operation);
        
        if(operation === "=") {
            if (this.endsInSymbol) return;

            this.currentExpression = eval(this.currentExpression).toString();
            this.expressionIsAns = true;
        } else if (operation === '.') {
            if(this.expressionIsAns) {
                this.clear();
            }

            if (this.inDecimal) return;

            this.inDecimal = true;
            this.currentExpression += operation;
            this.endsInSymbol = true;
        } else if (isSymbol) {
            if (this.endsInSymbol && (operation === '*' || this.inDecimal)) return;

            this.currentExpression = this.currentExpression + " " + operation + " ";
            this.expressionIsAns = false;
            this.endsInSymbol = true;
            this.inDecimal = false;
        } else { //number
            if(this.expressionIsAns) {
                this.clear();
            }
            
            this.currentExpression = this.currentExpression + operation;
            this.endsInSymbol = false;
        }
        
        display.setText(this.currentExpression);
        
    },
    clear: function(){
        this.currentExpression = "";
        this.expressionIsAns = false;
        this.inDecimal = false;
        this.endsInSymbol = false;
    },
    clearToZero: function() {
        this.currentExpression = "0";
        this.expressionIsAns = true;
        this.inDecimal = false;
        this.endsInSymbol = false;
        display.setText(this.currentExpression);
    }
};

if (clearButton) {
    console.log('clear button exists, trying to connect');
    clearButton.addClickHandler(calculator.clearToZero.bind(calculator));
}

display.setText(calculator.currentExpression);

console.log("Loaded calc.js");
