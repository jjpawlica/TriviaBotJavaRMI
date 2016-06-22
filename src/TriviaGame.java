import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Project name: TriviaBotJavaRMI
 * Created by: jjpawlica on 22.06.2016.
 */

public class TriviaGame extends Thread {

    //Referencja do okna serwera
    private TriviaServerRunner serverWindow;

    private boolean isRunning = false;
    private boolean isPaused = false;

    private final int questionTimeout = 5000;
    private final int pauseLimit = 3;

    ArrayList<TriviaQuestion> questions = null;

    public TriviaGame(TriviaServerRunner serverWindow){
        this.serverWindow = serverWindow;
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
            serverWindow.showMessage("Questions loaded successfully.");
            serverWindow.showMessage("Loaded " + questions.size() +" questions.");
        } catch (FileNotFoundException ex) {
            serverWindow.showMessage("Couldn't load questions!");
        }
    }

    public void pause() {
        isPaused = true;
        serverWindow.showMessage("Game was paused");
    }

    public void finish() {
        isRunning = false;
        serverWindow.showMessage("The game has finished!");
        this.stop();
    }

    public int getNumberOfQuestion(){
        return questions.size();
    }

    public boolean getRunningStatus(){
        return isRunning;
    }

    public  boolean getPausedStatus(){
        return isPaused;
    }

    public String sendRandomQuestion(){
        return "HELLO FROM TRIVIA GAME";
    }

    public boolean checkAnswer(String s){
        return true; // if correct sendRQ to all, else only info to sender
    }
}
