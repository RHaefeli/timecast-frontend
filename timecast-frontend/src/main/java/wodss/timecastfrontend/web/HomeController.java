package wodss.timecastfrontend.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/")
public class HomeController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping
    public String get() {
        return "redirect:/projects";
    }

    @GetMapping(value="login")
    public String getLoginForm(HttpServletRequest request, Model model) {
        logger.debug("Request Login form");
        return "login";
    }
}
