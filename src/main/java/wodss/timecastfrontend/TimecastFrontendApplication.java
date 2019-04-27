package wodss.timecastfrontend;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastUnauthorizedException;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SpringBootApplication
public class TimecastFrontendApplication {
	@Value("${trust.store}")
	private Resource trustStore;
	@Value("${trust.store.password}")
	private String trustStorePassword;

	public static void main(String[] args) {
		SpringApplication.run(TimecastFrontendApplication.class, args);
	}

	@Bean
	RestTemplate restTemplate() throws Exception {
		SSLContext sslContext = new SSLContextBuilder()
				.loadTrustMaterial(trustStore.getURL(), trustStorePassword.toCharArray())
				.build();
		SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
		HttpClient httpClient = HttpClients.custom()
				.setSSLSocketFactory(socketFactory)
				.build();
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
		return new RestTemplate(factory);
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
