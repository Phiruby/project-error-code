package com.highvoltage.error;

public class OperationBlocks extends CodeBlocks{
    String a;
    String b;
    String operation;
    /**
     * Initializes the instance of the class
     * @param operationIn - the operation
     * @param editable - if  the condition is editable
     * @param tabsIn - number of tabs
     * @param stringA - the first number
     * @param stringB - the second number
     */
    public OperationBlocks(String operationIn, boolean editable,int tabsIn, String stringA,String stringB) {
        super("( "+stringA+" "+operationIn+" "+stringB+" )", editable, tabsIn);
        a = stringA;
        b = stringB;
        operation = operationIn;
    }
    /**
     * setVars - sets the variables
     * @param aIn - the first variable
     * @param bIn - the second variable
     */
    public void setVars(String aIn, String bIn){
        a = aIn;
        b = bIn;
    }
    /**
     * setOperation - sets the operation
     * @param operationIn - the new operation
     */
    public void setOperation(String operationIn){
        operation = operationIn;
        setCodeString("( "+a+" "+operationIn+" "+b+" )");
    }
}
