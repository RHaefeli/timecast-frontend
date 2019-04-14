package wodss.timecastfrontend.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import wodss.timecastfrontend.domain.Employee;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.services.auth.CookieUtil;
import wodss.timecastfrontend.services.auth.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalPermissionHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JwtUtil jwtUtil;

    @Value("${wodss.timecastfrontend.cookies.token}")
    private String tokenCookieName;

    @Autowired
    public GlobalPermissionHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @ModelAttribute
    public void addPermissionsToModel(HttpServletRequest request, HttpServletResponse response, Model model) {
        String email = "_unknown_";
        String permission = "NONE";
        Employee employee = null;

        String tokenValue = CookieUtil.getValue(request, tokenCookieName);
        if (tokenValue != null) {
            Token token = new Token(tokenValue);
            employee = jwtUtil.getEmployeeFromToken(token);
        }

        if (employee != null) {
            email = employee.getEmailAddress();
            permission = employee.getRole().getValue().toUpperCase();
        }
        logger.debug("Employee " + email + " has permission: " + permission);
        model.addAttribute("permission", permission);
    }
}
