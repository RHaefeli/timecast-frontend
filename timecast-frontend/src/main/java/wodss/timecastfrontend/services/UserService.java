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
import wodss.timecastfrontend.domain.User;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;
import wodss.timecastfrontend.exceptions.TimecastPreconditionFailedException;

import java.net.ConnectException;
import java.util.List;

@Component
public class UserService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${wodss.timecastfrontend.api.url.employee}")
    private String apiURL;

    private final RestTemplate restTemplate;

    @Autowired
    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<User> getUsers() {
        /*
        String url = "https://.../users";
        logger.debug("REST call url: " + url);
        UserDTO> user = restTemplate.getForObject(url, UserDTO.class);
        logger.debug("Received user " + user);
         */
        return null;
    }

    public User save(User user) throws TimecastNotFoundException, TimecastPreconditionFailedException,
            TimecastInternalServerErrorException {
        HttpEntity<User> request = new HttpEntity<>(user);
        ResponseEntity<User> response = restTemplate.exchange(apiURL, HttpMethod.POST, request, User.class);
        HttpStatus statusCode = response.getStatusCode();
        switch (statusCode) {
            case CREATED: break;
            case NOT_FOUND: throw new TimecastNotFoundException(response.getStatusCode().getReasonPhrase());
            case PRECONDITION_FAILED: throw new TimecastPreconditionFailedException(response.getStatusCode().getReasonPhrase());
            case INTERNAL_SERVER_ERROR: throw new TimecastInternalServerErrorException(response.getStatusCode().getReasonPhrase());
            default:
        }
        return response.getBody();
    }
}
