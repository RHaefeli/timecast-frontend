package wodss.timecastfrontend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import wodss.timecastfrontend.services.auth.CustomAuthenticationProvider;
import wodss.timecastfrontend.web.SessionHandlerInterceptor;


@Configuration
@EnableWebSecurity
public class WebMvcConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    private final SessionHandlerInterceptor sessionHandlerInterceptor;
    private final CustomAuthenticationProvider authProvider;

    @Autowired
    public WebMvcConfig(SessionHandlerInterceptor sessionHandlerInterceptor, CustomAuthenticationProvider authProvider) {
        this.sessionHandlerInterceptor = sessionHandlerInterceptor;
        this.authProvider = authProvider;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionHandlerInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login/**", "/css/app.css", "/css/login.css", "/logout/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/css/app.css", "/css/login.css").permitAll() // Allow css styles for login page
                .antMatchers("/employees/{id}").hasAuthority("DEVELOPER")
                .antMatchers("/employees").hasAuthority("ADMINISTRATOR")
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
                .permitAll()
                .logoutSuccessUrl("/")
                .and()
            .httpBasic();
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
