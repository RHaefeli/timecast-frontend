package wodss.timecastfrontend.services.auth;

import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {
    public static void createSessionCookie(HttpServletResponse response, String cookieName, String cookieValue, Boolean secure, Integer maxAge, String domain) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setSecure(secure);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        cookie.setDomain(domain);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static void clearCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public static String getValue(HttpServletRequest request, String cookieName) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        return cookie != null ? cookie.getValue() : null;
    }

    public static void setValue(HttpServletRequest request, String cookieName, String value) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        if (cookie != null) {
            cookie.setValue(value);
        }
    }
}
