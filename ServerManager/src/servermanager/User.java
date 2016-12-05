package servermanager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Erik
 */
public class User implements Runnable {

    private Thread t;

    private int id;
    private String profileName;
    private int requestedUser;
    private int connectedUser;
    private ArrayList<String> messages;

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public User(int id, Socket socket) {
        this.id = id;
        profileName = null;
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

    }

    public void outputObject(Object object) {

        try {
            output.writeObject(object);
            output.flush();
        } catch (IOException e) {
            System.out.println("Could not output object");

        }

    }

    public void closeStreams() throws IOException {
        output.close();
        input.close();
        socket.close();
        socket = null;
    }

}
