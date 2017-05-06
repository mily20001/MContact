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

class ThreadView {

    static HBox addMsg(String body, String details, boolean your, Double threadBoxWidth, ScrollPane threadPane) {
        HBox msg = new HBox();

        VBox msgContainer = new VBox();
        msgContainer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        msg.getChildren().add(msgContainer);

        Text msgBody = new Text(body);

        msgBody.setFont(msgBody.getFont());
        double msgBodyWidth = msgBody.getBoundsInLocal().getWidth();
        System.out.println("text width: "+ msgBodyWidth);
        if(msgBodyWidth > threadBoxWidth - 15) {
            System.out.println("will be wrapped");
            msgBody.setWrappingWidth(threadBoxWidth - 15);
        } else {
            msgBody.setWrappingWidth(msgBodyWidth + 5);
        }

        threadPane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.getWidth() == oldValue.getWidth())
                return;

            if(msgBodyWidth > newValue.getWidth() - 15) {
                msgBody.setWrappingWidth(newValue.getWidth() - 15);
            } else {
                msgBody.setWrappingWidth(msgBodyWidth + 5);
            }
        });

        VBox.setVgrow(msgBody, Priority.ALWAYS);

        Label msgDetails = new Label(details);

        msgContainer.getChildren().addAll(msgBody, msgDetails);

        if(your) {
            msg.setAlignment(Pos.TOP_RIGHT);
            msgContainer.getStyleClass().add("your_msg");
            msgContainer.setAlignment(Pos.TOP_RIGHT);
            msgBody.getStyleClass().add("your_msg_body");
            msgDetails.getStyleClass().add("your_msg_det");
        } else {
            msg.setAlignment(Pos.TOP_LEFT);
            msgContainer.getStyleClass().add("user_msg");
            msgContainer.setAlignment(Pos.TOP_LEFT);
            msgBody.getStyleClass().add("user_msg_body");
            msgDetails.getStyleClass().add("user_msg_det");
        }


        return msg;
    }

    public ThreadView(Stage stage, String name) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("ThreadView.fxml"));

        stage.setTitle("MContact - " + name);
        stage.setMinHeight(300);
        stage.setMinWidth(200);
        Scene scene = new Scene(root, 250, 350);

        scene.getStylesheets().add(this.getClass().getResource("ThreadStyle.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }
}