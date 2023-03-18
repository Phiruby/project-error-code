package com.highvoltage.error;

/**
 * CodeBlocks class is used to represent a line of code!!
 */
public class CodeBlocks {
    /** The code string */
    String code;
    /** The condition string */
    String condition;
    /** The left position of the code block */
    float left;
    /** The top position of the code block */
    float top;
    /** The right position of the code block */
    float right;
    /** The bottom position of the code block */
    float bottom;
    /** The number of tabs for the code block */
    int tabs;
    /** Indicates if the code block is editable */
    boolean isEditable;

    /**
     * Constructor for the CodeBlocks class.
     * @param codeString The code string for the code block.
     * @param editable Indicates if the code block is editable.
     * @param tabsIn The number of tabs for the code block.
     */
    public CodeBlocks(String codeString, boolean editable, int tabsIn) {
        code = codeString;
        isEditable = editable;
        tabs = tabsIn;
    }

    /**
     * setTabs - set the number of tabs for a line of code
     * @param tabNum - the number of tabs
     */
    public void setTabs(int tabNum) {tabs = tabNum;}

    /**
     * setIsEditable - set the editable status of the code block
     * @param editable - true if the code block is editable, false otherwise
     */
    public void setIsEditable(boolean editable) {
        isEditable = editable;
    }

    /**
     * setPos - set the position of the code block on the screen
     * @param x - the left position
     * @param y - the top position
     * @param width - the width of the code block
     * @param height - the height of the code block
     */
    public void setPos(float x, float y, float width, float height) {
        left = x;
        top = y;
        right = x + width;
        bottom = y - height;
    }

    /**
     * getPos - get the position of the code block on the screen
     * @return an array of floats that contains the values of left, top, right, and bottom
     */
    public float[] getPos() {
        return new float[]{left, top, right, bottom};
    }

    /**
     * setCodeString - set the code string for the code block
     * @param text - the code string
     */
    public void setCodeString(String text) {
        code = text;
    }
}
