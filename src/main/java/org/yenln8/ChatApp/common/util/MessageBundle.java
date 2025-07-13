package org.yenln8.ChatApp.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

@Slf4j
@Service
public class MessageBundle {
    /**
     * Lấy message với locale cụ thể (hỗ trợ đa ngôn ngữ)
     * @param key - key trong file properties
     * @param params - các tham số
     * @return formatted message
     */
    public static String getMessage(String key, Object... params) {
        try {
            String bundleName = "messages";
            String locale = LocaleContextHolder.getLocale().getLanguage();
//            String locale = "en";
            ResourceBundle localeBundle = ResourceBundle.getBundle(bundleName, Locale.forLanguageTag(locale));
            String message = localeBundle.getString(key);
            log.info(message);
            if (params != null && params.length > 0) {
                return MessageFormat.format(message, params);
            }

            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageFormat.format("Fail to get message with key \"{0}\", params \"{1}\",\n {2}", key, params != null ? Arrays.toString(params) : "[]",e.getMessage());
        }
    }
}