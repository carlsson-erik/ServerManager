
package servermanager;

import java.util.ArrayList;

/**
 *
 * @author carls
 */
public class ServerPackage {

    
    private ArrayList<User> users;
    private String message;
    private int requestConnectId;
    
    public ServerPackage(ArrayList<User> users){
        this.users = users;
    }
    
    public ServerPackage(int requestConnectId){
        this.requestConnectId = requestConnectId;
    }
    
    public ServerPackage(String message){
        this.message = message;
    }
    
    
}
