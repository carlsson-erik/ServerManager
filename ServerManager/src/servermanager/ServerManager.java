package servermanager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author Erik
 */
public class ServerManager extends JFrame implements Runnable,ActionListener {
    
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
    
    private JLabel yourIp,userLabel;
    private JTextField text,portText,ipText;
    private JScrollPane scrollPane;
    private JTextArea textArea;
    private JButton sendButton,connectButton,leaveButton;
    
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
        t = new Thread(this, "ServerManager");
        
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

        this.setTitle("ServerManager");
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 600);
        this.setResizable(false);
        this.setLayout(new FlowLayout(1));

        sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(100, 100));
        sendButton.setVisible(true);

        connectButton = new JButton("Connect");
        connectButton.setPreferredSize(new Dimension(100, 100));
        connectButton.setVisible(true);
        
        leaveButton = new JButton("Leave");
        leaveButton.setPreferredSize(new Dimension(100, 100));
        leaveButton.setVisible(true);
        leaveButton.setEnabled(false);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setVisible(true);
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(200, 400));
        scrollPane.setVisible(true);

        text = new JTextField("Text");
        text.setName("Text");
        text.setPreferredSize(new Dimension(200, 50));
        text.setVisible(true);
        
        yourIp = new JLabel();
        try {
            yourIp.setText(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            Logger.getLogger(ServerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ipText = new JTextField("192.168.1.1");
        try {
            ipText.setText(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            Logger.getLogger(ServerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        ipText.setPreferredSize(new Dimension(200, 50));
        ipText.setVisible(true);

        portText = new JTextField("" + port);
        portText.setPreferredSize(new Dimension(200, 50));
        portText.setVisible(true);

        sendButton.addActionListener(this);
        connectButton.addActionListener(this);
        text.addActionListener(this);
        leaveButton.addActionListener(this);

        this.add(yourIp);
        this.add(ipText);
        this.add(portText);
        this.add(connectButton, BorderLayout.BEFORE_FIRST_LINE);
        this.add(leaveButton);
        this.add(sendButton, BorderLayout.WEST);
        this.add(scrollPane, BorderLayout.EAST);
        this.add(text, BorderLayout.SOUTH);
        
        this.validate();
        this.repaint();
        
    }
    
    @Override
    public void run() {
        mainSocket.start();
        while (running) {
            
            if (!mainSocket.getNewUsers().isEmpty()) {
                users.add(new User(userCount, mainSocket.getNewUsers().get(0)));
                mainSocket.getNewUsers().remove(mainSocket.getNewUsers().get(0));
                showMessage("New User");
                
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
                if(!u.getMessages().isEmpty()){
                    showMessage(u.getMessages().get(0));
                    
                    u.getMessages().remove(0);
                }
                if (u.getConnectedUser() != 0) {
                    if (!u.getMessages().isEmpty()) {
                        findUserById(u.getConnectedUser()).outputServerPackage(new ServerPackage(u.getMessages().get(0)));
                    }
                }
                //sends Users update
                if(u.isRequestUsersUpdate()){
                    for(User u1 : users){
                    u.outputServerPackage(new ServerPackage(u1.getId(),u1.getProfileName()));
                    showMessage("Sends Users update to : " + u.getId());
                    }
                    u.setRequestUsersUpdate(false);
                }
                
                
                
            }
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerManager.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public void sendMessage(String message){
        for(User u : users){
            u.outputServerPackage(new ServerPackage(message));
        }
        textArea.append("\n" + message);
    }
    
    public void showMessage(String message){
        textArea.append("\n" + message);
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {

        String cmd = e.getActionCommand().toString();

        switch (cmd) {
            case "Send":
                sendMessage(text.getText());
                break;

            case "Leave":
        
            
       
        
                break;
                
            case "Connect": 
                
            

            break;
        }
    }
}
