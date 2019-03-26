package wodss.timecastfrontend.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceAccessException.class)
    public String handleConnectionException(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exception",
                "Cannot connect to API Server.\nPlease check the configured path is correct and the server is available.");
        return "redirect:/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleExceptions() {
        return "redirect:/error";
    }
}
