package com.highvoltage.error;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.Font;
import java.util.Arrays;

public class Items {
    CodeBlocks[] items = new CodeBlocks[0];
    OperationBlocks[] conditions = new OperationBlocks[0];
    CodeBlocks activeItem;
    int itemIndex;
    int numItems = 0;
    int numOperators = 0;
    OperationBlocks activeCond;
    Helper helper = new Helper();

    /**
     * addItem - adds an item to the player's inventory
     * @param itemName - the itemName in string form
     */
    public void addItem(String itemName) {
        items = Arrays.copyOf(items,items.length + 1);
        items[numItems] = new CodeBlocks(itemName, true,0);
        numItems++;
    }

    /**
     * addItem - adds an item to the player's inventory
     * @param itemName - an operation block (specifically to add a condition)
     */
    public void addItem(OperationBlocks itemName) {
        conditions = Arrays.copyOf(conditions,conditions.length + 1);
        conditions[numOperators] = itemName;
        numOperators++;
    }

    /**
     * removeItem - removes an item from the player's inventory
     * @param item - the item index
     * @param isCond - whether the item to remove is a condition or not
     */
    public void removeItem(int item, boolean isCond) {
        if (!isCond) { //Run this only if the item to remove is not a condition
            for (int i = item; i < numItems - 1; i++) {
                items[i] = new CodeBlocks(items[i + 1].code, items[i + 1].isEditable, 0);
            }

            if (items.length > 0) {
                numItems--;
                items = Arrays.copyOf(items, numItems);
            }
        }
        else {
            for (int i = item; i < numOperators - 1; i++) {
                conditions[i] = new OperationBlocks(conditions[i+1].operation,true,0,
                        conditions[i+1].a,conditions[i+1].b);
            }
            if (conditions.length > 0) {
                numOperators--;
                conditions = Arrays.copyOf(conditions,numOperators);
            }
        }
    }

    /**
     * getActiveItemType - Determines the type of the active item.
     * @return a string representing the type of the active item.
     * "Operation" if the active item's code is "==", ">", "<", or "!=",
     * "Value" if the active item's code is a single character and not "{}"
     * "Condition" if the active item's code contains "==", ">", "<", or "!=",
     * "Hammer" if the active item's code contains the word "Hammer",
     * "Logic" if the active item's code contains the words "if" or "while",
     * "Curly" if the active item's code contains "{}"
     * "Tool" if none of the above conditions are met.
     *
     */
    public String getActiveItemType() {
        try {
            //-----------------------------Check for the type of active item------------------------------------\\
            if((activeItem.code.equals("==") || activeItem.code.equals(">") ||
                    activeItem.code.equals("<") || activeItem.code.equals("!="))) {
                return "Operation";
            }else if(activeItem.code.length()<2&&!activeItem.code.equals("{")&&!activeItem.code.equals("}")){
                return "Value";
            }else if (activeItem.code.contains("==") || activeItem.code.contains(">") ||
                activeItem.code.contains("<") || activeItem.code.contains("!=")) {
                return "Condition";
            } else if (activeItem.code.contains("Hammer")) {
                return "hammer";
            } else if (activeItem.code.contains("if")||activeItem.code.contains("while")){
                return "Logic";
            } else if (activeItem.code.contains("{") || activeItem.code.contains("}")) {
                return "Curly";
            }
            else{
                return "Tool";
            }
        } catch (NullPointerException e) {
            return null;
        }
    }
    /**
     * getLastVar - get the last variable
     * @param type - the item type
     * @return the latest variable
     */
    public String getLastVar(String type){
        for(int i=items.length-1;i>0;i--){
            activeItem = items[i];
            if(type.equals(getActiveItemType())){
                removeItem(i,false);
                return activeItem.code;
            }
        }
        return "";
    }

    /**
     * getItems - get the player's items
     * @return - get the player's items (includes hammer, curlies, etc.)
     */
    public CodeBlocks[] getItems() {
        return items;
    }

    /**
     * getConditions - get the player's conditions
     * @return OperationBlocks - the player's conditions
     */
    public OperationBlocks[] getConditions() {
        return conditions;
    }

    /**
     * displayItems - displays the player's conditions on a separate GUI
     * @param conditions - the conditions the player has
     * @param spriteTexture - the texture of the sprite to display over each condition
     * @param batch - the sprite batch to draw on
     * @param startY - the starting Y position
     * @param startX - the starting X position
     * @param screenWidth - the width of the screen
     * @oaram screenHeight - the height of the screen
     */

    public void displayItems(CodeBlocks[] code, Texture spriteTexture, SpriteBatch batch, float startY, float startX,
                             float screenWidth, float screenHeight) {
        BitmapFont font = new BitmapFont();
        float width = 20;
        float ppu = Gdx.graphics.getWidth() / width;
        float scale = ppu / 100;
        font.getData().setScale(4*scale);
        font.setColor(Color.WHITE);
        float endX = screenWidth - startX;
        float endY = screenHeight - startY;
        float currentX = startX;
        float currentY = startY;
        //--------------------------------Displays conditions the player has-------------------------\\
        for (int i = 0; i < code.length; i++) {
            Sprite enSprite = new Sprite(spriteTexture);
            enSprite.setCenter(currentX+screenWidth/30,currentY+screenHeight/20);
            enSprite.draw(batch);
            font.draw(batch,code[i].code,currentX,currentY);
            currentX += screenWidth/5;
            if (currentX > endX) {
                currentY -= screenWidth/8;
                currentX = startX;
            }
        }
    }

    /**
     * onClick - runs anything necassary after the user clicks on the screen (checks for active items and conditions)
     * @param screenHeight - the screen height
     * @param screenWidth - the screen width
     */
    public void onClick(float screenWidth, float screenHeight) {
//-----------------------Reworked; now, it equips the item that is closest to the tap (provided that the tap is within the zone)
        //Get mouse pos
        float mouseX = Gdx.input.getX();
        float mouseY = screenHeight - Gdx.input.getY();
        if (mouseX > 0 && mouseX < screenWidth*5/24) { //Check if the tap is within the rectangle
            int smallestIndex = getClosestItem(conditions,items,mouseX,mouseY); //Get closest item index to the tap
            System.out.println("--------------------------");
            if (smallestIndex >= conditions.length) {
                //If the index is higher than conditions length, then it is closer to an item

                //equp the item and unequp the condition
                if (activeItem == null || !activeItem.equals(items[smallestIndex - conditions.length])) {
                    activeItem = items[smallestIndex - conditions.length];
                    itemIndex = smallestIndex - conditions.length;
                    activeCond = null;
                }
                else {
                    //Unequip item
                    activeItem = null;
                }

            } else {
                //Equip the condition, if the index is length then condition length
                if (activeCond != null){
                    //Unequips the condition
                    if (activeCond.equals(conditions[smallestIndex])) {
                        activeCond = null;
                    }
                    else { //Equip the item!
                        activeCond = conditions[smallestIndex];
                        activeItem = null;
                    }
                } else { //Equip the item!
                    activeCond = conditions[smallestIndex];
                    activeItem = null;
                }
            }
        }
        System.out.println(activeItem);
    }

    /**
     * getItemDrop - drop a random item when a player wins!
     * @param medProb - the probability of dropping a medium
     * @param legProb - the probability of dropping a legendary
     * @return String[] - an array of conditions that the bot has dropped
     */
    public String[] getItemDrop(float medProb, float legProb) {
        String[] items = new String[0];
        int count = 0;
        String[] medItems = new String[] {"if","!=","<=","=="}; //medium items
        String[] legendary = new String[] {"bandage","glasses"}; //legendary items
        double randNum = Math.random();
        if (randNum < medProb) { //if med item, get a random med item
            items = Arrays.copyOf(items,items.length+1);
            items[count] = medItems[(int) (medItems.length*Math.random())];
            count++;
        }
        if (randNum < legProb) { //if legendary item, get random legendary item
            items = Arrays.copyOf(items,items.length+1);
            items[count] = legendary[(int) (legendary.length*Math.random())];
            count++;
        }
        return items;
    }

    /**
     * getActiveItem - gets the current active item
     * @return CodeBlocks - the active item
     */
    public CodeBlocks getActiveItem() {return activeItem;}

    /**
     * getActiveCond - get the equipped active condition
     * @return - get the equipped condition
     */
    public OperationBlocks getActiveCond() {return activeCond;}
    /**
     * getItemIndex - get the current active item's index
     * @return - the integer of item index
     */
    public int getItemIndex(){
        return itemIndex;
    }
    /**
     * getItemsLength - get the total length of the player's inventory
     * @return - length of conditions and items of the player
     */
    public int getItemsLength() {return items.length + conditions.length;}

    /**
     * getClosesItem - get the closest item to a user's tap
     * @param items - the array of codeblocks
     * @param conditions - The array of condition (class OperationBlocks)
     * @return CodeBlocks[] - a new array of code, with closed curlies
     */
    private int getClosestItem(OperationBlocks[] conditions, CodeBlocks[] items, float x, float y) {
        for (int i = 0; i < conditions.length + items.length; i++) {
            if (i < conditions.length) {
                System.out.println(conditions[i].code+" Cond");
            } else {
                System.out.println(items[i - conditions.length].code+" Item");
            }
        }
        //------------------------Get Distances--------------------------\\
        float[] dist = new float[conditions.length + items.length];
        for (int i = 0; i < conditions.length + items.length; i++) {
            if (i < conditions.length) {
                dist[i] = helper.calculateDistance(x, y, conditions[i].getPos()[0], conditions[i].getPos()[1]);
            } else if (i >= conditions.length){
                dist[i] = helper.calculateDistance(x, y, items[i - conditions.length].getPos()[0], items[i - conditions.length].getPos()[1]);
            }
        }
        System.out.println("Distances: "+Arrays.toString(dist));

        //----------------------Find shortest distance--------------------\\
        float minVal = dist[0];
        int minIndex = 0;
        for (int i = 1; i < dist.length; i++) {
            if (minVal > dist[i]) {
                minIndex = i;
                minVal = dist[i];
            }
        }
        System.out.println(minIndex+" MINIMUM INDEX");
        return minIndex;
    }
}
