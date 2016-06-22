

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * Project name: TriviaBotJavaRMI
 * Created by: jjpawlica on 21.06.2016.
 */
public interface TriviaClient extends Remote{

    public void joinMessage(String playerName, Vector<TriviaClient> playersList) throws RemoteException;
    public void leaveMessage(String playerName, Vector<TriviaClient> playerList) throws RemoteException;

    public void answer(String answer) throws RemoteException;

    public String getPlayerName() throws RemoteException;
    public int getPlayerScore() throws RemoteException;

    public void setPlayerName() throws RemoteException;
    public int setPlayerScore() throws RemoteException;

}
