import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

/**
 * Project name: TriviaBotJavaRMI
 * Created by: jjpawlica on 21.06.2016.
 */

public class TriviaClientRemote extends UnicastRemoteObject implements TriviaClient{

    private final static String JOIN_MESSAGE = "New player joined the game: ";
    private final static String LEAVE_MESSAGE = "Player has left the game: ";

    //Przechowuje nazwę gracza
    private String playerName;

    //Przechowuje liczbę punktów gracza
    private int playerScore = 0;

    //Przechowuje referencję do okna serwera
    private TriviaClientRunner clientWindow;

    public TriviaClientRemote(TriviaClientRunner clientWindow, String playerName) throws RemoteException{
        this.clientWindow = clientWindow;
        this.playerName = playerName;
    }

    @Override
    public void joinMessage(String playerName, Vector<TriviaClient> playersList) throws RemoteException {
        this.clientWindow.showMessage(JOIN_MESSAGE + this.playerName);
        this.clientWindow.refreshPlayerList();
    }

    @Override
    public void leaveMessage(String playerName, Vector<TriviaClient> playerList) throws RemoteException {
        this.clientWindow.showMessage(LEAVE_MESSAGE + this.playerName);
        this.clientWindow.refreshPlayerList();
    }

    @Override
    public void answer(String answer) throws RemoteException {

    }

    @Override
    public String getPlayerName() throws RemoteException {
       return this.playerName;
    }

    @Override
    public int getPlayerScore() throws RemoteException {
        return this.playerScore;
    }

    @Override
    public void setPlayerName(String newName) throws RemoteException {
      this.playerName = newName;
    }

    @Override
    public void setPlayerScore(int newScore) throws RemoteException {
        this.playerScore = newScore;
    }
}
