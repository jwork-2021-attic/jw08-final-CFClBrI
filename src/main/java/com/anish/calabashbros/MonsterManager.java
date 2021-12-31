package com.anish.calabashbros;

import java.util.Vector;
import java.io.Serializable;
import java.util.Random;

public class MonsterManager extends Thread implements Serializable {
    
    private World world;    
    private Vector<Monster> monsters;
    private Vector<Calabash> players;

    private int mazeBegin;
    private int mazeSize;
    private int monsterLimit = 10;
    private Random random;
    private boolean side = true;
    private static final long serialVersionUID = 9L;

    public MonsterManager(World world, Vector<Calabash> players) {
        this.world = world;
        this.mazeBegin = world.mazeBegin;
        this.mazeSize = world.mazeSize;
        this.players = players;
        monsters = new Vector<>();
        random = new Random();
    }

    public MonsterManager(World world, int mazeBegin, int mazeSize, Vector<Calabash> players, Vector<Monster> monsters) {
        this(world, players);

        this.monsters = monsters;
        for (int i = 0; i < monsters.size(); i++) {
            Monster monster = monsters.get(i);
            monster.setPlayers(players);
            Thread monsterThread = new Thread(monster);
            monsterThread.start();
        }
    }

    private void createMonster(int posX, int posY) {
        Monster monster = new Monster(world, players);
        Thread monsterThread = new Thread(monster);        
        monsters.add(monster);
        world.put(monster, posX, posY);
        monsterThread.start();
    }

    private int countMonster() {
        int i = 0;
        while (i < monsters.size()) {
            if (monsters.get(i).getAlive()) {
                i++;
            }
            else {
                monsters.remove(i);
            }            
        }
        return monsters.size();
    }

    private boolean putMonster(int posX, int posY) {
        Thing thing = world.get(posX, posY);
        if (thing instanceof Calabash || thing instanceof Wall ||
            thing instanceof Monster) {
            return false;
        }
        createMonster(posX, posY);
        side = !side;
        return true;
    }

    private void generateMonster() {
        if (countMonster() == monsterLimit) {
            return;
        }
        int posX, posY;
        do {
            int pos = random.nextInt(mazeSize);
            if (side) {
                posX = mazeBegin + mazeSize - 1;
                posY = mazeBegin + pos;                        
            }
            else {
                posX = mazeBegin + pos; 
                posY = mazeBegin + mazeSize - 1;                                          
            }                       
        } while (!putMonster(posX, posY));
    }

    public void run() {
        while (!world.getOver()) {
            generateMonster();
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
