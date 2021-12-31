import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import com.anish.calabashbros.World;

import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/*
class UIScreen extends JFrame implements Runnable, KeyListener {

    private AsciiPanel terminal;
    private Screen screen;

    private int mazeBegin = 1;
    private int mazeSize = 30;
    private int playerId;

    UIScreen(AsciiPanel terminal, Screen screen, int playerId) {
        super();
        this.terminal = terminal;
        this.screen = screen;
        this.playerId = playerId;
        add(terminal);
        pack();
        addKeyListener(this);        
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void respondToUserInput(KeyEvent e) {
        repaint();
    }

    public void stopUserInput() {
        repaint();
    }

    @Override
    public void repaint() {
        terminal.clear(' ', mazeBegin + mazeSize + 1, 1, 16, mazeSize);
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
*/

public class GameClient extends JFrame implements KeyListener, Runnable {

    private Socket echoSocket;
    private PrintWriter out;
    private ObjectInputStream ois;
    private BufferedReader in;

    private AsciiPanel terminal;
    private int mazeBegin = 1;
    private int mazeSize = 30;
    private String playerId = "";

    public GameClient(AsciiPanel terminal) {
        super();

        this.terminal = terminal;
        add(terminal);
        pack();
        addKeyListener(this);        
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startClient();
    }

    private void startClient() {
        String hostName = getHostName();
        int portNumber = 8888;
        try {
            echoSocket = new Socket(hostName, portNumber);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            ois = new ObjectInputStream(echoSocket.getInputStream());

            out.println("connect");
            playerId = in.readLine().trim();
        }
        catch (IOException e) {
            e.printStackTrace();
        } 
    } 

    public static String getHostNameForLiunx() {
		try {
			return (InetAddress.getLocalHost()).getHostName();
		} catch (UnknownHostException uhe) {
			String host = uhe.getMessage(); // host = "hostname: hostname"
			if (host != null) {
				int colon = host.indexOf(':');
				if (colon > 0) {
					return host.substring(0, colon);
				}
			}
			return "UnknownHost";
		}
	}
 
	public static String getHostName() {
		if (System.getenv("COMPUTERNAME") != null) {
			return System.getenv("COMPUTERNAME");
		} else {
			return getHostNameForLiunx();
		}
	}

    public void respondToUserInput(KeyEvent e) {
        out.println("keyEvent");
        out.println(playerId);
        out.println(String.valueOf(e.getKeyCode()));
        repaint();
    }

    public void stopUserInput() {
        out.println("stopKeyEvent");
        out.println(playerId);
        repaint();
    }

    private boolean stringEqual(String s1, String s2) {
        if (s1.length() != s2.length()) {
            return false;
        }
        for (int i = 0; i < s1.length(); i++) {
            if ((int)s1.charAt(i) != (int)s2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void repaint() {
        out.println("repaint");
        terminal.clear(' ', mazeBegin + mazeSize + 1, 1, 16, mazeSize);
        String str = "";
        try {        
                        
            str = in.readLine().trim();
            //System.out.println(str);
            
            for (int x = 0; x < World.WIDTH; x++) {
                for (int y = 0; y < World.HEIGHT; y++) {
                
                    str = in.readLine().trim();
                    
                    if (str.length() == 0) {
                        continue;
                    }
                    if (str.length() == 1) {
                        terminal.write(str.charAt(0), x, y);
                    }
                    else {
                        terminal.write(str, x, y);
                    }
                                        
                }
            }

                        
            str = in.readLine().trim();
            //System.out.println(str);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void main(String[] args) {
        AsciiPanel terminal = new AsciiPanel(World.WIDTH, World.HEIGHT, AsciiFont.CP437_32x32);
        GameClient gameClient = new GameClient(terminal);
        Thread thread = new Thread(gameClient);
        thread.start();
    }
}
