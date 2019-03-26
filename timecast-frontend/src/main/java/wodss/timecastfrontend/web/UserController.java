package wodss.timecastfrontend.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wodss.timecastfrontend.domain.User;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;
import wodss.timecastfrontend.exceptions.TimecastPreconditionFailedException;
import wodss.timecastfrontend.services.UserService;

import javax.validation.Valid;
import java.net.ConnectException;

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
        logger.debug("Get all users");
        return "users/list";
    }

    @GetMapping(params = "form")
    public String createForm(Model model) {
        logger.debug("Get create user form");
        model.addAttribute("user", new User());
        return "users/create";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        logger.debug("Create user: " + user);
        if (bindingResult.hasErrors()) {
            logger.debug("Binding error: " + bindingResult.getAllErrors());
            return "users/create";
        }
        try {
            User newUser = userService.save(user);
            redirectAttributes.addFlashAttribute("success", "Success");
            return "redirect:/users";
        } catch (TimecastNotFoundException
                | TimecastPreconditionFailedException
                | TimecastInternalServerErrorException ex) {
            model.addAttribute("exception", ex.getMessage());
            return "users/create";
        }
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Long id) {
        return null;
    }

    @PutMapping("/{id}")
    public String updateById(@PathVariable Long id) {
        // TODO: pass form
        // TODO: show list or users?
        return "users/update";
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id) {
        // TODO: redirect
        return null;
    }
}
