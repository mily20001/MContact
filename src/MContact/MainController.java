package MContact;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.InputEvent;
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
import java.net.UnknownHostException;
import java.util.Optional;

public class MainController {
    private MainModel mainModel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label portLabel;

    @FXML
    private Button connectButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button changePortButton;

    @FXML
    private Button changeNameButton;

    public MainController() throws IOException {
        System.out.println("hejka z glownego controllera");
        mainModel = new MainModel("John Smith", 8420);
        Server0();
    }

    @FXML
    void initialize() {
        nameLabel.setText("Hello " + mainModel.getName() + "!");
        portLabel.setText("You are listening on port " + mainModel.getServerPort());
        connectButton.setOnAction((e) -> {
            try {
                ConnectButtonClicked();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        exitButton.setOnAction((e) -> {
            System.out.println("closing app");
            exitButtonClicked(e);
        });

        changePortButton.setOnAction((e) -> {
            changePortButtonClicked();
        });

        changeNameButton.setOnAction((e) -> {
            changeNameButtonClicked();
        });
    }

    @FXML void exitButtonClicked(ActionEvent e) {
        final Node source = (Node) e.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
        closingMainWindow();
    }

    @FXML
    void changePortButtonClicked() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change your port");
        dialog.setHeaderText("Change your port number");
        dialog.setContentText("Enter new port number:");

        Optional<String> result = dialog.showAndWait();

        try {
            if (result.isPresent()) {
                mainModel.setServerPort(Integer.parseInt(result.get()));
                portLabel.setText("You are listening on port " + mainModel.getServerPort());
                mainModel.getServerSocket().close();
                Server0();
            }
        }
        catch (NumberFormatException e) {
            System.out.println("Wrong port format");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().getStylesheets().add(
                    getClass().getResource("dialogs.css").toExternalForm());
            alert.getDialogPane().getStyleClass().add("errorAlert");
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Port " + result.get() + " is invalid.");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void changeNameButtonClicked() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change your name");
        dialog.setHeaderText("Change your name");
        dialog.setContentText("Enter new name:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            mainModel.setName(result.get());
            nameLabel.setText("Hello " + mainModel.getName() + "!");
        }
    }

    @FXML
    public void ConnectButtonClicked() throws IOException {

        Dialog<Pair<String, String>> dialog = new Dialog<>();

        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("dialogs.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("connectDialog");

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
            System.out.println("Connecting to " + ipPort.getKey() + ":" + ipPort.getValue());
            Stage stage = new Stage();
            try {
                Socket tmpSocket = new Socket(ipPort.getKey(), Integer.parseInt(ipPort.getValue()));

                ThreadController threadController = new ThreadController(tmpSocket, mainModel.getName());
                /*TODO tu trzeba przekazywac imie drugiego usera, a nie swoje*/
                new ThreadView(stage, mainModel.getName(), threadController);

            }catch (UnknownHostException e) {
                System.out.println("Wrong addresSSs");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.getDialogPane().getStylesheets().add(
                        getClass().getResource("dialogs.css").toExternalForm());
                alert.getDialogPane().getStyleClass().add("errorAlert");
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Could not connect to " + ipPort.getKey() + ":" + ipPort.getValue());
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (NumberFormatException e) {
                System.out.println("Wrong port format");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.getDialogPane().getStylesheets().add(
                        getClass().getResource("dialogs.css").toExternalForm());
                alert.getDialogPane().getStyleClass().add("errorAlert");
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Port " + ipPort.getValue() + " is invalid.");
                alert.showAndWait();
            }
        });
    }

    private void openNewThread(Socket socket) throws IOException{
        Stage stage = new Stage();
        ThreadController threadController = new ThreadController(socket, mainModel.getName());
        /*TODO tu trzeba przekazywac imie drugiego usera, a nie swoje*/
        new ThreadView(stage, mainModel.getName(), threadController);
    }

    public void closingMainWindow() {
        System.out.println("closing main server");
        try {
            mainModel.getServerSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Server0() throws IOException {
        Runnable serverLoop = () -> {
            System.out.println("Starting server 0");
            try {
                mainModel.setServerSocket(new ServerSocket(mainModel.getServerPort()));

                while (true) {
                    // block until we get a connection from a client
                    final Socket clientSocket = mainModel.getServerSocket().accept();
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
                System.out.println("Socket SERVER0 (MAIN) closed, user has shutdown the connection, or network has failed");
            } catch (IOException ex) {
                System.out.println(ex.getMessage() + ex);
            } catch (Exception ex){
                System.out.println(ex.getMessage() + ex);
            }
            finally {
                System.out.println("MAIN SERVER CLOSED");
            }

        };
        new Thread(serverLoop).start();

        System.out.println("Server 0 started");

    }
}
