package com.anish.calabashbros;

public class Wall extends Thing {

    public Wall(int num, World world) {
        super("resources/wall/wall" + num + ".png", world);
    }

}
