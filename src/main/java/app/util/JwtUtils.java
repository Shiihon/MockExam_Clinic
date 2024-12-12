package app.util;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Properties;

public class JwtUtils {

    private static String SECRET_KEY;

    // Static block to load the secret key from the config.properties file
    static {
        loadConfig();  // Load properties when the class is loaded
    }

    // Load properties from the config.properties file
    private static void loadConfig() {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("config.properties")) {
            properties.load(fileInputStream);
            SECRET_KEY = properties.getProperty("jwt.secretKey");
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception, maybe throw a RuntimeException or log an error
        }
    }

    // Method to extract claims from the JWT
    private static JWTClaimsSet extractClaims(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet(); // Get all claims from the token
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to extract the username (subject) from the JWT token
    public static String extractUsername(String token) {
        JWTClaimsSet claims = extractClaims(token);
        if (claims != null) {
            return claims.getSubject();  // Get username (subject) from claims
        }
        return null;
    }

    // Method to extract the full name from the JWT token
    public static String extractName(String token) throws ParseException {
        JWTClaimsSet claims = extractClaims(token);
        if (claims != null) {
            return claims.getStringClaim("name");  // Get 'name' claim from token
        }
        return null;
    }
}
