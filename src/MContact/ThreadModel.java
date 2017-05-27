package MContact;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.crypto.SecretKey;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/** Model class of single thread window */
class ThreadModel {
    private PrintWriter netOut;

    private Socket socket;

    private String yourName;

    private String partnerName=null;

    private Stage threadStage;
    private VBox threadBox;
    private ScrollPane threadPane;
    private HBox typingMessageHBox;

    private KeyPair RSApair;
    private SecretKey AESkey;
    private SecretKey partnerAESkey;
    private PublicKey partnerRSAkey;
    private Boolean encryptionReady = false;

    /** Map of all messages */
    private Map <String, Message> messages = new HashMap<>();
    private TypingMessage typingMessage;

    /**
     * Adds new message to message map
     * @param msg message object to add
     */
    void addMessage(Message msg){
        messages.put(msg.getId(), msg);
        threadBox.getChildren().add(msg.render(threadBox.getWidth(), threadPane));
    }

    /**
     * Checks if there is currently any typing indicator and if not creates new one
     * @param author name of person who is typing
     */
    void setTypingIndicator(String author) {
        if(typingMessageHBox == null){
            typingMessage = new TypingMessage(author);
            typingMessageHBox = typingMessage.render(threadBox.getWidth(), threadPane);
            threadBox.getChildren().add(typingMessageHBox);
        }
    }

    /**
     * Removes typing indicator if there is any
     */
    void unsetTypingIndicator() {
        if(typingMessageHBox != null){
            threadBox.getChildren().remove(typingMessageHBox);
            typingMessageHBox = null;
            typingMessage = null;
        }
    }

    /**
     * Encrypt string using your AES key
     * @param msg string to be encrypted
     * @return encrypted string
     */
    String encrypt(String msg) {
        return AES.encrypt(msg, AESkey);
    }

    /**
     * Decrypt string using your partner AES key
     * @param msg encrypted string
     * @return decrypted string
     */
    String decrypt(String msg) {
        try {
            return AES.decrypt(msg, partnerAESkey);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return "decrypt error";
    }

    /**
     * Returns your RSA public key
     * @return your RSA public key
     */
    PublicKey getPublicKey() {
        return RSApair.getPublic();
    }

    /**
     * Return your secret AES key
     * @return your secret AES key
     */
    SecretKey getAESKey() {
        return AESkey;
    }

    /**
     * Calculate MD5 hash of given AES key and encode it in Base64 format (not necessarily correct)
     * @param key AES key to be encoded
     * @return Base64 encoded hash of given AES key
     */
    private String getAESHash(SecretKey key) {
        String wyn = "";
        try {
            byte[] bytesOfMessage = key.getEncoded();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(bytesOfMessage);
            wyn = new String(Base64.getEncoder().encode(thedigest));
            //delete all = from base64 code
            wyn = wyn.replace("=", "");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return wyn;
    }

    /**
     * Returns hash of your AES key
     * @return hash of your AES key
     */
    String getYourAESHash() {
        return getAESHash(AESkey);
    }

    /**
     * Returns hash of your partner AES key
     * @return hash of your partner AES key
     */
    String getPartnerAESHash() {
        return getAESHash(partnerAESkey);
    }

    /**
     * Saves partner AES key to variable for later use
     * @param key partner secret AES key
     */
    void setPartnerAESkey(String key) {
        try {
            partnerAESkey = RSA.decrypt(key, RSApair.getPrivate());
            encryptionReady = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tells if encryption is ready (if keys are exchanged)
     * @return encryption status
     */
    Boolean isEncryptionReady() { return  encryptionReady; }

    /**
     * Returns your partner RSA public key
     * @return your partner RSA public key
     */
    PublicKey getPartnerRSAkey() {
        return  partnerRSAkey;
    }

    /**
     * Returns your partner secret AES key
     * @return your partner secret AES key
     */
    SecretKey getPartnerAESkey() {
        return  partnerAESkey;
    }

    /**
     * Decode and set to variable your partner RSA public key
     * @param key Base64 encoded string of RSA public key
     */
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

    /**
     * Saves threadBox and threadPane, they can't be passed in constructor since they may be not initialised when constructor runs
     * @param threadBox VBox placed inside Scrollpane
     * @param threadPane main Scrollpane in thread window
     */
    void setThreadBoxPane(VBox threadBox, ScrollPane threadPane) {
        this.threadBox = threadBox;
        this.threadPane = threadPane;
    }

    /**
     * Constructs new thread model and generate RSA and AES keys
     * @param yourName your name/nick
     * @param threadStage thread stage
     * @param socket socket used for connections
     * @param netOut printwriter used for sending data
     */
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

    /**
     * Marks message as delivered
     * @param msgId id of message which was delivered
     */
    void delivered(String msgId) {
        if(!messages.containsKey(msgId)) {
            System.out.println("Got delivery report for message that don't exist");
            return;
        }
        messages.get(msgId).delivered();
    }

    /**
     * Saves your partner name
     * @param newName new partner's name
     */
    void setPartnerName(String newName) {
        this.partnerName = newName;
    }

    /**
     * Returns your partner name
     * @return your partner name
     */
    String getPartnerName() {
        return partnerName;
    }

    /**
     * Returns your name
     * @return your name
     */
    String getYourName() {
        return yourName;
    }

    /**
     * Returns socket used for this thread
     * @return socket used for this thread
     */
    Socket getSocket() {
        return socket;
    }

    /**
     * Returns thread's stage
     * @return thread's stage
     */
    Stage getThreadStage() {
        return threadStage;
    }

    /**
     * Returns thread's PrintWriter
     * @return thread's PrintWriter
     */
    PrintWriter getNetOut() {
        return netOut;
    }
}
