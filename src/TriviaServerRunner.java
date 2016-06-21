import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


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

    //Referencja do okna servera
    private TriviaServerRunner serverWindow;

    public TriviaServerRunner() {

        //Ustawienie nazwy okna
        super(WINDOWS_NAME);

        //Ustawienie GUI
        this.prepareGUI();

        //Ustaw referencję do okna serwera
        this.serverWindow = this;
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
                server.run();

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
                server.pause();
            }
        });

        stopButton = new JButton("Stop Game");
        stopButton.setEnabled(false);
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Stop server thread
                server.kill();

                //Ustaw pola i przyciski na nieaktywne
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

    private class Server extends Thread{

        private Registry mRegistry;

        public void run() {

            try {
                mRegistry = LocateRegistry.createRegistry(new Integer(portNumberTextField.getText()));
                showMessage("Created new registry at port number: " + portNumberTextField.getText());
            } catch (Exception e) {
                showMessage("Couldn't create new registry, trying existing one...");
            }

            if (mRegistry == null) {
                try {
                    mRegistry = LocateRegistry.getRegistry();
                } catch (Exception e) {
                    showMessage("No registry running!");
                }
            }

            try {
                TriviaServerRemote serverRemote = new TriviaServerRemote(serverWindow);
                mRegistry.rebind("TriviaBot Server", serverRemote);
                showMessage("Server was successfully registered and is running at port number: " + portNumberTextField.getText());
            } catch (Exception e) {
                showMessage("Couldn't create new server!");
            }
        }

        public void pause(){
            showMessage("You paused the game");
        }

        public void kill(){
            try {
                mRegistry.unbind("TriviaBot Server");
                showMessage("Server was unregistered successfully!");
            } catch (Exception e) {
                showMessage("Coudlnt unregister server!");
            }

        }
    }

    public void showMessage(String message){
        messagesTextArea.append(message + "\n");
        messagesTextArea.setCaretPosition(messagesTextArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        new TriviaServerRunner();
    }
}


