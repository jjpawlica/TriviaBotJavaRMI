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
    private JButton startServerButton, stopServerButton, startGameButton, pauseGameButton, finishGameButton;
    private JLabel portNumberLabel;
    private JTextField portNumberTextField;
    private JTextArea messagesTextArea;

    //Podstawowe ustawienie serwera
    private int portNumber = 1099;

    //Referencja do serwera
    private Server mServer;
    TriviaServerRemote serverRemote;

    //Referencja do okna servera
    private TriviaServerRunner mServerWindow;

    //Referencja do logiki gry
    private TriviaGame mTriviaGame;

    public TriviaServerRunner() {

        //Ustawienie nazwy okna
        super(WINDOWS_NAME);

        //Ustawienie GUI
        this.prepareGUI();

        //Ustaw referencję do okna serwera
        this.mServerWindow = this;
    }

    private void prepareGUI() {

        this.setSize(768, 640);
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

        startServerButton = new JButton("Start Server");
        startServerButton.setEnabled(true);
        startServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Utwórz i uruchom serwer
                mServer = new Server();
                mServer.run();

                //Uruchom grę
                mTriviaGame = new TriviaGame(mServerWindow, serverRemote);

                //Ustaw pola i przyciski na nieaktywne
                portNumberTextField.setEnabled(false);
                startServerButton.setEnabled(false);
                stopServerButton.setEnabled(true);
                startGameButton.setEnabled(true);
                repaint();
            }
        });

        stopServerButton = new JButton("Stop Server");
        stopServerButton.setEnabled(false);
        stopServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Automatycznie zatrzymaj grę
                if (mTriviaGame.getRunningStatus()) {
                    mTriviaGame.finish();
                }

                //Wyłącz serwer
                mServer.kill();

                //Ustaw pola i przyciski na nieaktywne
                portNumberTextField.setEnabled(true);
                startServerButton.setEnabled(true);
                stopServerButton.setEnabled(false);
                startGameButton.setEnabled(false);
                pauseGameButton.setEnabled(false);
                finishGameButton.setEnabled(false);
                repaint();
            }
        });

        startGameButton = new JButton("Start Game");
        startGameButton.setEnabled(false);
        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Uruchom nową grę
                mTriviaGame.run();

                //Ustaw przyciski
                if (mTriviaGame.getNumberOfQuestion() > 0) {
                    startGameButton.setEnabled(false);
                    pauseGameButton.setEnabled(true);
                    finishGameButton.setEnabled(true);
                    repaint();
                } else {
                    mTriviaGame.stop();
                }
            }
        });

        pauseGameButton = new JButton("Pause Game");
        pauseGameButton.setEnabled(false);
        pauseGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!mTriviaGame.getPausedStatus()){
                mTriviaGame.pause();
                }
                repaint();
            }
        });

        finishGameButton = new JButton("Stop Game");
        finishGameButton.setEnabled(false);
        finishGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mTriviaGame.getRunningStatus()) {
                    mTriviaGame.finish();
                }

                startGameButton.setEnabled(true);
                pauseGameButton.setText("Pause Game");
                pauseGameButton.setEnabled(false);
                finishGameButton.setEnabled(false);
            }
        });

        messagesTextArea = new JTextArea();
        messagesTextArea.setLineWrap(true);
        messagesTextArea.setEditable(false);

        //Dodaj elementy do panelu menu
        mPanel.add(portNumberLabel);
        mPanel.add(portNumberTextField);
        mPanel.add(startServerButton);
        mPanel.add(stopServerButton);
        mPanel.add(startGameButton);
        mPanel.add(pauseGameButton);
        mPanel.add(finishGameButton);

        //Dodaj elementy do okna
        this.add(mPanel, BorderLayout.NORTH);
        this.add(new JScrollPane(messagesTextArea), BorderLayout.CENTER);

        setVisible(true);
    }

    private class Server extends Thread {

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
                serverRemote = new TriviaServerRemote(mServerWindow, mTriviaGame);
                mRegistry.rebind("TriviaBot Server", serverRemote);
                showMessage("Server was successfully registered and is running at port number: " + portNumberTextField.getText());
            } catch (Exception e) {
                showMessage("Couldn't create new server!");
            }
        }

        public void kill() {
            try {
                mRegistry.unbind("TriviaBot Server");
                showMessage("Server was unregistered successfully!");
            } catch (Exception e) {
                showMessage("Couldn't unregister server!");
            }
        }
    }

    public void showMessage(String message) {
        messagesTextArea.append(message + "\n");
        messagesTextArea.setCaretPosition(messagesTextArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        new TriviaServerRunner();
    }
}


