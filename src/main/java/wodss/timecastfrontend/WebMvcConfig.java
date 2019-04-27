package wodss.timecastfrontend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import wodss.timecastfrontend.domain.Role;
import wodss.timecastfrontend.security.CustomAuthenticationProvider;
import wodss.timecastfrontend.web.SessionHandlerInterceptor;

import java.util.Locale;


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
                .excludePathPatterns("/", "/login/**", "/css/app.css", "/css/login.css", "/logout/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/css/app.css", "/css/login.css").permitAll() // Allow css styles for login page
                .antMatchers("/employees").hasAuthority(Role.ADMINISTRATOR.getValue())
                .antMatchers("/employees/{id}").hasAuthority(Role.ADMINISTRATOR.getValue())
                .antMatchers("/employees/{id}/contracts").hasAuthority(Role.ADMINISTRATOR.getValue())
                .antMatchers("/projects").hasAnyAuthority(Role.ADMINISTRATOR.getValue(), Role.PROJECTMANAGER.getValue())
                .antMatchers("/projects/{id}").hasAnyAuthority(Role.ADMINISTRATOR.getValue(), Role.PROJECTMANAGER.getValue())
                .antMatchers("/contracts").hasAuthority(Role.ADMINISTRATOR.getValue())
                .antMatchers("/contracts/{id}").hasAuthority(Role.ADMINISTRATOR.getValue())
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .failureUrl("/login?error")
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

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.US);
        return slr;
    }
}
