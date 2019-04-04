package wodss.timecastfrontend.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.RestTemplate;

@ControllerAdvice
public class GlobalPermissionHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RestTemplate restTemplate;

    @Autowired
    GlobalPermissionHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @ModelAttribute
    public void addPermissionsToModel(Model model) {
        // TODO: Check permissions based on user. (What if connection error?)
        try {
            String permission = "DEVELOPER";
            String user = "..tbd..";
            logger.debug("Employee " + user + " has permission: " + permission);
            model.addAttribute("permission", permission);
        } catch (Exception ex) {
            logger.error("Exception while accessing api for a user. Set no permissions for user.");
            model.addAttribute("permission", "");
        }
    }
}
