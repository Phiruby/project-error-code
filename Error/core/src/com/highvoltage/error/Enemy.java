package com.highvoltage.error;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Enemy {
    String[] enemyNames = {"Cup O' Java", "Battery Acid","Cup O' Joes","Enigma","Python","Snake","Hydra","Rattlesnake","Scratch","Charger","Sanic","Lasagna Cat","Octocat","Cthulhu","Kraken"};
    String image;
    String name;
    String text;
    String[] textOptions = {"Hello World","@#%!^@&!","join us","riddle me this...","#waterloogoals"};
    CodeBlocks[] code = new CodeBlocks[10];
    OperationBlocks[] conditions = new OperationBlocks[1];
    Color[] colors = {new Color(0.2F,0.44F,0.9F,1),new Color(0.65F,0.39F,0.82F,1),new Color(0.82F,0.26F,0.16F,1),new Color(0.78F,0.78F,0.78F,1),new Color(0.78F,0.78F,0.78F,1)};
    Color color;
    Random rand = new Random();
    boolean willSyntaxDebug = false;
    boolean willConditionDebug = false;
    int num;
    int lvl;
    int health;
    Helper helper = new Helper();
    int[] values;
    String[] vars;

    /**
     * Initialize the instance of enemy class
     * @param lvlIn - the level
     * @param healthIn - the enemy health
     * @param fileReadCode - the array of strings that are potential code
     */
    public Enemy(int lvlIn, int healthIn, String[] fileReadCode) throws IOException {
        System.out.println(Arrays.toString(fileReadCode));
        lvl = lvlIn;
        health = healthIn;
        num = rand.nextInt(2);
        if(lvl>=2&&lvl<5&&num==1){
            willSyntaxDebug=true;
        }else if(lvl>=5){
            willSyntaxDebug=true;
        }
        num = rand.nextInt(2);
        //check if enemy will debug
        if(lvl>=4&&lvl<7&&num==1){
            willConditionDebug=true;
        }else if(lvl>=7){
            willSyntaxDebug=true;
        }
        //rand num for enemy name
        num = rand.nextInt(enemyNames.length);

        name = enemyNames[num];
        image = enemyNames[num]+".png";
        color = colors[num%4];

        num = rand.nextInt(textOptions.length);
        text = textOptions[num];
        //Choose random code from the file
        int numCodes = 6;
        int randNum;
        //-------------Chose code by level----------------\\
        if (lvl <= 2) {
            randNum  = rand.nextInt(1);
        } else if (lvl <= 4) {
            randNum = rand.nextInt(2) + 2;
        } else {
            randNum  = numCodes - rand.nextInt(1);
        }

        //get conditions and lines in the code
        conditions = helper.getCodeConditions(fileReadCode, randNum);
        code = helper.readFileToGetCode(fileReadCode, randNum);
        code[0].setCodeString("public class "+name+" {");
        code[code.length-1].setIsEditable(false);
        code[code.length-2].setIsEditable(false);

        vars = helper.getVariables(code);
        values = helper.getVariableValues(code);

        for(int i=0;i<code.length;i++){
            if(code[i].code.contains("println")){
                code[i].setCodeString("System.out.println(''"+text+"'');");
            }
        }
    }



}
