package com.anish.calabashbros;

import java.util.concurrent.TimeUnit;

public class Bullet extends Moveable {
    
    private int direction;
    private static final long serialVersionUID = 2L;

    public Bullet(World world, int direction, Position isOn) {
        super("resources/arrow/" + direction + ".png", world);

        this.direction = direction;
        this.isOn = isOn;
    }

    private Position judgeMove(int directionX, int directionY) {
        Thing thing = world.get(getX() + directionX, getY() + directionY);
        if (thing instanceof Wall) {
            return Position.WALL;
        }
        else if (thing instanceof Calabash) {            
            return Position.CALABASH;
        }
        else if (thing instanceof Monster) {
            Monster monster = (Monster)thing;
            monster.kill();
            return Position.MONSTER;
        }
        else if (thing instanceof Bullet) {
            Bullet bullet = (Bullet)thing;
            bullet.stop();
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

    public void run() {
        while (!isStop) {
            int directionX = directions[direction][0];
            int directionY = directions[direction][1];
            Position position = judgeMove(directionX, directionY);
            while (position == Position.CALABASH) {
                directionX += directions[direction][0];
                directionY += directions[direction][1];
                position = judgeMove(directionX, directionY);
            }
            if (position == Position.WALL || position == Position.MONSTER ||
                position == Position.BULLET) {                
                stop();
                continue;                           
            }
            move(getX() + directionX, getY() + directionY);
            isOn = position;
            try {
                TimeUnit.MILLISECONDS.sleep(150);
            }
            catch(InterruptedException e) {
                break;
            }
        }
    }
}
