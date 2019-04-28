package wodss.timecastfrontend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import wodss.timecastfrontend.domain.Token;

@Component
public class LoginService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private RestTemplate restTemplate;
    private String apiURL;

    @Autowired
    public LoginService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.token}") String apiURL) {
        this.restTemplate = restTemplate;
        this.apiURL = apiURL;
    }

    public Token createToken(String email, String password) {
        logger.debug("Create Token for " + email + " on api: " + apiURL);
        HttpEntity<String> request = new HttpEntity<>("{\"emailAddress\":\"" + email + "\",\"rawPassword\":\"" + password + "\"}");
        ResponseEntity<Token> response = restTemplate.exchange(apiURL, HttpMethod.POST, request, Token.class);
        HttpStatus statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.CREATED) {
            // Other status codes are mapped by the RestTemplate Error Handler
            throw new IllegalStateException(statusCode.toString());
        }
        logger.debug("Received Token");
        return response.getBody();
    }

    public Token updateToken(Token oldToken) {
        logger.debug("Update Token on api: " + apiURL);
        HttpEntity<Token> request = new HttpEntity<>(oldToken);
        ResponseEntity<Token> response = restTemplate.exchange(apiURL, HttpMethod.PUT, request, Token.class);
        HttpStatus statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            // Other status codes are mapped by the RestTemplate Error Handler
            throw new IllegalStateException(statusCode.toString());
        }
        logger.debug("Received new Token");
        return response.getBody();
    }
}
