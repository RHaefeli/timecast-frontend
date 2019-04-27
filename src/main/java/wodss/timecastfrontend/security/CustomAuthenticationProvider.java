package wodss.timecastfrontend.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import wodss.timecastfrontend.domain.Employee;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;
import wodss.timecastfrontend.exceptions.TimecastPreconditionFailedException;
import wodss.timecastfrontend.services.LoginService;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LoginService loginService;
    private final JwtUtil jwtUtil;

    @Autowired
    public CustomAuthenticationProvider(LoginService loginService, JwtUtil jwtUtil) {
        this.loginService = loginService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        try{
            Token token = loginService.createToken(email, password);
            if (token != null) {
                Employee emp = jwtUtil.getEmployeeFromToken(token);
                List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
                grantedAuthorities.add(new SimpleGrantedAuthority(emp.getRole().getValue()));
                // the token value is saved as the user name
                return new UsernamePasswordAuthenticationToken(token.getToken(), "", grantedAuthorities);
            } else {
                return null;
            }
        } catch (TimecastPreconditionFailedException | TimecastNotFoundException
                | HttpClientErrorException.Unauthorized | HttpClientErrorException.NotFound ex) {
            logger.debug("Login was not successful. Exception: " + ex.getMessage());
            throw new BadCredentialsException("User or password incorrect");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
