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
import java.net.*;

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

        netOut.println(msg.toJSON());

        inputArea.setText("");

    }

    @FXML
    public void addMsg(String json) {
        Message msg = new Message(json);
        String body = msg.body;
        System.out.println("Adding message: "+body);
        Platform.runLater(() -> threadBox.getChildren().add(ThreadView.addMsg(msg, false, threadBox.getWidth(), threadPane)));
    }

    @FXML
    public void addInfoMsg(String body) {
        Message msg = new Message(body, "Internal");
        Platform.runLater(() -> threadBox.getChildren().add(ThreadView.addMsg(msg, false, threadBox.getWidth(), threadPane)));
    }

    @FXML
    public void inputAreaKeyPressed(KeyEvent e) {
        if(e.getCode() == KeyCode.ENTER) {
            sendMessage();
            e.consume();
        }
    }

    public ThreadController(Socket _socket) throws IOException {

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
