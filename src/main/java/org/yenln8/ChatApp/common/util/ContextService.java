package org.yenln8.ChatApp.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yenln8.ChatApp.dto.other.CurrentUser;

@Slf4j
public class ContextService {
    public static CurrentUser getCurrentUser() {
        try {
            Authentication securityContextHolder = SecurityContextHolder.getContext().getAuthentication();
            return (CurrentUser) securityContextHolder.getPrincipal();
        }
        catch (Exception e) {
            log.info("Exception in ContextService", e);
            throw e;
        }
    }
}
