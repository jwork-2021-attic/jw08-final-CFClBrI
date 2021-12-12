package com.anish.calabashbros;

import java.util.Random;
import java.util.LinkedList;
import java.util.Vector;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class Monster extends Creature {
    
    private Random random;
    private Calabash calabash;
    private int mazeBegin;
    private int mazeSize;
    private Queue<int[]> queue;
    private int[][] seen;
    private int[][] record;
    private Vector<Integer> path;
    private int step;
    private int storeX;
    private int storeY;
    private int hardLevel = 10;

    public Monster(World world, Calabash calabash, int mazeBegin, int mazeSize) {
        super("resources/ghost/direction3.png", world);
        
        random = new Random();        
        this.calabash = calabash;
        this.mazeBegin = mazeBegin;
        this.mazeSize = mazeSize;
        isOn = Position.FLOOR;
    }

    private void bfs() {
        //0:left 1:up 2:right 3:down
        queue = new LinkedList<>();
        seen = new int[mazeSize][mazeSize];
        record = new int[mazeSize][mazeSize];
        seen[getY() - mazeBegin][getX() - mazeBegin] = 2;
        queue.add(new int[]{getX(), getY()});
        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int currX = curr[0];
            int currY = curr[1];
            if (currX == calabash.getX() && currY == calabash.getY()) {
                break;
            }
            for (int direction = 0; direction < 4; direction++) {
                int directionX = directions[direction][0];
                int directionY = directions[direction][1];
                int neighborX = currX + directionX;
                int neighborY = currY + directionY;
                Thing thing = world.get(neighborX, neighborY);
                if (thing instanceof Wall || thing instanceof Char || 
                    thing instanceof Monster) {
                    continue;
                }
                if (seen[neighborY - mazeBegin][neighborX - mazeBegin] == 0) {
                    seen[neighborY - mazeBegin][neighborX - mazeBegin] = 2;
                    record[neighborY - mazeBegin][neighborX - mazeBegin] = direction;
                    queue.add(new int[]{neighborX, neighborY});
                }
            }
            seen[currY - mazeBegin][currX - mazeBegin] = 1;
        }
    }

    private void createPath() {        
        do {
            bfs();
        } while (seen[calabash.getY() - mazeBegin][calabash.getX() - mazeBegin] == 0);
        int currX = calabash.getX();
        int currY = calabash.getY();       
        path = new Vector<>();
        while (currX != getX() || currY != getY()) {
            int direction = record[currY - mazeBegin][currX - mazeBegin];
            path.insertElementAt(direction, 0);
            int directionX = directions[direction][0];
            int directionY = directions[direction][1];
            currX = currX - directionX;
            currY = currY - directionY;
        }
        step = 0;
    }

    private int chooseDirection() {
        if (path != null && step < path.size() && calabash.getX() == storeX && calabash.getY() == storeY) {
            return path.get(step);
        }
        storeX = calabash.getX();
        storeY = calabash.getY();
        createPath();
        return path.get(step);
    }

    private boolean chooseMoveType() {
        if (calabash.getInvincible()) {
            return false;
        }
        return random.nextInt(10) < hardLevel;
    }

    private Position judgeMove(int directionX, int directionY) {
        Thing thing = world.get(getX() + directionX, getY() + directionY);
        if (thing instanceof Wall || thing instanceof Monster) {
            return Position.WALL;
        }
        else if (thing instanceof Calabash) {
            Calabash calabash = (Calabash)thing;
            if (!calabash.getInvincible()) {
                calabash.decreaseHP();
            }            
            return Position.CALABASH;
        }
        else if (thing instanceof Bullet) {
            Bullet bullet = (Bullet)thing;
            bullet.kill();
            return Position.BULLET;
        }
        else if (thing instanceof Bean) {
            return Position.BEAN;
        }
        else if (thing instanceof Cherry) {
            return Position.CHERRY;
        }
        else if (thing instanceof Drug) {
            return Position.DRUG;
        }
        return Position.FLOOR;
    }

    public boolean getAlive() {
        return alive;
    }

    @Override
    public void run() {
        while (alive) {
            Position position;
            int direction, directionX, directionY;
            boolean moveType = chooseMoveType();
            do {   
                if (moveType) {
                    direction = chooseDirection();
                }             
                else {
                    direction = random.nextInt(4);
                }
                setUrl("resources/ghost/direction" + direction + ".png");
                directionX = directions[direction][0];
                directionY = directions[direction][1];
                position = judgeMove(directionX, directionY);
            } while (position == Position.WALL);    
            if (position == Position.CALABASH || position == Position.BULLET) {
                kill();
                continue;
            }
            move(getX() + directionX, getY() + directionY);
            if (moveType) {
                step++;
            }
            else {
                createPath();
            }        
            isOn = position;            
            try {
                TimeUnit.MILLISECONDS.sleep(450);
            }
            catch(InterruptedException e) {
                break;
            }          
        }
    }
}
