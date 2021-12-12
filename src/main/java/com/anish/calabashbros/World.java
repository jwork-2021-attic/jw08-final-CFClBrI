package com.anish.calabashbros;

public class World {

    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;

    private int mazeBegin;
    private int mazeSize;
    private boolean isOver = false;
    public int yBegin = 0;

    private Tile<Thing>[][] tiles;

    public World(int mazeBegin, int mazeSize) {

        this.mazeBegin = mazeBegin;
        this.mazeSize = mazeSize;

        if (tiles == null) {
            tiles = new Tile[WIDTH][HEIGHT];
        }

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                tiles[i][j] = new Tile<>(i, j);
                if (i < mazeSize && j < mazeSize) {
                    tiles[i][j].setThing(new Floor(this));
                }                
            }
        }
    }

    public Thing get(int x, int y) {
        return this.tiles[x][y].getThing();
    }

    public void put(Thing t, int x, int y) {
        this.tiles[x][y].setThing(t);
    }

    public void clear(int x, int y) {
        tiles[x][y] = new Tile<>(x, y);
    }

    public boolean getOver() {
        return isOver;
    }

    private void showChar(char ch, int posX, int posY) {
        Char character = new Char(this, ch);
        put(character, posX, posY);
    }

    private void showString(String str, int posX, int posY) {
        for (int i = 0; i < str.length(); i++) {
            showChar(str.charAt(i), posX + i, posY);
        }
    }

    public void showHP(int HP) {
        int HPBeginX = mazeBegin + mazeSize + 2;
        int HPBeginY = mazeBegin + mazeSize / 2; 
        showString("HP:" + HP, HPBeginX, HPBeginY);       
    }
   
    public void showInvincible(int second) {
        int invinBeginX = mazeBegin + mazeSize + 2;
        int invinBeginY = mazeBegin + mazeSize / 2 + 1;        
        showString("Invincible:" + second / 10 + second % 10, invinBeginX, invinBeginY);        
    }

    public void hideInvincible() {
        int invinBeginX = mazeBegin + mazeSize + 2;
        int invinBeginY = mazeBegin + mazeSize / 2 + 1;
        for (int i = 0; i < 13; i++) {
            clear(invinBeginX + i, invinBeginY);
        }
    }

    public void showWin() {
        isOver = true;
        int winBeginX = mazeBegin + mazeSize + 2;
        int winBgeinY = mazeBegin + mazeSize / 2 - 1; 
        showString("You Win", winBeginX, winBgeinY);
    }

    public void showLose() {
        isOver = true;
        int loseBeginX = mazeBegin + mazeSize + 2;
        int loseBgeinY = mazeBegin + mazeSize / 2 - 1;  
        showString("You Lose", loseBeginX, loseBgeinY);
    }

    public void killMonsters() {        
        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                Thing thing = get(mazeBegin + i, mazeBegin + j);
                if (thing instanceof Monster) {
                    Monster monster = (Monster)thing;
                    monster.kill();
                }
            }
        }        
    }
}
