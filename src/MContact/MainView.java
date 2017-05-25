package MContact;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/** Class representing main window view */
class MainView {
    /**
     * Constructs main view, adds controller to it and display main window
     * @param stage stage to be used
     * @param mainController mainController which will be binded to view
     * @throws IOException if fxml or css files not found
     */
    MainView(Stage stage, MainController mainController) throws IOException {
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
