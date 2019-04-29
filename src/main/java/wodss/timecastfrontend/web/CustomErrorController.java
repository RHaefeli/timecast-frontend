package wodss.timecastfrontend.web;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * Overrides spring error handler to show own error pages on exceptions outside of
 * implemented controllers
 *
 */
@Controller
@RequestMapping(value = "/error")
public class CustomErrorController implements ErrorController {

	/**
	 * Handles error and show error page
	 * @param request
	 * @return 
	 */
    @GetMapping
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.valueOf(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) return "errors/404";
            else if (statusCode == HttpStatus.FORBIDDEN.value()) return "errors/403";
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) return "errors/500";
        }

        return "errors/error";
    }

    @Override
    public String getErrorPath() {
        return "error";
    }
}
