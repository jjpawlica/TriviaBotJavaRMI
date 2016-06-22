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

    //Przechowuje referencję do obecnej gry
    private TriviaGame mTriviaGame;


    public TriviaServerRemote(TriviaServerRunner serverWindow, TriviaGame mTriviaGame) throws RemoteException {
        this.serverWindow = serverWindow;
        this.mTriviaGame = mTriviaGame;
    }

    public synchronized void join(TriviaClient client) throws RemoteException {
        triviaClients.add(client);

        //check if game is running if not than join server if yes than join the game
        serverWindow.showMessage("Player " + client.getPlayerName() + " has joined the game");
    }

    public synchronized void pause(TriviaClient client) throws RemoteException {

        //check if game is running if not that cannot pause the game if yes than pause the game and add the pause limit
        serverWindow.showMessage("Player " + client.getPlayerName() + " has paused the game");
        serverWindow.showMessage("Player " + client.getPlayerName() + " can pause the game " + client.getPlayerPauseLimit() + " more times.");
        serverWindow.showMessage("Game will resume in 10 seconds!");
    }

    public synchronized void leave(TriviaClient client) throws RemoteException {

        //check if game is running if not than leave server if yes than leave the game
        triviaClients.remove(client);
        serverWindow.showMessage("Player " + client.getPlayerName() + " has left the game");
    }

    public synchronized void answer(TriviaClient client, String answer) throws RemoteException {

        //check if game than check if answer is right
        //if its right then give next question and add point if not than send message
        serverWindow.showMessage("Player " + client.getPlayerName() + " got the right answer");
        serverWindow.showMessage("Player " + client.getPlayerName() + " has " + client.getPlayerScore() + " points");
    }

}
