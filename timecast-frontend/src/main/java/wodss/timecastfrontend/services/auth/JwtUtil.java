package wodss.timecastfrontend.services.auth;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wodss.timecastfrontend.domain.Employee;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;

import java.security.PublicKey;
import java.util.Date;


@Component
public class JwtUtil {
    @Value("${wodss.timecastfrontend.api.pubkey}")
    private String SECRET_KEY;
    private ObjectMapper objectMapper = new ObjectMapper();
    private PublicKey publicKey;

    public JwtUtil () {
        String publicKeyPEM = RsaUtil.getKey("public_key.pem");
        publicKey = RsaUtil.getPublicKeyFromString(publicKeyPEM);
    }

    public Employee getEmployeeFromToken(Token token) {
        Claims claims = Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(token.getToken()).getBody();
        return objectMapper.convertValue(claims.get("employee"), Employee.class);
    }

    public long getExpirationTimeFromToken(Token token) {
        Claims claims = Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(token.getToken()).getBody();
        Date exp = claims.getExpiration();
        if (exp == null) throw new TimecastInternalServerErrorException(""); // TODO: Error?
        Date now = new Date();
        return exp.getTime() - now.getTime();
    }
}
