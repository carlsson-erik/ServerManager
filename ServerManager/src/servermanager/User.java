package servermanager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Erik
 */
public class User implements Runnable,Serializable {

    private Thread t;

    private int id;
    private String profileName;
    private int requestedUser;
    private int connectedUser;
    private ArrayList<String> messages;
    
    private boolean requestUsersUpdate;
    
    

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public User(int id, Socket socket) {
        this.id = id;
        messages = new ArrayList();
        profileName = "TestName";
        requestedUser = 0;
        connectedUser = 0;
        t = new Thread(this, "user:" + id);
        this.socket = socket;
        t.start();
    }

    @Override
    public void run() {

        while (true) {
            
            if (socket != null) {

                try {
                    setupStreams();
                    whileConnected();
                    closeStreams();
                } catch (IOException ex) {

                    Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }
    }

    public void connectToServer(String ip, int port) throws IOException {

        System.out.println("Attempting to connect...");

        socket = new Socket(ip, port);

        System.out.println("Now connected to: " + socket.getInetAddress().getHostName());

    }

    public void setupStreams() throws IOException {
        System.out.println("Setting up streams");
        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(socket.getInputStream());
        System.out.println("Streams setup");
    }

    public void whileConnected() throws IOException {

        System.out.println("Connected");

        do {
            try {
                readInputObject((UserPackage) input.readObject());

            } catch (ClassNotFoundException n) {
                System.out.println("Error reading input stream file");
            }

        } while (true);

    }

    public void readInputObject(UserPackage uPack) {
        switch(uPack.getCommand()){
        //requestConnection
        case 001 :
            requestedUser = Integer.parseInt(uPack.getData());
            
            break;
            //Send Message
        case 010 :
            
            //if(connectedUser !=0){
                messages.add(uPack.getData());
          //  }
            
            break;
            //SetPrfileName
        case 011:
            if(uPack.getData() != null){
            profileName = uPack.getData();
        }
            break;
            //requestUsersUpdate
        case 100:
            
            requestUsersUpdate = true;
            
            break;
        
    }
    }

    public void outputServerPackage(ServerPackage serverPackage) {

        try {
            output.writeObject(serverPackage);
            output.flush();
        } catch (IOException e) {
            System.out.println("Could not output serverPackage");

        }

    }

    public void closeStreams() throws IOException {
        output.close();
        input.close();
        socket.close();
        socket = null;
    }

    /**
     * @return the requestedUser
     */
    public int getRequestedUser() {
        return requestedUser;
    }

    /**
     * @param requestedUser the requestedUser to set
     */
    public void setRequestedUser(int requestedUser) {
        this.requestedUser = requestedUser;
    }

    /**
     * @return the connectedUser
     */
    public int getConnectedUser() {
        return connectedUser;
    }

    /**
     * @param connectedUser the connectedUser to set
     */
    public void setConnectedUser(int connectedUser) {
        this.connectedUser = connectedUser;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the messages
     */
    public ArrayList<String> getMessages() {
        return messages;
    }

    /**
     * @return the requestUsersUpdate
     */
    public boolean isRequestUsersUpdate() {
        return requestUsersUpdate;
    }

    /**
     * @param requestUsersUpdate the requestUsersUpdate to set
     */
    public void setRequestUsersUpdate(boolean requestUsersUpdate) {
        this.requestUsersUpdate = requestUsersUpdate;
    }

    /**
     * @return the profileName
     */
    public String getProfileName() {
        return profileName;
    }

}
