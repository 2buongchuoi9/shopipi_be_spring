package shopipi.click.utils;

import org.springframework.util.SerializationUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Base64;
import java.util.Optional;
import java.util.stream.Stream;

public class CookieUtils {

    public static Optional<Cookie> getCookie(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        Optional<Cookie> cookie = Optional.empty();

        if (cookies != null && cookies.length > 0)
            cookie = Stream.of(cookies)
                    .filter(ck -> ck.getName().equals(name))
                    .findFirst();

        // if (cookie != null) {
        // System.out.println("gettt cookie: " + cookie.get().getName() + " - " +
        // cookie.get().getValue());
        // }
        return cookie;
    }

    public static void addCookie(HttpServletResponse res, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        res.addCookie(cookie);
        System.out.println("name cookie: " + name + " - value cookie: " + value);
        // System.out.println("Added cookie: " + cookie.getName() + " - " +
        // cookie.getValue());
    }

    public static void deleteCookie(HttpServletRequest req, HttpServletResponse res, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null && cookies.length > 0)
            Stream.of(cookies)
                    .filter(cookie -> cookie.getName().equals(name))
                    .forEach(cookie -> {
                        cookie.setValue("");
                        cookie.setPath("/");
                        cookie.setMaxAge(0);
                        res.addCookie(cookie);
                    });

    }

    public static String serialize(Object object) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(object));
    }

    @SuppressWarnings("deprecation")
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(cookie.getValue())));
    }

}
