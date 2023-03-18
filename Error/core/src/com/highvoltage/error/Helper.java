package com.highvoltage.error;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Helper {

    /**
     * calculateMinDist - get the shortest distance of a player from an enemy
     * @param enemyPos - the array of enemy positions
     * @return float - the shortest distance
     */
    public float calculateMinDist(float enemyPos[][]) {
        float[] dist = new float[0];
        for (int i = 0; i < enemyPos.length; i++) {
            float enX = enemyPos[i][0];
            float enY = enemyPos[i][1];
            float distance = (float) Math.pow(Math.pow(enX,2) + Math.pow(enY,2),0.5);
            dist = Arrays.copyOf(dist,dist.length + 1);
            dist[dist.length - 1] = distance;
        }
        Arrays.sort(dist);
        return dist[0];
    }

    /**
     * dialogueToDisplay - get the string to display for a dialogue, based on time
     * @param finalString - the final dialogue sentance to be displayed
     * @param textSpeed - the speed at which the text should show (chars / second)
     * @param deltaTime - the time that has passed so far
     * @return String - the string to display on a screen
     */
    public String dialogueToDisplay(String finalString, float textSpeed, float deltaTime) {
        float stringCompleted = 0;
        stringCompleted += textSpeed * deltaTime;
        int charToDisplay = (int) stringCompleted;
        if (charToDisplay > finalString.length()) {
            return finalString;
        }
        try {
            return finalString.substring(0, charToDisplay - 1);
        } catch (StringIndexOutOfBoundsException e) {
            return finalString;
        }
    }

    /**
     * countNumLinesWithString - counts the number of lines in a code with a particular string
     * @param e - the string to look for
     * @param codes - the bot's code
     * @return int - the number of lines in the code with this string
     */
    public int countNumLinesWithString(String e, CodeBlocks[] codes) {
        int count = 0;
        for (int i = 0; i < codes.length; i++) {
            if (codes[i].code.contains(e)) {
                count += 1;
            }
        }
        return count;
    }

    /**
     * readFileToGetCode - reads an array of code, got by the launcher, and converts into the codeblocks class
     * @param codeString - the array of code in string form
     * @param randNum - which set of code to read
     * @return CodeBlocks[] - the array of bot's code
     */
    public CodeBlocks[] readFileToGetCode(String[] codeString, int randNum) throws IOException {
        //Change below to a relative path
        int codeNumber = 0;
        boolean toAdd = false;
        CodeBlocks[] code = new CodeBlocks[0];
//        Gdx.files.internal("codes.txt");
        // Declaring a string variable
        int curlyCount = 0; //Curly count (to calculate indentation)
        int count = 0;
        boolean prevWasIf = false;
        //Step through the text file
       for (int i = 0; i < codeString.length; i++) {
           String st = codeString[i].trim();
           //If this tring is found, break!
           if (st.contains("CODENAMETOBREAK")) {
               toAdd = true;
           }
           //System.out.println(codeNumber+"; "+randCodeToChoose);
           if (codeNumber == randNum) {
               if (st.contains("CODENAMETOBREAK")) {
                   break;
               }
               System.out.println(i+"; "+codeString[i]);
               if (st.contains("}")) {
                   curlyCount--;
               }

               if (!prevWasIf) { //Add the line of code into code blocks ONLY if previous line was not an if (otherwise,
                   //the next line will be a condition, which has a different array)
                   code = Arrays.copyOf(code, code.length + 1);
                   code[count] = new CodeBlocks(st, getIfEditable(st), curlyCount);
                   count++;
               }
               prevWasIf = false;
               if (st.contains("if") || st.contains("while")) { //Set if to true if there is an if in the string!
                   prevWasIf = true;
               }
               if (st.contains("{")) {
                   curlyCount++;
               } //Add and subtract curly count if it is found
           }
           if (toAdd) {codeNumber++; toAdd = false;}
       }
        return code;
    }

    /**
     * getCodeConditions - get the conditions in the code and convert it to the OperationBlocks class
     * @param codeString - the array of string of the code
     * @param randNum - the code number to read
     * @return operationblocks[] - the array of operation blocks
     */
    public OperationBlocks[] getCodeConditions(String[] codeString, int randNum) throws IOException {
        OperationBlocks[] conditions = new OperationBlocks[0];
        boolean toAdd = false;
        int codeCount = 0;
        // Declaring a string variable
        int curlyCount = 0;
        int count = 0;
        boolean prevWasIf = false;
        for (int i = 0; i < codeString.length; i++) {
            String st = codeString[i].trim();
            //This method is almost the same as the one above!
            if (st.contains("CODENAMETOBREAK")) {toAdd = true;}
            if (codeCount == randNum) {
                if (st.contains("CODENAMETOBREAK")) {
                    break;
                }

                if (st.contains("}")) {
                    curlyCount--;
                }
                System.out.println("All: " + st + " end");
                if (prevWasIf) { //Only add conditions if the previous line of code had an if statement!
                    conditions = Arrays.copyOf(conditions, conditions.length + 1);
                    System.out.println("CODESTRING: " + st + " end");
                    for (int u = 0; u < st.length(); u++) {
                        System.out.print("u: " + st.charAt(u) + ", ");
                    }
                    //get the number by splitting the string and taking the last element
                    String[] stringos = st.split(" ",-2);
                    conditions[count] = new OperationBlocks(stringos[1], true, 0, "" + st.charAt(0), stringos[stringos.length - 1]);
                    count++;
                    System.out.println(conditions[count - 1].a + conditions[count - 1].code + conditions[count - 1].b);

                }
                prevWasIf = false;
                if (st.contains("if") || st.contains("while")) {
                    prevWasIf = true;
                }

                if (st.contains("{")) {
                    curlyCount++;
                }
            }
            if (toAdd) {codeCount++; toAdd = false;}
        }
        return conditions;
    }

    /**
     * getVariables - gets all variables in the code, and return their names in an array of string
     * @param codeString  - the string array of code
     * @return
     */
    public String[] getVariables(CodeBlocks[] codeString) {
        String[] varNames = new String[0];
        for (int i = 0; i < codeString.length; i++) {
            if (codeString[i].code.contains("int ") ) {
                varNames = Arrays.copyOf(varNames,varNames.length+1);
                varNames[varNames.length - 1] = ""+codeString[i].code.charAt(4); //Assuming the variable is only one letter
            }
        }
        return varNames;
    }

    /**

     This method takes an array of CodeBlocks as a parameter and returns an array of integers representing the values of the variables declared in the code.
     @param codeString an array of CodeBlocks representing the code
     @return an array of integers representing the values of the variables declared in the code
     */
    public int[] getVariableValues(CodeBlocks[] codeString) {
        int[] val = new int[0];
        for (int i = 0; i < codeString.length; i++) {
            if (codeString[i].code.contains("int ")) {
                //split the line of code to get the variable value
                String[] splits = codeString[i].code.split(" ",-2);
                splits = splits[splits.length - 1].split(";",-2);
                int num = Integer.parseInt(splits[0]);
                //add the variable value to the array
                val = Arrays.copyOf(val,val.length+1);
                val[val.length - 1] = num;
            }
        }
        return val;
    }

    /**
     * Get the operation from a code line
     * @param codeLine - the line of code
     * @return - the operation
     */
    public String getOperation(String codeLine) {
        for (int i = 0; i < codeLine.length() - 1; i++) {
            String character = ""+codeLine.charAt(i);
            String nextCharacter = ""+codeLine.charAt(i+1);
            if (character.equals("=") && nextCharacter.equals("=")) {
                return "==";
            } else if (character.equals(">") && nextCharacter.equals("=")) {
                return ">=";
            } else if (character.equals("<") && nextCharacter.equals("=")) {
                return "<=";
            } else if (character.equals("!") && nextCharacter.equals("=")) {
                return "!=";
            }
        }
        return null;
    }

    /**
     * Check if a line of code is editable
     * @param string - the line of code
     * @return - true if editable, false otherwise
     */
    private boolean getIfEditable(String string) {
        if (string.contains("while")||string.contains("if")||string.equals("}")||string.equals("{")||string.contains("else")) {
            return true;
        }
        return false;
    }

    /**
     * Get a new array with an element removed
     * @param removeIndex - the index of the element to remove
     * @param array - the original array
     * @return - the new array
     */
    public CodeBlocks[] getNewArray(int removeIndex, CodeBlocks[] array) {
        CodeBlocks[] newArray = new CodeBlocks[array.length - 1];
        System.arraycopy(array,0,newArray,0,removeIndex);
        System.arraycopy(array,removeIndex+1,newArray,removeIndex,array.length - removeIndex - 1);
        return newArray;
    }

    /**
     * Method to calculate the distance between two points in 2D space
     * @param x1 - x-coordinate of point 1
     * @param y1 - y-coordinate of point 1
     * @param x2 - x-coordinate of point 2
     * @param y2 - y-coordinate of point 2
     * @return the distance between the two points as a float
     */
    public float calculateDistance(float x1, float y1, float x2, float y2) {
        double toReturn = Math.pow(Math.pow(x2-x1,2) + Math.pow(y2-y1,2),0.5);
        System.out.println((float)toReturn);
        return (float) toReturn;
    }

    /**
     * Method to count the number of occurences of a character in a string
     * @param character - the character to count the occurences of
     * @param wholeString - the string to search through
     * @return the number of occurences of the character in the string
     */
    public int countNumOccurences(String character, String wholeString) {
        int count = 0;
        for (int i = 0; i < wholeString.length(); i++) {
            if (wholeString.charAt(i) == character.charAt(0)) {
                count++;
            }
        }
        return count;
    }
}
