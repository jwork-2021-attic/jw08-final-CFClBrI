package com.anish.calabashbros;

import java.util.concurrent.TimeUnit;

public class Calabash extends Creature {
    
    private int beanCount;
    private int HP;
    private int invincibleTime = 0;
    private int beginX;
    private int beginY;
    private int mazeBegin;
    private int mazeSize;
    private int step = 0;
    private static final long serialVersionUID = 3L;

    public Calabash(World world, int beanCount, int beginHP,
                    int beginX, int beginY, int currX, int currY,
                    int mazeBegin, int mazeSize) {
        super("resources/player/3_stand.png", world);
        this.beanCount = beanCount;
        this.beginX = beginX;
        this.beginY = beginY;
        this.mazeBegin = mazeBegin;
        this.mazeSize = mazeSize;
        HP = beginHP;
        moveTo(currX, currY);
        isOn = Position.FLOOR;
        showHP(HP);
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

    public int getHP() {
        return HP;
    }

    public void decreaseHP() {
        HP--;
        showHP(HP);
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

    public void enterInvincible(int time) {
        if (time == 0) {
            return;
        }
        invincibleTime = time;
        showInvincible(invincibleTime);       
        Thread thread = new Thread(this);
        thread.start();    
    }

    private void leaveInvinsible() {        
        hideInvincible();               
    }

    public boolean getInvincible() {
        return invincibleTime != 0;
    }

    public int getInvincibleTime() {
        return invincibleTime;
    }

    private void eatBean() {        
        beanCount--;
        if (beanCount <= 0) {
            win();
        }
    }

    private void eatCherry() {
        if (invincibleTime != 0) {
            invincibleTime += 10;
            return;
        }
        enterInvincible(10);     
    }

    private void eatDrug() {
        if (HP < 9) {
            HP++;
            showHP(HP);
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
        showWin();
        world.killMonsters();
    }

    private void lose() {
        kill();
        showLose();
        world.killMonsters();
    }

    public void showHP(int HP) {
        int HPBeginX = mazeBegin + mazeSize + 2;
        int HPBeginY = mazeBegin + mazeSize / 2; 
        world.showString("HP:" + HP, HPBeginX, HPBeginY);       
    }
   
    public void showInvincible(int second) {
        int invinBeginX = mazeBegin + mazeSize + 2;
        int invinBeginY = mazeBegin + mazeSize / 2 + 1;        
        world.showString("Invincible:" + second / 10 + second % 10, invinBeginX, invinBeginY);        
    }

    public void hideInvincible() {
        int invinBeginX = mazeBegin + mazeSize + 2;
        int invinBeginY = mazeBegin + mazeSize / 2 + 1;
        for (int i = 0; i < 13; i++) {
            world.clear(invinBeginX + i, invinBeginY);
        }
    }

    public void showWin() {
        world.setOver();
        int winBeginX = mazeBegin + mazeSize + 2;
        int winBgeinY = mazeBegin + mazeSize / 2 - 1; 
        world.showString("You Win", winBeginX, winBgeinY);
    }

    public void showLose() {
        world.setOver();
        int loseBeginX = mazeBegin + mazeSize + 2;
        int loseBgeinY = mazeBegin + mazeSize / 2 - 1;  
        world.showString("You Lose", loseBeginX, loseBgeinY);
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
            if (invincibleTime == 0) {
                decreaseHP();
            }          
        }
    }

    public void stopWalk() {
        String url = getUrl();
        setUrl(url.substring(0, 19) + "stand.png");
    }

    @Override
    public void run() {
        if (invincibleTime == 0) {
            return;
        }
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
                invincibleTime--;                              
                if (invincibleTime == 0) {
                    leaveInvinsible();
                    break;
                }
                else {
                    showInvincible(invincibleTime);  
                }
            }
            catch(InterruptedException e) {
                break;
            }          
        }
    }
}
