package com.anish.screen;

import java.awt.event.KeyEvent;

import java.util.Random;

import com.anish.calabashbros.Bean;
import com.anish.calabashbros.Calabash;
import com.anish.calabashbros.Char;
import com.anish.calabashbros.Cherry;
import com.anish.calabashbros.World;
import com.anish.calabashbros.Wall;
import com.anish.calabashbros.Floor;
import com.anish.calabashbros.Drug;
import com.anish.calabashbros.Thing;
import com.anish.calabashbros.MonsterManager;

import asciiPanel.AsciiPanel;

import com.anish.mazeGenerator.MazeGenerator;

public class WorldScreen implements Screen {

    private World world;
    private Calabash calabash;
    private MonsterManager monsterManager;

    private int mazeBegin = 1;
    private int mazeSize = 30;
    private int beanCount = 0;
    private int cherryLimit = 3;
    private int drugLimit = 2;

    Random random = new Random();

    public WorldScreen() {         
        world = new World(mazeBegin, mazeSize);
        int[][] maze = createMaze();
        setWorld(maze);               
        calabash = new Calabash(world, beanCount, mazeBegin, mazeBegin);
        monsterManager = new MonsterManager(world, mazeBegin, mazeSize, calabash);
        monsterManager.start();
    }

    private int[][] createMaze() {
        MazeGenerator mazeGenerator = new MazeGenerator(mazeSize);
        mazeGenerator.generateMaze();        
        int[][] maze = mazeGenerator.getMaze();
        
        int cherryCount = 0, drugCount = 0;
        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                if (maze[i][j] == 0 || i == mazeSize - 1 || j == mazeSize - 1) {
                    continue;
                }                
                if (random.nextInt(10000) % 2 == 0) { //bean
                    maze[i][j] = 2;
                }
                else if (cherryCount < cherryLimit && 
                        random.nextInt(10000) % 80 == 0) { //cherry
                    maze[i][j] = 3;
                    cherryCount++;
                }
                else if (drugCount < drugLimit &&
                        random.nextInt(10000) % 80 == 0) { //drug
                    maze[i][j] = 4;
                    drugCount++;
                }                
            }
        }
        maze[0][0] = 1;
        return maze;
    }

    private void setWorld(int[][] maze) {
        for (int i = -1; i <= mazeSize; i++) {
            for (int j = -1; j <= mazeSize; j++) {
                Thing thing;
                if (i < 0 || i >= mazeSize || j < 0 || j >= mazeSize || 
                    maze[i][j] == 0) { //wall
                    int num = random.nextInt(4);
                    thing = new Wall(num, world);                   
                }
                else if (maze[i][j] == 2) { //bean
                    thing = new Bean(world);                    
                    beanCount++;
                }
                else if (maze[i][j] == 3) { //cherry
                    thing = new Cherry(world);                    
                }
                else if (maze[i][j] == 4) { //drug
                    thing = new Drug(world);                    
                }
                else { //floor
                    thing = new Floor(world);                    
                }
                world.put(thing, mazeBegin + i, mazeBegin + j);
            }
        }
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        for (int x = 0; x < World.WIDTH; x++) {
            for (int y = 0; y < World.HEIGHT - world.yBegin; y++) {
                Thing thing = world.get(x, y + world.yBegin);
                if (thing == null) {
                    continue;
                }
                if (thing instanceof Char) {
                    Char character = (Char)thing;                    
                    terminal.write(character.getCh(), x, y);
                }
                else {
                    terminal.write(thing.getUrl(), x, y);
                }
            }
        }
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        int keyCode = key.getKeyCode();
        if (keyCode >= KeyEvent.VK_LEFT && keyCode <= KeyEvent.VK_DOWN) {                    
            calabash.walk(keyCode - KeyEvent.VK_LEFT);
            if (keyCode == KeyEvent.VK_DOWN) {
                if (world.yBegin < 7 && calabash.getY() - world.yBegin >= 20) {
                    world.yBegin++;
                }
            }
            else if (keyCode == KeyEvent.VK_UP) {
                if (world.yBegin > 0 && calabash.getY() <= world.yBegin + 5) {
                    world.yBegin--;
                }
            }
        }
        else if (keyCode == KeyEvent.VK_ENTER) {
            calabash.createBullet();
        }
        return this;
    }

    @Override
    public Screen stopUserInput() {
        calabash.stopWalk();
        return this;
    }
}
