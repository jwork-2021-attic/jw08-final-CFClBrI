package com.anish.calabashbros;

public class Char extends Thing {

    private char ch;
    private static final long serialVersionUID = 4L;

    public char getCh() {
        return ch;
    }

    public Char(World world, char ch) {
        super("", world);
        this.ch = ch;
    }

    @Override
    public String getUrl() {
        return String.valueOf(ch);
    }
}