package encryption;

import javax.crypto.*;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

/**
 * Handles encryption/decryption of data
 */
public class Secret {
    final private PublicKey publicKey, signingPublicKey;
    final private PrivateKey privateKey, signingPrivateKey;
    private SecretKey aesKey;
    private Cipher cipher;

    public Secret() {
        KeyPair encryptionKeyPair = generateRSAKeys();
        publicKey = encryptionKeyPair.getPublic();
        privateKey = encryptionKeyPair.getPrivate();

        KeyPair signingKeyPair = generateRSAKeys();
        signingPublicKey = signingKeyPair.getPublic();
        signingPrivateKey = signingKeyPair.getPrivate();

        try {
            cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        }
        catch(GeneralSecurityException e) {
            System.out.println("Error initiating cipher");
        }
    }

    /* ***** RSA ***** */
    private KeyPair generateRSAKeys() {
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

    public PublicKey getSigningKey() {
        return signingPublicKey;
    }

    public String encryptRSA(String plaintext, PublicKey key) {
        try {
            return encryptRSA(plaintext.getBytes("UTF-8"), key);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String encryptRSA(byte[] plaintext, PublicKey key) {
        // Initialize cipher in encrypt mode and encrypt with cipher
        byte[] ciphertext;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            ciphertext = cipher.doFinal(plaintext);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }

        return Base64.getEncoder().encodeToString(ciphertext);
    }

    /**
     * Decrypt the provided ciphertext using public key belonging to this RSA object
     * @param ciphertext Ciphertext to decrypt
     * @return A plaintext string of the provided ciphertext
     */
    public byte[] decryptRSA(String ciphertext, String signature, PublicKey key) {
        // Initialize cipher in decrypt mode and decrypt with cipher
        byte[] decrypted;
        try {
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            decrypted = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }

        // Verify data signature
        if(!verify(Base64.getEncoder().encodeToString(decrypted), signature, key)) {
            System.out.println("Cannot verify signature");
            return null;
        }

        // Convert bytes to a String and return
        return decrypted;
    }

    public String sign(String plaintext) {

        Signature privateSignature;
        byte[] signature;
        try {
            privateSignature = Signature.getInstance("SHA256withRSA");
            privateSignature.initSign(signingPrivateKey);
            privateSignature.update(plaintext.getBytes(StandardCharsets.UTF_8));
            signature = privateSignature.sign();
        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
            e.printStackTrace();
            return null;
        }

        return Base64.getEncoder().encodeToString(signature);
    }

    private boolean verify(String message, String signature, PublicKey key) {
        Signature publicSignature = null;
        try {
            publicSignature = Signature.getInstance("SHA256withRSA");
            publicSignature.initVerify(key);
            publicSignature.update(message.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return false;
        }

        boolean valid = false;
        try {
            byte[] signatureBytes = Base64.getDecoder().decode(signature);

            valid = publicSignature.verify(signatureBytes);
        } catch (SignatureException e) {
            e.printStackTrace();
            return false;
        }

        return valid;
    }

    /* ***** AES ***** */
    public SecretKey generateAESKey() {
        KeyGenerator keyGen;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        // Create random byte generate
        SecureRandom r = new SecureRandom();

        // Initialize key generator
        keyGen.init(256, r);

        SecretKey key = keyGen.generateKey();

        return key;
    }

    public void setAESKey(SecretKey key) {
        aesKey = key;
    }
}
