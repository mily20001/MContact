package MContact;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    private ThreadModel threadModel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label portLabel;

    public MainController() throws IOException {
        System.out.println("hejka z glownego controllera");
        threadModel = new ThreadModel();
    }

    @FXML
    void initialize() {
        nameLabel.setText("Hello " + threadModel.name + "!");
        portLabel.setText("You are listening on port " + threadModel.serverPort);
    }

    @FXML
    public void ConnectButtonClicked() throws IOException {
        Stage stage = new Stage();
        ThreadView threadView = new ThreadView(stage, threadModel.name);
    }
}
