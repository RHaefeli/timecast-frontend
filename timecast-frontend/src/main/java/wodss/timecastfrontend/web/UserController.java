package wodss.timecastfrontend.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import wodss.timecastfrontend.domain.User;
import wodss.timecastfrontend.services.UserService;

import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping(value="/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String getAll() {
        return null;
    }

    @GetMapping(params = "form")
    public String createForm(Model model) {
        model.addAttribute("user", new User());
        return "users/create";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        // TODO: pass form
        // TODO: redirect
        return "users/create";
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
