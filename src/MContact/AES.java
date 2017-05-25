package MContact;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

/** Class which handles AES key generation, encryption and decryption */
class AES {
    /**
     * Returns a new secret AES key.
     * @return a secret AES key
     */
    static SecretKey generateKey(){
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyGenerator.generateKey();
    }

    /**
     * Returns an AES-encrypted base64 encoded string of given plaintext string encrypted using a secret AES key.
     * @param plaintext a plaintext string
     * @param key a secret AES key
     * @return encrypted string
     */
    static String encrypt(String plaintext, SecretKey key){
        byte[] ciphertext = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[16]));
            byte[] encode = Base64.getEncoder().encode(plaintext.getBytes("UTF-8"));
            ciphertext = cipher.doFinal(encode);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new String(Base64.getEncoder().encode(ciphertext));

    }

    /**
     * Returns the plaintext of a given AES-encrypted string, and a secret AES key.
     * @param encryptedString an AES encrypted Base64 String
     * @param key a secret AES key
     * @return plaintext
     */
    static String decrypt(String encryptedString, SecretKey key){
        byte[] ciphertext = Base64.getDecoder().decode(encryptedString);
        byte[] plaintext = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[16]));
            plaintext = cipher.doFinal(ciphertext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(Base64.getDecoder().decode(plaintext));
    }
}