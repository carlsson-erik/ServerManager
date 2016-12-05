package servermanager;

import java.io.Serializable;

/**
 *
 * @author Erik
 */
public class UserPackage implements Serializable{
    private String data;
    private int command;
    
    public UserPackage(int command, String data){
        this.data = data;
        this.command = command;
    }

    /**
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * @return the command
     */
    public int getCommand() {
        return command;
    }
    
    
}
