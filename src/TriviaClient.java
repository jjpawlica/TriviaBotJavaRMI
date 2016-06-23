

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * Project name: TriviaBotJavaRMI
 * Created by: jjpawlica on 21.06.2016.
 */
public interface TriviaClient extends Remote{

    public void message(String message) throws RemoteException;

    public String getPlayerName() throws RemoteException;

    public int getPlayerScore() throws RemoteException;
    public void setPlayerScore(int newScore) throws RemoteException;

    public void refreshPlayerList(Vector<TriviaClient> playersList) throws RemoteException;

}
