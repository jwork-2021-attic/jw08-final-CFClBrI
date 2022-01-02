import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import com.anish.calabashbros.World;

import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameClient extends JFrame implements KeyListener, Runnable {
/*
    private Socket echoSocket;
    private PrintWriter out;
    private ObjectInputStream ois;
    private BufferedReader in;
*/
    private ByteBuffer byteBuffer;
    private SocketChannel socketChannel;

    private AsciiPanel terminal;
    private int mazeBegin = 1;
    private int mazeSize = 30;
    private int capacity = 1000;
    private String playerId = "";
    private int portNumber = 8888;

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

    private String fillString(String str, int targetLen) {
        String res = new String(str);
        for (int i = 0; i < targetLen - str.length(); i++) {
            res += ' ';
        }
        return res;
    }

    private void writeData(SocketChannel channel, String data) throws IOException {
        data = fillString(data, 20);
        byteBuffer = ByteBuffer.wrap(data.getBytes());
        channel.write(byteBuffer);        
    }

    private String readData(SocketChannel channel) throws IOException {
        /*
        String inputLine = "";
        try {
            CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
            inputLine = decoder.decode(byteBuffer.asReadOnlyBuffer()).toString().trim();            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        */
        byteBuffer = ByteBuffer.allocate(capacity);
        channel.read(byteBuffer);       
        byte[] bytes = byteBuffer.array();
        String res = new String(bytes).trim();
        return res;
    }

    private void startClient() {
        /*
        String hostName = getHostName();
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
        */
        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(portNumber));
            socketChannel.configureBlocking(false);
            playerId = readData(socketChannel);
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
        //out.println("keyEvent|" + playerId + "|" + e.getKeyCode());
        try {
            writeData(socketChannel, "keyEvent|" + playerId + "|" + e.getKeyCode());
        }
        catch (IOException exp) {
            exp.printStackTrace();
        }
        repaint();
    }

    public void stopUserInput() {
        //out.println("stopKeyEvent|" + playerId);
        try {
            writeData(socketChannel, "stopKeyEvent|" + playerId);
        }
        catch (IOException exp) {
            exp.printStackTrace();
        }
        repaint();
    }

    @Override
    public void repaint() {
        //out.println("repaint");        
        try {
            writeData(socketChannel, "repaint");        
            terminal.clear(' ', mazeBegin + mazeSize + 1, 1, 16, mazeSize);
            byteBuffer = ByteBuffer.allocate(capacity);
            String str = "";
            int readCount = 0;
            do {
                byteBuffer.clear();
                readCount = socketChannel.read(byteBuffer);
                byte[] bytes = byteBuffer.array();
                str += new String(bytes).trim();
            } while (readCount != 0);
            //System.out.println(res + "\n");
            
            //String str = readData(socketChannel);
            //str = in.readLine().trim();
            
            String[] output = str.split("\\|");
            if (output.length < World.HEIGHT * World.WIDTH) {
                return;
            }

            for (int y = 0; y < World.HEIGHT; y++) {
                for (int x = 0; x < World.WIDTH; x++) {
                    str = output[y * World.HEIGHT + x];
                    if (str.length() == 0) {
                        continue;
                    }
                    
                    if (str.charAt(0) == '^') {
                        continue;
                    }
                    else if (str.length() == 1) {
                        terminal.write(str.charAt(0), x, y);
                    }
                    else {
                        terminal.write(str, x, y);
                    }
                                        
                }
            }
                       
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
