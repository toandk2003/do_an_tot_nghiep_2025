//package org.yenln8.ChatApp.common.schedule_task;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.yenln8.ChatApp.entity.AccountPending;
//import org.yenln8.ChatApp.entity.OTP;
//import org.yenln8.ChatApp.repository.AccountPendingRepository;
//import org.yenln8.ChatApp.repository.OTPRepository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Component
//@Slf4j
//@AllArgsConstructor
//public class FreedomOTPSchedule {
//    private static final int batchSize = 100;
//    private OTPRepository otpRepository;
//    private AccountPendingRepository accountPendingRepository;
//
//    @Scheduled(fixedDelay = 10000)
//    public void scheduleFreedomOTPRegister() {
//        log.info("Schedule FreedomOTPSchedule is running..............");
//        try {
//            List<OTP> otps = otpRepository.findAllByTypeAndDeletedAtIsNull(OTP.TYPE.REGISTER_ACCOUNT, PageRequest.of(0, batchSize)).getContent();
//            for (OTP otp : otps) {
//                if (!otp.getStatus().equals(OTP.STATUS.VERIFIED) && otp.getExpireAt().isBefore(LocalDateTime.now())) {
//                    otp.setStatus(OTP.STATUS.EXPIRED);
//                    otp.setDeletedAt(LocalDateTime.now());
//                    otp.setDeleted(otp.getId());
//                    this.otpRepository.save(otp);
//
//                    log.warn("Freedom success OTP is:  {}", otp.getOtpCode());
//
//                    Optional<AccountPending> optionalAccountPending = this.accountPendingRepository.findByOtpIdAndStatusAndDeletedAtIsNull(otp.getId(), AccountPending.STATUS.PENDING);
//                    if (optionalAccountPending.isPresent()) {
//                        AccountPending accountPending = optionalAccountPending.get();
//                        accountPending.setStatus(AccountPending.STATUS.EXPIRED);
//                        accountPending.setDeletedAt(LocalDateTime.now());
//                        accountPending.setDeleted(accountPending.getId());
//                        this.accountPendingRepository.save(accountPending);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//    }
//}
