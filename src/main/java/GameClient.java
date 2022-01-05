import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import com.anish.calabashbros.World;
import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameClient extends JFrame implements KeyListener, Runnable {

    private ByteBuffer byteBuffer;
    private SocketChannel socketChannel;

    private AsciiPanel terminal;
    private int mazeBegin = 1;
    private int mazeSize = 30;
    private int capacity = 1000;
    private String playerId = "";
    private int portNumber = 8888;

    public GameClient(AsciiPanel terminal) throws IOException {
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
        byteBuffer = ByteBuffer.allocate(10);
        channel.read(byteBuffer);       
        byte[] bytes = byteBuffer.array();
        String res = new String(bytes).trim();
        return res;
    }

    private void startClient() throws IOException {        
        socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(portNumber));            
        playerId = readData(socketChannel);
        socketChannel.configureBlocking(false);        
    }

    public void respondToUserInput(KeyEvent e) {
        try {
            writeData(socketChannel, "keyEvent|" + playerId + "|" + e.getKeyCode());
        }
        catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    public void stopUserInput() {
        try {
            writeData(socketChannel, "stopKeyEvent|" + playerId);
        }
        catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    @Override
    public void repaint() {      
        try {
            writeData(socketChannel, "repaint");        
            terminal.clear(' ', mazeBegin + mazeSize + 1, 0, 16, mazeSize);
            String str = "";
            int readCount = 0;
            do {
                byteBuffer = ByteBuffer.allocate(capacity);
                readCount = socketChannel.read(byteBuffer);
                byte[] bytes = byteBuffer.array();
                str += new String(bytes, 0, Math.min(bytes.length, readCount));
            } while (readCount > 0);
            str = str.trim();
            if (str == "") {
                return;
            }            
            String[] output = str.split("\\|");
            if (output.length < World.SCREEN_HEIGHT * World.WIDTH) {
                System.out.println(output.length);
                return;
            }

            for (int y = 0; y < World.SCREEN_HEIGHT; y++) {
                for (int x = 0; x < World.WIDTH; x++) {
                    str = output[y * World.WIDTH + x];
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
        try {
            GameClient gameClient = new GameClient(terminal);
            Thread thread = new Thread(gameClient);
            thread.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
