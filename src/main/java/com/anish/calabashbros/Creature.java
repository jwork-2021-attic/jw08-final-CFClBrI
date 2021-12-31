package com.anish.calabashbros;

public class Creature extends Moveable  {

    protected boolean alive = true;

    Creature(String url, World world) {
        super(url, world);
    }

    public void kill() {
        reduct();
        alive = false;
    }

    public boolean getAlive() {
        return alive;
    }
}
