package servermanager;

/**
 *
 * @author Erik
 */
public class UserPackage {
    private String data;
    private int command;
    
    UserPackage(int command, String data){
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
