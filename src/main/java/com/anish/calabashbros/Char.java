package com.anish.calabashbros;

public class Char extends Thing {

    private char ch;

    public char getCh() {
        return ch;
    }

    public Char(World world, char ch) {
        super("", world);
        this.ch = ch;
    }
}