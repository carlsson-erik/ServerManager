package servermanager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Erik
 */
public class MainSocket implements Runnable {

    private int port, backLog;
    private ServerSocket serverSocket;
    private ArrayList<Socket> newUsers;

    private Thread t;
    public MainSocket(int port, int backLog) {
        t = new Thread(this,"MainSocket");
        this.port = port;
        this.backLog = backLog;
        newUsers = new ArrayList();
    }

    @Override
    public void run() {

        createServerSocket(port, backLog);

        while (true) {
            
            try {
                waitForConnect();
            } catch (IOException ex) {
                Logger.getLogger(ServerManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
    
    public void start(){
        t.start();
    }

    public void createServerSocket(int port, int backLog) {
        try {
            serverSocket = new ServerSocket(port, backLog);
        } catch (IOException ex) {
            Logger.getLogger(ServerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void waitForConnect() throws IOException {
        
        newUsers.add(serverSocket.accept());
        

    }
    

    /**
     * @return the newUsers
     */
    public ArrayList<Socket> getNewUsers() {
        return newUsers;
    }

    /**
     * @param newUsers the newUsers to set
     */
    public void setNewUsers(ArrayList<Socket> newUsers) {
        this.newUsers = newUsers;
    }

}
