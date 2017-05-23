package MContact;

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

    public Message(String body, String author) {
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

//    public Message(String body, String author, Date date) {
//        this.body = body;
//        this.author = author;
//        this.date = date;
//    }

    public Message(String json) {
        JSONObject parsed = new JSONObject(json);
        body = parsed.getString("body");
        author = parsed.getString("author");
        id = parsed.getString("id");
        System.out.println("md5RECEIVED: "+id);
        //TODO: oznaczyc odpowiednia wiadomosc jako odczytana

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



}
