import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Project name: TriviaBotJavaRMI
 * Created by: jjpawlica on 21.06.2016.
 */
public class TriviaClientRunner extends JFrame{

    private static final String WINDOWS_NAME = "Trivia Bot Client";
    private static final String SERVER_LABEL = "Server RMI: ";

    //GUI
    private JPanel mPanel;
    private JButton joinButton, leaveButton, pauseButton;
    private JLabel serverLabel;
    private JTextField hostName, answerText;
    private JTextArea messagesTextArea;
    private JList<String> players;
    private DefaultListModel<String> playersList;

    //Podstawowe ustawienie serwera
    private String serverName = "localhost";

    //Referencja do serwera
    private Client client;

    //Referencja do
    private TriviaServer mTriviaServer;
    private TriviaClient mTriviaClient;

    //Referencja do okna clienta
    private TriviaClientRunner clientWindow;

    public TriviaClientRunner(){

        //Ustawienie nazwy okna
        super(WINDOWS_NAME);

        //Ustawienie GUI
        this.prepareGUI();

        //Ustaw referencję do okna serwera
        this.clientWindow = this;
    }

    private void prepareGUI(){

        this.setSize(768, 640);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
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
                joinButton.setEnabled(false);
                leaveButton.setEnabled(true);
                pauseButton.setEnabled(true);
                hostName.setEnabled(false);
                client = new Client();
                client.start();
            }
        });

        leaveButton = new JButton("Leave Game");
        leaveButton.setEnabled(false);
        leaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
        });

        pauseButton = new JButton("Pause Game");
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    if (pauseButton.getText() == "Pause Game"){
                        pauseButton.setText("Resume Game");
                        mTriviaServer.pause(mTriviaClient);
                    } else {
                        pauseButton.setText("Pause Game");
                        // to do resume
                    }
                } catch (Exception ex){
                    showMessage("Couldn't pause the game!");
                    showMessage("Fallowing exception occurred: " + ex);
                }
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
                    try {
                        mTriviaServer.answer(mTriviaClient, answerText.getText());
                        answerText.setText("");
                    } catch (Exception ex) {
                        showMessage("Couldn't send you answer to the server.");
                        showMessage("Fallowing exception occurred: " + ex);
                    }
                }
            }
        });

        //Dodaj elementy do panelu menu
        mPanel.add(serverLabel);
        mPanel.add(hostName);
        mPanel.add(joinButton);
        mPanel.add(leaveButton);
        mPanel.add(pauseButton);

        //Dodaj elementy do okna
        this.add(mPanel, BorderLayout.NORTH);
        this.add(new JScrollPane(messagesTextArea), BorderLayout.CENTER);
        this.add(new JScrollPane(players), BorderLayout.EAST);
        this.add(answerText, BorderLayout.SOUTH);

        setVisible(true);

    }

    private class Client extends Thread{

        private Registry mRegistry;

        public void run() {
            try {
                mRegistry = LocateRegistry.getRegistry(hostName.getText());
                mTriviaServer = (TriviaServer) mRegistry.lookup("TriviaBot Server");
                showMessage("You join the game server");
                String playerName = JOptionPane.showInputDialog(null, "Provide new player name");
                mTriviaClient = new TriviaClientRemote(clientWindow, playerName);
                mTriviaServer.join(mTriviaClient);

            } catch (Exception e) {
                System.out.println("Error in connecting to the server: " + e);
            }
        }
    }

    public void showMessage(String message){
        messagesTextArea.append(message + "\n");
        messagesTextArea.setCaretPosition(messagesTextArea.getDocument().getLength());
    }

    public void refreshPlayerList(){}

    public static void main(String[] args) {
        new TriviaClientRunner();
    }

}
