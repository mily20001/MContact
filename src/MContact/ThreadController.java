package MContact;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ThreadController {
    @FXML
    private Label nameLabel;

    @FXML
    private TextArea inputArea;

    @FXML
    private Button sendButton;

    @FXML
    private Button connectButton;

    @FXML
    private VBox threadBox;

    @FXML
    private ScrollPane threadPane;

    private PrintWriter netOut;


    private String role;
    private Socket socket;

    @FXML
    public void sendMessage() {

        String body = inputArea.getText();
        if(body.length() < 1) {
            return;
        }

        Message msg = new Message(body, "You");

        System.out.println(msg.toJSON());

        threadBox.getChildren().add(ThreadView.addMsg(msg, true, threadBox.getWidth(), threadPane));

//        threadBox.getChildren().add(ThreadView.addMsg(body, "17.09.2017", true, threadBox.getWidth(), threadPane));

        netOut.println(msg.toJSON());

        inputArea.setText("");

    }

    @FXML
    public void addMsg(String json) {
        Message msg = new Message(json);
        String body = msg.body;
        System.out.println("Adding message: "+body);
//        Platform.runLater(() -> threadBox.getChildren().add(ThreadView.addMsg(body, "16.09.2017", false, threadBox.getWidth(), threadPane)));
        Platform.runLater(() -> threadBox.getChildren().add(ThreadView.addMsg(msg, false, threadBox.getWidth(), threadPane)));
    }

    @FXML
    public void addInfoMsg(String body) {
        Message msg = new Message(body, "Internal");
//        Platform.runLater(() -> threadBox.getChildren().add(ThreadView.addMsg(body, "16.09.2017", false, threadBox.getWidth(), threadPane)));
        Platform.runLater(() -> threadBox.getChildren().add(ThreadView.addMsg(msg, false, threadBox.getWidth(), threadPane)));
    }

    @FXML
    public void inputAreaKeyPressed(KeyEvent e) {
        if(e.getCode() == KeyCode.ENTER) {
            sendMessage();
            e.consume();
        }
    }

    public ThreadController(PrintWriter out) throws IOException {
        netOut = out;
        role = "server";
        Platform.runLater(() -> addInfoMsg("Server"));
        System.out.println("hejka z serwera");
    }

    public ThreadController(String addr, Integer port) throws IOException {
        role = "client";
        Connect0(addr, port);
        Platform.runLater(() -> addInfoMsg("Client"));
        System.out.println("hejka jako klient");
    }

    public ThreadController(Socket _socket) throws IOException {
        role = "server";
//        Connect0(addr, port);
        Platform.runLater(() -> addInfoMsg("Server"));
        System.out.println("hejka jako serwer");

        socket=_socket;

        Runnable runnable = new Runnable() {
            public synchronized void run() {
                while (true) {
                    try {
                        if(socket.isClosed())
                            break;

                        netOut = new PrintWriter(socket.getOutputStream(),true);
                        BufferedReader netIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        String input = null;

                        input = netIn.readLine();

                        if(input == null) {
                            Platform.runLater(() -> addInfoMsg("Partner disconnected."));
                            System.out.println("Client disconnected from server");
                            break;
                        }

                        final String msgbody = input;
                        System.out.println("Server 0 got: " + input + " from " + socket.getInetAddress());


                        Platform.runLater(() -> addMsg(msgbody));

                    } catch (SocketException sx) {
                        System.out.println("Socket SERVER0 (in Client) closed, user has shutdown the connection, or network has failed");
//                        sx.printStackTrace();
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage() + ex);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage() + ex);
                    }


                }
            }
        };
        new Thread(runnable).start();
    }

    public void closingWindow() {
        System.out.println("closing controller");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void Connect0(String addr, Integer port) throws IOException {
        Runnable runnable = () -> {

            try {
                socket = new Socket(addr, port);

                // loop forever, or until the server closes the connection
                while (true) {

                    netOut = new PrintWriter(socket.getOutputStream(),true);
                    BufferedReader netIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    String input = netIn.readLine();
                    final String msgbody = input;

                    if(input == null) {
                        Platform.runLater(() -> addInfoMsg("Partner disconnected."));
                        System.out.println("Server disconnected from client");
                        break;
                    }

                    Platform.runLater(() -> addMsg(msgbody));
                    System.out.println("Client 0 got: " + input);
                }
            } catch (SocketException sx) {
                System.out.println("Socket CLIENT0 closed, user has shutdown the connection, or network has failed");
//                sx.printStackTrace();
            } catch (IOException ex) {
                System.out.println(ex.getMessage() + ex);
            } catch (Exception ex) {
                System.out.println(ex.getMessage() + ex);
            } finally {

            }
        };

        new Thread(runnable).start();


        System.out.println("Client 0 connected");
    }

    private static void slowScrollToBottom(ScrollPane scrollPane) {
        Animation animation = new Timeline(
                new KeyFrame(Duration.seconds(0.15),
                        new KeyValue(scrollPane.vvalueProperty(), 1)));
        animation.play();
    }

    @FXML
    void initialize() {
        nameLabel.setText("John Smith");

        threadBox.heightProperty().addListener((observable, oldValue, newValue) -> {
            slowScrollToBottom(threadPane);
        });

        inputArea.setOnKeyPressed(this::inputAreaKeyPressed);
        inputArea.setOnKeyReleased(this::inputAreaKeyPressed);
        sendButton.setOnAction((e) -> sendMessage());
    }
}
