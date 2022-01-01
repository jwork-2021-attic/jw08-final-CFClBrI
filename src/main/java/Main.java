import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.anish.calabashbros.World;
import com.anish.screen.Screen;
import com.anish.screen.WorldScreen;

public class Main {

    private static boolean stringEqual(String s1, String s2) {
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

    public static void main(String[] args) {
        Screen screen = new WorldScreen();
        int playerId = 0;

        int portNumber = 8888;
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();     
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());                  
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String[] inputLine;            
            do {
                inputLine = in.readLine().trim().split("\\|");
                if (stringEqual(inputLine[0], "connect")) {
                    out.println(Integer.toString(playerId));                    
                    playerId++;
                }
                else if (stringEqual(inputLine[0], "keyEvent")) {                    
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
                    out.println(str);
                    
                }
                else {
                    System.out.println("unknown input: " + inputLine + ".");
                }
            } while (inputLine != null);
            serverSocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
