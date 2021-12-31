package com.anish.screen;

import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.Vector;

import com.anish.calabashbros.Bean;
import com.anish.calabashbros.Calabash;
import com.anish.calabashbros.Char;
import com.anish.calabashbros.Cherry;
import com.anish.calabashbros.World;
import com.anish.calabashbros.Wall;
import com.anish.calabashbros.Floor;
import com.anish.calabashbros.ImagePiece;
import com.anish.calabashbros.Drug;
import com.anish.calabashbros.Thing;
import com.anish.calabashbros.MonsterManager;

import asciiPanel.AsciiPanel;

import com.anish.mazeGenerator.MazeGenerator;

public class WorldScreen implements Screen {

    private World world;
    private MonsterManager monsterManager;

    private int mazeBegin;
    private int mazeSize;
    private int default_mazeBegin = 1;
    private int default_mazeSize = 30;
    private int beanCount;
    private int beginHP = 3;
    private boolean start = true;
    private int[][] playerPositons;
    private int playerCount = 1;
    Random random = new Random();

    public WorldScreen() {                 
        initScreen();
    }

    public void initScreen() {
        world = new World(24, 20);
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 24; j++) {
                ImagePiece piece = new ImagePiece(world, "(" + j + "," + i + ")");
                world.put(piece, j, i);
            }
        }
        world.showString("Press C to continue", 0, 21);
        world.showString("Press N to start a new game", 0, 22);
    }

    public void newGame() {
        start = false;
        mazeBegin = default_mazeBegin;
        mazeSize = default_mazeSize;
        world = new World(mazeBegin, mazeSize);
        int[][] maze = createMaze();
        setWorld(maze);
        int posX = playerPositons[0][0], posY = playerPositons[0][1];    
        Calabash calabash = new Calabash(world, beanCount, beginHP,
                                posX, posY, posX, posY,
                                mazeBegin, mazeSize);
        Vector<Calabash> players = new Vector<>();
        players.add(calabash);
        world.setPlayers(players);
        monsterManager = new MonsterManager(world, players);
        monsterManager.start();
    }

    private int[][] createMaze() {
        MazeGenerator mazeGenerator = new MazeGenerator(mazeSize, playerCount);
        mazeGenerator.generateMaze();        
        int[][] maze = mazeGenerator.getMaze();
        playerPositons = mazeGenerator.getPlayerPositions();        
        return maze;
    }

    private void setWorld(int[][] maze) {
        beanCount = 0;
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
        world.showString("Press S to save", mazeBegin + mazeSize + 2, 
                         mazeBegin + mazeSize / 2 - 1);
    }
 
    public void loadGame() {
    /*
        start = false;
        try {            
            List<String> lines = Files.readAllLines(Path.of("resources/store.txt"));
            if (lines.size() == 0) {
                System.out.println("store.txt is empty!");
                newGame();
            }
            String[] currLine = lines.get(0).split(" ");
            mazeBegin = Integer.parseInt(currLine[0]);
            mazeSize = Integer.parseInt(currLine[1]);
            world = new World(mazeBegin, mazeSize);
            world.yBegin = Integer.parseInt(currLine[2]);
            currLine = lines.get(1).split(" ");
            int HP = Integer.parseInt(currLine[0]);
            int invincibleTime = Integer.parseInt(currLine[1]);  
            int currX = mazeBegin, currY = mazeBegin;
            String playerUrl = "resources/player/3_stand.png";
            Vector<Monster> monsters = new Vector<>(); 
            beanCount = 0;         
            for (int i = 2; i < lines.size(); i++) {
                currLine = lines.get(i).split(" ");
                for (int j = 0; j < currLine.length; j++) {
                    String currThing = currLine[j];                   
                    Thing thing = null;
                    if (currThing.indexOf("wall") != -1) {
                        thing = new Wall(currThing.charAt(19) - '0', world);
                    }
                    else if (currThing.indexOf("ghost") != -1) {
                        Monster monster = new Monster(world, null, mazeBegin, mazeSize);
                        String[] currMonster = currThing.split("\\|");
                        String url = currMonster[0];
                        String isOnId = currMonster[1];
                        monster.setUrl(url);
                        Position position = Position.getElementById(isOnId);
                        monster.setOn(position);
                        monsters.add(monster);
                        world.put(monster, j, i - 2);
                        if (position == Position.BEAN) {
                            beanCount++;
                        }
                    }
                    else if (currThing.indexOf("arrow") != -1) {
                        String[] currArrow = currThing.split("\\|");
                        String url = currArrow[0];
                        String isOnId = currArrow[1];
                        int direction = url.charAt(16) - '0';
                        Position position = Position.getElementById(isOnId);
                        Bullet bullet = new Bullet(world, direction, position);
                        world.put(bullet, j, i - 2);
                        Thread bulletThread = new Thread(bullet);
                        bulletThread.start();
                        if (position == Position.BEAN) {
                            beanCount++;
                        }
                    }
                    else if (currThing.indexOf("player") != -1) {
                        currX = j;
                        currY = i - 2;
                        String[] player = currThing.split("\\|");
                        playerUrl = player[0];
                    }
                    else if (currThing.indexOf("bean") != -1) {
                        thing = new Bean(world);
                        beanCount++;
                    }
                    else if (currThing.indexOf("invincible_potion") != -1) {
                        thing = new Cherry(world);
                    }
                    else if (currThing.indexOf("life_potion") != -1) {
                        thing = new Drug(world);
                    }
                    else if (currThing.indexOf("floor") != -1) {
                        thing = new Floor(world);
                    }
                    else if (currThing.indexOf("^") == -1) {
                        thing = new Char(world, currThing.charAt(0));
                    }
                    if (thing != null) {
                        world.put(thing, j, i - 2);
                    }
                }
            }
            calabash = new Calabash(world, beanCount, HP,
                                    mazeBegin, mazeBegin, currX, currY, 
                                    mazeBegin, mazeSize);
            calabash.setUrl(playerUrl);
            if (invincibleTime != 0) {
                calabash.enterInvincible(invincibleTime);
            }            
            monsterManager = new MonsterManager(world, mazeBegin, mazeSize, calabash, monsters);
            monsterManager.start();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    */
        start = false;
        try {
            FileInputStream fiStream = new FileInputStream("resources/store.txt");
            ObjectInputStream oiStream = new ObjectInputStream(fiStream);
            world = (World)oiStream.readObject();
            fiStream.close(); oiStream.close();
            world.continueGame();
            fiStream = new FileInputStream("resources/store2.txt");
            oiStream = new ObjectInputStream(fiStream);
            monsterManager = (MonsterManager)oiStream.readObject();
            fiStream.close(); oiStream.close();
            monsterManager.start();
        }
        catch (IOException e) {
            e.printStackTrace();            
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void storeGame() {
    /*
        FileWriter fw = null;
        try {
            fw = new FileWriter("resources/store.txt");
            fw.write(mazeBegin + " " + mazeSize + " " + world.yBegin + "\n");
            fw.write(calabash.getHP() + " " + calabash.getInvincibleTime() + "\n");
            for (int j = 0; j < World.HEIGHT; j++) {
                for (int i = 0; i < World.WIDTH; i++) {
                    Thing thing = world.get(i, j);
                    if (thing != null) {
                        if (thing instanceof Char) {
                            Char ch = (Char)thing;
                            if (ch.getCh() == ' ') {
                                fw.write("^");
                            }
                            else {
                                fw.write(ch.getCh());
                            }
                        }
                        else {
                            fw.write(thing.getUrl());
                            if (thing instanceof Moveable) {
                                Moveable moveable = (Moveable)thing;
                                fw.write("|" + moveable.getOn().getId());
                            }        
                        }                                                                                        
                    }
                    else {
                        fw.write("^");
                    }
                    fw.write(' ');
                }
                fw.write('\n');
            }
            fw.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    */
        try {
            FileOutputStream foStream = new FileOutputStream("resources/store.txt");
            ObjectOutputStream ooStream = new ObjectOutputStream(foStream);
            ooStream.writeObject(world);
            foStream.close(); ooStream.close();            
            foStream = new FileOutputStream("resources/store2.txt");
            ooStream = new ObjectOutputStream(foStream);
            ooStream.writeObject(monsterManager);
            foStream.close(); ooStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
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
    public String[][] getOutput() {
        String[][] output = new String[World.WIDTH][World.HEIGHT];
        for (int x = 0; x < World.WIDTH; x++) {
            for (int y = 0; y < World.HEIGHT; y++) {
                output[x][y] = "";
            }
        }
        for (int x = 0; x < World.WIDTH; x++) {
            for (int y = 0; y < World.HEIGHT - world.yBegin; y++) {
                Thing thing = world.get(x, y + world.yBegin);
                if (thing != null) {
                    output[x][y] = thing.getUrl();
                }         
            }
        }
        return output;
    }

    @Override
    public Screen respondToUserInput(int num, int keyCode) {
        if (start) {
            if (keyCode == KeyEvent.VK_C) {
                loadGame();
            }
            else if (keyCode == KeyEvent.VK_N) {
                newGame();                
            }
            return this;
        }
        if (keyCode == KeyEvent.VK_S) {
            storeGame();
        }
        else {
            world.respondToUserInput(num, keyCode);
        }
        return this;
    }

    @Override
    public Screen stopUserInput(int num) {
        if (start) {
            return this;
        }    
        world.stopUserInput(num);
        return this;
    }
}
