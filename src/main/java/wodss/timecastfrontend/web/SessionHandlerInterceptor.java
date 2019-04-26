package wodss.timecastfrontend.web;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import wodss.timecastfrontend.domain.Employee;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.exceptions.*;
import wodss.timecastfrontend.services.LoginService;
import wodss.timecastfrontend.services.auth.JwtUtil;
import wodss.timecastfrontend.services.mocks.MockLoginService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Component
public class SessionHandlerInterceptor implements HandlerInterceptor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JwtUtil jwtUtil;
    private final LoginService loginService;

    @Autowired
    public SessionHandlerInterceptor(JwtUtil jwtUtil, MockLoginService loginService) {
        this.jwtUtil = jwtUtil;
        this.loginService = loginService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws ServletException {
        String tokenValue = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.debug("Token value: " + tokenValue);
        if (tokenValue == null) {
            throw new TimecastUnauthorizedException("");
        }

        Token token = new Token(tokenValue);
        try {
            if (checkTokenExpiresSoon(token)) {
                token = loginService.updateToken(token);
                Employee emp = jwtUtil.getEmployeeFromToken(token);
                List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
                grantedAuthorities.add(new SimpleGrantedAuthority(emp.getRole().getValue()));
                Authentication newAuth = new UsernamePasswordAuthenticationToken(token.getToken(), "", grantedAuthorities);
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            }
        } catch (TimecastInternalServerErrorException | TimecastNotFoundException
                | TimecastPreconditionFailedException ex) {
            logger.warn("Exception occurred while updating Token: " + ex.getMessage());
            request.logout();
            throw new TimecastInternalServerErrorException(ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.debug("Token expired");
            request.logout();
            throw new TimecastUnauthorizedException(ex.getMessage());
        } catch (SignatureException ex) {
            logger.warn("Attempt to validate a token with invalid signature: " + ex.getMessage());
            request.logout();
            throw new TimecastForbiddenException(ex.getMessage());
        }

        return true;
    }

    private boolean checkTokenExpiresSoon(Token token) {
        long ms = jwtUtil.getExpirationTimeFromToken(token);
        return ms < 25 * 60 * 1000; // 25 min
    }
}
