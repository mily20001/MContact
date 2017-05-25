package MContact;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.*;
import java.net.*;
import java.util.Optional;
import java.util.Properties;

/** Controller class of main application */
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

    /**
     * Loads config from file, if not found loads default values, then create new main model
     */
    private void loadConfig() {
        Properties props = new Properties();
        InputStream is = null;

        try {
            File f = new File("config.cfg");
            is = new FileInputStream( f );
            props.load( is );
        }
        catch ( Exception e ) { is = null; }

        String name = props.getProperty("Name", "John Smith");
        Integer port = new Integer(props.getProperty("Port", "8080"));

        mainModel = new MainModel(name, port);
    }

    /** Saves current configuration to file */
    private void saveConfig() {
        try {
            Properties props = new Properties();
            props.setProperty("Name", mainModel.getName());
            props.setProperty("Port", mainModel.getServerPort().toString());
            File f = new File("config.cfg");
            OutputStream out = new FileOutputStream( f );
            props.store(out, "Mcontact settings");
        }
        catch (Exception e ) {
            System.out.println("Error while saving config");
            e.printStackTrace();
        }
    }

    /** Construct new controller and launch server */
    public MainController() {
        loadConfig();

        startServer();
    }

    /** Sets events handlers and put proper text in main window */
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

    /**
     * Handles pressing exit button
     * @param e event
     */
    @FXML
    private void exitButtonClicked(ActionEvent e) {
        final Node source = (Node) e.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
        closingMainWindow();
    }

    /** Handles pressing 'Change Port' button by displaying dialog and setting given values. Also restart server on new port */
    @FXML
    private void changePortButtonClicked() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change your port");
        dialog.setHeaderText("Change your port number");
        dialog.setContentText("Enter new port number:");

        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("dialogs.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("inputDialog");

        Optional<String> result = dialog.showAndWait();

        try {
            if (result.isPresent()) {
                mainModel.setServerPort(Integer.parseInt(result.get()));
                portLabel.setText("You are listening on port " + mainModel.getServerPort());
                mainModel.getServerSocket().close();
                startServer();
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

    /** Handles pressing 'Change name' button and set your new name in model and in window */
    @FXML
    private void changeNameButtonClicked() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change your name");
        dialog.setHeaderText("Change your name");
        dialog.setContentText("Enter new name:");

        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("dialogs.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("inputDialog");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            mainModel.setName(result.get());
            nameLabel.setText("Hello " + mainModel.getName() + "!");
        }
    }

    /**
     * Handles pressing connect button by displaying connection dialog and staring new thread after successful connection
     * @throws IOException if css are not found
     */
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

                ThreadController threadController = new ThreadController(tmpSocket, mainModel.getName(), stage);
                new ThreadView(stage, threadController);

            }catch (UnknownHostException | ConnectException e) {
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

    /**
     * Opens new thread window
     * @param socket socket on which parter is connected
     * @throws IOException when thread controller throws it
     */
    private void openNewThread(Socket socket) throws IOException{
        Stage stage = new Stage();
        new ThreadController(socket, mainModel.getName(), stage);
    }

    /** Handles closing main application, saves config and closes server */
    void closingMainWindow() {
        saveConfig();
        if(!mainModel.getServerSocket().isClosed()) {
            System.out.println("closing main server");
            try {
                mainModel.getServerSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** Start new server and makes it handing new connections properly */
    private void startServer(){
        Runnable serverLoop = () -> {
            try {
                mainModel.setServerSocket(new ServerSocket(mainModel.getServerPort()));

                while (true) {
                    // block until we get a connection from a client
                    final Socket clientSocket = mainModel.getServerSocket().accept();
                    System.out.println("Client connected to server from " + clientSocket.getInetAddress());

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
        };
        new Thread(serverLoop).start();
    }
}
