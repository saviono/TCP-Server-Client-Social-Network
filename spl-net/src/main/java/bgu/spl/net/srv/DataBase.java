package bgu.spl.net.srv;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A Class represents a shared object for the server
 */
public class DataBase {


    private ConcurrentHashMap<String, User> dataBase;//all the users in the server
    private ConcurrentHashMap<Integer,User> hashMapByConnectionId;//all the active users in the server (logged in users)
    private ConcurrentHashMap<String,Integer> hashMapByUserNameToIdConnection; //all the active users in the server (logged in users)
    private String usersByNamesQueueSeperetedByZero;
    private AtomicInteger registeredUsersCounter;
    private Object locker = new Object();

    public DataBase() {
        this.dataBase = new ConcurrentHashMap<>();
        this.hashMapByConnectionId=new ConcurrentHashMap<>();
        this.hashMapByUserNameToIdConnection=new ConcurrentHashMap<>();
        this.usersByNamesQueueSeperetedByZero="";
        this.registeredUsersCounter = new AtomicInteger(0);
    }


    public ConcurrentHashMap<String, User> getDataBase() {
        return dataBase;
    }

    /**
     * Tries register a new user.
     * Returns false if already registered, else true
     */
    public boolean addUser(String userName, String password) {

        User temp = dataBase.putIfAbsent(userName, new User(userName, password));
        if (temp!=null){
            return false;
        }
        synchronized (locker) {
            this.usersByNamesQueueSeperetedByZero = this.usersByNamesQueueSeperetedByZero + (userName + "\0");
        }
        registeredUsersCounter.incrementAndGet();
        return true;
    }

    /**
     * Tries register a new user.
     * Returns false if already registered, else true
     */
    public boolean addActiveUser(int connectionId ,String userName, String password) {
        if(dataBase.containsKey(userName) && (!hashMapByConnectionId.containsKey(userName))&&(!hashMapByConnectionId.containsKey(connectionId))) {
            User myUser = dataBase.get(userName);
                if (myUser.getPassword().equals(password)) {
                    Integer temp2 = this.hashMapByUserNameToIdConnection.putIfAbsent(userName, connectionId);
                    if (temp2 != null) {
                        return false;
                    }
                        User temp1 = this.hashMapByConnectionId.putIfAbsent(connectionId, myUser);
                        if (temp1 != null) {
                            return false;
                        }

                    return true;
                }
                return false;
            }
        else
            return false;
    }


    /**
     * Tries Logout a user.
     * Returns false if already logout, else true
     */
    public boolean logMeOut(int connectionId){
        if(hashMapByConnectionId.containsKey(connectionId)){

            synchronized (hashMapByConnectionId.get(connectionId)){

                User myUser =hashMapByConnectionId.get(connectionId);
                hashMapByConnectionId.remove(connectionId);
                hashMapByUserNameToIdConnection.remove(myUser.getUserName());
            }

            return true;
        }
        return false;
    }


    /**
     *Returns UserByName or null if not exist
     */
    public User getUserByName (String userName){
        return this.dataBase.get(userName);
    }


    /**
     *Checks if userName has registered in the System in the past.
     */
    public boolean isUserExist(String userName){
        return this.dataBase.containsKey(userName);
    }

    public boolean isActive(String userName){
        return (isUserExist(userName) & this.hashMapByUserNameToIdConnection.containsKey(userName));
    }

    public boolean isActiveByConnectionID(int connectionID){
        if (this.hashMapByConnectionId.containsKey(connectionID)){
            return true;
        }
        return false;
    }




    public ConcurrentHashMap<String, Integer> getHashMapByUserNameToIdConnection() {
        return hashMapByUserNameToIdConnection;
    }

    public String getUsersByNamesQueueSeperetedByZero() {
        synchronized (locker) {
            return usersByNamesQueueSeperetedByZero;
        }
    }

    public int getRegisteredUsersCounter() {
        return registeredUsersCounter.get();
    }

}

