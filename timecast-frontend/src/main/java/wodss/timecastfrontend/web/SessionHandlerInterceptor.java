package wodss.timecastfrontend.web;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.exceptions.*;
import wodss.timecastfrontend.services.LoginService;
import wodss.timecastfrontend.services.auth.CookieUtil;
import wodss.timecastfrontend.services.auth.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SessionHandlerInterceptor implements HandlerInterceptor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JwtUtil jwtUtil;
    private final LoginService loginService;

    @Value("${wodss.timecastfrontend.cookies.token}")
    private String tokenCookieName;

    @Autowired
    public SessionHandlerInterceptor(JwtUtil jwtUtil, LoginService loginService) {
        this.jwtUtil = jwtUtil;
        this.loginService = loginService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) {
        String tokenValue = CookieUtil.getValue(request, tokenCookieName);
        logger.debug("Token value: " + tokenValue);
        if (tokenValue == null) {
            throw new TimecastUnauthorizedException("No Cookie present containing the token information");
        }
        Token token = new Token(tokenValue);

        try {
            if (checkTokenExpiresSoon(token)) {
                token = loginService.updateToken(token);
                CookieUtil.setValue(request, tokenCookieName, token.getToken());
            }
        } catch (TimecastInternalServerErrorException | TimecastNotFoundException
                | TimecastPreconditionFailedException ex) {
            logger.warn("Exception occurred while updating Token: " + ex.getMessage());
            CookieUtil.clearCookie(response, tokenCookieName);
            throw new TimecastInternalServerErrorException(ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.debug("Token expired");
            CookieUtil.clearCookie(response, tokenCookieName);
            throw new TimecastUnauthorizedException(ex.getMessage());
        } catch (SignatureException ex) {
            // TODO: handle signature exception
            logger.warn("Attempt to validate a token with invalid signature: " + ex.getMessage());
            CookieUtil.clearCookie(response, tokenCookieName);
            throw new TimecastForbiddenException(ex.getMessage());
        }

        return true;
    }

    private boolean checkTokenExpiresSoon(Token token) {
        long ms = jwtUtil.getExpirationTimeFromToken(token);
        return ms < 10 * 60 * 1000; // 10 min
    }
}
