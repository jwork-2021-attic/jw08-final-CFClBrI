import java.io.IOException;
import java.net.InetSocketAddress;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.anish.calabashbros.World;
import com.anish.screen.Screen;
import com.anish.screen.WorldScreen;

public class GameServer extends Thread {
    
    private ByteBuffer byteBuffer;
    private Screen screen;
    private int playerId = 0;
    private int capacity = 20;
    private int portNumber = 8888;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private SelectionKey key;
    private boolean repaintRequest = false;

    public GameServer(Screen screen) throws IOException {
        this.screen = screen;

        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(portNumber));
        selector = Selector.open();
        serverSocketChannel.configureBlocking(false);
        key = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
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

    private String readData(SocketChannel channel) throws IOException {
        byteBuffer = ByteBuffer.allocate(capacity);
        channel.read(byteBuffer);
        byte[] bytes = byteBuffer.array();
        String res = new String(bytes).trim();
        return res;
    }

    private String getOutPutStr() {
        String[][] output = screen.getOutput();
        String str = "";                                                    
        for (int y = 0; y < World.HEIGHT; y++) {
            for (int x = 0; x < World.WIDTH; x++) {
                if (output[x][y] == "") {
                    str += '^';
                }
                else {
                    str += output[x][y];
                }
                str += '|';
            }
        }
        return str;
    }

    @Override
    public void run() {
        try {       
            while (true) {
                int readyChannel = selector.select();
                if (readyChannel == 0) {
                    continue;
                }
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext()) {
                    key = iterator.next();
                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        byteBuffer = ByteBuffer.wrap(Integer.toString(playerId).getBytes());
                        socketChannel.write(byteBuffer);
                        playerId++;
                    }
                    else if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel)key.channel();
                        String[] inputLine = readData(socketChannel).split("\\|");
                        if (stringEqual(inputLine[0], "keyEvent")) {                  
                            int num = Integer.parseInt(inputLine[1]);
                            int keyCode = Integer.parseInt(inputLine[2]);
                            screen.respondToUserInput(num, keyCode);                    
                        }
                        else if (stringEqual(inputLine[0], "stopKeyEvent")) {                    
                            int num = Integer.parseInt(inputLine[1]);
                            screen.stopUserInput(num);                   
                        }
                        else if (stringEqual(inputLine[0], "repaint")) {
                            repaintRequest = true;
                        }
                    }
                    else if (key.isWritable()) {
                        if (repaintRequest) {
                            SocketChannel socketChannel = (SocketChannel)key.channel();
                            String str = getOutPutStr();
                            byteBuffer = ByteBuffer.wrap(str.getBytes());
                            while (byteBuffer.hasRemaining()) {
                                socketChannel.write(byteBuffer);
                            }
                            repaintRequest = false;
                        }                        
                    }
                    iterator.remove();
                }
            }            
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Screen screen = new WorldScreen();
        try {
            GameServer gameServer = new GameServer(screen);
            gameServer.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
