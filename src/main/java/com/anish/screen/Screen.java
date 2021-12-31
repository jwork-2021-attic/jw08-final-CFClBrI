package com.anish.screen;

import asciiPanel.AsciiPanel;

public interface Screen {

    public void displayOutput(AsciiPanel terminal);

    public String[][] getOutput();

    public Screen respondToUserInput(int num, int keyCode);

    public Screen stopUserInput(int num);
}
