package MContact;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;

public class Message {
    protected String body;
    protected Date date;
    protected String author;
    private String id="";
    private Boolean delivered;
    private Boolean your;

    private VBox msgContainer;
    private Text msgBody;
    private Label msgDetails;

    public Message(String body, String author) {
        delivered = false;
        your = true;
        this.body = body;
        this.author = author;
        date = new Date();

        Long mili = System.currentTimeMillis();
        try {
            byte[] bytesOfMessage = (toJSON() + mili).getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(bytesOfMessage);
            id = new String(Base64.getEncoder().encode(thedigest));
            System.out.println("md5: "+id);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Message(String json) {
        delivered = true;
        your = false;
        JSONObject parsed = new JSONObject(json);
        body = parsed.getString("body");
        author = parsed.getString("author");
        id = parsed.getString("id");
        System.out.println("md5RECEIVED: "+id);
        //TODO: oznaczyc odpowiednia wiadomosc jako dostarczoną

        System.out.println(parsed.getString("date"));
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss zzz", Locale.ENGLISH);
        try {
            date = format.parse(parsed.getString("date"));
        } catch (ParseException e) {
            System.out.println("error parsing date while adding new message");
            e.printStackTrace();
        }
    }

    public String getDateString() {
        SimpleDateFormat ft = new SimpleDateFormat ("dd.MM HH:mm:ss");
        return ft.format(date);
    }

    public String toJSON() {
        SimpleDateFormat ft = new SimpleDateFormat ("dd.MM.yyyy HH:mm:ss zzz", Locale.ENGLISH);
        String dateString = ft.format(date);
        JSONObject obj = new JSONObject();
        obj.put("body", body);
        obj.put("date", dateString);
        obj.put("author", author);
        obj.put("id", id);

        return obj.toString();
    }

    public HBox render(Double threadBoxWidth, ScrollPane threadPane) {
        String details = this.author + ", " + this.getDateString();
        HBox msg = new HBox();

        msgContainer = new VBox();
        msgContainer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        msg.getChildren().add(msgContainer);

        msgBody = new Text(this.body);

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

        msgDetails = new Label(details);

        msgContainer.getChildren().addAll(msgBody, msgDetails);

        if(this.your) {
            msg.setAlignment(Pos.TOP_RIGHT);
            msgContainer.getStyleClass().add("your_msg_sent");
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

    String getId() {
        return id;
    }

    void delivered() {
        delivered = true;
        msgContainer.getStyleClass().clear();
        msgContainer.getStyleClass().add("your_msg_delivered");
    }

}
