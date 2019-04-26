package wodss.timecastfrontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastUnauthorizedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SpringBootApplication
public class TimecastFrontendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimecastFrontendApplication.class, args);
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	HandlerExceptionResolver errorHandler() {
		// Exception handling for interceptors and filters outside of a controller
		// https://stackoverflow.com/questions/29999949/spring-exception-handler-outside-controller
		// HandlerExceptionResolver example
		// https://www.logicbig.com/tutorials/spring-framework/spring-web-mvc/handler-exception-resolver.html
		return new HandlerExceptionResolver() {
			@Override
			public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
				String redirectPage = "redirect:/error"; // this is the default error page
				if ( ex instanceof TimecastUnauthorizedException) {
					// if an unauthorized error is thrown by an interceptorHandler
					redirectPage = "redirect:/login";
				} else if (ex instanceof TimecastInternalServerErrorException) {
					// if an internalServerError error is thrown by an interceptorHandler
					redirectPage = "redirect:/error";
				}
				ModelAndView model = new ModelAndView(redirectPage);
				model.addObject("exceptionType", ex);
				model.addObject("handlerMethod", handler);
				return model;
			}
		};
	}
}
