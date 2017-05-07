package MContact;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Optional;

public class MainController {
    private MainModel mainModel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label portLabel;

    public MainController() throws IOException {
        System.out.println("hejka z glownego controllera");
        mainModel = new MainModel();
        Server0();
    }

    @FXML
    void initialize() {
        nameLabel.setText("Hello " + mainModel.name + "!");
        portLabel.setText("You are listening on port " + mainModel.serverPort);
    }

    @FXML
    public void ConnectButtonClicked() throws IOException {

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Connect to user");
        dialog.setHeaderText("Enter user ip address and port");
        ButtonType loginButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField ip = new TextField();
        ip.setPromptText("IP");
        TextField port = new TextField();
        port.setPromptText("Port");

        grid.add(new Label("IP:"), 0, 0);
        grid.add(ip, 1, 0);
        grid.add(new Label("Port:"), 0, 1);
        grid.add(port, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(ip.getText(), port.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(ipPort -> {
            Stage stage = new Stage();
            try {
                ThreadController threadController = new ThreadController(ipPort.getKey(), Integer.parseInt(ipPort.getValue()));
                /*TODO tu trzeba przekazywac imie drugiego usera, a nie swoje*/
                new ThreadView(stage, mainModel.name, threadController);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Connecting to " + ipPort.getKey() + ":" + ipPort.getValue());
        });
    }

    private void openNewThread(Socket socket) throws IOException{
        Stage stage = new Stage();
        ThreadController threadController = new ThreadController(socket);
        /*TODO tu trzeba przekazywac imie drugiego usera, a nie swoje*/
        new ThreadView(stage, mainModel.name, threadController);
    }

    public void Server0() throws IOException {
        Runnable serverLoop = () -> {
            System.out.println("Starting server 0");
            try {
                ServerSocket serverSocket = new ServerSocket(8420);

                while (true) {
                    // block until we get a connection from a client
                    final Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected to server 0 from " + clientSocket.getInetAddress());

                    Platform.runLater(() -> {
                        try {
                            openNewThread(clientSocket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }

            } catch (SocketException sx) {
                System.out.println("Socket SERVER0 closed, user has shutdown the connection, or network has failed");
            } catch (IOException ex) {
                System.out.println(ex.getMessage() + ex);
            } catch (Exception ex){
                System.out.println(ex.getMessage() + ex);
            }

        };
        new Thread(serverLoop).start();

        System.out.println("Server 0 started");

    }
}
