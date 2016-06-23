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
            client.refreshList(triviaClients);

            for (TriviaClient player : triviaClients) {
                if(client != player){
                   player.message("Player " + client.getPlayerName() + " has joined the game");
                   player.refreshList(triviaClients);
                }
            }

        } else{
            serverWindow.showMessage("Player " + client.getPlayerName() + " has joined the server");
            client.message("You have join the server!");
            client.refreshList(triviaClients);

            for (TriviaClient player : triviaClients) {
                if(client != player){
                    player.message("Player " + client.getPlayerName() + " has joined the server");
                    player.refreshList(triviaClients);
                }
            }
        }

        //Odśwież listę graczy na serwerze
        serverWindow.showMessage("Current number of players:" + triviaClients.size());
        serverWindow.refreshPlayerList(triviaClients);
    }

    //Co się dzieje jak gracz opuści grę
    public synchronized void leave(TriviaClient client) throws RemoteException {

        client.message("You have left the server!");

        triviaClients.remove(client);

        if (mTriviaGame != null) {
            mTriviaGameRunningStatut = mTriviaGame.getRunningStatus();
        } else {
            mTriviaGameRunningStatut = false;
        }

        if (mTriviaGameRunningStatut) {
            serverWindow.showMessage("Player " + client.getPlayerName() + " has joined the game");

            for (TriviaClient player : triviaClients) {
                    player.message("Player " + client.getPlayerName() + " has joined the game");
                    player.refreshList(triviaClients);
            }
        } else {
            serverWindow.showMessage("Player " + client.getPlayerName() + " has left the sever");

            for (TriviaClient player : triviaClients) {
                player.message("Player " + client.getPlayerName() + " has left the server");
                player.refreshList(triviaClients);
            }
        }

        //Odśwież listę graczy na serwerze i u gracz dołączającego
        serverWindow.showMessage("Current number of players:" + triviaClients.size());
        serverWindow.refreshPlayerList(triviaClients);
    }

    //Co się dzieje jak gracz wyślę odpowiedź
    public synchronized void answer(TriviaClient client, String answer) throws RemoteException {

        //Co się dzieje jak opowiedź jest prawidłowa
        if (mTriviaGame.checkAnswer(answer)) {
            serverWindow.showMessage("Player " + client.getPlayerName() + " got the right answer!");
            client.setPlayerScore(client.getPlayerScore() + 1);

            //Odśwież listę graczy na serwerze
            serverWindow.refreshPlayerList(triviaClients);

            for (TriviaClient player : triviaClients) {
                //Odśwież listę graczy u pozostałych graczy
                player.message("Player " + client.getPlayerName() + " got the right answer");
                player.refreshList(triviaClients);
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

        //Co zrobić jak są jakczyś gracze w grze
        if(triviaClients.size() >0 ) {

            TriviaClient highestScoreClient = triviaClients.firstElement();
            int maxScore = highestScoreClient.getPlayerScore();

            //Wyświetl kto wygrał
            for (TriviaClient client : triviaClients) {
                if (client.getPlayerScore() >= maxScore) {
                    highestScoreClient = client;
                    maxScore = client.getPlayerScore();
                }
                client.message("The game has finished, player " + highestScoreClient.getPlayerName() + " won with score " + highestScoreClient.getPlayerScore()+"!");
            }
            serverWindow.showMessage("The game has finished, player " + highestScoreClient.getPlayerName() + " won with score " + highestScoreClient.getPlayerScore()+"!");

            //Zresetuj punkty wszystkich graczy
            for (TriviaClient client : triviaClients) {
                client.setPlayerScore(0);
            }

            //Odśwież listę graczy na serwerze i u pozostałych graczy
            serverWindow.refreshPlayerList(triviaClients);
            for (TriviaClient player : triviaClients) {
                //Odśwież listę graczy u pozostałych graczy
                player.refreshList(triviaClients);
            }

        } else {
            serverWindow.showMessage("The game has finished with no players!");
        }

    }
}
