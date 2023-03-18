package com.highvoltage.error;

import java.util.HashMap;
import java.util.Map;

public class Variables {
    HashMap<String, Integer> var = new HashMap<>();
    /**
     * This method will add variables to the hashmap
     * @param variable - the var name
     * @param varValue - the corresponding value
     */
    public void addVariable(String variable, int varValue) {
        var.put(variable,varValue);
    }
    /**
     * get the variables in the code
     * @return - the hashmap of variables
     */
    public HashMap<String,Integer> getVariables() {return var;}

}
