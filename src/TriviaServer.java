import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Project name: TriviaBotJavaRMI
 * Created by: jjpawlica on 21.06.2016.
 */
public interface TriviaServer extends Remote{

    public void join(TriviaClient k) throws RemoteException;
    public void pause(TriviaClient k) throws RemoteException;
    public void leave(TriviaClient k) throws RemoteException;
    public void message(TriviaClient k, String s) throws RemoteException;
}
