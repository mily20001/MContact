package MContact;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Controller {
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

    private String role;
    private PrintWriter netOut;
    private BufferedReader netIn;

    @FXML
    public void sendMessage() {

        String body = inputArea.getText();
        if(body.length() < 1) {
//            inputArea.setText("");
            return;
        }
//        if(role == "server")
//        {
//            body = "msg from sever";
//        }
//        else
//        {
//            body = "msg from client";
//        }

        threadBox.getChildren().add(View.addMsg(body, "16.09.2017", true, threadBox.getWidth(), threadPane));

        netOut.println(body);

        inputArea.setText("");

    }

    @FXML
    private void addMsg(String body) {
        threadBox.getChildren().add(View.addMsg(body, "16.09.2017", false, threadBox.getWidth(), threadPane));
    }

    @FXML
    public void inputAreaKeyPressed(KeyEvent e) {
        if(e.getCode() == KeyCode.ENTER) {
            sendMessage();
            e.consume();
        }
    }

    public Controller() throws IOException {
        System.out.println("hejka");
    }

    @FXML
    public void Connect0() throws IOException {

        role = "client";
        Runnable runnable = new Runnable() {

            public void run () {

                try {
                    Socket socket = new Socket("127.0.0.1", 8420);

                    // loop forever, or until the server closes the connection
                    while (true) {

                        netOut = new PrintWriter(socket.getOutputStream(),true);
                        netIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        String input = netIn.readLine();
                        final String msgbody = input;

                        Platform.runLater(new Runnable() {
                            @Override public void run() {
                                addMsg(msgbody);
                            }
                        });
                        System.out.println("Client 0 got: " + input);
                    }
                } catch (SocketException sx) {
                    System.out.println("Socket CLIENT0 closed, user has shutdown the connection, or network has failed");
                } catch (IOException ex) {
                    System.out.println(ex.getMessage() + ex);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage() + ex);
                } finally {

                }
            }
        };

        new Thread(runnable).start();


        System.out.println("Client 0 connected");
    }

    @FXML
    public void Server0() throws IOException {
        role = "server";
        Runnable serverLoop = new Runnable() {
            public synchronized void run() {
                System.out.println("Starting server 0");
                try {
                    ServerSocket serverSocket = new ServerSocket(8420);

                    while (true) {
                        // block until we get a connection from a client
                        final Socket clientSocket = serverSocket.accept();
                        System.out.println("Client connected to server 0 from " + clientSocket.getInetAddress());

                        Runnable runnable = new Runnable() {
                            public synchronized void run() {
                                while (true) {
                                    try {
                                        netOut = new PrintWriter(clientSocket.getOutputStream(),true);
                                        netIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

//                                        System.out.println("Server 0 is sending data");

//                                        out.println("server0server0server0server0server0server0server0");

//                                        System.out.println("Server 0 sent data");

                                        String input = null;

                                        input = netIn.readLine();

                                        final String msgbody = input;

                                        Platform.runLater(new Runnable() {
                                            @Override public void run() {
                                                addMsg(msgbody);
                                            }
                                        });

                                        System.out.println("Server 0 got: " + input + " from " + clientSocket.getInetAddress());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }
                        };
                        new Thread(runnable).start();
                    }

                } catch (SocketException sx) {
                    System.out.println("Socket SERVER0 closed, user has shutdown the connection, or network has failed");
                } catch (IOException ex) {
                    System.out.println(ex.getMessage() + ex);
                } catch (Exception ex){
                    System.out.println(ex.getMessage() + ex);
                }

            }
        };
        new Thread(serverLoop).start();

        System.out.println("Server 0 started");

    }

    static void slowScrollToBottom(ScrollPane scrollPane) {
        Animation animation = new Timeline(
                new KeyFrame(Duration.seconds(0.15),
                        new KeyValue(scrollPane.vvalueProperty(), 1)));
        animation.play();
    }

    @FXML
    void initialize() {
        nameLabel.setText("John Smith");

        threadBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldvalue, Number newValue) {
                System.out.println("height changed: " + newValue);
                //threadPane.setVvalue(1.0);
                slowScrollToBottom(threadPane);
            }
        });

//        threadPane.widthProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldvalue, Number newValue) {
//                System.out.println("width changed of scrollpane: " + newValue);
////                threadPane.setVvalue(1.0);
//            }
//        });
    }

    @FXML
    public void onSendButton() {
//        System.out.println("sendButton clicked");
        HBox tmpmsg = View.addMsg("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "14.09.2016", true, threadBox.getWidth(), threadPane);
//        Text tmptext = (Text)(((VBox)tmpmsg.getChildren().get(0)).getChildren().get(0));


        threadBox.getChildren().add(tmpmsg);
        threadBox.getChildren().add(View.addMsg("bbbbb b bbbbbb bbbbbb bbbbb bbbb bb bb bb bbbb bbbbbb bbb bbb", "16.09.2017", false, threadBox.getWidth(), threadPane));
        //System.out.println(threadPane.getVvalue());
        //threadPane.setVvalue(2.0);
    }

}
