package MContact;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

class MainView {
    public MainView(Stage stage, MainController mainController) throws IOException {
        FXMLLoader loader= new FXMLLoader(getClass().getResource("MainView.fxml"));
        loader.setController(mainController);
        loader.setClassLoader(getClass().getClassLoader());
        Parent root = loader.load();

        stage.setTitle("MContact Main");
        stage.setMinHeight(100);
        stage.setMinWidth(350);
        Scene scene = new Scene(root, 350, 100);

        scene.getStylesheets().add(this.getClass().getResource("MainStyle.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
        stage.toFront();

        stage.setOnCloseRequest(we -> mainController.closingMainWindow());
    }
}
