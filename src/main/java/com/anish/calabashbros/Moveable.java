package com.anish.calabashbros;

public class Moveable extends Thing implements Runnable {
    
    protected int[][] directions;
    protected Position isOn;

    public Moveable(String url, World world) {
        super(url, world);

        directions = new int[4][2];
        directions[0][0] = -1; directions[0][1] = 0; //left
        directions[1][0] = 0; directions[1][1] = -1; //up
        directions[2][0] = 1; directions[2][1] = 0; //right
        directions[3][0] = 0; directions[3][1] = 1; //down
    }

    public void moveTo(int xPos, int yPos) {
        this.world.put(this, xPos, yPos);
    }

    protected void move(int xPos, int yPos) {
        reduct();
        moveTo(xPos, yPos);
    }

    protected Position getOn() {
        return isOn;
    }

    protected void reduct() {
        Thing thing;    
        if (isOn == Position.BEAN) {
            thing = new Bean(world);                
        }
        else if (isOn == Position.CHERRY) {
            thing = new Cherry(world);                
        }
        else if (isOn == Position.DRUG) {
            thing = new Drug(world);                
        }
        else { //if (isOn == Position.FLOOR)
            thing = new Floor(world);
        }
        world.put(thing, getX(), getY());
    }

    public void run() {
        
    }
}
