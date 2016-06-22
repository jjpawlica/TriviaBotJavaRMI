/**
 * Project name: TriviaBotJavaRMI
 * Created by: jjpawlica on 22.06.2016.
 */

public class TriviaQuestion {

    private String question;
    private String answer;

    public TriviaQuestion() {
    }

    public TriviaQuestion(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
