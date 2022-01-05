package com.anish.screen;

public interface Screen {

    public String[][] getOutput();

    public Screen respondToUserInput(int num, int keyCode);

    public Screen stopUserInput(int num);
}
