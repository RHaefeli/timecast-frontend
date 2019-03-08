package wodss.timecastfrontend.web;

import com.fizzed.rocker.RockerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import wodss.timecastfrontend.domain.User;
import wodss.timecastfrontend.services.UserService;

import javax.servlet.http.HttpServletRequest;
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
    @ResponseBody
    public RockerModel getAll() {
        return templates.users.List.template();
    }

    @GetMapping(params = "form")
    @ResponseBody
    public RockerModel createForm() {
        return templates.users.Create.template(new User());
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public RockerModel create(@RequestParam Map<String, String> name) {
        // TODO: pass form
        // TODO: redirect
        String s = "not worked!";
        String n = name.get("name");
        if (n != null) {
            s = n;
        }
        return templates.Index.template("Welcome", s);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public RockerModel getById(@PathVariable Long id) {
        return null;
    }

    @PutMapping("/{id}")
    @ResponseBody
    public RockerModel updateById(@PathVariable Long id) {
        // TODO: pass form
        // TODO: show list or user?
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public RockerModel deleteById(@PathVariable Long id) {
        // TODO: redirect
        return null;
    }
}
