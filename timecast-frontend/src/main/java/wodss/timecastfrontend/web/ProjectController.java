package wodss.timecastfrontend.web;


import com.fizzed.rocker.RockerModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProjectController {
    @GetMapping()
    @ResponseBody
    public RockerModel index() {
        return templates.Index.template("Welcome", "Hi, Welcome to Rocker!");
    }
}
