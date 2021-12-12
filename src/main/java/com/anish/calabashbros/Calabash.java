package com.anish.calabashbros;

import java.util.Timer;
import java.util.TimerTask;

public class Calabash extends Creature {
    
    private int beanCount;
    private int HP = 3;
    private boolean isInvincible = false;
    private int invincibleTime = 0;
    private int beginX;
    private int beginY;
    private int step = 0;

    public Calabash(World world, int beanCount,
                    int beginX, int beginY) {
        super("resources/player/3_stand.png", world);
        this.beanCount = beanCount;
        this.beginX = beginX;
        this.beginY = beginY;
        moveTo(beginX, beginY);
        isOn = Position.FLOOR;
        world.showHP(HP);
    }

    private void judgeEat(Moveable moveable) {
        if (moveable.getOn() == Position.BEAN) {
            eatBean();
        }
        else if (moveable.getOn() == Position.CHERRY) {
            eatCherry();
        }
        else if (moveable.getOn() == Position.DRUG) {
            eatDrug();
        }
    }

    private Position judgeMove(int directionX, int directionY) {
        Thing thing = world.get(getX() + directionX, getY() + directionY);
        if (thing instanceof Wall || thing instanceof Calabash ||
            thing instanceof Bullet) {
            return Position.WALL;
        }
        else if (thing instanceof Monster) {
            Monster monster = (Monster)thing;
            judgeEat(monster);
            monster.kill();
            return Position.MONSTER;
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

    public void decreaseHP() {
        HP--;
        world.showHP(HP);
        if (HP <= 0) {
            lose();
        }
        else {
            resurrect();
        }
    }

    private void resurrect() {
        move(beginX, beginY);
        setUrl("resources/player/3_stand.png");
        world.yBegin = 0;
        eatCherry();
    }

    private void enterInvincible() {
        isInvincible = true;
        invincibleTime = 10;
        world.showInvincible(invincibleTime);
    }

    private void leaveInvinsible() {
        if (isInvincible) {
            isInvincible = false;
            world.hideInvincible();
        }        
    }

    public boolean getInvincible() {
        return isInvincible;
    }

    private void eatBean() {        
        beanCount--;
        if (beanCount <= 0) {
            win();
        }
    }

    private void eatCherry() {
        if (isInvincible) {
            invincibleTime += 10;
            return;
        }
        enterInvincible();
        Timer timer = new Timer();                        
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                invincibleTime--;                              
                if (invincibleTime == 0) {
                    leaveInvinsible();
                    timer.cancel();
                }
                else {
                    world.showInvincible(invincibleTime);  
                }
            }
        }, 1000, 1000);        
    }

    private void eatDrug() {
        if (HP < 9) {
            HP++;
            world.showHP(HP);
        }        
    }

    public void createBullet() {
        String url = getUrl();
        int direction = url.charAt(17) - '0';
        int directionX = directions[direction][0];
        int directionY = directions[direction][1];
        Thing thing = world.get(getX() + directionX, getY() + directionY);
        while (thing instanceof Calabash) {
            directionX += directions[direction][0];
            directionY += directions[direction][1];
            thing = world.get(getX() + directionX, getY() + directionY);
        }
        if (thing instanceof Wall || thing instanceof Bullet) {
            return;
        }
        else if (thing instanceof Monster) {
            Monster monster = (Monster)thing;
            monster.kill();
            return;
        }
        Position position;
        if (thing instanceof Bean) {            
            position = Position.BEAN;
        }
        else if (thing instanceof Cherry) {
            position = Position.CHERRY;
        }
        else if (thing instanceof Drug) {
            position = Position.DRUG;
        }
        else { //if (thing instanceof Floor)
            position = Position.FLOOR;
        }
        Bullet bullet = new Bullet(world, direction, position);
        world.put(bullet, getX() + directionX, getY() + directionY);
        Thread bulletThread = new Thread(bullet);
        bulletThread.start();
    }

    private void win() {
        kill();
        world.showWin();
        world.killMonsters();
    }

    private void lose() {
        kill();
        world.showLose();
        world.killMonsters();
    }

    public void walk(int direction) {
        if (!alive) {
            return;
        }
        setUrl("resources/player/" + direction + "_walk" + step + ".png");
        step = (step + 1) % 2;
        int directionX = directions[direction][0];
        int directionY = directions[direction][1];        
        Position position = judgeMove(directionX, directionY);
        if (position == Position.WALL || position == Position.BULLET) {
            return;
        }
        move(getX() + directionX, getY() + directionY);
        if (position == Position.BEAN) {
            eatBean();
        }
        else if (position == Position.CHERRY) {
            eatCherry();
        }
        else if (position == Position.DRUG) {
            eatDrug();
        }
        else if (position == Position.MONSTER) {
            if (!isInvincible) {
                decreaseHP();
            }
        }
    }

    public void stopWalk() {
        String url = getUrl();
        setUrl(url.substring(0, 19) + "stand.png");
    }
}
