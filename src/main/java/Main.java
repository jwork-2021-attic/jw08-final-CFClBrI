

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import com.anish.calabashbros.World;
import com.anish.screen.Screen;
import com.anish.screen.WorldScreen;

import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;

class UIScreen extends JFrame implements Runnable, KeyListener {

    private AsciiPanel terminal;
    private Screen screen;

    private int mazeBegin = 1;
    private int mazeSize = 30;

    UIScreen(AsciiPanel terminal, Screen screen) {
        super();
        this.terminal = terminal;
        this.screen = screen;
        add(terminal);
        pack();
        addKeyListener(this);        
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void respondToUserInput(KeyEvent e) {
        screen = screen.respondToUserInput(e);
        repaint();
    }

    public void stopUserInput() {
        screen = screen.stopUserInput();
        repaint();
    }

    @Override
    public void repaint() {
        terminal.clear(' ', mazeBegin + mazeSize + 1, 1, 15, mazeSize);
        screen.displayOutput(terminal);
        super.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        respondToUserInput(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        stopUserInput();
    }

    public void run() {
        try {
            while (true) {                
                TimeUnit.MILLISECONDS.sleep(100);
                repaint();
            }
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        AsciiPanel terminal = new AsciiPanel(World.WIDTH, World.HEIGHT, AsciiFont.CP437_32x32);
        Screen screen = new WorldScreen();
        UIScreen uiScreen = new UIScreen(terminal, screen);
        Thread uiThread = new Thread(uiScreen);
        uiThread.start();
    }

}
