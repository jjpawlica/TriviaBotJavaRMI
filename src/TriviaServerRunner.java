import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/**
 * Project name: TriviaBotJavaRMI
 * Created by: jjpawlica on 21.06.2016.
 */

public class TriviaServerRunner extends JFrame {

    private static final String WINDOWS_NAME = "Trivia Bot Server";
    private static final String PORT_NUMBER_LABEL = "Port RMI: ";

    //GUI
    private JPanel mPanel;
    private JButton startButton, pasueButton, stopButton;
    private JLabel portNumberLabel;
    private JTextField portNumberTextField;
    private JTextArea messagesTextArea;

    //Serwer config
    private int portNumber = 1099;

    //Referencja do serwera
    private Server server;

    public TriviaServerRunner() {

        //Ustawienie nazwy okna
        super(WINDOWS_NAME);

        //Ustawienie GUI
        this.prepareGUI();
    }

    private void prepareGUI(){

        this.setSize(640, 480);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        this.setLayout(new BorderLayout());

        mPanel = new JPanel(new FlowLayout());
        portNumberLabel = new JLabel(PORT_NUMBER_LABEL);
        portNumberTextField = new JTextField((new Integer(portNumber)).toString(), 8);

        startButton = new JButton("Start Game");
        startButton.setEnabled(true);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server = new Server();

                //Start server thread

                //Ustaw pola i przyciski na nieaktywne
                portNumberTextField.setEnabled(false);
                startButton.setEnabled(false);
                pasueButton.setEnabled(true);
                stopButton.setEnabled(true);
                repaint();
            }
        });

        pasueButton = new JButton("Pause Game");
        pasueButton.setEnabled(false);
        pasueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Włącz pauze na serwerze
            }
        });

        stopButton = new JButton("Stop Game");
        stopButton.setEnabled(false);
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Stop server thread
                portNumberTextField.setEnabled(true);
                startButton.setEnabled(true);
                pasueButton.setEnabled(false);
                stopButton.setEnabled(false);
                repaint();
            }
        });

        messagesTextArea = new JTextArea();
        messagesTextArea.setLineWrap(true);
        messagesTextArea.setEditable(false);

        //Dodaj elementy do panelu menu
        mPanel.add(portNumberLabel);
        mPanel.add(portNumberTextField);
        mPanel.add(startButton);
        mPanel.add(pasueButton);
        mPanel.add(stopButton);

        //Dodaj elementy do okna
        this.add(mPanel, BorderLayout.NORTH);
        this.add(new JScrollPane(messagesTextArea), BorderLayout.CENTER);

        setVisible(true);
    }

    private class Server {

    }

    public static void main(String[] args) {
        new TriviaServerRunner();
    }
}


