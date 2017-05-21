package MContact;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public class RSA {
    /**
     * Returns a new RSA key pair.
     * @return public and private RSA keys
     */
    public static KeyPair generateKeyPair(){
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * Returns an RSA-encrypted byte array given a plaintext string and a public RSA key.
     * @param plaintext a plaintext string
     * @param key a public RSA key
     * @return ciphertext
     */
    public static byte[] encrypt(byte[] plaintext, PublicKey key){
        byte[] ciphertext = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            ciphertext = cipher.doFinal(plaintext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ciphertext;
    }

    /**
     * Returns the plaintext of a given RSA-encrypted string and a private RSA key.
     * @param encryptedKey an RSA-encrypted Base64 encoded string with AES secret key
     * @param key a private RSA key
     * @return plaintext
     */
    public static SecretKey decrypt(String encryptedKey, PrivateKey key){
        byte[] ciphertext = Base64.getDecoder().decode(encryptedKey);
        byte[] plaintext = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            plaintext = cipher.doFinal(ciphertext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new SecretKeySpec(plaintext, 0, plaintext.length, "AES");
    }
}