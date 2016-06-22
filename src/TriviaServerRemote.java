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

    //Przechowuje referencję do gry
    private TriviaGameLogic mTriviaGame = serverWindow.getTriviaGame();

    public TriviaServerRemote(TriviaServerRunner serverWindow) throws RemoteException {
        this.serverWindow = serverWindow;
    }

    public synchronized void join(TriviaClient client) throws RemoteException{
        triviaClients.add(client);
        serverWindow.showMessage("New player joined the game");
    }

    public synchronized void pause(TriviaClient client) throws RemoteException{
        serverWindow.showMessage("Player " + client.getPlayerName() + " has paused the game");
        serverWindow.showMessage("Player " + client.getPlayerName() + " can pause the game " + client.getPlayerPauseLimit() + " more times.");
        serverWindow.showMessage("Game will resume in 10 seconds!");
    }

    public synchronized void leave(TriviaClient client) throws RemoteException{
        triviaClients.remove(client);
        serverWindow.showMessage("Player left the game");
    }

    public synchronized void answer(TriviaClient client, String answer) throws RemoteException{
        serverWindow.showMessage("Player got the right answer");
    }

}
