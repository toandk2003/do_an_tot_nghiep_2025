package org.yenln8.ChatApp.common.schedule_task;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.yenln8.ChatApp.entity.AccountPending;
import org.yenln8.ChatApp.entity.OTP;
import org.yenln8.ChatApp.entity.PasswordPending;
import org.yenln8.ChatApp.repository.AccountPendingRepository;
import org.yenln8.ChatApp.repository.OTPRepository;
import org.yenln8.ChatApp.repository.PasswordPendingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class RemoveAttachmentExpiredInS3Schedule {
    private static final int batchSize = 100;
    private OTPRepository otpRepository;
    private AccountPendingRepository accountPendingRepository;
    private PasswordPendingRepository passwordPendingRepository;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void schedule() {
        //TODO - limit usage
    }
}
