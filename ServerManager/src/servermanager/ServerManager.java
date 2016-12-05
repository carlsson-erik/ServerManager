package servermanager;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;

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
    
    private JLabel userLabel;
    
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
    
    public void createAndShowGUI() {
        this.setSize(500, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        
        userLabel = new JLabel();
        userLabel.setText("hej");
        userLabel.setPreferredSize(new Dimension(200, 50));
        userLabel.setVisible(true);
        userLabel.setBackground(Color.red);
        this.add(userLabel);
    }
    
    @Override
    public void run() {
        mainSocket.start();
        while (running) {
            
            if (!mainSocket.getNewUsers().isEmpty()) {
                users.add(new User(userCount, mainSocket.getNewUsers().get(0)));
                mainSocket.getNewUsers().remove(mainSocket.getNewUsers().get(0));
                System.out.println("New user");
                userLabel.setText("new user");
            }
            
            for (User u : users) {
                //Send connectRequests
                if (u.getRequestedUser() != 0) {
                    User u1 = findUserById(u.getId());
                    if (u1 != null) {
                        u1.outputServerPackage(new ServerPackage(u.getRequestedUser()));
                    }
                }
                //connect Two users
                if (u.getRequestedUser() != 0) {
                    if (findUserById(u.getRequestedUser()).getRequestedUser() == u.getId()) {
                        u.setConnectedUser(findUserById(u.getRequestedUser()).getId());
                        findUserById(u.getRequestedUser()).setConnectedUser(u.getId());
                    }
                }

                //Send Messages
                if (u.getConnectedUser() != 0) {
                    if (!u.getMessages().isEmpty()) {
                        findUserById(u.getConnectedUser()).outputServerPackage(new ServerPackage(u.getMessages().get(0)));
                    }
                }
                if(u.isRequestUsersUpdate()){
                    u.outputServerPackage(new ServerPackage(users));
                }
                
            }
            
        }
    }
    
    public User findUserById(int id) {
        
        for (User u : users) {
            if (u.getId() == id) {
                return u;
            }
        }
        
        return null;
    }
    
}
