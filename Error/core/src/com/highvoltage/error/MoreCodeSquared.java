package com.highvoltage.error;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import jdk.dynalink.Operation;
//TODO: fix bugs, while loops, items, make variables work even if it is second in the condition, equipping while loop
public class MoreCodeSquared extends ApplicationAdapter implements GestureDetector.GestureListener {
	private Stage stage;
	private SpriteBatch batch;
	private BitmapFont font;
	private Texture enemyTexture;
	private Sprite enSprite;
	private CodeBlocks[] code = new CodeBlocks[10];
	private String programState = "MainMenu";
	private OperationBlocks[] conditions = new OperationBlocks[1];
	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	private Color lightGreen = new Color(0.5F,0.66F,.18F,1);
	private Color offBlack = new Color(0.113F,0.094F,0.08F,1);
	private Color enemyColor;
	private Enemy enemy;
	private Boolean battleEndPopup = false;
	float[] damage = {0,0};
	String[] fileReadCode;
	float[] battleButtonBounds;
	//--------------------------Open World---------------------------------\\
	boolean justFought = false;
	private Sprite inventoryOutOfSpaceInterface;
	private Player player = new Player();
	private Sprite bgSprite;
	private SpriteBatch openWorldSB;
	private Sprite enemyArt;
	private Sprite enemyArt2;
	private Sprite playerDraw;
	private Sprite upArrow;
	boolean drawPopup = false;
	private Sprite downArrow;
	private Sprite rightArrow;
	private Sprite leftArrow;
	private Sprite inventoryIcon;
	float[][] butPos = new float[4][4];
	float[][] enemyPos = new float[2][4]; //Change length based on number of enemies
	Sprite[] enemyList = new Sprite[2];
	int lvl = 1;
	int moves = 0;
	boolean firstLaunch = true;
	boolean firstRun = true;
	boolean canSwitchToBattle = false;
	boolean canCloseMenu = false;
	int playerLevel = 1;

	float[] bgPos;
	//------------------------------------Claim Screen/Win Screen-----------------------------------\\
	private Sprite claimBut;
	String[] itemDrop;
	//------------------------------------Main Menu----------------------------------\\
	private Sprite background;
	private Sprite playBut;
	private Sprite settings;
	private Sprite controls;
	float deltaTime;
	//-----------------------------------Inventory------------------------------------\\
	private Sprite interfaceSprite;
	private Sprite conditionIcon;
	private Sprite backButton;
	//-------------------------------How-to Page-------------------------------------\\
	private Sprite objective;
	private Sprite bugging;
	String toDraw = "objective";

	private Sprite battleButton;
	private Sprite winScreenInterface;
	private DebugCode debugger;
	private String[] terminalText;
	private Helper helper = new Helper();
	float playerHealth = 5;
	float enemyHealth = 1;
	float startTime;
	float screenWidth;
	float screenHeight;
	float codeScroll;
	Variables vars = new Variables();
	float itemScroll;

	private Items playerItems = new Items();
	CodeBlocks activeItem = new CodeBlocks("",false,0);
	/**
	 * Set the code through the launcher
	 * @param codes - the array of string of each code line
	 */
	public void setCodeFileString(String[] codes) {
		fileReadCode = new String[codes.length];
		fileReadCode = codes;
	}

	//Default values for positions are: [[20.0, 20.0, 200.0, 200.0], [-500.0, 20.0, 200.0, 200.0]]#-480.0,-270.0#1#
	/**
	 * initialize player position and enemy level
	 * @param myEnemyPos - all enemy pos
	 * @param bgX, bgY - the background x and y
	 * @param level - the enemy level
	 */
	public void initializePos(float[][] myEnemyPos, float bgX, float bgY, int level) {
		enemyPos[0] = myEnemyPos[0];
		enemyPos[1] = myEnemyPos[1];
		bgPos = new float[] {bgX, bgY};
		lvl = level;
	}
	/**
	 * setItems - sets the player's items at the start (read from a file)
	 * @param itemNames - a string of item names in an array (read from a file)
	 */
	public void setItems(String[] itemNames) {
		String st;
		for (int i = 0; i < itemNames.length; i++) {
			st = itemNames[i];
			if(st.equals("==") || st.equals("!=") || st.equals(">=") || st.equals("<=")){
				playerItems.addItem(st);
			}else if (st.contains("==") || st.contains("!=") || st.contains(">=") || st.contains("<=")) {
				playerItems.addItem(helper.getOperation(st));
			} else if (!st.equals("")){
				playerItems.addItem(st);
			}
		}
	}

	/**
	 * setVars - initialized variables from the code
	 * @param var - the string of variable names
	 * @param val - the corresponding values of the variables
	 */
	public void setVars(String[] var, int[] val) {
		if (var.length != val.length) {throw new RuntimeException("The arrays do not have the same length");}
		for (int i = 0; i < var.length; i++) {
			vars.addVariable(var[i],val[i]); //add variables in the hashmap
		}
	}

	/**
	 * initialize all variables, sprites, textures, spritebatches, and font
	 */
	@Override
	public void create () {
		screenHeight = Gdx.graphics.getHeight();
		screenWidth = Gdx.graphics.getWidth();
		backButton = new Sprite(new Texture("BACK.png"));
		backButton.setScale(0.5F);

		battleButtonBounds = new float[] {screenWidth/2 + 2*screenWidth/17 + screenWidth/10, Gdx.graphics.getHeight() / 2 + screenHeight/35, Gdx.graphics.getWidth() / 10, Gdx.graphics.getHeight()/10};
		System.out.println(screenWidth+" DIMENSIONS "+ screenHeight);

		if (programState.equals("battleScreen")) {
			//System.out.println("Graphics x: "+Gdx.graphics.getWidth()+" Y: "+Gdx.graphics.getHeight());
			if (enemy==null) {
				try {
					//System.out.println("BOLA: " + Arrays.toString(fileReadCode));
					enemy = new Enemy(lvl, 1, fileReadCode);
					enemyList[0] = new Sprite(new Texture(enemy.image));
					setVars(enemy.vars,enemy.values);
				} catch (IOException e) {
					//System.out.println("HERE: " + e);
				}
			}
			enemyHealth = 1;
			playerHealth = 1;
			float width = 20;
			float ppu = Gdx.graphics.getWidth() / width;
			float scale = ppu/32;
			terminalText = new String[0];
			addTerminalText(enemy.name+" lvl"+enemy.lvl);
			enemyColor = enemy.color;
			batch = new SpriteBatch();
			font = new BitmapFont();
			font.setColor(Color.WHITE);

			camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			battleButton = new Sprite(new Texture("Battle!.png"));
			shapeRenderer = new ShapeRenderer();
			shapeRenderer.setAutoShapeType(true);
			font.getData().setScale(scale);
			enemyTexture = new Texture(Gdx.files.internal(enemy.image));
			enSprite = new Sprite(enemyTexture);
			enSprite.setScale(Gdx.graphics.getHeight()/500);
			enSprite.setPosition(Gdx.graphics.getWidth() *4/5 , Gdx.graphics.getHeight()*5/8);
			conditions = enemy.conditions;
			code = enemy.code;
			debugger = new DebugCode(code);
			battleButton.setCenter(screenWidth/9,screenHeight/20);
			battleButton.setScale(0.5F);
//			playerItems.addItem("Hammer");
//			playerItems.addItem(new OperationBlocks("==",true,0,"5","9"));
//			playerItems.addItem(new OperationBlocks(">=",true,0,"3","1"));
			stage = new Stage(new ScreenViewport());
			//stage.addActor(button);
			Gdx.input.setInputProcessor(new GestureDetector(this));

		} else if (programState.equals("loseScreen")) {
			//TODO: Remove the justFought variable and instead reset enemy and player positions
			justFought = true;
			System.out.println("Setting up lose screen");
			batch = new SpriteBatch();
			font.setColor(Color.WHITE);
			float width = 20;
			float ppu = Gdx.graphics.getWidth() / width;
			float scale = ppu / 100;
			font.getData().setScale(5*scale);
			interfaceSprite = new Sprite(new Texture("hologramInterface/Card X3/Card X6.png"));
			backButton.setCenter(3*screenWidth/4,screenHeight/10);
			interfaceSprite.setScale(screenWidth / interfaceSprite.getWidth(), screenHeight / interfaceSprite.getHeight());
			interfaceSprite.setCenter(screenWidth/2,screenHeight/2);

			//-------------------Remove all player items-------------------------\\
			int condLength = playerItems.getConditions().length;
			for (int i = 0; i < condLength; i++) {
				playerItems.removeItem(i,true);
			}


		} else if (programState.equals("winScreen")) {
			batch = new SpriteBatch();
			winScreenInterface = new Sprite(new Texture("ItemDrop.png"));
			winScreenInterface.setScale(screenWidth/winScreenInterface.getWidth(),screenHeight/winScreenInterface.getHeight());
			winScreenInterface.setCenter(screenWidth/2,screenHeight/2);
			claimBut = new Sprite(new Texture("Claim.png"));
			claimBut.setScale(0.5F);
			claimBut.setCenter(screenWidth/2,
					screenHeight/6);
			itemDrop = playerItems.getItemDrop(1,0.1F);
			Gdx.input.setInputProcessor(new GestureDetector(this));

		} else if (programState.equals("openWorld")) {
			font = new BitmapFont();
			float width = 20;
			float ppu = Gdx.graphics.getWidth() / width;
			float scale = ppu / 100;
			font.getData().setScale(4*scale);
			shapeRenderer = new ShapeRenderer();
			shapeRenderer.setAutoShapeType(false);
			openWorldSB = new SpriteBatch();
			playerDraw = new Sprite(new Texture(Gdx.files.internal("bug.png")));
			Texture background = new Texture(Gdx.files.internal("bigBackground.jpg"));
			inventoryIcon = new Sprite(new Texture("inventoryBut.png")); //Change the icon so it looks neater
			inventoryIcon.setCenter(screenWidth/2 - 1*inventoryIcon.getWidth()/2,0);
			inventoryIcon.setScale(0.5F);
			bgSprite = new Sprite(background);
			if (firstLaunch) {bgSprite.setPosition(bgPos[0],bgPos[1]);}
			else {bgSprite.setPosition(bgPos[0],bgPos[1]);}
			//bgSprite.setPosition(-Gdx.graphics.getWidth()/2,-Gdx.graphics.getHeight()/2);
			bgSprite.setScale(Gdx.graphics.getWidth()/960,Gdx.graphics.getHeight()/450);
			downArrow = new Sprite(new Texture(Gdx.files.internal("Individual Icons/blue-!arrowdown.png")));
			upArrow = new Sprite(new Texture(Gdx.files.internal("Individual Icons/blue-!arrowup.png")));
			rightArrow = new Sprite(new Texture(Gdx.files.internal("Individual Icons/blue-!arrowright.png")));
			leftArrow = new Sprite(new Texture(Gdx.files.internal("Individual Icons/blue-!arrowleft.png")));
			enemyArt = new Sprite(new Texture(Gdx.files.internal("Cup O' Java.png")));

			enemyArt2 = new Sprite(new Texture(Gdx.files.internal("Enigma.png")));

			enemyList[0] = enemyArt;
			enemyList[1] = enemyArt2;

			for (int i = 0; i < enemyPos.length; i++) {
				enemyList[i].setPosition(enemyPos[i][0], enemyPos[i][1]);
			}

			int sc = 5;
			downArrow.scale(sc);
			upArrow.scale(sc);
			rightArrow.scale(sc);
			leftArrow.scale(sc);
			downArrow.setPosition(-Gdx.graphics.getWidth()/3,-Gdx.graphics.getHeight()/4-sc*downArrow.getHeight());
			upArrow.setPosition(-Gdx.graphics.getWidth()/3,-Gdx.graphics.getHeight()/3+sc*downArrow.getHeight());
			leftArrow.setPosition(-Gdx.graphics.getWidth()/3-sc*downArrow.getWidth(),-Gdx.graphics.getHeight()/3+sc/3*downArrow.getHeight());
			rightArrow.setPosition(-Gdx.graphics.getWidth()/3+sc*downArrow.getWidth(),-Gdx.graphics.getHeight()/3+sc/3*downArrow.getHeight());
			butPos[0] = new float[]{-Gdx.graphics.getWidth() / 3 - sc * downArrow.getWidth(), -Gdx.graphics.getHeight() / 3 + downArrow.getHeight(),
					leftArrow.getWidth()*sc/2, leftArrow.getHeight()*sc}; //Left Button
			butPos[1] = new float[] {-Gdx.graphics.getWidth()/3+sc*downArrow.getWidth(),-Gdx.graphics.getHeight()/3+sc/2*downArrow.getHeight(),
			rightArrow.getWidth()*sc/2,rightArrow.getHeight()*sc}; //Right button
			butPos[2] = new float[] {-Gdx.graphics.getWidth()/3,-Gdx.graphics.getHeight()/3+sc*downArrow.getHeight(),
			upArrow.getWidth(),sc*upArrow.getHeight()}; // Up Arrow
			butPos[3] = new float[] {-Gdx.graphics.getWidth()/3,-1*Gdx.graphics.getHeight()/3,downArrow.getWidth(),
			downArrow.getHeight()*sc*4/5}; //Down arrow
			camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			Gdx.input.setInputProcessor(new GestureDetector(this));

		} else if (programState.equals("MainMenu")) {
			batch = new SpriteBatch();
			background = new Sprite(new Texture("png/BG.png"));
			background.setScale(Gdx.graphics.getWidth()/background.getWidth(),Gdx.graphics.getHeight()/background.getHeight());
			background.setCenter(screenWidth/2,screenHeight/2);
			playBut = new Sprite(new Texture("playBut.png"));
			playBut.setCenter(screenWidth/2,screenHeight/2);
			settings = new Sprite(new Texture("settingsBut.png"));
			settings.setCenter(screenWidth/2,screenHeight/3);
			Gdx.input.setInputProcessor(new GestureDetector(this));

		} else if (programState.equals("Inventory")) {
			batch = new SpriteBatch();
			font.setColor(Color.WHITE);
			float width = 20;
			float ppu = Gdx.graphics.getWidth() / width;
			float scale = ppu / 100;
			font.getData().setScale(5*scale);
			interfaceSprite = new Sprite(new Texture("hologramInterface/Card X3/Card X6.png"));
			conditionIcon = new Sprite(new Texture("hologramInterface/Icons/06.png"));
			backButton.setCenter(3*screenWidth/4,screenHeight/7);
			interfaceSprite.setScale(screenWidth / interfaceSprite.getWidth(), screenHeight / interfaceSprite.getHeight());
			interfaceSprite.setCenter(screenWidth/2,screenHeight/2);

		} else if (programState.equals("controls")) {
			objective = new Sprite(new Texture("objective.png"));
			bugging = new Sprite(new Texture("enemy bugging.png"));
			batch = new SpriteBatch();
			bugging.setCenter(screenWidth/2,screenHeight/2);
			bugging.setScale(screenWidth/bugging.getWidth(), screenHeight/bugging.getHeight());

			objective.setCenter(screenWidth/2,screenHeight/2);
			objective.setScale(screenWidth/objective.getWidth(),screenHeight/objective.getHeight());
			backButton.setCenter(15*screenWidth/16,9*screenHeight/10);
			Gdx.input.setInputProcessor(new GestureDetector(this));
		} else if (programState.equals("OutOfSpace")) {

			batch = new SpriteBatch();
			backButton.setCenter(7*screenWidth/8,screenHeight/9);
			inventoryOutOfSpaceInterface = new Sprite(new Texture("inventoryFull.png"));
			inventoryOutOfSpaceInterface.setCenter(screenWidth/2, screenHeight/2);
			inventoryOutOfSpaceInterface.setScale(screenWidth/inventoryOutOfSpaceInterface.getWidth(),screenHeight/inventoryOutOfSpaceInterface.getHeight());
			Gdx.input.setInputProcessor(new GestureDetector(this));

			int itemsLength = playerItems.getItems().length;
			int condLength = playerItems.getItemsLength() - itemsLength;
//			while (itemsLength+condLength > 9) { //The number can be changed to player's inventory size
//				if (condLength <= 1) {break;}
//				itemsLength = playerItems.getItems().length; //Get the number of items
//				condLength = playerItems.getConditions().length; //Get number of conditions
//				Random randIndex = new Random();
//				//Removes a random item for player inventory!
//				int randNum = randIndex.nextInt(condLength);
//				playerItems.removeItem(randNum+3, true); //Remove items from the player inventory
//			}
			playerItems.items = Arrays.copyOf(playerItems.items,15);
		}
	}

	/**
	 * draw everything on the screen, based on what is necessary!
	 */
	@Override
	public void render () {
		if (programState.equals("battleScreen")) {
			ScreenUtils.clear(0.2F, 0.2F, 0.2F, 0.2F);
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			//draw background
			drawRect(0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Color.WHITE); //img space
			//Player inventory background
			drawRect(screenWidth/96, Gdx.graphics.getHeight() - screenHeight/56, screenWidth*5/24, Gdx.graphics.getHeight() - screenHeight/28, lightGreen);

			//Battle button background
			drawRect(screenWidth/2 + 2*screenWidth/17, Gdx.graphics.getHeight() / 2 + screenHeight/35, Gdx.graphics.getWidth() / 10, Gdx.graphics.getHeight()/10, lightGreen);
			//Bot Code Background
			drawRect(5*screenWidth/24+screenWidth/42, Gdx.graphics.getHeight() - screenHeight/56, Gdx.graphics.getWidth()*6/11, Gdx.graphics.getHeight() - screenHeight/28, offBlack);

			//Main terminal Background
			drawRect(screenWidth*2/3+screenWidth/8, screenHeight/56, screenWidth*21/100, -screenHeight*6/10, offBlack);

			//Draw player health bar
			drawRect(Gdx.graphics.getWidth() * 2 / 7, Gdx.graphics.getHeight() * 1 / 10, Gdx.graphics.getWidth() / 5 * playerHealth, screenHeight/28,lightGreen);
			font.setColor(lightGreen);
			font.draw(batch, "Your Health: " + Math.round(playerHealth * 100), Gdx.graphics.getWidth() * 4 / 14, Gdx.graphics.getHeight() * 1 / 20);

			font.setColor(lightGreen);
			font.setColor(offBlack);
			//drawRect(screenWidth*2/3+screenWidth/8,screenHeight*6/10, screenWidth*19/100,-screenHeight*4/10,Color.RED);
			font.setColor(lightGreen);
			displayTerminalText(terminalText);
			//draw enemy code
			displayCode(code, Gdx.graphics.getHeight() / 15);

			stage.draw();
			battleButton.draw(batch);
			enSprite.draw(batch);
			batch.end();
		} else if (programState.equals("loseScreen")) {
			bgPos[0] = -(bgSprite.getWidth()*screenWidth/800)/7;
			bgPos[1] = -2*(bgSprite.getHeight()*screenHeight/450)/3;
			ScreenUtils.clear(0F, 0F, 0F, 0F);
			batch.begin();
			interfaceSprite.draw(batch);
			backButton.draw(batch);
			//font.draw(batch, "Return", backButton.getX() + backButton.getWidth()/20, backButton.getY() + backButton.getHeight());
			font.draw(batch, "You lost!",screenWidth/2 - font.getXHeight() * "You lost!".length()/2,screenHeight - screenHeight/8);
			if (firstRun) {startTime = System.nanoTime(); firstRun = false;}
			deltaTime = (System.nanoTime() - startTime) / 1000000000.0f;
			String textToDisplay = helper.dialogueToDisplay("You had to quickly rush to the hospital to minimize damage",30,deltaTime);
			font.draw(batch,textToDisplay,screenWidth/40,
					screenHeight/2 + screenHeight/100);
			batch.end();

		} else if (programState.equals("winScreen")) {
			ScreenUtils.clear(1F, 0.2F, 0.2F, 0.2F);
			batch.begin();
			winScreenInterface.draw(batch);
			claimBut.draw(batch);
			batch.end();
		} else if (programState.equals("openWorld")) {
			ScreenUtils.clear(1,1,1,1);
			openWorldSB.setProjectionMatrix(camera.combined);
			openWorldSB.begin(); //begin batch
			bgSprite.draw(openWorldSB);
			if (!drawPopup && !battleEndPopup) { //Draw the movement arrows only when the player has not encountered an enemy
				upArrow.draw(openWorldSB);
				downArrow.draw(openWorldSB);
				leftArrow.draw(openWorldSB);
				rightArrow.draw(openWorldSB);
				inventoryIcon.draw(openWorldSB);
			}
			//draw enemies
			for (int i = 0; i < enemyList.length; i++) {
				enemyList[i].draw(openWorldSB);
			}
			//move background and enemy
			checkMovement(battleEndPopup,drawPopup);
			if (helper.calculateMinDist(enemyPos) > 300) {
				justFought = false;
			}
			//move enemy to player after certain moves
			Random rand = new Random();
			int num = rand.nextInt(100);
			if(moves>=num+200){
				enemyPos[0][0] = playerDraw.getX();
				enemyPos[0][1] = playerDraw.getY();
				moves = 0;
			}
			openWorldSB.end();//end batch
			//draw popup chat
			if (drawPopup || battleEndPopup) {
				drawRect(Gdx.graphics.getWidth() / 6, Gdx.graphics.getHeight() / 4, Gdx.graphics.getWidth() - 2 * Gdx.graphics.getWidth() / 6,
						Gdx.graphics.getHeight() / 5, Color.WHITE);
			}
			openWorldSB.begin();
			//draw popup char when player hits enemy
			if (player.encounterEnemy(enemyPos,playerDraw.getX(),playerDraw.getY()+playerDraw.getHeight())&&!justFought) {
				moves = 0;
				if (firstRun) {
					startTime = System.nanoTime();
					firstRun = false;
				}
				//----------------------------Change string to display over time------------------------\\
				float deltaTime = (System.nanoTime() - startTime) / 1000000000.0f;
				float requiredTime = "Looks like the bot wants to fight you!".length() / 30;
				String textToDisplay = helper.dialogueToDisplay("Looks like the bot wants to fight you!",30,deltaTime);
				font.setColor(offBlack);
				font.draw(openWorldSB,textToDisplay,Gdx.graphics.getWidth()/6 - Gdx.graphics.getWidth()/2 + 20,
						-Gdx.graphics.getHeight()/4 - Gdx.graphics.getHeight()/300);
				drawPopup = true;
				if (textToDisplay.equals("Looks like the bot wants to fight you!") && deltaTime > requiredTime) {
					canSwitchToBattle = true;
				}
			} else if (battleEndPopup) {
				if (firstRun) {
					startTime = System.nanoTime();
					firstRun = false;
				}
				//-------------------Change string to display over time----------------------\\
				float deltaTime = (System.nanoTime() - startTime) / 1000000000.0f;
				float requiredTime = "How..? How did someone like you beat me?!".length() / 30;
				String textToDisplay = helper.dialogueToDisplay("How..? How did someone like you beat me?!",30,deltaTime);
				font.setColor(offBlack);
				font.draw(openWorldSB,textToDisplay,Gdx.graphics.getWidth()/6 - Gdx.graphics.getWidth()/2 + 20,
						-Gdx.graphics.getHeight()/4 - Gdx.graphics.getHeight()/300);
				if (deltaTime > requiredTime && textToDisplay.equals("How..? How did someone like you beat me?!")) {
					canCloseMenu = true; //enables user to close popup
				}
			}
			playerDraw.draw(openWorldSB);
			openWorldSB.end();
		} else if (programState.equals("MainMenu")) {
			ScreenUtils.clear(0,0,0,1);
			//------------------draw background and buttons-----------------\\
			batch.begin();
			background.draw(batch);
			settings.draw(batch);
			playBut.draw(batch);
			batch.end();
		} else if (programState.equals("Inventory")) {
			//----------------draw background and buttons---------------------\\
			ScreenUtils.clear(0,0,0,1);
			batch.begin();
			interfaceSprite.draw(batch);
			backButton.draw(batch);
			//font.draw(batch, "Back", backButton.getX() + backButton.getWidth()/20, backButton.getY() + backButton.getHeight());
			font.draw(batch, "Your Items",screenWidth/2 - font.getXHeight() * "Your Items".length()/2,screenHeight - screenHeight/8);
			playerItems.displayItems(playerItems.getItems(),conditionIcon.getTexture(),batch,screenHeight-3*screenHeight/8,100,screenWidth,screenHeight);
			batch.end();
		} else if (programState.equals("controls")) {
			//---------------draw background and buttons------------------------\\
			ScreenUtils.clear(0,0,0,1);
			batch.begin();
			if (toDraw.equals("objective")) {
				objective.draw(batch);
			} else if (toDraw.equals("bugging")) {
				bugging.draw(batch);
			}
			backButton.draw(batch);
			batch.end();
		} else if (programState.equals("OutOfSpace")) {
			//---------------draw background and buttons----------------\\
			batch.begin();
			inventoryOutOfSpaceInterface.draw(batch);
			backButton.draw(batch);
			batch.end();
		}
	}
	/**
	 * dispose things off to avoid memory leaks
	 */
	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
		shapeRenderer.dispose();
		stage.dispose();

	}
	/**
	 * drawRect - draw a rectangle on the screen
	 * @param x - the x position
	 * @param y - the y position
	 * @param width - the width
	 * @param height - the height
	 * @param color - the color
	 */
	public void drawRect(float x, float y, float width, float height,Color color) {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(color);
		shapeRenderer.rect(x, y - height, width, height);
		shapeRenderer.end();
	}

	/**
	 * displayCode - displays the enemy's code and the player's inventory
	 * @param code - the array of code to display
	 * @param ySpace - the vertical space between each line of code
	 */
//This method displays the code of the bot on the screen
	public void displayCode(CodeBlocks[] code, double ySpace) {
		float startY = screenHeight - screenHeight/27; //The starting Y position for displaying the code
		int lineNum = 0; //Keeps track of the line number
		int count = 0; //Keeps track of the number of conditions
		int tabCount = 0; //Keeps track of the number of tabs
		//-----------------Display the bot code----------------------\\
		for (int i = 0; i < code.length; i++) {
			font.setColor(enemyColor); //Sets the color of the font to enemy color
			if(!code[i].code.equals("{")) { //if the code is not an opening curly brace
				if(code[i].code.contains("}")&&tabCount>0) //if the code contains a closing curly brace and tabCount is greater than 0
					tabCount--; //decrement the tabCount
				lineNum++; //increment the line number
				font.draw(batch, lineNum + " ", screenWidth/4, startY+codeScroll); //draw the line number on the left side of the screen
				if (code[i].isEditable) //if the line is editable
					font.setColor(lightGreen); //change the color of the font
				if(code[i].code.contains("//")) //if the code contains a comment
					font.setColor(Color.WHITE); //change the color of the font to white
				code[i].tabs = tabCount; //set the number of tabs for the current code block
				code[i].setPos(screenWidth/4 +screenWidth/32 + tabCount * screenWidth / 40, startY+codeScroll, code[i].code.length() * font.getXHeight(), font.getXHeight() * 2F); //set the position of the code block
				if(code[i].code.contains("{")&&tabCount<5) //if the code contains an opening curly brace and tab count is less than 5
					tabCount++;
			} else{
				//if the current code block is a "{" increase the tabCount,
				//and set the x position to the end of the previous line
				float xPos = code[i-1].getPos()[2]+screenWidth/96;
				if(tabCount<5)
					tabCount+=1;
				if(code[i-1].code.contains("if")||code[i-1].code.contains("while")) {
					try {
						xPos = conditions[count - 1].getPos()[2] + screenWidth / 96;
					} catch (Exception e) {
						//if the previous line does not contain a condition, catch the exception
					}
				}
				//set the color of the code block to lightGreen
				font.setColor(lightGreen);
				//increase the y position by ySpace
				startY += ySpace;
				//set the position and size of the code block
				code[i].setPos(xPos,startY+codeScroll,code[i].code.length() * font.getXHeight(), font.getXHeight() * 2F);
			}
			//draw the code block
			font.draw(batch, code[i].code, code[i].getPos()[0], startY+codeScroll);

			if(code[i].code.contains("if")||code[i].code.contains("while")) {
				try {
					//set the position and size of the condition
					conditions[count].setPos(code[i].getPos()[2], (code[i].getPos()[1]), conditions[count].code.length() * font.getXHeight(), font.getXHeight() * 2F);
					//draw the condition
					font.draw(batch, conditions[count].code, conditions[count].getPos()[0], conditions[count].getPos()[1]);
				} catch (ArrayIndexOutOfBoundsException e) {

				}
				count++;
			}
			startY -= ySpace;
		}
		//------------------Display Player's Inventory-----------------\\
		CodeBlocks[] items = playerItems.getItems();
		OperationBlocks[] cond = playerItems.getConditions();
		startY = Gdx.graphics.getHeight() - Gdx.graphics.getHeight()/10;
		font.setColor(offBlack);
		activeItem = playerItems.getActiveItem();
		float prevScale = font.getScaleX();
		for (int i=0;i<items.length;i++) {
			//new color for active item
			if (items[i].equals(activeItem)) {
				font.setColor(1,0F,0F,1);
				font.draw(batch,items[i].code,screenWidth/32,startY+itemScroll);
				font.setColor(offBlack);
			} else {
					font.draw(batch, items[i].code, screenWidth/32, startY+itemScroll);
				}
			items[i].setPos(0,startY+itemScroll,18*screenWidth/54 - screenWidth/32, 4*font.getXHeight());
			startY -= ySpace;
		}
		//new color for active cond
		OperationBlocks activeCond = playerItems.getActiveCond();
		for (int i = 0; i < cond.length; i++) {
			if (cond[i].equals(activeCond)) {
				font.setColor(1,0F,0F,1);
				font.draw(batch,cond[i].code,screenWidth/32,startY);
				font.setColor(offBlack);
			} else {
				font.draw(batch,cond[i].code,screenWidth/32,startY);
			}
			cond[i].setPos(0,startY,18*screenWidth/54 - screenWidth/32,font.getXHeight());
			startY -= ySpace;
		}
		font.getData().setScale(prevScale);
	}

	/**
	 * This function checks if the mouse position is within the bounds of a condition.
	 * If so, it will perform actions depending on what item is currently active in the playerItems inventory.
	 * @param mouseX - the play's mouse x
	 * @param mouseY - the player's mouse Y
	 * @return boolean - true if the player clicked on a condition
	 */

	public boolean clickedConditionSpot(float mouseX, float mouseY) {
		//Get the currently active condition in the playerItems inventory
		OperationBlocks activeCond = playerItems.getActiveCond();
		//Check if the player has an active item or active condition
		if (activeItem != null || activeCond != null) {
			String itemType = playerItems.getActiveItemType();
			//Iterate through all the conditions
			for (int i = 0; i < conditions.length; i++) {
				//Check if the mouse position is within the bounds of the current condition
				if (mouseX > conditions[i].getPos()[0] && mouseX < conditions[i].getPos()[2] &&
						mouseY < conditions[i].getPos()[1] && mouseY > conditions[i].getPos()[3]) {
					//Check if the player has an active item
					if (itemType != null) {
						//If hammer, use it
						if (itemType.equals("hammer")) {
							useHammer(0, i + 1, true); //First parameter is 0 since it won't be used!
						}
						//If the active item is a value, add it to the condition
						else if(itemType.equals("Value")){
							if(conditions[i].a.equals("")) {
								conditions[i].setVars(activeItem.code,conditions[i].b);
								conditions[i].setOperation(conditions[i].operation);
								playerItems.removeItem(playerItems.getItemIndex(),false);
								//add value b to condition
							}else if(conditions[i].b.equals("")) {
								conditions[i].setVars(conditions[i].a,activeItem.code);
								conditions[i].setOperation(conditions[i].operation);
								System.out.println(playerItems.getItemIndex() + "");
								playerItems.removeItem(playerItems.getItemIndex(), false);
							}
						}
						//If the active item is an operator and the condition currently has no operator, add it to the condition
						else if (itemType.equals("Operation") && conditions[i].operation.equals("")) {
							conditions[i].setOperation(activeItem.code);
							playerItems.removeItem(playerItems.getItemIndex(), false);
						}
					}
					//Check if the player has an active condition
					else if (activeCond != null) {
						//if the condition currently has no operator, set it to the active condition
						if (conditions[i].operation.equals("")) {
							OperationBlocks con = playerItems.getActiveCond();
							System.out.println(con.code + " HERE IS THE CONDITION");
							conditions[i].setVars(con.a, con.b);
							conditions[i].setOperation(con.operation);
							playerItems.removeItem(playerItems.getItemIndex(), true);
						}
					}
				}
			}
			//-----------------------------Checks for clicks on any lines of code------------------------\\
			if (itemType != null) {
				for (int i = 0; i < code.length; i++) {
					int condCounter = 0;
					if (code[i].code.contains("if") || code[i].code.contains("while"))
						condCounter++;
					// if mouse pos is within a code line and it is editable, remove line
					if (mouseX > code[i].getPos()[0] && mouseX < code[i].getPos()[2] &&
							mouseY < code[i].getPos()[1] && mouseY > code[i].getPos()[3]) {
						//if hammer is selected
						if (itemType.equals("hammer") && code[i].isEditable) { //If itemType is an empty string, then it is a hammer
							useHammer(i, condCounter - 1, false); //2nd parameter is 0 since it won't be used
							//if codeblock is selected
						} else if (itemType.equals("Logic")) {
							if (activeItem.code.contains("if") || activeItem.code.contains("while")) {
								addEmptyCondition(condCounter);
								playerItems.removeItem(playerItems.getItemIndex(), false);
							}
							addCodeLine(activeItem.code, i + 1);
						} else if (itemType.equals("Curly")) {
							addCodeLine(activeItem.code,i + 1);
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * battleClick - runs events after the player clicks the battle button
	 * @param mouseX - the player's mouse x
	 * @param mouseY - the player's mouse Y
	 */
	public void battleClick(float mouseX, float mouseY) {
		mouseX = Gdx.input.getX();
		mouseY = screenHeight - Gdx.input.getY();

		boolean conditionDebug = false;
		boolean syntaxDebug = false;
		CodeBlocks[] prevCode = new CodeBlocks[code.length];
		System.arraycopy(code,0,prevCode,0,code.length);
		if (battleButton.getBoundingRectangle().contains(mouseX, mouseY)) {

			damage[0] = debugger.runBotCode(code,conditions,vars.getVariables())[0];
			damage[1] = debugger.runBotCode(code,conditions,vars.getVariables())[1];
			conditionDebug = conditionDebug(conditions,conditionDebug);
			code = debugger.closeCurlies(code,conditions);
			if(!syntaxDebug&&debugger.syntaxError){damage[0]=0;damage[1]=1;}

			if (damage[0] == 0 && damage[1] == 0) {damage[0] = 0.1F;addTerminalText("nothing happened");} //If no damage happened, lower player health
			playerHealth-=damage[0];

			if (damage[1]>0){enemyHealth=0;}

			if (Math.round(enemyHealth*100) <= 0) { //Change to win screen if enemy dies
				lvl+=1;
				//Make a method with a set probability of going to win screen (so player gets new items)
				double randNum = Math.random();
				if (randNum < 1) { //There is a 25% change the player can get an item after winning!
					programState = "winScreen";
					justFought = true;
				} else {
					programState = "openWorld"; //Change to open world if no items drop
					justFought = true;
				}
				battleEndPopup = true;
				FileHandle file = Gdx.files.local("codeSpace/posInfo.txt");
				file.writeString(""+Arrays.deepToString(enemyPos)+"#",false);
				file.writeString(bgSprite.getX()+","+bgSprite.getY()+"#",true);
				file.writeString(lvl+"#",true);
				create();
			} else if (Math.round(playerHealth*100) <= 0) { //Change to lose screen if player dies
				programState = "loseScreen";
				create();
			}
			String dmgText;
			//---------------Add terminal text based on what happens----------------\\
			if (damage[0] > 0) {
				if (damage[0] >= 0.4) {dmgText = "used attack";}
				else {dmgText = enemy.text;}
				addTerminalText(dmgText);
			}
			if (enemy.willConditionDebug) {
				addTerminalText(enemy.name+" has");
				addTerminalText("debugged missing conditions!");
			}
			if (!Arrays.equals(code,prevCode)) {
				addTerminalText(enemy.name+ " has");
				addTerminalText("debugged syntax errors!");}
		}
	}

	/**
	 * addCodeLine - adds a line of code to the bot's code
	 * @param lineOfCode - the line of code to add
	 * @param lineNum - the line to add it on
	 */
	public void addCodeLine(String lineOfCode, int lineNum) {
		code = Arrays.copyOf(code,code.length + 1);
		code[code.length-1] = new CodeBlocks("",false,0);
		for (int i = code.length - 1; i > lineNum; i--) {
				code[i].setCodeString(code[i - 1].code);
				code[i].setIsEditable(code[i - 1].isEditable);
		}
		code[lineNum].setCodeString(lineOfCode);
		code[lineNum].setIsEditable(true);
	}
	/**
	 * addEmptyCondition - adds an empty condition in the conditions array
	 * @param lineNum - the line to add the new condition to
	 */
	public void addEmptyCondition(int lineNum) {
		conditions = Arrays.copyOf(conditions,conditions.length + 1);
		conditions[conditions.length-1] = new OperationBlocks("",true,0,"","");
		//move code blocks down in the array
		for (int i = conditions.length - 1; i > lineNum; i--) {
			conditions[i].a=conditions[i-1].a;
			conditions[i].b=conditions[i-1].b;
			conditions[i].setOperation(conditions[i - 1].operation);
		}
		//adds empty condition
		conditions[lineNum].a="";
		conditions[lineNum].b="";
		conditions[lineNum].setOperation("");
	}

	/**
	 * removeLine - removes a line of code from the code array
	 * @param line - the index to remove the line
	 */
	public void removeLine(int line) {
		for (int i = line; i < code.length - 1; i++) {
			code[i] = code[i+1];
		}
		int length = code.length;
		code = Arrays.copyOf(code,length - 1);
	}
	/**
	 * removeCondition - removes a condition from the array of conditions
	 * @param line - the index to remove the condition
	 */
	public void removeCondition(int line) {
		for (int i = line; i < conditions.length - 1; i++) {
			conditions[i] = conditions[i+1];
		}
		int length = conditions.length;
		conditions = Arrays.copyOf(conditions,length - 1);
	}
	/**
	 * useHammer - apply the hammer item on a line of code
	 * @param codeLine - the line of code the hammer
	 * @param conditionCount - the condition index to hammer
	 * @param isCondition - whether the thing being hammered is a condition
	 */
	public void useHammer(int codeLine, int conditionCount, boolean isCondition) {
		if (isCondition) { //Run if the clicked spot was a condition
			if (!conditions[conditionCount - 1].code.equals("(    )")) { //Run if condition is not empty
				playerItems.addItem(conditions[conditionCount - 1].operation);
				playerItems.addItem(conditions[conditionCount - 1].a); //adds the condition in player inventory
				playerItems.addItem(conditions[conditionCount - 1].b);
				conditions[conditionCount - 1].setVars("", "");
				conditions[conditionCount - 1].setOperation("");
			}
		}
		else { //Run if the line of code that was clicked is not a condition
			if (!code[codeLine].code.equals("{") && !code[codeLine].code.equals("}"))
				playerItems.addItem(code[codeLine].code);
			if(code[codeLine+1].code.equals("{"))
				removeLine(codeLine+1);
			if(code[codeLine].code.contains("if")||code[codeLine].code.contains("while")) {
				if(!conditions[conditionCount].code.equals("(    )")) {
					playerItems.addItem(conditions[conditionCount].a); //adds the condition in player inventory
					playerItems.addItem(conditions[conditionCount].b);
					playerItems.addItem(conditions[conditionCount].operation);
					removeCondition(conditionCount + 1);
				}
			}
			removeLine(codeLine);
		}
	}
	/**
	 * conditionDebug - debugs the condition (i.e adds a condition if it is empty)
	 * @param conditions - all conditions in the bot's code
	 * @return boolean - whether the code has been debugged
	 */
	public boolean conditionDebug(OperationBlocks[] conditions,boolean willDebug) {
		boolean hasDebugged = false;
		for (int i = 0; i < conditions.length; i++) {
			if (conditions[i].code.equals("(    )")){ //adds conditions if it is empty
				if(willDebug) {
					playerItems.activeCond = new OperationBlocks(playerItems.getLastVar("Operation"), true, 0, playerItems.getLastVar("Value"), playerItems.getLastVar("Value"));
					conditions[i].setVars(playerItems.activeCond.a, playerItems.activeCond.b);
					conditions[i].setOperation(playerItems.activeCond.operation);
				}else{
					damage[1] = 1;
					damage[0] = 0;
				}
			}
		}
		return hasDebugged;
	}

	/**
	 * displayTerminalText - displays terminal text on the screen
	 * @param text - the array of strings to display on the terminal
	 */
	private void displayTerminalText(String[] text) {
		float startY = battleButtonBounds[1] - screenHeight/8;
		float prevScale = font.getScaleX();
		font.getData().setScale(font.getScaleX());
		//iterate and display terminal text from the array
		for (int i = 0; i < text.length; i++) {
			font.setColor(enemyColor);
			font.draw(batch,text[i],Gdx.graphics.getWidth()/2+34*screenWidth/112,startY);
			startY -= Gdx.graphics.getHeight()/30;
		}
		font.getData().setScale(prevScale);
	}
	/**
	 * addTerminalText - adds a string to the terminal array, displayed on the screen
	 * @param text - the string that will be added to display
	 */
	public void addTerminalText(String text) {
		terminalText = Arrays.copyOf(terminalText, terminalText.length + 1);
		terminalText[terminalText.length - 1] = text;
	}

	/**
	 * clearTerminalText - clears the terminal text
	 */
	public void clearTerminalText(){
		terminalText = Arrays.copyOf(terminalText,0);
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {

		return false;
	}
	/**
	 * onIconClick - does things (i.e check for program state change) when an icon is clicked
	 * @param mouseX -the mouse X
	 * @param mouseY - the mouseY
	 */
	public void onIconClick(float mouseX, float mouseY) {
		//Current icons: inventory
		float x = Gdx.input.getX() - screenWidth/2;
		float y = Gdx.input.getY() - screenHeight/2;

		if (inventoryIcon.getBoundingRectangle().contains(x,y)) {
			programState = "Inventory";
			create();
		}
	}

	/**
	 * This method is called when the screen is tapped.
	 * It checks for different conditions and performs different actions based on the current state of the program.
	 *
	 * @param x the x coordinate of the tap
	 * @param y the y coordinate of the tap
	 * @param count the number of taps
	 * @param button the button used to tap
	 * @return true if the tap was handled, false otherwise
	 */
	@Override
	public boolean tap(float x, float y, int count, int button) {
		System.out.println(playerItems.getActiveItemType());
		if (programState.equals("battleScreen")) {
			y = Gdx.graphics.getHeight() - y;
			playerItems.onClick(screenWidth,screenHeight);
			clickedConditionSpot(x, y);//change things by click
			clearTerminalText();
			addTerminalText(enemy.name+" lvl"+enemy.lvl);
			if(activeItem!=null&&x>=5*screenWidth/24) {
				//------------------Uses the player's items-------------------\\
				if (activeItem.code.equals("glasses")) {
					if (!enemy.willConditionDebug && !enemy.willSyntaxDebug) {
						addTerminalText("won't debug code");
					}
					if (enemy.willConditionDebug) {
						addTerminalText("will debug empty");
						addTerminalText("conditions");
					}
					if (enemy.willSyntaxDebug) {
						addTerminalText("will debug");
						addTerminalText("curly brackets");
					}
					playerItems.removeItem(playerItems.itemIndex,false);
				} else if (activeItem.code.equals("bandage")) {
					playerHealth += 30;
					addTerminalText("you healed!");
					playerItems.removeItem(playerItems.itemIndex,false);
				}
			}
			battleClick(x, y);

		} else if (programState.equals("openWorld")) {
			onIconClick(x,y);
			//------------------------checks if the user can close enemy popup-----------------\\
			if (canSwitchToBattle){
				firstRun = true;
				programState = "battleScreen";
				canSwitchToBattle = false;
				try {
					enemy = new Enemy(lvl,1,fileReadCode);
					setVars(enemy.vars,enemy.values);
					enemyList[0] = new Sprite(new Texture(enemy.image));
				} catch (IOException e) {
					e.printStackTrace();
				}
				create();
				drawPopup = false;
			} else if (canCloseMenu) {
				battleEndPopup = false;
				firstRun = true;
				canCloseMenu = false;
			}
		} else if (programState.equals("winScreen")) {
			float mouseX = Gdx.input.getX();
			float mouseY = screenHeight - Gdx.input.getY();

			if (claimBut.getBoundingRectangle().contains(mouseX,mouseY)) {
				FileHandle file = Gdx.files.local("codeSpace/playerItems.txt");
				for (int i = 0; i < itemDrop.length; i++) {
					playerItems.addItem(itemDrop[i]);
					file.writeString(itemDrop[i], true);    //write to file
				}
				programState = "openWorld";
				if (playerItems.getItemsLength() > 15) {
					programState = "OutOfSpace";
				}
				create();
			}
		} else if (programState.equals("MainMenu")) {
			//x = Gdx.graphics.getWidth() - x;
			y = Gdx.graphics.getHeight() - y;
			//Check if play button was clicked
			if (x > playBut.getX() && x < playBut.getX()+playBut.getWidth() && y > playBut.getY() && y < playBut.getY() + playBut.getHeight()) {
				programState = "openWorld";
				create();
			} else if (x > settings.getX() && x < settings.getX() + settings.getWidth() && y > settings.getY() && y < settings.getY() + settings.getHeight()) {
				programState = "controls";
				create();
			}
		} else if (programState.equals("Inventory") || programState.equals("OutOfSpace")) {
			float mouseX = Gdx.input.getX();
			float mouseY = screenHeight - Gdx.input.getY();
			//System.out.println(backButton.getY() - backButton.getHeight()/2+"-->"+(backButton.getY()+2*backButton.getHeight() - backButton.getHeight()/2));
			if (backButton.getBoundingRectangle().contains(mouseX,mouseY)) {
				programState = "openWorld";
				create();
			}
		} else if (programState.equals("loseScreen")) {
			float xMouse = Gdx.input.getX();
			float yMouse = screenHeight - Gdx.input.getY();

			justFought = true;
			float currentBgX = bgSprite.getX();
			float currentBgY = bgSprite.getY();
			float targetX = -480; //The x position of the hospital
			float targetY = -270;//The y position of the hospital
			float deltaX = targetX - currentBgX;
			float deltaY = targetY - currentBgY;
			//Reset background to desired position
			bgSprite.setPosition(targetX,targetY);
			//Move enemy position relative to the hospital position
			for (int i = 0; i < enemyList.length; i++) {
				enemyPos[i][0] += deltaX;
				enemyPos[i][1] += deltaY;
				enemyList[i].setPosition(enemyPos[i][0],enemyPos[i][1]);
			}
			//Write the new positions into the file
			FileHandle file = Gdx.files.local("codeSpace/posInfo.txt");
			file.writeString(""+Arrays.deepToString(enemyPos)+"#",false);
			file.writeString(bgSprite.getX()+","+bgSprite.getY()+"#",true);
			file.writeString(lvl+"#",true);

			//Checks for mouse click on the back button
			if (backButton.getBoundingRectangle().contains(xMouse,yMouse)) {
				programState = "openWorld";
				create();
			}

		} else if (programState.equals("controls")) {
			if (toDraw.equals("objective")) {
				toDraw = "bugging";
			}
			else if (toDraw.equals("bugging")) {
				toDraw = "objective";
			}
			float mouseX = Gdx.input.getX();
			float mouseY = screenHeight - Gdx.input.getY();

			if (backButton.getBoundingRectangle().contains(mouseX,mouseY)) {
				programState = "MainMenu";
				create();
			}
		}
		//code[2].changeCondition("(3 + 3 = 3 + 3)");

		return false;
	}
	/**
	 * checkMovement - checks if the player clicked on a movement arrow, and moves accordingly
	 * @param battleEndPopup - a boolean, indicating if the player should be able to move
	 * @param drawPopup - a boolean, indicating if the player should be able to move
	 */
	public void checkMovement(boolean battleEndPopup, boolean drawPopup) {
		if (!drawPopup && !battleEndPopup && Gdx.input.isTouched()) { //Player can move only when not encountered an enemy
			float mouseX = Gdx.input.getX() - screenWidth/2;
			float mouseY = (screenHeight/2 - Gdx.input.getY());
			//System.out.println(Arrays.toString(helper.buttonsClicked(butPos, (x - Gdx.graphics.getWidth() / 2), -(y - Gdx.graphics.getHeight() / 2))));

			//boolean[] butClicked = helper.buttonsClicked(butPos, (Gdx.input.getX() - Gdx.graphics.getWidth() / 2), -(Gdx.input.getY() - Gdx.graphics.getHeight() / 2));
			//----------------------Checks which sprite(s) were clicked, and move accordingly----------------\\
			if (leftArrow.getBoundingRectangle().contains(mouseX,mouseY)) {
				moveBackground(enemyPos, 5, 0);
        moves++;
			} else if (rightArrow.getBoundingRectangle().contains(mouseX,mouseY)) {
				moveBackground(enemyPos, -5, 0);
        moves++;
			} else if (upArrow.getBoundingRectangle().contains(mouseX,mouseY)) {
				moveBackground(enemyPos, 0, 5);
        moves++;
			} else if (downArrow.getBoundingRectangle().contains(mouseX,mouseY)) {
				moveBackground(enemyPos, 0, -5);
        moves++;
			}
			//----------------------------Write the new position in the file----------------------\\
			bgPos[0] = bgSprite.getX();
			bgPos[1] = bgSprite.getY();
			for (int i = 0; i < enemyList.length; i++) {
				enemyPos[i][0] = enemyList[i].getX();
				enemyPos[i][1] = enemyList[i].getY();
			}
			FileHandle file = Gdx.files.local("codeSpace/posInfo.txt");
			file.writeString(""+Arrays.deepToString(enemyPos)+"#",false);
			file.writeString(bgSprite.getX()+","+bgSprite.getY()+"#",true);
			file.writeString(lvl+"#",true);
		}
	}

	/**
	 * moveBackground - moves the background when player moves (also moves the enemies)
	 * @param enemyPos - an array of all enemy position
	 * @param moveX - the amount to shift in the x direction
	 */
	public void moveBackground(float[][] enemyPos, float moveX, float moveY) {
		System.out.println(bgSprite.getX()+"; "+bgSprite.getY()+" HERE IS THE LENGTH AND WIDTH");
		System.out.println(-2*(bgSprite.getHeight()*screenHeight/450)/3);
		//-------------Checks if the player is at boundaries----------------\\
		if (bgSprite.getY() > -(bgSprite.getHeight()*screenHeight/960)/3 && moveY < 0) {moveY = 0;} //Bottom boundary
		else if (bgSprite.getY() <= -2*(bgSprite.getHeight()*screenHeight/450)/3 && moveY > 0) {moveY = 0;}//Top
		if (bgSprite.getX() < -(bgSprite.getWidth()*screenWidth/450)/3 && moveX < 0) {moveX = 0;}//Right
		else if (bgSprite.getX() > -(bgSprite.getWidth()*screenWidth/800)/7 && moveX > 0) {moveX = 0;}//Left
		//Change enemy and background position
		for (int i = 0; i < enemyPos.length; i++) {
			enemyPos[i][0] += moveX;
			enemyPos[i][1] -= moveY;
			enemyList[i].setPosition(enemyPos[i][0],enemyPos[i][1]);
		}
		bgSprite.translate(moveX,-moveY);
	}
	@Override
	public boolean longPress(float x, float y) {

		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	/**
	 * this method is called when the user pans over the screen
	 */
	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {

		if(programState.equals("battleScreen")){
			//within enemy code
			if(x>=5*screenWidth/24&&x<=Gdx.graphics.getWidth() / 2 + 5*screenWidth/48) {
				//scroll up
				if ((deltaY<0&&code[code.length-1].bottom<20)||
						(deltaY>0&&codeScroll>0&&code[0].top>=Gdx.graphics.getHeight() - 20)){
					codeScroll -= deltaY;
				}
			}else if(x<5*screenWidth/24) {
				if ((deltaY<0&&playerItems.items[playerItems.items.length-1].bottom<100)||
						(deltaY>0&&itemScroll>0&&playerItems.items[0].top>=Gdx.graphics.getHeight() - 20)){
					itemScroll -=deltaY;
				}
			}
		}
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}

	@Override
	public void pinchStop() {

	}
}
