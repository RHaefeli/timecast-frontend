package wodss.timecastfrontend.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wodss.timecastfrontend.domain.Employee;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;

import java.security.PublicKey;
import java.util.Date;


@Component
public class JwtUtil {
    private ObjectMapper objectMapper = new ObjectMapper();
    private PublicKey publicKey;

    public JwtUtil (@Value("${wodss.timecastfrontend.jwt.key-store}") String publicKeyLocation) {
        String publicKeyPEM = RsaUtil.getKey(publicKeyLocation);
        publicKey = RsaUtil.getPublicKeyFromString(publicKeyPEM);
    }

    /**
     * Extracts the employee from the employee Claim in the JWT.
     * @param token The JWT Token which contains an employee Claim.
     * @return The extracted employee.
     */
    public Employee getEmployeeFromToken(Token token) {
        Claims claims = Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(token.getToken()).getBody();
        return objectMapper.convertValue(claims.get("employee"), Employee.class);
    }

    /**
     * Gets the expiration time of a JWT Token.
     * @param token The JWT Token.
     * @return The expiration time.
     */
    public long getExpirationTimeFromToken(Token token) {
        Claims claims = Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(token.getToken()).getBody();
        Date exp = claims.getExpiration();
        if (exp == null) throw new TimecastInternalServerErrorException("");
        Date now = new Date();
        return exp.getTime() - now.getTime();
    }
}
