package wodss.timecastfrontend.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wodss.timecastfrontend.exceptions.TimecastForbiddenException;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;
import wodss.timecastfrontend.exceptions.TimecastUnauthorizedException;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(ResourceAccessException.class)
    public String handleConnectionException(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exception",
                "Cannot connect to API Server.\nPlease check the configured path is correct and the server is available.");
        return "redirect:/error";
    }

    @ExceptionHandler({TimecastUnauthorizedException.class, HttpClientErrorException.Unauthorized.class})
    public String handleUnauthorizedException(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        logger.info("Attempt to get access without being authorized");
        StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();
        if (queryString != null) {
            requestURL.append('?').append(queryString);
        }

        logger.debug("After login, redirect to url: " + requestURL.toString());
        redirectAttributes.addFlashAttribute("redirect", requestURL.toString());
        return "redirect:/login";
    }

    @ExceptionHandler({TimecastForbiddenException.class, HttpClientErrorException.Forbidden.class})
    public String handleForbiddenException() {
        logger.warn("Attempt to get access without having the required permissions");
        return "errors/403";
    }

    @ExceptionHandler({TimecastNotFoundException.class, HttpClientErrorException.NotFound.class})
    public String handleNotFoundException() {
        return "errors/404";
    }

    @ExceptionHandler({TimecastInternalServerErrorException.class})
    public String handleInternalServerErrorException(TimecastInternalServerErrorException ex) {
        logger.error("Internal Server Error: " + ex.getMessage());
        return "errors/500";
    }

    @ExceptionHandler(Exception.class)
    public String handleExceptions(Exception ex) {
        logger.error("Unexpected Error occurred: " + ex.getMessage());
        return "errors/500";
    }
}
