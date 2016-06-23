import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Vector;

/**
 * Project name: TriviaBotJavaRMI
 * Created by: jjpawlica on 21.06.2016.
 */
public class TriviaClientRunner extends JFrame{

    //Teksty do wyświetlenie w GUI
    private static final String WINDOWS_NAME = "Trivia Bot Client";
    private static final String SERVER_LABEL = "Server RMI: ";

    //GUI
    private JPanel mPanel;
    private JButton joinButton, leaveButton;
    private JLabel serverLabel;
    private JTextField hostName, answerText;
    private JTextArea messagesTextArea;
    private JList<String> players;
    private DefaultListModel<String> playersList;

    //Podstawowe ustawienie serwera
    private String serverName = "localhost";

    //Referencja do klienta
    private Client client;

    //Referencja do
    private TriviaServer mTriviaServer;
    private TriviaClient mTriviaClient;

    //Referencja do okna clienta
    private TriviaClientRunner clientWindow;

    //Podstawowy konstruktor dla okna klienta
    public TriviaClientRunner(){

        //Ustawienie nazwy okna
        super(WINDOWS_NAME);

        //Ustawienie GUI
        this.prepareGUI();

        //Ustaw referencję do okna serwera
        this.clientWindow = this;
    }

    //Metoda ustawiająca okno serwera i odpowiadająca za jego zmiany
    private void prepareGUI(){

        this.setSize(768, 640);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                leaveButton.doClick();
                setVisible(false);
                System.exit(0);
            }
        });

        this.setLayout(new BorderLayout());

        mPanel = new JPanel(new FlowLayout());
        serverLabel = new JLabel(SERVER_LABEL);
        hostName = new JTextField(serverName, 16);

        joinButton = new JButton("Join Game");
        joinButton.setEnabled(true);
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                joinGame();
            }
        });

        leaveButton = new JButton("Leave Game");
        leaveButton.setEnabled(false);
        leaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leaveGame();
            }
        });

        messagesTextArea = new JTextArea();
        messagesTextArea.setLineWrap(true);
        messagesTextArea.setEditable(false);

        playersList = new DefaultListModel<String>();
        players = new JList<String>(playersList);
        players.setFixedCellWidth(120);

        answerText = new JTextField();
        answerText.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    sendAnswer();
                }
            }
        });

        //Dodaj elementy do panelu menu
        mPanel.add(serverLabel);
        mPanel.add(hostName);
        mPanel.add(joinButton);
        mPanel.add(leaveButton);

        //Dodaj elementy do okna
        this.add(mPanel, BorderLayout.NORTH);
        this.add(new JScrollPane(messagesTextArea), BorderLayout.CENTER);
        this.add(new JScrollPane(players), BorderLayout.EAST);
        this.add(answerText, BorderLayout.SOUTH);

        setVisible(true);

    }

    //Metoda dołączająca do gry
    private void joinGame(){
        joinButton.setEnabled(false);
        leaveButton.setEnabled(true);
        hostName.setEnabled(false);
        client = new Client();
        client.start();
    }

    //Metoda wychodząca  gry
    private void leaveGame(){
        playersList.clear();
        try {
            mTriviaServer.leave(mTriviaClient);
        } catch (Exception ex) {
            showMessage("Couldn't leave the game!");
            showMessage("Fallowing exception occurred: " + ex);
        }
        leaveButton.setEnabled(false);
        joinButton.setEnabled(true);
        hostName.setEnabled(true);
    }

    //Metoda wysyłająca odpowiedź
    private  void sendAnswer(){
        try {
            mTriviaServer.answer(mTriviaClient, answerText.getText());
            answerText.setText("");
        } catch (Exception ex) {
            showMessage("Couldn't send you answer to the server.");
            showMessage("Fallowing exception occurred: " + ex);
            answerText.setText("");
        }
    }

    //Klasa odpowiedzialna za tworzenie i usuwanie klienta RMI
    private class Client extends Thread{

        private Registry mRegistry;

        public void run() {
            try {
                mRegistry = LocateRegistry.getRegistry(hostName.getText());
                mTriviaServer = (TriviaServer) mRegistry.lookup("TriviaBot Server");
                String playerName = JOptionPane.showInputDialog(null, "Provide new player name:");
                mTriviaClient = new TriviaClientRemote(clientWindow, playerName);
                mTriviaServer.join(mTriviaClient);
            } catch (Exception ex){
                showMessage("Error in connecting to the server: " + ex);
            }
        }
    }

    //Metoda odpowiedzialna za wyświetlanie wiadomości w oknie klienta
    public void showMessage(String message){
        messagesTextArea.append(message + "\n");
        messagesTextArea.setCaretPosition(messagesTextArea.getDocument().getLength());
    }

    //Metoda odpowiedziala za odświeżanie listy graczy na kliencie
    public void refreshPlayerList(Vector<TriviaClient> newPlayersList) {
        playersList.clear();

        for (TriviaClient client : newPlayersList) {
            try {
                playersList.addElement(client.getPlayerName() + ": " + client.getPlayerScore());
            } catch (Exception ex) {
                showMessage("Couldn't refresh players list!");
                showMessage("Exception: " + ex);
            }
        }
    }

    //Główna metoda programu klienta
    public static void main(String[] args) {
        new TriviaClientRunner();
    }

}
