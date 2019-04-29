package wodss.timecastfrontend.security;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.util.ResourceUtils;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;

import java.io.*;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Contains useful methods to work with the Rsa encryption
 *
 */
public class RsaUtil {
	
	/**
	 * Extracts private key
	 * @param key
	 * @return RSAPrivateKey
	 */
    public static RSAPrivateKey getPrivateKeyFromString(String key) {
        String privateKeyPEM = key;
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
        privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
        byte[] encoded = Base64.decodeBase64(privateKeyPEM);
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            return (RSAPrivateKey) kf.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new TimecastInternalServerErrorException("Incorrect RSA Private Key: " + ex.getMessage());
        }
    }

    /**
     * Extracts the public key 
     * @param key 
     * @return RSAPublicKey
     */
    public static RSAPublicKey getPublicKeyFromString(String key) {
        String publicKeyPEM = key;
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----\n", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
        byte[] encoded = Base64.decodeBase64(publicKeyPEM);
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new TimecastInternalServerErrorException("Incorrect RSA Public Key: " + ex.getMessage());
        }
    }

    /**
     * Reads the key file
     * @param filename of key
     * @return key as String
     */
    public static String getKey(String filename) {
        // Read key from file
        StringBuilder strKeyPEM = new StringBuilder();
        File file = null;
        try {
            file = ResourceUtils.getFile(filename);
        } catch (FileNotFoundException ex) {
            throw new TimecastInternalServerErrorException(ex.getMessage());
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while ((line = br.readLine()) != null) {
                strKeyPEM.append(line).append("\n");
            }
        } catch (IOException ex) {
            throw new TimecastInternalServerErrorException(ex.getMessage());
        }

        return strKeyPEM.toString();
    }
}
