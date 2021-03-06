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
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Objects;

/** Controller class of single thread window */
class ThreadController {
    @FXML
    private Label nameLabel;

    @FXML
    private TextArea inputArea;

    @FXML
    private Button sendButton;

    @FXML
    private Button detailsButton;

    @FXML
    private VBox threadBox;

    @FXML
    private ScrollPane threadPane;

    private ThreadModel threadModel;
    private ThreadView threadView;

    /** Construct new message from textarea and sends it */
    @FXML
    private void sendMessage() {

        String body = inputArea.getText();
        if(body.length() < 1) {
            return;
        }

        Message msg = new Message(body, threadModel.getYourName());

        System.out.println(msg.toJSON());

        Platform.runLater(() -> threadModel.addMessage(msg));

        JSONObject obj = new JSONObject();
        obj.put("body", msg.toJSON());

        sendObject("message", obj);

        inputArea.setText("");

    }

    /**
     * Adds new message from json
     * @param json stringified JSON object containing message object
     */
    @FXML
    private void addMsg(String json) {
        Message msg = new Message(json);
        sendDelivered(msg.getId());
        Platform.runLater(() -> threadModel.addMessage(msg));
    }

    /**
     * Adds new internal message
     * @param body body of message which will be added
     */
    @FXML
    private void addInfoMsg(String body) {
        Message msg = new Message(body, "Internal");
        Platform.runLater(() -> threadModel.addMessage(msg));
    }

    /**
     * Handle pressing ENTER on textarea
     * @param e event
     */
    @FXML
    private void inputAreaKeyPressed(KeyEvent e) {
        if(e.getCode() == KeyCode.ENTER) {
            sendMessage();
            sendTyping(0);
            e.consume();
        }
        else{
            sendTyping(inputArea.getText().length());
        }
    }

    /** Call window which display connection details */
    @FXML
    private void displayDetails() {
        threadView.detailsAlert(threadModel.getYourAESHash(), threadModel.getPartnerAESHash());
    }

    /**
     * Creates new JSON object containing type and data field, then sends it via socket
     * @param type data type
     * @param data data to send, should be in ASCII
     */
    private void sendData(String type, String data) {
        try {
            JSONObject finalObj = new JSONObject();
            finalObj.put("type", type);
            finalObj.put("data", data);

            String finalData = new String (Base64.getEncoder().encode(finalObj.toString().getBytes("US-ASCII")));
            threadModel.getNetOut().println(finalData);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Encode and encrypt JSON object using your AES key to Base64, then call @see sendData.
     * @param type message type
     * @param obj object to send
     */
    private void sendObject(String type, JSONObject obj) {
        try {
            String data = new String (Base64.getEncoder().encode(obj.toString().getBytes("UTF-8")));
            data = threadModel.encrypt(data);
            sendData(type, data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Creates and sends object containing info about message delivery.
     * @param id id of message that was delivered
     */
    private void sendDelivered(String id) {
        JSONObject obj = new JSONObject();
        obj.put("id", id);

        sendObject("deliver", obj);
    }

    /**
     * Checks if there is any text in textArea and sends proper message to partner
     * @param textLength length of text currently entered in textArea
     */
    private void sendTyping(Integer textLength) {
        JSONObject obj = new JSONObject();
        if(textLength > 0) {
            obj.put("author", threadModel.getYourName());
            sendObject("typing", obj);
        }
        else {
            sendObject("stopped_typing", obj);
        }
    }

    /**
     * Creates and sends object containing info with your name
     */
    private void handshake() {
        JSONObject obj = new JSONObject();
        obj.put("body", threadModel.getYourName());

        sendObject("name", obj);
    }

    /**
     * Handle reveived data by parsing it and calling proper functions
     * @param input Base64 encoded string with data from partner
     */
    private void getData(String input) {

        input = new String(Base64.getDecoder().decode(input));

        JSONObject finalMsg = new JSONObject(input);
        String msgType = finalMsg.getString("type");

        //only these two are sent directly (data is not an json object)
        if(Objects.equals(msgType, "rsa") || Objects.equals(msgType, "aes")){
            if(Objects.equals(msgType, "rsa")) {
                System.out.println("Partner RSA key: " + finalMsg.getString("data"));
                threadModel.setPartnerRSAkey(finalMsg.getString("data"));

                sendAESKey(threadModel.getAESKey());
            }

            else if(Objects.equals(msgType, "aes")) {
                threadModel.setPartnerAESkey(finalMsg.getString("data"));
                System.out.println("Partner AES key: " + new String(Base64.getEncoder().encode(threadModel.getPartnerAESkey().getEncoded())));
                handshake();
            }
        }
        else {
            String decryptedString = threadModel.decrypt(finalMsg.getString("data"));
            String parsedString = new String(Base64.getDecoder().decode(decryptedString));
            JSONObject parsedMsg = new JSONObject(parsedString);


            if (Objects.equals(msgType, "name")) {
                System.out.println("Got partner name: " + parsedMsg.getString("body"));
                threadModel.setPartnerName(parsedMsg.getString("body"));
                Platform.runLater(() -> nameLabel.setText("Talking with " + parsedMsg.getString("body")));

                Platform.runLater(() -> threadModel.getThreadStage().setTitle(parsedMsg.getString("body")));
            } else if (Objects.equals(msgType, "message")) {
                final String msgJSON = parsedMsg.getString("body");

                Platform.runLater(() -> addMsg(msgJSON));
            } else if (Objects.equals(msgType, "deliver")) {
                threadModel.delivered(parsedMsg.getString("id"));
            } else if (Objects.equals(msgType, "typing")) {
                Platform.runLater(() -> threadModel.setTypingIndicator(parsedMsg.getString("author")));
            } else if (Objects.equals(msgType, "stopped_typing")) {
                Platform.runLater(() -> threadModel.unsetTypingIndicator());
            } else {
                System.out.println("Unknown message type");
            }
        }
    }

    /**
     * Encode your RSA public key and sends it via {@link #sendData} function
     * @param key your RSA public key
     */
    private void sendRSAKey(PublicKey key) {
        System.out.println("Your RSA public key: " + new String(Base64.getEncoder().encode(key.getEncoded())));
        sendData("rsa", new String(Base64.getEncoder().encode(key.getEncoded())));
    }

    /**
     * Encrypt and encode your AES secret key using your RSA private key and sends it via {@link #sendData} function
     * @param key your secret AES key
     */
    private void sendAESKey(SecretKey key) {
        byte[] plainKey = key.getEncoded();

        byte[] encryptedKey = RSA.encrypt(plainKey, threadModel.getPartnerRSAkey());

        sendData("aes", new String(Base64.getEncoder().encode(encryptedKey)));
    }

    /**
     * Construct new thread controller. Also starts listening for incoming messages
     * @param socket socket used for connections
     * @param yourName your nick/name
     * @param stage thread's window stage
     * @throws IOException when ThreadView fails to construct
     */
    ThreadController(Socket socket, String yourName, Stage stage) throws IOException {
        threadModel = new ThreadModel(yourName, stage, socket, new PrintWriter(socket.getOutputStream(),true));

        threadView = new ThreadView(stage, this);

        sendRSAKey(threadModel.getPublicKey());

        Runnable runnable = () -> {
            while (true) {
                try {
                    if(socket.isClosed())
                        break;

                    BufferedReader netIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    String input = null;

                    input = netIn.readLine();

                    if(input == null) {
                        Platform.runLater(() -> addInfoMsg("Partner disconnected."));
                        System.out.println("Client disconnected from server");
                        break;
                    }

                    getData(input);

                } catch (SocketException sx) {
                    System.out.println("Socket SERVER0 (in Client) closed, user has shutdown the connection, or network has failed");
                } catch (IOException ex) {
                    System.out.println(ex.getMessage() + ex);
                    ex.printStackTrace();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage() + ex);
                    ex.printStackTrace();
                }


            }
        };
        new Thread(runnable).start();
    }

    /**
     * Handle closing window, mainly by closing connection
     */
    void closingWindow() {
        System.out.println("closing controller");
        try {
            threadModel.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Take care of scrolling to the bottom after adding new message or resizing window
     * @param scrollPane scrollPane which we want to scroll
     */
    private static void slowScrollToBottom(ScrollPane scrollPane) {
        Animation animation = new Timeline(
                new KeyFrame(Duration.seconds(0.15),
                        new KeyValue(scrollPane.vvalueProperty(), 1)));
        animation.play();
    }

    /**
     * FXML constructor, called after initialization of all FXML elements. Bind events to proper elements.
     */
    @FXML
    void initialize() {

        threadModel.setThreadBoxPane(threadBox, threadPane);

        threadBox.heightProperty().addListener((observable, oldValue, newValue) -> {
            slowScrollToBottom(threadPane);
        });

        inputArea.setOnKeyPressed(this::inputAreaKeyPressed);
        inputArea.setOnKeyReleased(this::inputAreaKeyPressed);
        sendButton.setOnAction((e) -> sendMessage());
        if(threadModel.getPartnerName()!=null)
        {
            nameLabel.setText("Talking with " + threadModel.getPartnerName());
            threadModel.getThreadStage().setTitle(threadModel.getPartnerName());
        }

        detailsButton.setOnAction((e) -> displayDetails());
    }
}
