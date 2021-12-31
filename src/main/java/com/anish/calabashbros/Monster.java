package com.anish.calabashbros;

import java.util.Random;
import java.util.LinkedList;
import java.util.Vector;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class Monster extends Creature {
    
    private Random random;
    private Vector<Calabash> players;
    private int mazeBegin;
    private int mazeSize;
    private Queue<int[]> queue;
    private int[][] seen;
    private int[][] record;
    private Vector<Integer> path;
    private int step;
    private Vector<int[]> store;
    private int destination = -1;
    private int hardLevel = 0;
    private static final long serialVersionUID = 8L;

    public Monster(World world, Vector<Calabash> players) {
        super("resources/ghost/direction3.png", world);
        
        random = new Random();        
        this.players = players;
        this.mazeBegin = world.mazeBegin;
        this.mazeSize = world.mazeSize;
        store = new Vector<>(players.size());
        isOn = Position.FLOOR;
    }

    public void setPlayers(Vector<Calabash> players) {
        this.players = players;
    }

    protected int bfs(Vector<Calabash> players) {
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
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getInvincible()) {
                    continue;
                }
                if (currX == players.get(i).getX() &&
                    currY == players.get(i).getY()) {
                    return i;
                }
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
        return -1;
    }

    protected void createPath(Vector<Calabash> players) {  
        destination = -1;      
        do {
            destination = bfs(players);
        } while (destination == -1);
        int currX = players.get(destination).getX();
        int currY = players.get(destination).getY();       
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

    private boolean judgePlayersMove() {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getX() != store.get(i)[0] ||
                players.get(i).getY() != store.get(i)[1]) {
                return true;
            }
        }
        return false;
    }

    private boolean judgePlayersInvincible() {
        for (int i = 0; i < players.size(); i++) {
            if (!players.get(i).getInvincible()) {
                return false;
            }
        }
        return true;
    }

    private void storePlayersPos() {
        for (int i = 0; i < players.size(); i++) {
            store.get(i)[0] = players.get(i).getX();
            store.get(i)[1] = players.get(i).getY();
        }
    }

    protected int chooseDirection() {
        if (path != null && step < path.size() &&
            !players.get(destination).getInvincible() && !judgePlayersMove()) {
            return path.get(step);
        }
        storePlayersPos();
        createPath(players);
        return path.get(step);
    }

    private boolean chooseMoveType() {
        if (judgePlayersInvincible()) {
            return false;
        }
        return random.nextInt(10) < hardLevel;
    }

    protected Position judgeMove(int directionX, int directionY) {
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
                createPath(players);
            }        
            isOn = position;            
            try {
                TimeUnit.MILLISECONDS.sleep(250);
            }
            catch(InterruptedException e) {
                break;
            }          
        }
    }
}
