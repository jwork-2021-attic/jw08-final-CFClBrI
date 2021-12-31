package com.anish.calabashbros;

public class Wall extends Thing {

    private static final long serialVersionUID = 14L;

    public Wall(int num, World world) {
        super("resources/wall/wall" + num + ".png", world);
    }

}
