import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

/**
 * Project name: TriviaBotJavaRMI
 * Created by: jjpawlica on 21.06.2016.
 */

public class TriviaClientRemote extends UnicastRemoteObject implements TriviaClient {

    //Przechowuje nazwę gracza
    private String playerName;

    //Przechowuje liczbę punktów gracza
    private int playerScore = 0;

    //Przechowuje referencję do okna serwera
    private TriviaClientRunner clientWindow;

    //Podstawowy konstuktor klienta
    public TriviaClientRemote(TriviaClientRunner clientWindow, String playerName) throws RemoteException {
        this.clientWindow = clientWindow;
        this.playerName = playerName;
    }


    //Metoda do wysyłania wiadomości graczowi
    @Override
    public void message(String message) throws RemoteException {
        clientWindow.showMessage(message);
    }

    //Metoda pobiera nazwę gracza
    @Override
    public String getPlayerName() throws RemoteException {
        return this.playerName;
    }

    //Metoda pobier obecny wyniki gracza
    @Override
    public int getPlayerScore() throws RemoteException {
        return this.playerScore;
    }

    //Metoda ustwania nowy wyniki graczowi
    @Override
    public void setPlayerScore(int newScore) throws RemoteException {
        this.playerScore = newScore;
    }

    //Metoda oświeża listę graczy u klienta
    @Override
    public void refreshPlayerList(Vector<TriviaClient> playerList) throws RemoteException {
        clientWindow.refreshPlayerList(playerList);
    }
}

