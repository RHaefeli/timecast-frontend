package wodss.timecastfrontend.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import wodss.timecastfrontend.domain.Employee;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.services.LoginService;
import wodss.timecastfrontend.services.auth.CookieUtil;
import wodss.timecastfrontend.services.auth.JwtUtil;
import wodss.timecastfrontend.services.mocks.MockLoginService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value="/")
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LoginService loginService;
    private final JwtUtil jwtUtil;

    @Value("${wodss.timecastfrontend.cookies.token}")
    private String tokenCookieName;

    @Value("${server.address}")
    private String serverDomain;

    @Autowired
    public LoginController(MockLoginService loginService, JwtUtil jwtUtil) {
        this.loginService = loginService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping(value="login")
    public String getLoginForm(HttpServletRequest request, Model model) {
        logger.debug("Request Login form");
        if (alreadyLoggedIn(request)) {
            logger.debug("User already logged in");
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping(value="login")
    public String login(HttpServletRequest request, HttpServletResponse response, String email,
                        String password, String redirect, Model model) {
        // Check if already logged in
        logger.debug("Perform Login");
        if (alreadyLoggedIn(request)) {
            logger.debug("User already logged in");
            return "redirect:/";
        }

        Token token = loginService.createToken(email, password);
        // TODO: set "secure" true to require https only
        CookieUtil.createSessionCookie(response, tokenCookieName, token.getToken(), false, -1, serverDomain);
        Employee employee = jwtUtil.getEmployeeFromToken(token);
        if (employee != null) {
            model.addAttribute("permission", employee.getRole().getValue().toUpperCase());
        } else {
            model.addAttribute("permission", "NONE");
        }
        if ("null".equals(redirect)) logger.debug("Redirect to " + redirect);
        return "null".equals(redirect) ? "redirect:/" : "redirect:" + redirect;
    }

    @GetMapping(value="logout")
    public String logout(HttpServletResponse response) {
        logger.debug("Perform Logout");
        // TODO: clear other cookies?
        CookieUtil.clearCookie(response, tokenCookieName);
        return "redirect:/login";
    }

    private boolean alreadyLoggedIn(HttpServletRequest request) {
        String tokenVal = CookieUtil.getValue(request, tokenCookieName);
        if (tokenVal != null) {
            try {
                long ms = jwtUtil.getExpirationTimeFromToken(new Token(tokenVal));
                return ms > 0;
            } catch (Exception ex) {
                // if the token has expired or is somehow invalid just ignore it and let the user login again
            }
        }
        return false;
    }
}
