package servermanager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Erik
 */
public class ServerManager extends JFrame implements Runnable {

    ServerSocket serverSocket;
    Socket socket;
    ObjectOutputStream output;
    ObjectInputStream input;

    ArrayList<User> users;

    int port, userCount;

    private Thread t;
    private int backLog;

    private MainSocket mainSocket;

    private boolean running;

    public static void main(String[] args) {
        try {
            // Set System L&F
            //UIManager.setLookAndFeel(
            //       UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new ServerManager();
    }

    public ServerManager() {
        t = new Thread(this, "ClientManager");

        port = 4567;

        userCount = 0;
        backLog = 10;
        running = true;

        mainSocket = new MainSocket(port, backLog);

        t.start();
    }

    @Override
    public void run() {

        while (running) {

            if (!mainSocket.getNewUsers().isEmpty()) {
                users.add(new User(userCount, mainSocket.getNewUsers().get(0)));
                mainSocket.getNewUsers().remove(mainSocket.getNewUsers().get(0));

            }
            
            

        }
    }

    public void createServerSocket(int port, int backLog) {
        try {
            serverSocket = new ServerSocket(port, backLog);
        } catch (IOException ex) {
            Logger.getLogger(ServerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void waitForConnect() throws IOException {

        serverSocket.setSoTimeout(1000);
        users.add(new User(userCount, serverSocket.accept()));

    }

}
