package com.highvoltage.error;

public class Player {
    /**
     * encounterEnemy - checks when the player encounters an enemy
     * @param enemyPos - the array of all enemy positions
     * @param playerX - the player x
     * @param playerY - the player y
     * @return boolean - whether player hit an enemy
     */
    public boolean encounterEnemy(float[][] enemyPos, float playerX, float playerY) {
        for (int i = 0; i < enemyPos.length; i++) {
            float x = enemyPos[i][0];
            float y = enemyPos[i][1];
            float width = enemyPos[i][2];
            float height = enemyPos[i][3];
            if (playerX >= x && playerX <= x+width && playerY >= y && playerY < y+height) {
                return true;
            }
        }
        return false;
    }
}
