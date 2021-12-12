package com.anish.calabashbros;

import java.util.Vector;
import java.util.Random;

public class MonsterManager extends Thread {
    
    private World world;    
    private Vector<Monster> monsters;
    private Calabash calabash;

    private int mazeBegin;
    private int mazeSize;
    private int monsterLimit = 10;
    private Random random;
    private boolean side = true;

    public MonsterManager(World world, int mazeBegin, int mazeSize, Calabash calabash) {
        this.world = world;
        this.mazeBegin = mazeBegin;
        this.mazeSize = mazeSize;
        this.calabash = calabash;
        monsters = new Vector<>();
        random = new Random();
    }

    private void createMonster(int posX, int posY) {
        Monster monster = new Monster(world, calabash, mazeBegin, mazeSize);
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
