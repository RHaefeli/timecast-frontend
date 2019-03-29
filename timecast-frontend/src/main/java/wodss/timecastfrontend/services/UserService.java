package wodss.timecastfrontend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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
public class UserService extends AbstractService<User> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.employee}") String apiURL) {
        super(restTemplate, apiURL, User.class);
    }
}
