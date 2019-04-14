package wodss.timecastfrontend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import wodss.timecastfrontend.web.SessionHandlerInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final SessionHandlerInterceptor sessionHandlerInterceptor;

    @Autowired
    public WebMvcConfig(SessionHandlerInterceptor sessionHandlerInterceptor) {
        this.sessionHandlerInterceptor = sessionHandlerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionHandlerInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login/**", "/css/app.css", "/css/login.css", "/logout/**");
    }
}
