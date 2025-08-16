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
public class ExpireOTPSchedule {
    private static final int batchSize = 100;
    private OTPRepository otpRepository;
    private AccountPendingRepository accountPendingRepository;
    private PasswordPendingRepository  passwordPendingRepository;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void scheduleExpireOTP() {
        try {
            List<OTP> otps = otpRepository.findAllByDeletedAtIsNull(PageRequest.of(0, batchSize)).getContent();
            for (OTP otp : otps) {
                if (otp.getStatus().equals(OTP.STATUS.BE_SENT) && otp.getExpireAt().isBefore(LocalDateTime.now())) {
                    otp.setStatus(OTP.STATUS.EXPIRED);
                    otp.setDeletedAt(LocalDateTime.now());
                    otp.setDeleted(otp.getId());
                    this.otpRepository.save(otp);

                    log.warn("Freedom success OTP is:  {}", otp.getOtpCode());

                    switch (otp.getType()) {
                        case REGISTER_ACCOUNT -> this.handleExpireOTPRegistration(otp);
                        case CHANGE_PASSWORD, FORGOT_PASSWORD -> this.handleExpireOTPForgotAndChangePassword(otp);
                    }

                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void handleExpireOTPRegistration(OTP otp) {
        Optional<AccountPending> optionalAccountPending = this.accountPendingRepository.findByOtpIdAndStatusAndDeletedAtIsNull(otp.getId(), AccountPending.STATUS.PENDING);

        if (optionalAccountPending.isPresent()) {
            AccountPending accountPending = optionalAccountPending.get();

            accountPending.setStatus(AccountPending.STATUS.EXPIRED);
            accountPending.setDeletedAt(LocalDateTime.now());
            accountPending.setDeleted(accountPending.getId());

            this.accountPendingRepository.save(accountPending);
        }
    }

    private void handleExpireOTPForgotAndChangePassword(OTP otp) {
        Optional<PasswordPending> optionalPasswordPending = this.passwordPendingRepository.findByOtpIdAndStatusAndDeletedAtIsNull(otp.getId(), PasswordPending.STATUS.PENDING);

        if (optionalPasswordPending.isPresent()) {
            PasswordPending passwordPending = optionalPasswordPending.get();

            passwordPending.setStatus(PasswordPending.STATUS.EXPIRED);
            passwordPending.setDeletedAt(LocalDateTime.now());
            passwordPending.setDeleted(passwordPending.getId());

            this.passwordPendingRepository.save(passwordPending);
        }
    }

}
