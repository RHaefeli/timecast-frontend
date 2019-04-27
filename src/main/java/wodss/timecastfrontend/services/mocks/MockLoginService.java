package wodss.timecastfrontend.services.mocks;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import wodss.timecastfrontend.domain.Employee;
import wodss.timecastfrontend.domain.Role;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.services.LoginService;
import wodss.timecastfrontend.services.auth.JwtUtil;
import wodss.timecastfrontend.services.auth.RsaUtil;

import java.security.PrivateKey;
import java.util.Date;

@Component
public class MockLoginService extends LoginService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final long VALIDATION_DURATION_IN_MS = 30 * 60 * 1000;
    private final JwtUtil jwtUtil;

    /*
    @Value("${wodss.timecastfrontend.mock.api.privkey}")
    private String SECRET_KEY;
    */
    private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS512;

    @Autowired
    public MockLoginService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.token}") String apiURL, JwtUtil jwtUtil) {
        super(restTemplate, apiURL);
        this.jwtUtil = jwtUtil;
        logger.debug("Using Mock Login Service!");
        logger.debug("API URL " + apiURL + " will not be used in the mock service!");
    }

    @Override
    public Token createToken(String email, String password) {
        logger.debug("Create Token for " + email);
        Employee employee = new Employee();
        employee.setId(0);
        employee.setEmailAddress(email);
        employee.setRole(Role.PROJECTMANAGER);


        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + VALIDATION_DURATION_IN_MS);

        // Using RSA512 Algorithm
        String privateKeyPEM = RsaUtil.getKey("private_key.pem");
        PrivateKey key = RsaUtil.getPrivateKeyFromString(privateKeyPEM);

        // Using HS512 Algorithm
        // byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        // Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        return new Token(Jwts.builder()
                .setIssuer("FHNW wodss")
                .setSubject(email)
                .claim("employee", employee)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(key, signatureAlgorithm)
                .compact());
    }


    @Override
    public Token updateToken(Token oldToken) {
        logger.debug("Update Token: " + oldToken.getToken());

        Employee employee = jwtUtil.getEmployeeFromToken(oldToken);
        return createToken(employee.getEmailAddress(), "");
    }

}
