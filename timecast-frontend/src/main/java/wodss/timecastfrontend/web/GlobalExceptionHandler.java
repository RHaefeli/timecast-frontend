package wodss.timecastfrontend.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wodss.timecastfrontend.exceptions.TimecastForbiddenException;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceAccessException.class)
    public String handleConnectionException(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exception",
                "Cannot connect to API Server.\nPlease check the configured path is correct and the server is available.");
        return "redirect:/error";
    }

    @ExceptionHandler(TimecastForbiddenException.class)
    public String handleForbiddenException() {
        return "redirect:/403";
    }

    @ExceptionHandler(TimecastNotFoundException.class)
    public String handleNotFoundException() {
        return "redirect:/404";
    }

    @ExceptionHandler(TimecastInternalServerErrorException.class)
    public String handleInternalServerErrorException() {
        return "redirect:/500";
    }

    @ExceptionHandler(Exception.class)
    public String handleExceptions() {
        return "redirect:/500";
    }
}
