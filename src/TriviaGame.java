import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.*;

/**
 * Project name: TriviaBotJavaRMI
 * Created by: jjpawlica on 22.06.2016.
 */

public class TriviaGame extends Thread {

    //Referencja do okna serwera
    private TriviaServerRunner serverWindow;
    private TriviaServerRemote serverRemote;

    private boolean isRunning = false;
    private boolean isPaused = false;

    private Random rand = new Random(System.currentTimeMillis());

    private Timer timer = new Timer();
    private final int questionTimeout = 30;
    private final int pauseLimit = 3;

    ArrayList<TriviaQuestion> questions = null;
    String expectedAnswer = null;

    public TriviaGame(TriviaServerRunner serverWindow, TriviaServerRemote serverRemote) {
        this.serverWindow = serverWindow;
        this.serverRemote = serverRemote;
    }

    public void run() {

        isRunning = true;
        questions = new ArrayList<TriviaQuestion>();

        //Załadowanie pytań do pamięci z pliku
        try {
            Scanner sc = new Scanner(new File("questions.csv"));
            while (sc.hasNext()) {
                String[] line = sc.nextLine().split(";");
                questions.add(new TriviaQuestion(line[0], line[1]));
            }
            serverWindow.showMessage("Questions loaded successfully. Loaded " + questions.size() + " questions.");
        } catch (FileNotFoundException ex) {
            serverWindow.showMessage("Couldn't load questions!");
        }

        try {
            serverRemote.annouce("The game starts now and the first questions is:");
            sendRandomQuestion();
        } catch (RemoteException ex) {
            serverWindow.showMessage("Couldn't send question!");
        }
    }

    public void pause() {
        isPaused = true;
        serverWindow.showMessage("Game was paused");
    }

    public void finish() {
        isRunning = false;
        serverWindow.showMessage("The game has finished!");
        timer.cancel();
        this.stop();
    }

    public int getNumberOfQuestion() {
        return questions.size();
    }

    public boolean getRunningStatus() {
        return isRunning;
    }

    public boolean getPausedStatus() {
        return isPaused;
    }

    public void sendRandomQuestion() {
        TriviaQuestion question = questions.get(rand.nextInt(questions.size()));
        expectedAnswer = question.getAnswer();
        try {
            serverRemote.annouce(question.getQuestion());
        } catch (RemoteException e) {
            serverWindow.showMessage("Couldn't send question!");
        }
        long questionAskedAt = System.currentTimeMillis();
        timer.cancel();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendRandomQuestion();
            }
        }, questionTimeout*1000, questionTimeout*1000);
    }

    public boolean checkAnswer(String answer) {
        if (answer.equalsIgnoreCase(expectedAnswer)) {
                expectedAnswer = null;
                return true;
        } else {
            return false;
        }
    }
}
