package MContact;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

class MainView {
    public MainView(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));

        stage.setTitle("MContact Main");
        stage.setMinHeight(100);
        stage.setMinWidth(350);
        Scene scene = new Scene(root, 350, 100);

        scene.getStylesheets().add(this.getClass().getResource("MainStyle.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }
}
