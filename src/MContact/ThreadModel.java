package MContact;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.crypto.SecretKey;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


class ThreadModel {
    private PrintWriter netOut;

    private Socket socket;

    private String yourName;

    private String partnerName=null;

    private Stage threadStage;
    private VBox threadBox;
    private ScrollPane threadPane;

    private KeyPair RSApair;
    private SecretKey AESkey;
    private SecretKey partnerAESkey;
    private PublicKey partnerRSAkey;
    private Boolean encryptionReady = false;

    private Map <String, Message> messages = new HashMap<>();

    void addMessage(Message msg){
        messages.put(msg.getId(), msg);
        threadBox.getChildren().add(msg.render(threadBox.getWidth(), threadPane));
    }

    String encrypt(String msg) {
        return AES.encrypt(msg, AESkey);
    }

    String decrypt(String msg) {
        try {
            return AES.decrypt(msg, partnerAESkey);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return "decrypt error";
    }

    PublicKey getPublicKey() {
        return RSApair.getPublic();
    }

    SecretKey getAESKey() {
        return AESkey;
    }

    void setPartnerAESkey(String key) {
        try {
            partnerAESkey = RSA.decrypt(key, RSApair.getPrivate());
            encryptionReady = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Boolean isEncryptionReady() { return  encryptionReady; }

    PublicKey getPartnerRSAkey() {
        return  partnerRSAkey;
    }

    SecretKey getPartnerAESkey() {
        return  partnerAESkey;
    }

    void setPartnerRSAkey(String key) {
        try {
            byte[] publicBytes = Base64.getDecoder().decode(key);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            partnerRSAkey = keyFactory.generatePublic(keySpec);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setThreadBoxPane(VBox threadBox, ScrollPane threadPane) {
        this.threadBox = threadBox;
        this.threadPane = threadPane;
    }

    ThreadModel(String yourName, Stage threadStage, Socket socket, PrintWriter netOut) {
        this.yourName = yourName;
        this.threadStage = threadStage;
        this.socket = socket;
        this.netOut = netOut;

        System.out.println("Generating keys");
        this.AESkey = AES.generateKey();
        this.RSApair = RSA.generateKeyPair();
        System.out.println("Keys ready");
    }

    void delivered(String msgId) {
        if(!messages.containsKey(msgId)) {
            System.out.println("Got delivery report for message that don't exist");
            return;
        }
        messages.get(msgId).delivered();
    }

    void setPartnerName(String newName) {
        this.partnerName = newName;
    }

    String getPartnerName() {
        return partnerName;
    }

    String getYourName() {
        return yourName;
    }

    Socket getSocket() {
        return socket;
    }

    Stage getThreadStage() {
        return threadStage;
    }

    PrintWriter getNetOut() {
        return netOut;
    }
}
