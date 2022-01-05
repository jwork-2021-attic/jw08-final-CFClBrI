package com.anish.calabashbros;

import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.Vector;

public class World implements Serializable {

    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;
    public static final int SCREEN_HEIGHT = 25;
    public int mazeBegin;
    public int mazeSize;
    public int yBegin = 0;
    private int beanCount = 0;
    private static final long serialVersionUID = 15L;

    private boolean isOver = false;
    private Vector<Calabash> players;
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

    public void setBeanCount(int beanCount) {
        this.beanCount = beanCount;
    }

    public void beanEaten() {
        beanCount--;
        if (beanCount <= 0) {
            win();
        }
    }

    private void win() {
        showGameResult(true);
        killCreatures();
    }

    private void lose() {
        showGameResult(false);
        killCreatures();
    }

    public void judgeLose() {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getAlive()) {
                return;
            }
        }
        lose();
    }

    public void showGameResult(boolean isWin) {
        setOver();
        int winBeginX = mazeBegin + mazeSize + 2;
        int winBgeinY = mazeBegin + 7; 
        for (int i = 0; i < 15; i++) {
            clear(winBeginX + i, winBgeinY);
        }
        if (isWin) {
            showString("Player Win", winBeginX, winBgeinY);
        }
        else {
            showString("Player Lose", winBeginX, winBgeinY);
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

    public void setOver() {
        isOver = true;
    }

    public void showChar(char ch, int posX, int posY) {
        Char character = new Char(this, ch);
        put(character, posX, posY);
    }

    public void showString(String str, int posX, int posY) {
        for (int i = 0; i < str.length(); i++) {
            showChar(str.charAt(i), posX + i, posY);
        }
    }

    public void setPlayers(Vector<Calabash> players) {
        this.players = players;
    }

    public void continueGame() {
        players = new Vector<>();
        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                Thing thing = get(mazeBegin + i, mazeBegin + j);
                if (thing instanceof Moveable) {
                    Thread thread = new Thread((Moveable)thing);
                    thread.start();
                    if (thing instanceof Calabash) {
                        Calabash player = (Calabash)thing;
                        players.add(player);
                    }
                }
            }
        }
    }

    public void killCreatures() {        
        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                Thing thing = get(mazeBegin + i, mazeBegin + j);
                if (thing instanceof Creature) {
                    Creature creature = (Creature)thing;
                    creature.kill();
                }
            }
        }        
    }

    public void respondToUserInput(int num, int keyCode) {
        Calabash calabash = players.get(num);
        if (keyCode >= KeyEvent.VK_LEFT && keyCode <= KeyEvent.VK_DOWN) {                    
            calabash.walk(keyCode - KeyEvent.VK_LEFT);
            if (keyCode == KeyEvent.VK_DOWN) {
                if (yBegin < 7 && calabash.getY() - yBegin >= 20) {
                    yBegin++;
                }
            }
            else if (keyCode == KeyEvent.VK_UP) {
                if (yBegin > 0 && calabash.getY() <= yBegin + 5) {
                    yBegin--;
                }
            }
        }
        else if (keyCode == KeyEvent.VK_ENTER) {
            calabash.createBullet();
        }
    }

    public void stopUserInput(int num) {
        Calabash calabash = players.get(num);
        calabash.stopWalk();
    }
}
