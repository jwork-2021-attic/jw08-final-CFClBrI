
import java.io.IOException;
import java.net.InetSocketAddress;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;

import com.anish.calabashbros.World;
import com.anish.screen.Screen;
import com.anish.screen.WorldScreen;

public class GameServer extends Thread {
    
    private ByteBuffer byteBuffer;
    private Screen screen = new WorldScreen();
    private int playerId = 0;
    private int capacity = 20;
    private int portNumber = 8888;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private SelectionKey key;

    public GameServer() throws IOException {
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

    private String fillString(String str, int targetLen) {
        String res = new String(str);
        for (int i = 0; i < targetLen - str.length(); i++) {
            res += ' ';
        }
        return res;
    }

    private void writeData(SocketChannel channel, String data) throws IOException {
        byteBuffer = ByteBuffer.wrap(data.getBytes());
        channel.write(byteBuffer);        
    }

    private String readData(SocketChannel channel) throws IOException {
        byteBuffer = ByteBuffer.allocate(capacity);
        channel.read(byteBuffer);
        byte[] bytes = byteBuffer.array();
        String res = new String(bytes).trim();
        return res;
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
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        writeData(socketChannel, Integer.toString(playerId));
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
                            //writeData(socketChannel, fillString(str, 20000));
                            /*
                            byteBuffer = ByteBuffer.wrap(str.getBytes());
                            while (byteBuffer.hasRemaining()) {
                                socketChannel.write(byteBuffer);
                            }
                            */
                            int i = 0;
                            String currStr = "";
                            for (i = 0; i < str.length(); i += 1000) {
                                currStr = str.substring(i, Math.min(i + 1000, str.length()));
                                byteBuffer = ByteBuffer.wrap(currStr.getBytes());
                                socketChannel.write(byteBuffer);
                            }
                            
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
        try {
            GameServer gameServer = new GameServer();
            gameServer.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
