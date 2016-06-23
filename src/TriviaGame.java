import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.*;

/**
 * Project name: TriviaBotJavaRMI
 * Created by: jjpawlica on 22.06.2016.
 */

public class TriviaGame extends Thread {

    //Referencja do serwera i okna serwera
    private TriviaServerRunner serverWindow;
    private TriviaServerRemote serverRemote;

    //Stan gry
    private boolean isRunning = false;

    //Generator liczb pseudolosowych
    private Random rand = new Random(System.currentTimeMillis());


    //Ustawienia związane z czasem i zasadami gry
    private Timer timer = new Timer();
    private final int questionTimeout = 30;
    private final int warningTime = 5;
    private final int pauseLimit = 3;

    //Ustawienia związane z pytaniami i odpowiedziamy
    ArrayList<TriviaQuestion> questions = null;
    TriviaQuestion currentQuestion = null;
    String expectedAnswer = null;

    //Podstawowy konstruktor nowej gry
    public TriviaGame(TriviaServerRunner serverWindow, TriviaServerRemote serverRemote) {
        this.serverWindow = serverWindow;
        this.serverRemote = serverRemote;
    }

    //Metoda uruchamiająca grę
    public void run() {

        //Ustawa status gry na uruchomioną
        isRunning = true;

        //Załaduj pytania do pamięci z pliku
        questions = new ArrayList<TriviaQuestion>();
        try {
            Scanner sc = new Scanner(new File("questions.csv"));
            while (sc.hasNext()) {
                String[] line = sc.nextLine().split(";");
                questions.add(new TriviaQuestion(line[0], line[1]));
            }
            //Wyświetl wiadomość na serwerze, że udało się pytania załadować poprawnie
            serverWindow.showMessage("Questions loaded successfully. Loaded " + questions.size() + " questions.");
        } catch (FileNotFoundException ex) {

            //Wyświet wiadomość na serwerze, że nie można załadować pytań
            serverWindow.showMessage("Couldn't load questions!");
            serverWindow.showMessage("Exception: " +ex);

            //Zakończ wątek gry
            this.stop();
        }

        //Zarejestruj wątek gry  na serwerze i uruchom zadawanie pytań
        try {
            serverRemote.registerGame(this);
            serverRemote.annouce("The game starts now and the first questions is:");
            sendRandomQuestion();
        } catch (RemoteException ex) {

            //Wyświet wiadomośc na serwerze, że nie można wysłać pytania
            serverWindow.showMessage("Couldn't send question!");
            serverWindow.showMessage("Exception: " +ex);
        }
    }

    //Metoda kończąca grę
    public void finish() {
        try {
            isRunning = false;
            serverRemote.finishGame();
            timer.cancel();
            this.stop();
        } catch (RemoteException ex) {
            //Wyświet wiadomośc na serwerze, że nie można zakończyć gry
            serverWindow.showMessage("Couldn't finish the game!");
            serverWindow.showMessage("Exception: " +ex);
        }
    }

    //Pobierz ilość pytań załadowanych do pamięci
    public int getNumberOfQuestion() {
        return questions.size();
    }

    //Sprawdź czy gra jest w trakcie
    public boolean getRunningStatus() {
        return isRunning;
    }

    //Metoday wysyłająca loswe pytanie do graczy
    public void sendRandomQuestion() {
        currentQuestion = questions.get(rand.nextInt(questions.size()));
        expectedAnswer = currentQuestion.getAnswer();
        try {
            serverRemote.annouce(currentQuestion.getQuestion());
            serverWindow.showMessage("Question asked.");
        } catch (RemoteException e) {
            serverWindow.showMessage("Couldn't send question!");
        }
        long questionAskedAt = System.currentTimeMillis();
        timer.cancel();

        //Ustawienie zadania aby wysyłać pytanie co 30 sekund jeżeli nie zostanie udzielona poprawan odpowiedź
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendRandomQuestion();
            }
        }, questionTimeout*1000, questionTimeout*1000);

        //Ustawienie zadania aby wysyłać przypomnienie o czasie jakie pozostał do odpowiedzenia na pytanie
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    serverRemote.annouce("Time remaining " + (questionTimeout - ((System.currentTimeMillis() - questionAskedAt)) / 1000) + " seconds remaining");
                } catch (RemoteException e) {
                    serverWindow.showMessage("Couldn't send announcement!");;
                }
            }
        }, warningTime * 1000, warningTime * 1000);
    }

    //Metoda sprawdzająca czy udzielona odpowiedź jest prawidłowa
    public boolean checkAnswer(String answer) {
        if (answer.equalsIgnoreCase(expectedAnswer)) {
                expectedAnswer = null;
                return true;
        } else {
            return false;
        }
    }

    public String getCurrentQuestion(){
        return currentQuestion.getQuestion();
    }
}
