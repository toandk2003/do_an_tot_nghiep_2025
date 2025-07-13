package org.yenln8.ChatApp.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Slf4j
@Component
public class LocaleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // Lấy Accept-Language từ header
        String acceptLanguage = request.getHeader("Accept-Language");

        if (StringUtils.hasText(acceptLanguage)) {
            try {
                // Parse Accept-Language header để lấy ngôn ngữ ưu tiên
                Locale locale = parseAcceptLanguage(acceptLanguage);

                // Gắn locale vào LocaleContextHolder
                LocaleContextHolder.setLocale(locale);

                log.info("Set locale: " + locale + " for request: " + request.getRequestURI());
            } catch (Exception e) {
                // Nếu không parse được, sử dụng locale mặc định
                LocaleContextHolder.setLocale(Locale.getDefault());
                log.error("Failed to parse Accept-Language, using default locale");
            }
        } else {
            // Nếu không có Accept-Language header, sử dụng locale mặc định
            LocaleContextHolder.setLocale(Locale.getDefault());
        }
        log.info("Current locale: " + LocaleContextHolder.getLocale().getLanguage() + " for request: " + request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion( @NonNull HttpServletRequest request,@NonNull  HttpServletResponse response,
                               @NonNull Object handler, Exception ex) {
        // Dọn dẹp LocaleContextHolder sau khi request hoàn thành
        LocaleContextHolder.resetLocaleContext();
    }

    private Locale parseAcceptLanguage(String acceptLanguage) {
        // Parse Accept-Language header (ví dụ: "en-US,en;q=0.9,vi;q=0.8")
        String[] languages = acceptLanguage.split(",");

        if (languages.length > 0) {
            // Lấy ngôn ngữ đầu tiên (có priority cao nhất)
            String primaryLanguage = languages[0].trim();

            // Loại bỏ quality value nếu có (;q=0.9)
            if (primaryLanguage.contains(";")) {
                primaryLanguage = primaryLanguage.split(";")[0].trim();
            }

            // Parse language tag (ví dụ: "en-US" -> Locale("en", "US"))
            if (primaryLanguage.contains("-")) {
                String[] parts = primaryLanguage.split("-");
                return new Locale(parts[0], parts[1]);
            } else {
                return new Locale(primaryLanguage);
            }
        }

        return Locale.getDefault();
    }
}