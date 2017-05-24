package MContact;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

class ThreadView {

    public void detailsAlert(String yourHash, String partnerHash) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("dialogs.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("errorAlert");
        alert.setTitle("Connection details");
        alert.setHeaderText(null);
        alert.setContentText("Your AES hash: "+yourHash + "\nPartner AES hash: "+partnerHash);
        alert.show();
    }

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