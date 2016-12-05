
package servermanager;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author carls
 */
public class ServerPackage implements Serializable{

    
    private ArrayList<User> users;
    private String message,profileName;
    private int requestConnectId,id;
    
    public ServerPackage(int id, String profileName){
        this.id = id;
        this.profileName = profileName;
    }
    
    public ServerPackage(int requestConnectId){
        this.requestConnectId = requestConnectId;
    }
    
    public ServerPackage(String message){
        this.message = message;
    }

    /**
     * @return the users
     */
    public ArrayList<User> getUsers() {
        return users;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the requestConnectId
     */
    public int getRequestConnectId() {
        return requestConnectId;
    }

    /**
     * @return the profileName
     */
    public String getProfileName() {
        return profileName;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    
    
}
