package bi.meteorite.license;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Stub encryption manager for license handling.
 * In the commercial version this handles license encryption/decryption.
 */
public class EncryptionManager {

    public EncryptionManager() {
    }

    public EncryptionManager(byte[] publicKey, byte[] privateKey)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        // Stub: ignore keys in community edition
    }

    public static byte[] readAll(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return readAll(fis);
        }
    }

    public static byte[] readAll(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int len;
        while ((len = is.read(buf)) != -1) {
            bos.write(buf, 0, len);
        }
        return bos.toByteArray();
    }

    /**
     * Stub: Returns the input unchanged (no encryption in community edition)
     */
    public String encrypt(String data) {
        return data;
    }

    /**
     * Stub: Returns the input unchanged (no decryption in community edition)
     */
    public String decrypt(String data) {
        return data;
    }

    /**
     * Stub: Always returns true (license always valid in community edition)
     */
    public boolean verifySignature(String data, String signature) {
        return true;
    }

    /**
     * Stub: Always returns true (license always valid in community edition)
     */
    public boolean verify(byte[] data, byte[] sig) {
        return true;
    }

    /**
     * Stub: Returns empty signature
     */
    public String sign(String data) {
        return "";
    }

    /**
     * Stub: Returns empty signature
     */
    public byte[] sign(byte[] data) {
        return new byte[0];
    }
}
