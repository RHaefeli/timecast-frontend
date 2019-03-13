package wodss.timecastfrontend.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import wodss.timecastfrontend.domain.User;
import wodss.timecastfrontend.services.UserService;

import javax.validation.Valid;

@Controller
@RequestMapping(value="/users")
public class UserController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String getAll() {
        return "users/list";
    }

    @GetMapping(params = "form")
    public String createForm(Model model) {
        logger.debug("Get create form");
        model.addAttribute("user", new User());
        return "users/create";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        logger.debug("Create: " + user);
        if (bindingResult.hasErrors()) {
            logger.debug("Binding error: " + bindingResult.getAllErrors());
            return "users/create";
        }
        // userService.save(user);
        return "redirect:/users";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Long id) {
        return null;
    }

    @PutMapping("/{id}")
    public String updateById(@PathVariable Long id) {
        // TODO: pass form
        // TODO: show list or users?
        return null;
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id) {
        // TODO: redirect
        return null;
    }
}
