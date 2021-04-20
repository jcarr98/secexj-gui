package encryption;

import javax.crypto.Cipher;
import java.io.UnsupportedEncodingException;
import java.security.*;

/**
 * Handles encryption/decryption of data
 */
public class Secret {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private Cipher cipher;

    public Secret() {
        KeyPair keyPair = generateKeys();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    private KeyPair generateKeys() {
        // Create key generator
        KeyPairGenerator gen;
        try {
            gen = KeyPairGenerator.getInstance("RSA");
        }
        catch(GeneralSecurityException e) {
            System.out.println("Error creating key generator");
            return null;
        }

        // Initialize key generator
        gen.initialize(2048, new SecureRandom());

        // Create keypair
        return gen.generateKeyPair();
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public byte[] encryptRSA(String plaintext, PublicKey key) {
        try {
            return encryptRSA(plaintext.getBytes("UTF-8"), key);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] encryptRSA(byte[] plaintext, PublicKey key) {

    }
}
