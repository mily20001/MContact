package MContact;

import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class TypingMessage extends Message {
    private String author;

    private VBox msgContainer;
    private Text msgBody;

    /**
     * Constructs typing indicator
     * @param author name of typing person
     */
    TypingMessage(String author){
        this.author = author;
    }

    /**
     * Generate HBox containing typing indicator and bind size listeners for dynamic resize.
     * @param threadBoxWidth width of containing box used for initial measure
     * @param threadPane message will listen for it's changes to dynamically resize
     * @return styled HBox containing typing indicator
     */
    @Override
    public HBox render(Double threadBoxWidth, ScrollPane threadPane) {
        HBox msg = new HBox();

        msgContainer = new VBox();
        msgContainer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        msg.getChildren().add(msgContainer);

        msgBody = new Text(this.author + " is typing...");

        msgBody.setFont(msgBody.getFont());
        double msgBodyWidth = msgBody.getBoundsInLocal().getWidth();
        if(msgBodyWidth > threadBoxWidth - 15) {
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

        msgContainer.getChildren().addAll(msgBody);

        msg.setAlignment(Pos.TOP_LEFT);
        msgContainer.getStyleClass().add("user_msg");
        msgContainer.setAlignment(Pos.TOP_LEFT);
        msgBody.getStyleClass().add("user_msg_body");

        return msg;
    }
}
