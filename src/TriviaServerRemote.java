import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
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
    private boolean mTriviaGameRunningStatut;

    //Podstawowy konstuktor dla serwera gry
    public TriviaServerRemote(TriviaServerRunner serverWindow) throws RemoteException {
        this.serverWindow = serverWindow;
    }

    //Co się dzieje jak gracz dołącza do gry
    public synchronized void join(TriviaClient client) throws RemoteException {
        triviaClients.add(client);

        if (mTriviaGame != null) {
            mTriviaGameRunningStatut = mTriviaGame.getRunningStatus();
        } else {
            mTriviaGameRunningStatut = false;
        }

        //Sprawdź czy gra jest w trakcie, wyświetl informacje na serwerze, wyślij obecne pytanie do gracza
        if (mTriviaGameRunningStatut) {
            serverWindow.showMessage("Player " + client.getPlayerName() + " has joined the game");
            client.message("You have joined the game!");
            client.message("Current question is: " + mTriviaGame.getCurrentQuestion());

            //Odśwież listę graczy u pozostałych graczy

        } else{
            serverWindow.showMessage("Player " + client.getPlayerName() + " has joined the server");
            client.message("You have join the server!");
        }
    }

    //Co się dzieje jak gracz uruchomił pauze
    public synchronized void pause(TriviaClient client) throws RemoteException {

        //check if game is running if not that cannot pause the game if yes than pause the game and add the pause limit
        serverWindow.showMessage("Player " + client.getPlayerName() + " has paused the game");
        serverWindow.showMessage("Player " + client.getPlayerName() + " can pause the game " + client.getPlayerPauseLimit() + " more times.");
        serverWindow.showMessage("Game will resume in 10 seconds!");
    }

    //Co się dzieje jak gracz opuści grę
    public synchronized void leave(TriviaClient client) throws RemoteException {
        triviaClients.remove(client);

        if (mTriviaGame != null) {
            mTriviaGameRunningStatut = mTriviaGame.getRunningStatus();
        } else {
            mTriviaGameRunningStatut = false;
        }

        triviaClients.remove(client);
        if (mTriviaGameRunningStatut) {
            serverWindow.showMessage("Player " + client.getPlayerName() + " has joined the game");

            //Odśwież listę graczy u pozostałych graczy
        } else {
            serverWindow.showMessage("Player " + client.getPlayerName() + " has left the sever");
        }
    }

    //Co się dzieje jak gracz wyślę odpowiedź
    public synchronized void answer(TriviaClient client, String answer) throws RemoteException {

        //Co się dzieje jak opowiedź jest prawidłowa
        if (mTriviaGame.checkAnswer(answer)) {
            serverWindow.showMessage("Player " + client.getPlayerName() + " got the right answer");
            client.setPlayerScore(client.getPlayerScore() + 1);
            serverWindow.showMessage("Player " + client.getPlayerName() + " has " + client.getPlayerScore() + " points");
            for (TriviaClient player : triviaClients) {
                player.message("Player " + client.getPlayerName() + " got the right answer");
                //Odśwież listę graczy u pozostałych graczy
            }
            mTriviaGame.sendRandomQuestion();

        } else {
            //Co się dzieje jak odpowiedź jest nieprawidłowa
            client.message("That is not the correct answer for this question!");
        }
    }

    //Zarejestru wątek gry
    public synchronized void registerGame(TriviaGame mTriviaGame) {
        this.mTriviaGame = mTriviaGame;
    }

    //Wyślij zawiadomienie do wszystkich graczy
    public synchronized void annouce(String announcement) throws RemoteException {
        for (TriviaClient client : triviaClients) {
            client.message(announcement);
        }
    }

    //Wyślij wiadomość jak gra została zakończona
    public synchronized void finishGame() throws RemoteException {
        TriviaClient highestScoreClient;
        for (TriviaClient client : triviaClients) {
            highestScoreClient = client;
            client.message("The game has finished");
        }
    }
}
