import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

/**
 * Project name: TriviaBotJavaRMI
 * Created by: jjpawlica on 21.06.2016.
 */

public class TriviaServerRemote extends UnicastRemoteObject implements TriviaServer {

    //Przechowuje listę graczy
    private Vector<TriviaClient> triviaClients = new Vector<>();

    //Przechowuje referencję do okna serwera
    private TriviaServerRunner serverWindow;

    public TriviaServerRemote(TriviaServerRunner serverWindow) throws RemoteException {
        this.serverWindow = serverWindow;
    }

    public synchronized void join(TriviaClient client){
        triviaClients.add(client);
        serverWindow.showMessage("New player joined the game");

    }

    public synchronized void pause(TriviaClient client){
        serverWindow.showMessage("Player paused the game");
    }

    public synchronized void leave(TriviaClient client){
        triviaClients.remove(client);
        serverWindow.showMessage("Player left the game");

    }

    public synchronized void message(TriviaClient client, String message){
        serverWindow.showMessage("Player got the right answer");
    }

}
