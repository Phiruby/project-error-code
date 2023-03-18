package com.highvoltage.error;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class DebugCode {
    CodeBlocks[] code;
    Helper helper = new Helper();
    String[] randConditions = {"!=", "==", ">=", "<="};
    boolean syntaxError;
    /**
     * initialize the instance of this class
     * @param codeLine - the array of code blocks
     */
    public DebugCode(CodeBlocks[] codeLine) {
        code = codeLine;
    }
    /**
     * closeCurlies - closes any blocks of code that isn't closed, by adding a curly
     * @param latestCode - the array of codeblocks
     * @param conditions - The array of condition (class OperationBlocks)
     * @return CodeBlocks[] - a new array of code, with closed curlies
     */
    public CodeBlocks[] closeCurlies(CodeBlocks[] latestCode, OperationBlocks[] conditions) {
        syntaxError = false;
        int numCloseCurlies = helper.countNumLinesWithString("}", latestCode);
        int numOpenCurlies = helper.countNumLinesWithString("{", latestCode);
        System.out.println(numCloseCurlies+" "+numOpenCurlies);
        int count = 1;
            for (int i = 2; i < latestCode.length-2; i++) {
                //---------------------If there is a missing curly after an if statement------------------------
                if ((latestCode[i].code.contains("if")||(latestCode[i].code.equals("else")))&&!latestCode[i+1].code.equals("{")){
                    latestCode = Arrays.copyOf(latestCode, latestCode.length + 1);
                    shiftCodeDown(i+1, 1, latestCode);
                    latestCode[i+1].setCodeString("{");
                    latestCode[i+1].setIsEditable(true);
                    count += 1;
                    syntaxError = true;
                }
                //------------------------If a statement block isn't closed, closes it!---------------------------\\
                if((!latestCode[i].code.equals("}")&&latestCode[i+1].code.contains("else"))){
                    latestCode = Arrays.copyOf(latestCode, latestCode.length + 1);
                    shiftCodeDown(i+1, 1, latestCode);
                    latestCode[i+1].setCodeString("}");
                    latestCode[i+1].setIsEditable(true);
                    syntaxError = true;
                }

                //-----------------------Removes a duplicate open curly on the same line---------------------\\
                if (latestCode[i].code.contains("{") && latestCode[i+1].code.contains("{")) {
                    latestCode = helper.getNewArray(i+1,latestCode);
                    syntaxError = true;
                }
            }
             numCloseCurlies = helper.countNumLinesWithString("}", latestCode);
             numOpenCurlies = helper.countNumLinesWithString("{", latestCode);
             while(numCloseCurlies<numOpenCurlies){
                latestCode = Arrays.copyOf(latestCode, latestCode.length + 1);
                 shiftCodeDown(latestCode.length-3, 1, latestCode);
                 latestCode[latestCode.length-3].setCodeString("}");
                 latestCode[latestCode.length-3].setIsEditable(true);
                 latestCode[latestCode.length-3].setTabs(latestCode[latestCode.length-3].tabs+1);
                 numCloseCurlies++;
                 syntaxError = true;
             }
        //}
        return latestCode;
    }

    /**
     * This method runs the bot's code and determines the damage dealt to the player and enemy
     * @param latestCode - the code blocks of the bot's code
     * @param conditions - the operation blocks of the bot's code
     * @param variables - the variables used in the bot's code
     * @return an array of floats where the first index is the player damage and the second index is the enemy damage
     */
    public float[] runBotCode(CodeBlocks[] latestCode, OperationBlocks[] conditions, HashMap<String, Integer> variables) {
        float[] dmg = new float[2]; //Index 0 is player damage, index 1 is enemy damage
        boolean misplacedCurlies = findBlockErrors(latestCode); //If true, the bot code has bugs!
        if (misplacedCurlies) {
            dmg[0] = 0F;
            dmg[1] = 0.5F;
            return dmg;
        }
        int conditionCounter = 0;
        boolean parentIfRan = false;
        int curlyCounter = 0;
        CodeBlocks[] code = new CodeBlocks[latestCode.length - 4];
        System.arraycopy(latestCode,2,code,0,code.length);
        for (int i = 0; i < code.length; i++) { //Step through the code
            variables = incrementVariables(variables,code[i].code);
            //If the code contains an opening curly brace, increment the curly counter
            if (code[i].code.contains("{")) {curlyCounter++;}
            //If the code contains a closing curly brace, decrement the curly counter
            else if (code[i].code.contains("}")) {curlyCounter--;}
            //If the curlyCounter is zero, it means we are not in a nested block, so we can run the individual line
            if (curlyCounter == 0) {dmg = runIndividualLines(code[i].code,dmg);}
            System.out.println("DAMAGE: "+dmg[1]+" "+code[i].code);
            //If the code contains an if statement
            if (code[i].code.contains("if")) {
                //(Parent ran, contains else) | (true, true) --> false ; (true, false) --> true ; (false, true) --> true ; (false, false)--> true
                if (ifShouldRun(parentIfRan,code[i].code.contains("else")) && curlyCounter == 0) {
                    int var1; //Find the variables that are being compared
                    int var2;
                    try {
                        //Convert the string variable into an integer
                        var1 = Integer.parseInt(conditions[conditionCounter].a);
                        var2 = Integer.parseInt(conditions[conditionCounter].b);
                    } catch (NumberFormatException e) {
                        if (conditions[conditionCounter].code.length() < 5) { //If there is not a valid condition
                            dmg[0] = 0;
                            dmg[1] = 1;
                            return dmg;
                        }
                        if (conditionHasVariable(conditions[conditionCounter].a+" ",variables) && conditionHasVariable(conditions[conditionCounter].b+" ",variables)) {
                            var1 = variables.get(conditions[conditionCounter].a);
                            var2 = variables.get(conditions[conditionCounter].b);
                        }
                        else if (conditionHasVariable(conditions[conditionCounter].a+" ",variables)) {
                            var1 = variables.get(conditions[conditionCounter].a);
                            System.out.println("HERE DUDE: "+var1);
                            var2 = Integer.parseInt(conditions[conditionCounter].b);
                        } else if (conditionHasVariable(conditions[conditionCounter].b+" ",variables)) {
                            var2 = variables.get(conditions[conditionCounter].b);
                            var1 = Integer.parseInt(conditions[conditionCounter].a);
                        }
                        else {
                            dmg[0] = 0;
                            dmg[1] = 0.4F;
                            return dmg; //If there is no variable, return true since there is an error
                        }
                    }
                    System.out.println("CONDITION ICI: "+code[i].code+"; "+conditions[conditionCounter].a+" "+conditions[conditionCounter].operation+
                            " "+conditions[conditionCounter].b);
                    //The "ifCondition" method will compare the two variables using the operator
                    if (ifCondition(var1, var2, conditions[conditionCounter].operation)) {
                        parentIfRan = true;
                        //The subset of conditions; (i.e a conditions array that is a unit smaller)
                        OperationBlocks[] toSend;
                        toSend = getToOperationSubset(conditions,code,i);

                        CodeBlocks[] subset = getSubsetCurlies(code,i,conditions.length);
                        //The "getSUbsetCurlies" method will find every line of code INSIDE the if statement; then "runIfStatement" runs them
                        dmg = runIfStatement(subset, var1, var2, conditions[conditionCounter].operation,
                                toSend, dmg,variables);
                    }
                }
            }
            if (code[i].code.contains("while")) {
                if (!conditions[conditionCounter].a.equals("") && !conditions[conditionCounter].operation.equals("") && !conditions[conditionCounter].b.equals("")){
                    CodeBlocks[] codeToSend = getSubsetCurlies(code, i, conditions.length);
                    OperationBlocks[] operationsToSend = getToOperationSubset(conditions, code, i);
                    dmg = runWhileLoop(codeToSend, operationsToSend, variables, dmg);
                }
            }
            if (code[i].code.contains("if") || code[i].code.contains("while")) {
                conditionCounter++;
            }
        }
        return dmg; //return false if no errors are found
    }

    /**
     * shiftCodeDown - shifts all code 1 the the right in an array
     * @param latestCode - the array of codeblocks
     * @param amountShift - The amount to shift each code line
     * @param startPoint - The starting index to begin the shifting
     */
    private void shiftCodeDown(int startPoint, int amountShift, CodeBlocks[] latestCode) {
        latestCode[latestCode.length - 1] = new CodeBlocks("",true,2); //Sp the last index is not null
        for (int i = latestCode.length - 1; i > startPoint ; i--) {
                latestCode[i].setCodeString(latestCode[i - amountShift].code);
                latestCode[i].setIsEditable(latestCode[i - amountShift].isEditable);
                latestCode[i].setTabs(latestCode[i - amountShift].tabs);
        }
    }

    /**
     * runWhileLoop - Runs a while loop given a set of code, conditions, variables, and current damage.
     * @param code - an array of CodeBlocks representing the lines of code within the while loop.
     * @param cond - an array of OperationBlocks representing the conditions of the while loop.
     * @param vars - a HashMap of variables and their values.
     * @param currentDamage - an array of floats representing the current damage.
     * @return an array of floats representing the new damage after running the while loop.
     */
    public float[] runWhileLoop(CodeBlocks[] code, OperationBlocks[] cond, HashMap<String,Integer> vars, float[] currentDamage) {
        //TODO: Make sure that the Helper get code method will also include conditions in the while loop. Also change this in the
        int sideCounter = 0; //To avoid program crashing
        //get condition subset method
        boolean hasVariable = conditionHasVariable(cond[0].code,vars);
        String ogOperator = cond[0].operation;
        int curlyCount = 0;
        float[] newDmg = new float[] {currentDamage[0],currentDamage[1]};
        if (!hasVariable && ifCondition(Integer.parseInt(cond[0].a),Integer.parseInt(cond[0].b),cond[0].operation)) {
            return new float[] {0,1};
        } //If no variables and condition is true, then while loop will run forever (so enemy dies)
        boolean firstNumIsVariable = conditionHasVariable(cond[0].a+" ",vars);
        if (firstNumIsVariable) {
            int var1 = vars.get(cond[0].a);
            int var2 = Integer.parseInt(cond[0].b);
            while (ifCondition(var1,var2,cond[0].operation)) { //Run while loop
                for (int i = 0; i < code.length; i++) { //Step through the code
                    if (code[i].code.contains("{")) {curlyCount++;}
                    if (code[i].code.contains("}")) {curlyCount--;}
                    System.out.println("MY X IS: "+vars.get("x")+" "+sideCounter + code[i].code);
                    if (code[i].code.contains("if")) { //If there is a nested if statement, recursion!
                        OperationBlocks[] toSend;

                        toSend = getToOperationSubset(cond, code, i);
                        int varOne;
                        int varTwo;
                        try {
                            varOne = Integer.parseInt(toSend[0].a);
                            varTwo = Integer.parseInt(toSend[0].b);
                        } catch (NumberFormatException e) {
                            //Note: having more than one nested if statement won't work atm
                            if (conditionHasVariable(toSend[0].a + " ", vars)) {
                                varOne = vars.get(toSend[0].a);
                                varTwo = Integer.parseInt(toSend[0].b);
                            } else {
                                varTwo = vars.get(toSend[0].b);
                                varOne = Integer.parseInt(toSend[0].a);
                            }

                        }
                        CodeBlocks[] codeToSend = getSubsetCurlies(code, i, cond.length);
                        if (codeToSend == null) {
                            return new float[]{0, 0.25F};
                        } //If there was a syntax error, return null
                        newDmg = runIfStatement(codeToSend, varOne, varTwo, toSend[0].operation, toSend, newDmg, vars);
                    } else if (code[i].code.contains("while") && i > 0) {
                        return new float[] {0,1}; //If chained while loop, stack overflow error
                    }

                    if (curlyCount == 1) { //Do individual lines only if it isn't in another block
                        newDmg = runIndividualLines(code[i].code,newDmg);
                        vars = incrementVariables(vars,code[i].code);
                        var1 = vars.get(cond[0].a);
                    }

                }
                sideCounter++;
                if (sideCounter >= 10) {
                    return new float[]{0,1};
                }
            }
        } else if (!firstNumIsVariable) {
            int var1 = Integer.parseInt(cond[0].a);
            int var2 = vars.get(cond[0].b);
            while (ifCondition(var1,var2,cond[0].operation)) { //Run while loop

                for (int i = 0; i < code.length; i++) { //Step through the code
                    if (code[i].code.contains("{")) {curlyCount++;}
                    if (code[i].code.contains("}")) {curlyCount--;}
                    if (code[i].code.contains("if")) { //If there is a nested if statement, recursion!
                        OperationBlocks[] toSend;
                        toSend = getToOperationSubset(cond, code, i);
                        int varOne;
                        int varTwo;
                        try {
                            varOne = Integer.parseInt(toSend[0].a);
                            varTwo = Integer.parseInt(toSend[0].b);
                        } catch (NumberFormatException e) {
                            //Note: having more than one nested if statement won't work atm
                            if (conditionHasVariable(toSend[0].a + " ", vars)) {
                                varOne = vars.get(toSend[0].a);
                                varTwo = Integer.parseInt(toSend[0].b);
                            } else {
                                varTwo = vars.get(toSend[0].b);
                                varOne = Integer.parseInt(toSend[0].a);
                            }

                        }
                        CodeBlocks[] codeToSend = getSubsetCurlies(code, i, cond.length);
                        if (codeToSend == null) {
                            return new float[]{0, 0.25F};
                        } //If there was a syntax error, return null
                        newDmg = runIfStatement(codeToSend, varOne, varTwo, toSend[0].operation, toSend, newDmg, vars);
                    } else if (code[i].code.contains("while") && i > 0) {
                        return new float[] {0,1}; //If chained while loop, stack overflow error
                    }
                    if (curlyCount == 1) { //Do individual lines only if it isn't in another block
                        newDmg = runIndividualLines(code[i].code,newDmg);
                        vars = incrementVariables(vars,code[i].code);
                        var2 = vars.get(cond[0].b);

                    }

                }
                sideCounter++;
                if (sideCounter >= 10) {
                    break;
                }
            }
        }
        return newDmg;
    }

    /**
     * findBlockErrors - checks if close curlies are in the wrong spot, or if there are unnecassary curlies
     * @param latestCode - the array of codeblocks
     * @return boolean - whether there is a bug in the enemy's code
     */
    private boolean findBlockErrors(CodeBlocks[] latestCode) {
        CodeBlocks[] subset = new CodeBlocks[latestCode.length - 4];
        System.arraycopy(latestCode,2,subset,0,latestCode.length - 4); //Get everything except the class and method lines
        int curlyCount = 0;
        for (int i = subset.length - 1; i > 0; i--) {
            //Checks if same sided curlies are on the same line in the code
            if ((subset[i].code.contains("{") && subset[i - 1].code.contains("{"))) {
                return true; //the bot has a bug if there is more than one open/close curlies in the same line!
            }
            //------------------------------Check if there is the same number of open curlies and close curlies!
            curlyCount += helper.countNumOccurences("{",subset[i].code);
            curlyCount -= helper.countNumOccurences("}",subset[i].code);
        }
        if (curlyCount == 0) {
            return false;
        }
        return true;
    }

    /**
     * ifCondition - checks if the condition is true or false
     * @param var1 - the intiger variable (first one)
     * @param var2 - the integer variable (second one)
     * @param cond - the operator (i.e >=)
     * @return boolean - if the condition is true or false
     */
    private boolean ifCondition(int var1, int var2, String cond) {
        //Compare the variables based on the operator (incomplete!)
        switch(cond) {
            case "==": return var1 == var2;
            case ">=": return var1 >= var2;
            case "<=": return var1 <= var2;
            case "!=": return var1 != var2;
            case ">": return var1 > var2;
            case "<": return var1 < var2;
            default: return false;
        }
    }

    /**
     * runIfStatement - runs the if statement, and everything inside that statement block
     * @param statementBlock - all codes inside the if statement
     * @param var1 - the first variable in the if statement
     * @param var2 - the second variable in the if statement
     * @param operator - the operator for the condition
     * @param condos - the conditions involved in the if statement
     * @param damage - the array of damage indicating the player's and enemy's damage
     * @return float[] - the player and enemy's damage
     */
    private float[] runIfStatement(CodeBlocks[] statementBlock, int var1, int var2, String operator, OperationBlocks[] condos, float[] damage,
                                   HashMap<String,Integer> variables) {

        int curlyCount = -1;
        for (int i = 0; i < statementBlock.length; i++) {
            System.out.println(statementBlock[i].code);
        }
        System.out.println("________---");
        float[] dmg = Arrays.copyOf(damage,damage.length);
        OperationBlocks[] conds = Arrays.copyOf(condos, condos.length);
        if (ifCondition(var1,var2,operator)) { //Proceed only if the operators with variables are true (i.e var1 > var2)
            for (int i = 1; i < statementBlock.length; i++) { //Step through the lines of code in the if statement
                variables = incrementVariables(variables,statementBlock[i].code);
                if (statementBlock[i].code.contains("{")) {
                    curlyCount++;
                } else if (statementBlock[i].code.contains("}")) {curlyCount--;}
                if (statementBlock[i].code.contains("if")) { //If there is a nested if statement, recursion!
                    OperationBlocks[] newCond = new OperationBlocks[conds.length - 1];
                    newCond = getToOperationSubset(conds,statementBlock,i);
                    int varOne;
                    int varTwo;
                    try {
                        varOne = Integer.parseInt(newCond[0].a);
                        varTwo = Integer.parseInt(newCond[0].b);
                    } catch (NumberFormatException e) {
                        //Note: having more than one nested if statement won't work atm
                        if (conditionHasVariable(newCond[0].a+" ", variables)) {
                            varOne = variables.get(newCond[0].a);
                            varTwo = Integer.parseInt(newCond[0].b);
                        } else {
                            varTwo = variables.get(newCond[0].b);
                            varOne = Integer.parseInt(newCond[0].a);
                        }

                    }
                    CodeBlocks[] codes = getSubsetCurlies(statementBlock, i,conds.length);
                    if (codes == null) {return new float[]{0,0.25F};} //If there was a syntax error, return null
                    dmg = runIfStatement(codes, varOne, varTwo, newCond[0].operation, newCond,damage,variables);
                } //change the parameter to new subset

                else if (statementBlock[i].code.contains("while") && i > 0) { //Run while loop again if there is a nested while loop
                    CodeBlocks[] toSend = getSubsetCurlies(code,i,conds.length);
                    System.out.println("I AM HERE");
                    if (toSend == null) {return new float[] {0,1};}
                    OperationBlocks[] toSendOperations = getToOperationSubset(conds,code,i);
                    dmg = runWhileLoop(toSend,toSendOperations,variables,dmg);
                }

                if (curlyCount == 0) {
                    dmg = runIndividualLines(statementBlock[i].code, damage);
                }
            }
        }
        return dmg;
    }

    /**
     * getSubsetCurlies - get the block of code (i.e an if statement, and everything inside of it)
     * @param code - the array of code
     * @param startLine - the startline of the subset
     * @return CodeBlocks[] - a subset of code to be used for other operations
     */
    private CodeBlocks[] getSubsetCurlies(CodeBlocks[] code, int startLine, int conditionLength) {
        try {
            // Initialize variables to keep track of curly brace count and the end index of the subset
            int amount = 0;
            int endIndex = 0;
            // Iterate through the code starting from the startLine
            for (int i = startLine + 1; i < code.length; i++) {
                // Check if the current line contains an opening curly brace
                if (code[i].code.contains("{")) {
                    // If so, increment the count
                    amount += 1;
                }
                // Check if the current line contains a closing curly brace
                else if (code[i].code.contains("}")) {
                    // If so, decrement the count
                    amount -= 1;
                }
                // Check if the count has reached zero
                if (amount == 0) {
                    // If so, set the endIndex to the current index and break out of the loop
                    endIndex = i;
                    break;
                }
            }
            // Create a new CodeBlocks array to hold the subset
            CodeBlocks[] subset = new CodeBlocks[endIndex - startLine + 1];
            // Copy the subset of code from the original array to the new array
            System.arraycopy(code, startLine, subset, 0, endIndex - startLine + 1);
            // Return the subset
            return subset;
        }
        // Catch any exceptions
        catch(Exception e) {
            // Return null if there is any exception
            return null;
        }
    }


    /**
     * runIndividualLines - runs individual lines in a code (i.e checks for prints, attacks, and any other thing)
     * @param st - the line of code in string version
     * @param damage - the current player and enemy damage
     * @return float[2] - the enemy player and enemy damage
     */
    private float[] runIndividualLines(String st, float[] damage) {
        System.out.println("String is: "+st);
        if (st.contains("System.out.print")) { //printing does 20 damage
            damage[0]+=0.2F;
            System.out.println("PRINTING: "+st);
        } else if (st.contains("attack")) {
            damage[0]+=0.4F; //40 damage (balance this?)
            System.out.println("ATTACKING: "+st);
        } else {

        }
        return damage;
    }

    /**
     * ifShouldRun - checks if the if statement should run (i.e if it is an else if, run it only if the parent if did not run)
     * @param parentRan - a boolean indicating if the parent ran
     * @param containsElse - a boolean indicating it the new if statement contains an else
     * @return boolean - whether the if statement should run or not
     */
    private boolean ifShouldRun(boolean parentRan, boolean containsElse) {
        if (parentRan && containsElse) {return false;}
        else if (!parentRan && containsElse) {return true;}
        else if (!containsElse && parentRan) {return true;}
        else if (!containsElse && !parentRan) {return true;}
        return false;
    }


    /**
     * getToOperationSubset - get the new operation block to send for the new subset of code that is being run
     * @param givenOperation - the current operations provided
     * @param code - the array of code
     * @param currentCodeIndex - the currentIndex to get the subset of
     * @return OperationBlocks[] - the new operationblocks that can be used in the subset
     */
    private OperationBlocks[] getToOperationSubset(OperationBlocks[] givenOperation, CodeBlocks[] code, int currentCodeIndex) {
        int numIfsPrior = 0;
        for (int i = 0; i < currentCodeIndex; i++) {
            if (code[i].code.contains("if") || code[i].code.contains("while")) {
                numIfsPrior++;
            }
        }
        OperationBlocks[] toSend = new OperationBlocks[givenOperation.length - numIfsPrior];
        try {
            System.arraycopy(givenOperation, numIfsPrior, toSend, 0, toSend.length);
        } catch(Exception e) {
            System.arraycopy(givenOperation,0,toSend,0,toSend.length);
        }
        return toSend;
    }
    /**
     * incrementVariables - increase/ decrease variables in the code
     * @param variables - a hashmaps of variables and their values
     * @param codeLine - the current code to determine if variables should be incremented
     * @return - a new hashmap of variables and their values
     */
    @SuppressWarnings("NewApi")
    private HashMap<String, Integer> incrementVariables(HashMap<String, Integer> variables, String codeLine) {
        // Create a set of keys for the variables HashMap
        Set<String> keysForVar = variables.keySet();
        // Create a new HashMap to store the updated variables
        HashMap<String, Integer> vars = new HashMap<>(variables);
        // Convert the set of keys to an array
        String[] keys = keysForVar.toArray(new String[keysForVar.size()]);
        // Iterate through the array of keys
        for (int i = 0; i < keys.length; i++) {
            // Check if the code line contains the current key and the "+=" operator
            if (codeLine.contains(keys[i]+" ") && codeLine.contains("+=")) {
                // Split the code line by spaces and get the last element
                String[] splits = codeLine.split(" ",-2);
                splits = splits[splits.length - 1].split(";");
                // Get the number to be added to the variable
                int num = Integer.parseInt(splits[0]);
                // Add the number to the current value of the variable and update the value in the HashMap
                vars.replace(keys[i],num+vars.get(keys[i]));
            } else if (codeLine.contains(keys[i]+" ") && codeLine.contains("*=")) {
                String[] splits = codeLine.split(" ", -2);
                splits = splits[splits.length - 1].split(";");
                int num = Integer.parseInt(splits[0]);
                vars.replace(keys[i], num * vars.get(keys[i]));
            }
        }
        return vars;
    }


    /**
     * conditionHasVariable - checks if a line of code has variables in it
     * @param cond - the codeLine
     * @param vars - the hashmap of variables
     * @return boolean - whether the code has variables in it
     */
    public boolean conditionHasVariable(String cond, HashMap<String,Integer> vars) {
        Set<String> keysForVar = vars.keySet();
        String[] keys = keysForVar.toArray(new String[keysForVar.size()]);
        for (int i = 0; i < keys.length; i++) {
            if (cond.contains(keys[i]+" ")) {
                return true;
            }
        }
        return false;
    }
}

