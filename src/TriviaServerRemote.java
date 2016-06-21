import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

/**
 * Project name: TriviaBotJavaRMI
 * Created by: jjpawlica on 21.06.2016.
 */

public class TriviaServerRemote extends UnicastRemoteObject implements TriviaServer {

    //Przechowuje listę graczy
    private Vector<TriviaClient> players = new Vector<>();

    //Przechowuje referencję do okna serwera
    private TriviaServerRunner server;

    public TriviaServerRemote(TriviaServerRunner server) throws RemoteException {
        this.server = server;
    }

    public synchronized void join(TriviaClient k){

    }

    public synchronized void pause(TriviaClient k){

    }

    public synchronized void leave(TriviaClient k){

    }

    public synchronized void message(TriviaClient k, String s){

    }

}
