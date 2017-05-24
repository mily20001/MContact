package MContact;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

class ThreadView {

    public ThreadView(Stage stage, ThreadController threadController) throws IOException {
        FXMLLoader loader= new FXMLLoader(getClass().getResource("ThreadView.fxml"));
        loader.setController(threadController);
        loader.setClassLoader(getClass().getClassLoader());
        Parent root = loader.load();

        stage.setMinHeight(300);
        stage.setMinWidth(200);
        Scene scene = new Scene(root, 250, 350);

        scene.getStylesheets().add(this.getClass().getResource("ThreadStyle.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
        stage.toFront();

        stage.setOnCloseRequest(we -> threadController.closingWindow());
    }
}