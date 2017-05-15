package MContact;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message {
    protected String body;
    protected Date date;
    protected String author;

    public Message(String body, String author) {
        this.body = body;
        this.author = author;
        date = new Date();
    }

    public Message(String body, String author, Date date) {
        this.body = body;
        this.author = author;
        this.date = date;
    }

    public Message(String json) {
        JSONObject parsed = new JSONObject(json);
        body = parsed.getString("body");
        author = parsed.getString("author");

        System.out.println(parsed.getString("date"));
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss zzz", Locale.ENGLISH);
        try {
            date = format.parse(parsed.getString("date"));
        } catch (ParseException e) {
            System.out.println("error parsing date while adding new message");
            e.printStackTrace();
        }
//        date = new Date(parsed.getString("date"));
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

        return obj.toString();
    }



}
