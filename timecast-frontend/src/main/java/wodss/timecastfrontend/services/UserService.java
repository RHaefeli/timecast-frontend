package wodss.timecastfrontend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import wodss.timecastfrontend.domain.User;

import java.util.List;

@Component
public class UserService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${wodss.timecastfrontend.api.url}")
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
}
