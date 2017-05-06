package MContact;

import com.sun.javafx.tk.Toolkit;
import com.sun.org.apache.regexp.internal.RE;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.geometry.Pos.CENTER;

/**
 * Created by milosz on 27.03.17.
 */

public class View {

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

        threadPane.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldvalue, Bounds newValue) {
//                Double fullwidth = msgBodyWidth;

                if(msgBodyWidth > newValue.getWidth() - 15) {
//                    System.out.println("will be wrapped");
                    msgBody.setWrappingWidth(newValue.getWidth() - 15);
                } else {
                    msgBody.setWrappingWidth(msgBodyWidth + 5);
                }
//                System.out.println("width changed of scrollpane in view: " + newValue.getWidth() + ", " + fullwidth);
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

    public View(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("view2.fxml"));

//        VBox root = new VBox();
//
//        HBox topBar = new HBox();
//        topBar.setAlignment(Pos.CENTER_LEFT);
//
//        root.getChildren().add(topBar);
//
//        Label nameLabel = new Label("test");
//        nameLabel.setMaxWidth(10000);
//        HBox.setHgrow(nameLabel, Priority.ALWAYS);
//
//        Button challangeButton = new Button("Challange");
//        challangeButton.setMinWidth(80);
//
//        topBar.getChildren().addAll(nameLabel, challangeButton);
//
//        ScrollPane threadPane = new ScrollPane();
//        threadPane.setFitToWidth(true);
//        VBox.setVgrow(threadPane, Priority.ALWAYS);
//        threadPane.setMaxSize(10000, 10000);
//
//        root.getChildren().add(threadPane);
//
//        HBox bottomBar = new HBox();
//
//        TextArea inputArea = new TextArea();
//        inputArea.setMaxSize(10000, 350);
//        inputArea.setPrefHeight(10);
//        inputArea.setWrapText(true);
//        Button sendButton = new Button("Send");
//        sendButton.setMinWidth(50);
//        sendButton.setPrefHeight(250);
//
//        bottomBar.getChildren().addAll(inputArea, sendButton);
//
//        root.getChildren().add(bottomBar);

        stage.setTitle("MContact");
        stage.setMinHeight(300);
        stage.setMinWidth(200);
        Scene scene = new Scene(root, 400, 512);

        scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
//
//        msg1.setMaxWidth(threadScroll.getWidth() - 54);
//        msg1.setMinWidth(threadScroll.getWidth() - 54);
//        msg1t1.setText(String.valueOf(threadScroll.getWidth() - 54));
        //VBox.setMargin(msg1, new Insets(5.0, 0, 0, 50));
    }
//    private VBox addMessage(String body, String details) {
//        VBox msg1 = new VBox();
//        msg1.getStyleClass().add("your_msg");
//        msg1.setMaxWidth(threadScroll.getWidth() - 50);
//        msg1.setPrefWidth(threadScroll.getWidth() - 50);
//        VBox.setVgrow(msg1, Priority.NEVER);
//        VBox.setMargin(msg1, new Insets(5.0, 0, 0, 50));
//        msg1.setAlignment(Pos.TOP_RIGHT);
//
//        Text msg1t1 = new Text();
//        msg1t1.setFill(Color.WHITE);
//        msg1t1.getStyleClass().add("your_msg_body");
//        msg1t1.setStrokeType(StrokeType.OUTSIDE);
//        msg1t1.setStrokeWidth(0.0);
//        msg1t1.setText(body);
//
//        Text msg1t2 = new Text();
//        msg1t2.setFill(Color.web("0xefff00"));
//        msg1t2.getStyleClass().add("your_msg_det");
//        msg1t2.setText(details);
//
//        msg1.getChildren().add(msg1t1);
//        msg1.getChildren().add(msg1t2);
//
//        return msg1;
//    }
}





//        GridPane root = new GridPane();
//
//
//
//        root.setAlignment(CENTER);
//        root.setMinHeight(300.0);
//        root.setMinWidth(200.0);
//        root.setPrefHeight(512.0);
//        root.setPrefWidth(400.0);
//        root.setVgap(10.0);
//        root.getStyleClass().add("test");
//
//        ColumnConstraints column1 = new ColumnConstraints();
//        column1.setHgrow(Priority.ALWAYS);
//        column1.setMinWidth(10.0);
//        column1.setPercentWidth(80);
//        column1.setPrefWidth(100.0);
//        ColumnConstraints column2 = new ColumnConstraints();
//        column2.setHgrow(Priority.ALWAYS);
//        column2.setMinWidth(60.0);
//        column2.setPercentWidth(20);
//
//        root.getColumnConstraints().addAll(column1, column2);
//
//        Text partnerName = new Text();
//        partnerName.setFill(Color.WHITE);
//        partnerName.setText("Talking with John Smith");
//
//        root.add(partnerName, 0, 0);
//
//        Button challangeBtn = new Button();
//        challangeBtn.setMinWidth(50.0);
//        challangeBtn.setMnemonicParsing(false);
//        challangeBtn.setPrefHeight(29.0);
//        challangeBtn.setPrefWidth(190.0);
//        challangeBtn.setText("Challange");
//
//        root.add(challangeBtn, 1, 0);
//
//        ScrollPane threadScroll = new ScrollPane();
//        threadScroll.setFitToWidth(true);
//        threadScroll.getStyleClass().add("test2");
//        GridPane.setHgrow(threadScroll, Priority.ALWAYS);
//        GridPane.setVgrow(threadScroll, Priority.ALWAYS);
//
//        root.add(threadScroll, 0, 1, 2, 1);
//
//        VBox threadBox = new VBox();
//        GridPane.setHgrow(threadBox, Priority.ALWAYS);
//        threadBox.setMaxWidth(threadScroll.getWidth());
//        threadBox.setPrefWidth(threadScroll.getWidth() - 20);
//
//        threadScroll.setContent(threadBox);
//
//        threadBox.getChildren().add(msg1);
//