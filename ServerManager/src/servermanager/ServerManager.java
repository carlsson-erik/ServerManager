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
        
        users = new ArrayList();
        
        port = 4567;

        userCount = 0;
        backLog = 10;
        running = true;

        mainSocket = new MainSocket(port, backLog);

        createAndShowGUI();
        t.start();
    }
    
   public void createAndShowGUI(){
       
   }

    @Override
    public void run() {
        mainSocket.start();
        while (running) {
            
            if (!mainSocket.getNewUsers().isEmpty()) {
                users.add(new User(userCount, mainSocket.getNewUsers().get(0)));
                mainSocket.getNewUsers().remove(mainSocket.getNewUsers().get(0));
                System.out.println("New user");
            }
            
            for(User u : users){
                
            }
            
            

        }
    }


}
